/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 28-jul-2003
 * Time: 14:59:35
 */
package com.compomics.mslims.util;

import com.compomics.util.general.CommandLineParser;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This class will read all unique accession numbers from a database
 * and retrieve all Netphos predictions for the whole protein sequences.
 * It will then read the Netphos output and attempt to correlate the Netphos
 * predictions with the found peptides.
 *
 * @author Lennart Martens
 */
public class GetPhosphoPredictions {

    public GetPhosphoPredictions() {

    }

    /**
     * This method is the entry point for the application.
     *
     * @param args  String[] with the start-up arguments.
     */
    public static void main(String[] args) {
        if(args == null || args.length == 0) {
            printUsage();
        }

        CommandLineParser clp = new CommandLineParser(args, new String[]{"", ""});

        // First we need to retrieve DB parameters.

        // Next up: select all unique accessions from the DB,
        // possibly including isoforms.

        // Now that we've got an accession number list,
        // retrieve all the sequences and write them to a temporary file.

        // Now we need to call netphos, with the temporary file as input.
        // The netphos output should be redirected to a file, for later parsing.
        // This automatically takes into account that we should wait for completion.

        // Parse the netphos output and correlate the predictions above a certain threshold
        // to the peptides in the DB.
    }

    /**
     * This method prints the usage for this class.
     */
    private static void printUsage() {
        System.err.println("");
        System.exit(1);
    }
}
