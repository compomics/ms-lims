/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.http.sequenceretrievers;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class NcbiSequenceRetriever {
	// Class specific log4j logger for NcbiSequenceRetriever instances.
	 private static Logger logger = Logger.getLogger(NcbiSequenceRetriever.class);
    /**
     * The protein sequence
     */
    private String iSequence = null;
    /**
     * The number of times the sequence retrieving was retried
     */
    private int iRetry = 0;

    /**
     * Constructor
     *
     * @param aIpiAccession Protein accession
     * @throws Exception
     */
    public NcbiSequenceRetriever(String aIpiAccession) throws Exception {
        iSequence = readSequenceUrl("http://www.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=protein&id=" + aIpiAccession  + "&rettype=fasta");
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
                if (lLines[j].startsWith("<")) {
                    lSequenceStarted = false;
                    j = lLines.length;
                } else {
                    //add the sequence
                    sequence = sequence + lLines[j].replace(" ", "");
                }
            } else if (lLines[j].startsWith(">")) {
                lSequenceStarted = true;
            }
        }

        if (sequence.length() == 0) {
            if(iRetry < 5){
                iRetry = iRetry + 1;
                sequence = readSequenceUrl(aUrl);
            } else {
                sequence = null;
            }

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
            NcbiSequenceRetriever retrieve = new NcbiSequenceRetriever("62473526");
            System.out.println(retrieve.getSequence());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
