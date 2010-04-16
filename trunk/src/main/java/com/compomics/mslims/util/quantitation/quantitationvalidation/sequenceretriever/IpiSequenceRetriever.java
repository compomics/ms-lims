package com.compomics.mslims.util.quantitation.quantitationvalidation.sequenceretriever;

import org.apache.log4j.Logger;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedInputStream;
import java.io.Reader;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA. User: niklaas Date: 26-jan-2009 Time: 14:45:15 To change this template use File | Settings
 * | File Templates.
 */
public class IpiSequenceRetriever {
    // Class specific log4j logger for IpiSequenceRetriever instances.
    private static Logger logger = Logger.getLogger(IpiSequenceRetriever.class);

    /**
     * The protein sequence
     */
    private String iSequence = null;

    /**
     * Constructor
     *
     * @param aIpiAccession Protein accession
     * @throws Exception
     */
    public IpiSequenceRetriever(String aIpiAccession) throws Exception {
        //remove the dot
        if (aIpiAccession.indexOf(".") > 0) {
            aIpiAccession = aIpiAccession.substring(0, aIpiAccession.indexOf("."));
        }
        iSequence = readSequenceUrl("http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-e+[IPI-acc:" + aIpiAccession + "]+-vn+2");
    }

    /**
     * This method reads a url a tries to extrect the protein sequence
     *
     * @param aUrl String with the url
     * @return String with the protein sequence
     * @throws Exception
     */
    public String readSequenceUrl(String aUrl) throws Exception {
        String sequence = "";

        URL myURL = new URL(aUrl);
        StringBuilder input = new StringBuilder();
        HttpURLConnection c = (HttpURLConnection) myURL.openConnection();
        BufferedInputStream in = new BufferedInputStream(c.getInputStream());
        Reader r = new InputStreamReader(in);

        int i;
        while ((i = r.read()) != -1) {
            input.append((char) i);
        }

        String inputString = input.toString();

        String[] lLines = inputString.split("\n");
        boolean lSequenceStarted = false;
        for (int j = 0; j < lLines.length; j++) {
            if (lSequenceStarted) {
                if (lLines[j].startsWith("//")) {
                    lSequenceStarted = false;
                } else {
                    //add the sequence
                    sequence = sequence + lLines[j].replace(" ", "");
                }
            } else if (lLines[j].startsWith("SQ")) {
                lSequenceStarted = true;
            }
        }

        if (sequence.length() == 0) {
            sequence = null;
        }
        return sequence;
    }

    /**
     * Getter for the protein sequence
     *
     * @return String with protein sequence
     */
    public String getSequence() {
        return iSequence;
    }

    public static void main(String[] args) {
        try {
            IpiSequenceRetriever retrieve = new IpiSequenceRetriever("IPI00015174.1");
            logger.info(retrieve.getSequence());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}

