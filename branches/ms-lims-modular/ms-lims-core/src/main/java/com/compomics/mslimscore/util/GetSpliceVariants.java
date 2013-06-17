/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 19-mei-2004
 * Time: 16:16:10
 */
package com.compomics.mslimscore.util;

import org.apache.log4j.Logger;

import com.compomics.util.general.CommandLineParser;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This class allows the caller to get all SwissProt splice variants for a given accession number.
 *
 * @author Lennart Martens
 * @version $Id: GetSpliceVariants.java,v 1.3 2004/07/08 13:14:19 lennart Exp $
 */
public class GetSpliceVariants {
    // Class specific log4j logger for GetSpliceVariants instances.
    private static Logger logger = Logger.getLogger(GetSpliceVariants.class);

    private static final String iUrl = "http://www.expasy.org/cgi-bin/get-all-varsplic.pl?";

    /**
     * This method finds the splice variants for the accession number specified and returns these in FASTA format.
     *
     * @param aAccession       String with the accession number to search for.
     * @param aIncludeOriginal boolean to indicate whether the original sequence (as it is present in SwissProt) needs
     *                         to be included as well.
     * @return String with the FASTA formatted sequences retrieved (or 'empty String' if none)
     * @throws IOException when the retrieval failed.
     */
    public String getSpliceVariants(String aAccession, boolean aIncludeOriginal) throws IOException {
        StringBuffer webOut = new StringBuffer();
        // Get the output from the Expasy site.
        String interMed = this.getWebResults(aAccession);
        // Process the output.
        String result = this.processWebOutput(interMed, aIncludeOriginal);

        // The final result; either 'null' (if nothing was found), or the String returned.
        return result;
    }

    public static void main(String[] args) {
        // Argument verification and validation...
        if (args == null || args.length == 0) {
            printUsage();
        }
        CommandLineParser clp = new CommandLineParser(args);
        String[] params = clp.getParameters();
        if (params == null || params.length != 1) {
            logger.error("\n\nInvalid number of parameters! I expect only one!");
            printUsage();
        }
        File input = new File(params[0]);
        if (!input.exists()) {
            logger.error("\n\nUnable to locate input file '" + params[0] + "'!\n\n");
            System.exit(1);
        }
        // See if the flag is set to include originals.
        boolean includeOriginal = false;
        if (clp.hasFlag("i")) {
            includeOriginal = true;
        }
        // Read the input file.
        ArrayList accessions = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(input));
            String line = null;
            while ((line = br.readLine()) != null) {
                accessions.add(line.trim());
            }
            br.close();
        } catch (IOException ioe) {
            logger.error("\n\nUnable to read input file '" + params[0] + "'!\n" + ioe.getMessage() + "\n\n");
            System.exit(1);
        }
        // Create the splice variant retriever.
        GetSpliceVariants gsv = new GetSpliceVariants();
        // Okay, cycle all accessions.
        Iterator iter = accessions.iterator();
        while (iter.hasNext()) {
            String accession = (String) iter.next();
            try {
                String result = gsv.getSpliceVariants(accession, includeOriginal);
                if (result != null && !result.equals("")) {
                    logger.info(result);
                } else {
                    logger.error("No results for accession '" + accession + "'!");
                }
            } catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
            }
        }
    }

    /**
     * This method prints the usage for this class and exits with the error flag raised.
     */
    private static void printUsage() {
        logger.error("\n\nUsage:\n\tGetSpliceVariants [-i] <input_file>");
        logger.error("\nFlags:\n\t- i: (optional) include original sequence in output\n\n");
        System.exit(1);
    }

    /**
     * This method goes to the website indicated by the 'iUrl' variable, fetches the results and puts them in the 'aOut'
     * StringBuffer.
     *
     * @param aAccession String with the accession number for which to find the isoforms.
     * @return String  in which the web results page will be stored.
     * @throws IOException when the web results could not be fetched.
     */
    private String getWebResults(String aAccession) throws IOException {
        // Try fetching results three times.
        StringBuffer lBuf = null;
        int count = 0;
        boolean lbContinue = true;
        while (lbContinue) {
            lBuf = new StringBuffer();
            try {
                URL lUrl = new URL(iUrl + aAccession);
                URLConnection uc = lUrl.openConnection();
                InputStream is = uc.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = br.readLine()) != null) {
                    lBuf.append(line + "\n");
                }
                br.close();
                is.close();
                lbContinue = false;
            } catch (IOException ioe) {
                count++;
                if (count < 3) {
                    logger.error("IOException retrieving accession '" + aAccession + "'... Retrying " + (2 - count) + " more times...");
                } else {
                    throw ioe;
                }
            }
        }

        return lBuf.toString();
    }

    /**
     * This method processes the web output from the SwissProt varsplic script for the FASTA sequences of the variably
     * spliced proteins.
     *
     * @param aWebOut          String with the output from the web interface.
     * @param aIncludeOriginal boolean to indicate whether the original sequence should be included as well.
     * @return String with the FASTA formatted output, or empty String if there was none.
     */
    private String processWebOutput(String aWebOut, boolean aIncludeOriginal) {
        StringBuffer result = new StringBuffer();

        int start = aWebOut.indexOf("'>>");
        int count = 0;
        while (start >= 0) {
            int end = aWebOut.indexOf("<", start + 3);
            if (end > 0) {
                if (count == 0 && !aIncludeOriginal) {
                } else {
                    result.append(aWebOut.substring(start + 2, end).trim() + "\n");
                }
                count++;
            }
            start = aWebOut.indexOf("'>>", start + 3);
        }

        return result.toString();
    }
}
