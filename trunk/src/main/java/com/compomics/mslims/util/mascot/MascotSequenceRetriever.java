/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 28-jul-2003
 * Time: 12:31:30
 */
package com.compomics.mslims.util.mascot;

import com.compomics.util.general.CommandLineParser;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/12/14 09:25:15 $
 */

/**
 * This class will contact a Mascot server (the one in the properties file) and request the
 * FASTA sequence for the specified accession number.
 *
 * @author Lennart Martens
 */
public class MascotSequenceRetriever {

    /**
     * The server URL.
     */
    private String iUrl = null;

    /**
     * Optional database name. When 'null', the autodetect feature should be used.
     */
    private String iDatabase = null;

    /**
     * This constructor takes the filename for the propertiesfile as argument
     * and reads the server URL from the 'URL' key. The properties file should be in the classpath and
     * it parses the value behind the 'URL' key such that it should start with 'HTTP://', then the server
     * name, then '/' and then 'mascot'. <br />
     * If any of this fails, a RuntimeException is thrown.
     *
     * @param   aPropertiesFile String  with the propertiesfile to read.
     * @throws  IllegalArgumentException when the propertiesfile was incorrectly formatted, or
     *          the value in the propertiesfile was incorrectly formatted (see above).
     */
    public MascotSequenceRetriever(String aPropertiesFile) {
        try {
            // See if we can find the properties file.
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(aPropertiesFile);
            if(is == null) {
                throw new IllegalArgumentException("The properties file '" + aPropertiesFile + "' was not found in the classpath!");
            }
            // Load the properties.
            Properties p = new Properties();
            p.load(is);
            // Try to retrieve the value, associated with the 'URL' key.
            String value = p.getProperty("URL");
            if(value == null || value.trim().equals("")) {
                throw new IllegalArgumentException("Properties file '" + aPropertiesFile + "' either does not contain a 'URL' key, or the value is empty!");
            }
            // See if the value is correctly formatted.
            value = value.trim().toLowerCase();
            if(!value.startsWith("http://")) {
                throw new IllegalArgumentException("Value '" + value + "' behind 'URL' key in the properties file '" + aPropertiesFile + "' does not start with 'http://'!");
            }
            int start = value.indexOf("/mascot");
            if(start < 0) {
                throw new IllegalArgumentException("Value '" + value + "' behind 'URL' key in the properties file '" + aPropertiesFile + "' does not contain with '/mascot'!");
            }
            // Okay, only retain the stuff up to and including '/mascot'.
            iUrl = value.substring(0, start+7);
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to read properties file '" + aPropertiesFile + "':" + e.getMessage());
        }
    }

    /**
     * This constructor allows the creation of a MascotSequenceRetriever
     * through the specification of a Mascot server hostname and an optional database name.
     *
     * @param   aHostname   String with the hostname for the Mascot server machine.
     * @param   aDatabase   String with the optional Mascot database name.
     *                      If this is absent, it will be autodetected.
     */
    public MascotSequenceRetriever(String aHostname, String aDatabase) {
        iUrl = "http://" + aHostname + "/mascot";
        iDatabase = aDatabase;
    }

    /**
     * This method reads the results from the server for the specified accession number.
     * It also automatically determines the database that needs be searched.
     *
     * @param aAccession    String with the accession number to look up.
     * @return  String with the FASTA formatted sequence.
     * @throws IOException  when the communication with the server broke down, or no
     *                      hit was found for the specified accession number.
     */
    public String getSequence(String aAccession) throws IOException {
        String result = null;
        BufferedReader br = this.retrieveSequence(aAccession, "seq");
        String line = null;
        String sequence = null;
        String header = null;
        boolean reading = false;
        while((line = br.readLine()) != null) {
            line = line.trim();
            if(line.startsWith("*")) {
                sequence = line;
            } else if(line.startsWith(">")) {
                header = line;
            }
        }

        if(header == null || sequence == null) {
            throw new IOException("Accession number '" + aAccession + "' was searched, yet no results were found!");
        } else {
            result = header + "\n" + sequence.substring(1);
        }

        return result;
    }

    /**
     * This method reads the results from the server for the specified accession number.
     * It also automatically determines the database that needs be searched.
     *
     * @param aAccession    String with the accession number to look up.
     * @return  String with the unformatted sequence.
     * @throws IOException  when the communication with the server broke down, or no
     *                      hit was found for the specified accession number.
     */
    public String getRawSequence(String aAccession) throws IOException {
        String result = null;
        BufferedReader br = this.retrieveSequence(aAccession, "all");
        String line = null;
        StringBuffer all = new StringBuffer();
        boolean reading = false;
        while((line = br.readLine()) != null) {
            line = line.trim();
            if(line.toUpperCase().startsWith("AC   ")) {
                reading = true;
                all.append(line+"\n");
            } else if(line.toUpperCase().indexOf("</PRE>")>=0) {
                reading = false;
            } else if(reading) {
                all.append(line+"\n");
            }
        }

        if(all.length() == 0) {
            throw new IOException("Accession number '" + aAccession + "' was searched, yet no results were found!");
        } else {
            result = all.toString();
        }
        return result;
    }

