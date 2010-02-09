/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 20-jul-2003
 * Time: 15:53:21
 */
package com.compomics.mslims.util.netphos;

import com.compomics.util.general.CommandLineParser;

import java.io.*;
import java.util.*;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This class can read netphos output from a file and parse it.
 *
 * @author Lennart Martens
 */
public class NetphosOutputReader {

    /**
     * The object mapping of the Netphos output.
     */
    private HashMap iNetphosOutput = null;

    /**
     * This constructor takes a File, pointing to the netphos output file.
     *
     * @param aInputFile    File pointing to the netphos output.
     * @throws IOException  when the reading of the file failed.
     */
    public NetphosOutputReader(File aInputFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(aInputFile));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        iNetphosOutput = this.parse(sb.toString());
    }

    /**
     * This method parses the netphos output file now in memory.
     *
     * @param aNetphosOutput the memory-mapped Netphos output file.
     * @return  a HashMap with the contents of the file as NetphosPredictions.
     * @exception IOException when the input file could not be parsed.
     */
    private HashMap parse(String aNetphosOutput) throws IOException {
        HashMap allPredictions = new HashMap();
        BufferedReader br = new BufferedReader(new StringReader(aNetphosOutput));
        String line = null;
        boolean titlePassed = false;
        boolean inProtein = false;
        NetphosPrediction np = null;
        String lAccession = null;
        String currentResidue = null;
        boolean possibleProtein = false;
        String possibleAccession = null;
        while((line = br.readLine()) != null) {
            line = line.trim();
            if(titlePassed) {
                // Determine whether we found the declaring line for a protein.
                boolean startOfProtein = false;
                // If the previous line started with a digit, see if this line starts with a letter
                // from the alphabet, and whether it ends in '80'.
                // If it does, we have found the start of a protein.
                if(possibleProtein) {
                    if(line.length() >= 1 && Character.isLetter(line.charAt(0)) && line.charAt(0) != '_' && line.endsWith("80")) {
                        startOfProtein = true;
                    }
                    possibleProtein = false;
                } else if(line.length() >= 1 && Character.isDigit(line.charAt(0))) {
                    // If the line starts with a digit, this could be a protein defintion.
                    possibleProtein = true;
                    possibleAccession = line;
                }
                if(inProtein) {
                    // This tests sees if we are in a new protein entry.
                    if(startOfProtein) {
                        // New protein entry, this entry has to be stored and a new one should be made.
                        // Storing this entry...
                        allPredictions.put(np.getAccession(), np.clone());
                        // ...and creating a new one.
                        lAccession = possibleAccession.substring(possibleAccession.indexOf(" ")+1).trim();
                        possibleAccession = null;
                        currentResidue = null;
                        np = new NetphosPrediction(lAccession);
                    } else if(!line.equals("")) {
                        // Okay, we're in a protein and this line could contain some information.
                        String lc = line.toLowerCase();
                        // See if we found one of the following:
                        //   - a residue header (serine, threonine or tyrosine), or
                        //   - a list of actual predictions.
                        if(lc.indexOf("serine") >= 0) {
                            currentResidue = "S";
                        } else if(lc.indexOf("threonine") >= 0) {
                            currentResidue = "T";
                        } else if(lc.indexOf("tyrosine") >= 0) {
                            currentResidue = "Y";
                        } else if(line.startsWith(lAccession)) {
                            // We found a prediction!
                            StringTokenizer parts = new StringTokenizer(line, " \t");
                            // First token is the accession number.
                            parts.nextToken();
                            // Second token is the location.
                            int loc = Integer.parseInt(parts.nextToken());
                            // Next token is the context.
                            String context = parts.nextToken();
                            // Next token is the score.
                            double score = Double.parseDouble(parts.nextToken());
                            // Last token is the residue. We just check whether or current residue is
                            // correct.
                            String residue = parts.nextToken();
                            if(residue.startsWith("*") && residue.endsWith("*")) {
                                if(!residue.substring(1, 2).equals(currentResidue)) {
                                    System.err.println("\nResidue as reported by Netphos was '" + residue.substring(1, 2) + "', while the program thought it was in the '" + currentResidue + "' section!");
                                }
                            }
                            // Add this prediction information!
                            np.addPrediction(loc, context, currentResidue, score);
                        }
                    }
                } else if(startOfProtein) {
                    inProtein = true;
                    lAccession = possibleAccession.substring(possibleAccession.indexOf(" ")+1).trim();
                    possibleAccession = null;
                    currentResidue = null;
                    np = new NetphosPrediction(lAccession);
                    startOfProtein = false;
                }
            } else if(line.toLowerCase().indexOf("netphos") >= 0) {
                titlePassed = true;
            }
        }
        if(!titlePassed) {
            throw new IOException("Not a Netphos output file!");
        } else if(np != null) {
            // Store the last one as well.
            allPredictions.put(np.getAccession(), np.clone());
        }
        return allPredictions;
    }

    /**
     * This method returns a String representation of this object.
     *
     * @return  String with a String representation of this object.
     */
    public String toString() {
        return this.toString(0.0);
    }

    /**
     * This method returns a HashMap with protein accession Strings as keys,
     * and all the predictions for the protein as values. <br />
     *
     * @return  HashMap with the predicted phosphorylation sites.
     */
    public HashMap getPredictions() {
        return this.iNetphosOutput;
    }

    /**
     * This method returns a String representation of this object.
     *
     * @param   aThreshold  double with the threshold required for printing.
     * @return  String with a String representation of this object.
     */
    public String toString(double aThreshold) {
        StringBuffer sb = new StringBuffer();
        Vector v = new Vector(iNetphosOutput.size());
        Iterator iter = iNetphosOutput.keySet().iterator();
        while(iter.hasNext()) {
            String s = (String)iter.next();
            v.add(s);
        }
        Collections.sort(v);
        int liSize = v.size();
        for(int i=0;i<liSize;i++) {
            NetphosPrediction np = (NetphosPrediction)iNetphosOutput.get(v.get(i));
            sb.append(np.toString(aThreshold) + "\n");
        }
        return sb.toString();
    }

    /**
     * The main method is the entry point for this application.
     *
     * @param args  String[] with the start-up arguments.
     */
    public static void main(String[] args) {
        if(args == null || args.length == 0) {
            System.err.println("\n\nUsage:\n\tNetphoOutputReader [--threshold <score_threshold_value>] <inputfile>\n\n");
            System.exit(1);
        }
        CommandLineParser clp = new CommandLineParser(args, new String[]{"threshold"});
        String thresholdString = clp.getOptionParameter("threshold");
        double threshold = -1;
        if(thresholdString != null) {
            try {
                threshold = Double.parseDouble(thresholdString);
                if(threshold < 0) {
                    throw new NumberFormatException("Threshold can not be negative!!");
                }
            } catch(NumberFormatException nfe) {
                System.err.println("\n\nThe threshold can only be a positive decimal number! You entered '" + thresholdString + "'!");
                System.exit(1);
            }
        }
        File f = new File(clp.getParameters()[0]);
        if(!f.exists()) {
            System.err.println("\n\nError:\n\tThe output file you specified (" + args[0] + ") does not exist!\n\n");
            System.exit(1);
        }
        try {
            NetphosOutputReader nor = new NetphosOutputReader(f);
            if(threshold >= 0) {
                System.out.println(nor.toString(threshold));
            } else {
                System.out.println(nor.toString());
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
