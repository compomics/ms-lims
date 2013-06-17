/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 17-jan-03
 * Time: 18:08:57
 */
package com.compomics.mslimscore.util.sequence;

import org.apache.log4j.Logger;

import com.compomics.util.general.CommandLineParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2005/12/20 12:50:47 $
 */

/**
 * This class allows the command-line based retrieval of protein subsequences, based on a protein accession number and a
 * 'seed' sequence. The number of residues to retrieve can be specified on N-terminal and C-terminal side seperately.
 *
 * @author Lennart Martens
 */
public class RetrievePeptide {

    /**
     * Main method for this program. Start-up parameters are the following:
     * <p/>
     * <ul> <li><b>Nres</b>: the number of residues to grab at the N-terminal side of the presented sequence</li> *
     * <li><b>Cres</b>: the number of residues to grab at the C-terminal side of the presented sequence</li>
     * <li><b>sequence</b>: the peptide sequence to localize.</li> <li><b>accession</b>: the accession number for the
     * protein of interest.</li> <li><b>infile</b>: inputfile with 'accession,sequence,Nres,Cres' entries.</li> </ul>
     *
     * @param args String[] with the start-up params.
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            printUsage();
        }
        CommandLineParser clp = new CommandLineParser(args, new String[]{"Nres", "Cres", "sequence", "accession", "infile", "database", "hostname"});

        String database = clp.getOptionParameter("database");
        String hostname = clp.getOptionParameter("hostname");
        if (hostname == null) {
            System.err.println("\n\nYou need to specify a Mascot server hostname (eg. mascotserv.domain.com) to retrieve the sequences from!\n");
            printUsage();
        }
        String infile = clp.getOptionParameter("infile");
        String nRes = clp.getOptionParameter("Nres");
        if (nRes == null && infile == null) {
            System.err.println("\n\nYou need to specify the maximum number of residues to grab at N-terminal side!\n");
            printUsage();
        }
        int nres = 0;
        String cRes = clp.getOptionParameter("Cres");
        if (cRes == null && infile == null) {
            System.err.println("\n\nYou need to specify the maximum number of residues to grab at C-terminal side!\n");
            printUsage();
        }
        int cres = 0;

        String sequence = null;
        String accession = null;
        File input = null;
        boolean file = false;
        if (infile == null) {
            try {
                nres = Integer.parseInt(nRes);
            } catch (Exception e) {
                System.err.println("\n\nYou need to specify a positive whole number of residues to grab at N-terminal side!\n");
                printUsage();
            }
            try {
                cres = Integer.parseInt(cRes);
            } catch (Exception e) {
                System.err.println("\n\nYou need to specify a positive whole number of residues to grab at C-terminal side!\n");
                printUsage();
            }

            file = false;
            sequence = clp.getOptionParameter("sequence").trim();
            accession = clp.getOptionParameter("accession").trim();
            if (sequence == null || accession == null) {
                System.err.println("\n\nYou need to specify either a sequence and accession OR a file with these!\n");
                printUsage();
            }
        } else {
            input = new File(infile);
            if (!input.exists()) {
                System.err.println("\n\nThe input file you specified (" + infile + ") does not exist!\n");
                System.exit(1);
            }
            file = true;
        }

        // In getting here, all checks should have been passed.
        // Get a DB loader for the database.
        try {
            PeptideSequenceRegionRetriever retriever = new PeptideSequenceRegionRetriever(hostname, database);
            Vector result = null;
            if (file) {
                result = new Vector();
                // Gather all necessary info + store in a Vector.
                BufferedReader lbr = new BufferedReader(new FileReader(input));
                String line = null;
                int counter = 0;
                while ((line = lbr.readLine()) != null) {
                    counter++;
                    line = line.trim();
                    if ((line.startsWith(",") || line.startsWith(";") || line.startsWith(" ")) && line.length() > 1) {
                        line = line.substring(1);
                    }
                    StringTokenizer lst = new StringTokenizer(line, ",;");
                    if (lst.countTokens() != 4) {
                        System.err.println("\n\nFile is not structured correctly!\nShould be: accession,sequence,nterm residue number,cterm residue number!");
                        System.err.println("Proceeding with currently read entries.\n");
                        break;
                    } else {
                        String ac = lst.nextToken().trim();
                        if (counter == 1 && ac.equalsIgnoreCase("accession")) {
                            // Probable header line.
                            System.err.println("\nSkipped probable header at line 1, starting with '" + ac + "'.\n");
                            continue;
                        }
                        String seq = lst.nextToken().trim();
                        int n = -1;
                        int c = -1;
                        try {
                            n = Integer.parseInt(lst.nextToken());
                            c = Integer.parseInt(lst.nextToken());
                        } catch (Exception e) {
                            // If it is line 1, it is probably the header.
                            if (counter != 1) {
                                System.err.println("\n\nThe number of residues for N-terminal and C-terminal inclusion should be positive, whole numbers!");
                                System.err.println("Proceeding with currently read entries.\n");
                            } else {
                                System.err.println("\nSkipped probable header '" + line + "' at line 1.\n");
                                continue;
                            }
                        }
                        result.add(new SequenceRegion(ac, seq, n, c));
                    }
                }
                lbr.close();

                // We have everything.
                // Let's batch submit.
                result = retriever.retrieveSequenceRegions(result);
            } else {
                // Do a single request. Store it in a hash anyway.
                SequenceRegion sr = new SequenceRegion(accession, sequence, nres, cres);
                sr = retriever.retrieveSequenceRegion(sr);
                result = new Vector(1);
                result.add(sr);
            }
            visualizeVector(result);
        } catch (IOException ioe) {
            System.err.println("\n\nIOException occurred! Could not read from file!");
            ioe.printStackTrace();
        }
    }


    /**
     * This method prints information on the usage of the program to the stderr.
     */
    private static void printUsage() {
        System.err.println("\n\nUsage:\n\n\tPeptideSequenceRegion --infile <inputfile> --hostname <Mascot_server_hostname> [--database <Mascot_DB_name]]\n");
        System.err.println("\t OR\n");
        System.err.println("\tPeptideSequenceRegion --residues <residues_retained_on_either_side> --sequence <sequence> --accession <db_accession_number> --servername <Mascot_server_hostname> [--database <Mascot_DB_name]\n");
        System.exit(1);
    }

    /**
     * This method prints the results from the provided Vector.
     *
     * @param aVector Vector with the results of the query.
     */
    private static void visualizeVector(Vector aVector) {
        System.out.println("\n\n;Accession;Query;Nterm;Cterm;Found;Nterm addition;Cterm addition;Complete");
        int liSize = aVector.size();
        for (int i = 0; i < liSize; i++) {
            SequenceRegion sr = (SequenceRegion) aVector.get(i);
            System.out.print(";" + sr.getAccession() + ";" + sr.getQuerySequence() + ";" + sr.getNterminalResidueCount() + ";" + sr.getCterminalResidueCount() + ";" + sr.isFound() + ";");
            if (sr.isFound()) {
                System.out.println(sr.getNterminalAddition() + ";" + sr.getCterminalAddition() + ";" + sr.getRetrievedSequence());
            } else {
                System.out.println(";" + ";");
            }
        }
    }
}