    /**
     * This method returns a reader for the results from the server for the specified accession number
     * in the specified mode. <br />
     * It also automatically determines the database that needs be searched.
     *
     * @param aAccession    String with the accession number to look up.
     * @param aMode String with the mode to retrieve the sequence in (eg.: 'all' or 'seq').
     * @return  BufferedReader with the reader to the sequence.
     * @throws IOException  when the communication with the server broke down, or no
     *                      hit was found for the specified accession number.
     */
    private BufferedReader retrieveSequence(String aAccession, String aMode) throws IOException {

        String database = iDatabase;
        String lowerCase = aAccession.toLowerCase();
        // See if we should autodetect the database, or use the specified one.
        if(iDatabase == null) {
            if(lowerCase.startsWith("o") || lowerCase.startsWith("p") || lowerCase.startsWith("q")) {
                database = "SwissProt";
            } else if(lowerCase.startsWith("ipi")) {
                database = "IPI_human";
            } else {
                database = "NCBInr";
            }
        }
        URL url = new URL(iUrl + "/cgi/getseq.pl?" + database + "+" + aAccession + "+" + aMode);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));;

        return br;
    }

    /**
     * This method can be used to retrieve a set of FASTA sequences from a set of accessions.<br />
     * It results in a series of calls to the 'getSequence' method, so no network optimization is done.
     *
     * @param aAccessions   String[] with the accession numbers for which to retrieve the FASTA sequences.
     * @return  String[] with the results, indexed in the same way as the accession numbers. So in the resultant
     *                   String[], element [0] will correspond to accession String[] element [0], [1] with [1], etc.
     * @throws IOException  when the communication with the server broke down, or no
     *                      hit was found for the specified accession number.
     */
    public String[] getSequences(String[] aAccessions) throws IOException {
        String[] allResults = new String[aAccessions.length];
        for(int i = 0; i < aAccessions.length; i++) {
            allResults[i] = this.getSequence(aAccessions[i]);
        }
        return allResults;
    }

    /**
     * This method can be used to retrieve a set of raw sequences from a set of accessions.<br />
     * It results in a series of calls to the 'getRawSequence' method, so no network optimization is done.
     *
     * @param aAccessions   String[] with the accession numbers for which to retrieve the raw sequences.
     * @return  String[] with the results, indexed in the same way as the accession numbers. So in the resultant
     *                   String[], element [0] will correspond to accession String[] element [0], [1] with [1], etc.
     * @throws IOException  when the communication with the server broke down, or no
     *                      hit was found for the specified accession number.
     */
    public String[] getRawSequences(String[] aAccessions) throws IOException {
        String[] allResults = new String[aAccessions.length];
        for(int i = 0; i < aAccessions.length; i++) {
            allResults[i] = this.getRawSequence(aAccessions[i]);
        }
        return allResults;
    }


    /**
     * This method can be used to retrieve a set of FASTA sequences from a set of accessions.<br />
     * It results in a series of calls to the 'getSequence' method, so no network optimization is done.
     *
     * @param aAccessions   Vector with the accession numbers for which to retrieve the FASTA sequences.
     * @return  String[] with the results, indexed in the same way as the accession numbers. So in the resultant
     *                   String[], element [0] will correspond to accession Vector element (0), [1] with (1), etc.
     */
    public String[] getSequences(Vector aAccessions) {
        int liSize = aAccessions.size();
        String[] allResults = new String[liSize];
        for(int i = 0; i < liSize; i++) {
            try {
                allResults[i] = this.getSequence((String)aAccessions.get(i));
            } catch(IOException ioe) {
                System.err.println("Failed to retrieve sequences: " + ioe.getMessage());
            }
        }
        return allResults;
    }

    /**
      * This method can be used to retrieve a set of raw sequences from a set of accessions.<br />
      * It results in a series of calls to the 'getSequence' method, so no network optimization is done.
      *
      * @param aAccessions   Vector with the accession numbers for which to retrieve the raw sequences.
      * @return  String[] with the results, indexed in the same way as the accession numbers. So in the resultant
      *                   String[], element [0] will correspond to accession Vector element (0), [1] with (1), etc.
      */
     public String[] getRawSequences(Vector aAccessions) {
         int liSize = aAccessions.size();
         String[] allResults = new String[liSize];
         for(int i = 0; i < liSize; i++) {
             try {
                 allResults[i] = this.getRawSequence((String)aAccessions.get(i));
             } catch(IOException ioe) {
                 System.err.println("Failed to retrieve sequences: " + ioe.getMessage());
             }
         }
         return allResults;
     }

    /**
     * This method is the netry point for th application.
     *
     * @param args  String[] with the start-up args.
     */
    public static void main(String[] args) {
        if(args == null || args.length == 0 || args.length > 2) {
            System.err.println("\n\nUsage:\n\tMascotSequenceRetriever [-a] <accession_number_to_retrieve>\n");
            System.err.println("\tOR\n");
            System.err.println("\tMascotSequenceRetriever [-a] @<file_with_accession_nbrs>\n\n");
            System.err.println("\n\tFlag significance:\n\t - a : get full-text report\n");
            System.exit(1);
        }
        CommandLineParser clp = new CommandLineParser(args);
        MascotSequenceRetriever msr = new MascotSequenceRetriever("MDMetOx.properties");
        Vector accessions = new Vector();
        String param = clp.getParameters()[0];
        if(param.startsWith("@")) {
            String file = param.substring(1);
            File f = new File(file);
            if(!f.exists()) {
                System.err.println("\n\nFile '" + file + "' could not be found!\n\n");
                System.exit(1);
            }
            // File to be read.
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line = null;
                while((line = br.readLine()) != null) {
                    line = line.trim();
                    if(!line.equals("")) {
                        accessions.add(line);
                    }
                }
                br.close();
            } catch(IOException ioe) {
                System.err.println("\n\nUnable to process file '': " + ioe.getMessage() + "\n");
                System.exit(1);
            }
        } else {
            accessions.add(param);
        }
        String[] result = null;;

        if(clp.hasFlag("a")) {
            result = msr.getRawSequences(accessions);
        } else {
            result = msr.getSequences(accessions);
        }


        for(int i = 0; i < result.length; i++) {
            String s = result[i];
            if(s != null) {
                System.out.println(s);
            } else {
                System.err.println(accessions.get(i) + ": result was 'null'!!");
            }
        }
    }
}
