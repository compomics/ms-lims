/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 13-jan-03
 * Time: 18:38:57
 */
package com.compomics.mslimscore.util.sequence;


import com.compomics.mslimscore.util.mascot.MascotSequenceRetriever;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/12/14 09:25:15 $
 */

/**
 * This class allows the user to obtain a sequence region from a protein, based on an accession number and a peptide of
 * that protein. E.G., when searching for patterns around identified sequences.
 *
 * @author Lennart Martens
 */
public class PeptideSequenceRegionRetriever {
    
    /**
     * The MascotSequenceRetriever that will return all FASTA sequences.
     */
    MascotSequenceRetriever msr = null;

    /**
     * This constructor allows the creation of a PeptideSequenceRegionRetriever through the specification of a Mascot
     * server hostname and an optional database name.
     *
     * @param aHostname String with the hostname for the Mascot server machine.
     * @param aDatabase String with the optional Mascot database name. If this is absent, it will be autodetected.
     */
    public PeptideSequenceRegionRetriever(String aHostname, String aDatabase) {
        msr = new MascotSequenceRetriever(aHostname, aDatabase);
    }

    /**
     * This method allows the caller to retrieve a single sequence region based on the SequenceRegion passed in.
     *
     * @param aRegion SequenceRegion to query with.
     * @return SequenceRegion    with the filled-in SequenceRegion.
     * @throws IOException when reading the database failed.
     */
    public SequenceRegion retrieveSequenceRegion(SequenceRegion aRegion) throws IOException {
        Vector v = new Vector(1);
        v.add(aRegion);
        v = this.retrieveSequenceRegions(v);
        SequenceRegion result = (SequenceRegion) v.get(0);
        return result;
    }

    /**
     * This method allows the caller to retrieve a set of sequence regions based on the SequenceRegions contained in the
     * Vector.
     *
     * @param aRegions Vector with the SequenceRegion instances to query with.
     * @return Vector    with the filled-in SequenceRegions in the Vector.
     * @throws IOException when reading the database failed.
     */
    public Vector retrieveSequenceRegions(Vector aRegions) throws IOException {
        int liSize = aRegions.size();
        HashMap all = new HashMap(liSize);
        for (int i = 0; i < liSize; i++) {
            Object o = aRegions.elementAt(i);
            if (o instanceof SequenceRegion) {
                SequenceRegion s = (SequenceRegion) o;
                s.setQueried(true);
                // Chances are a single accession number is present more than once in
                // the query. We should allow for this! So first check whether it is present
                // already.
                if (all.containsKey(s.getAccession())) {
                    // See if a subdivision has been made.
                    Object check = all.get(s.getAccession());
                    if (check instanceof Integer) {
                        // Already a subdivision in progress.
                        // Find out how deep.
                        int count = ((Integer) check).intValue();
                        // Add it to the end.
                        count++;
                        all.put(s.getAccession() + "\u00A7" + count, s);
                        // Update the counter.
                        all.put(s.getAccession(), new Integer(count));
                    } else {
                        // First one, split it out.
                        int count = 1;
                        // Original one gets index '1'.
                        all.put(s.getAccession() + "\u00A7" + count, check);
                        // Current one gets one more.
                        count++;
                        all.put(s.getAccession() + "\u00A7" + count, s);
                        // Store a counter as a flag.
                        all.put(s.getAccession(), new Integer(count));
                    }
                } else {
                    // Normal put here.
                    all.put(s.getAccession(), s);
                }

            }
        }
        // Get all results.
        Iterator iter = all.keySet().iterator();
        while (iter.hasNext()) {
            String accession = (String) iter.next();
            int start = accession.indexOf("\u00A7");
            if (start >= 0) {
                continue;
            }
            String sequence = null;
            try {
                sequence = msr.getSequence(accession);
                StringReader sr = new StringReader(sequence);
                BufferedReader br = new BufferedReader(sr);
                String line = null;
                StringBuffer sb = new StringBuffer();
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    // Skip all headers.
                    if (line.startsWith(">")) {
                        continue;
                    }
                    // Store sequence.
                    sb.append(line);
                }
                br.close();
                sr.close();
                sequence = sb.toString();
            } catch (IOException ioe) {
                System.err.println("\nAccession number '" + accession + "' did not yield any hits. It was skipped.");
                continue;
            }
            Object stored = all.get(accession);
            // The item can either be a SequenceRegion alone,
            // or a collection of SequenceRegions (different pieces of
            // sequences yet with the same accession number).
            if (stored instanceof SequenceRegion) {
                this.processRegionInProtein((SequenceRegion) stored, sequence);
            } else {
                // Find out how many similar ones we have.
                int count = ((Integer) stored).intValue();
                // Simply cycle them all.
                for (int i = 1; i <= count; i++) {
                    this.processRegionInProtein((SequenceRegion) all.get(accession + "\u00A7" + i), sequence);
                }
            }
        }

        return aRegions;
    }

    /**
     * This method does the real searching and retrieving per query sequence and protein sequence. The parameters are
     * changed due to a pass by reference... Don't mess with this unless you are very sure what you're doing. This is
     * NOT a very good piece of Java code!
     *
     * @param aRegion   SequenceRegion to update after being queried.
     * @param aSequence String with the sequence we want to query.
     */
    private void processRegionInProtein(SequenceRegion aRegion, String aSequence) {
        int location = aSequence.indexOf(aRegion.getQuerySequence());
        if (location < 0) {
            aRegion.setFound(false);
        } else {
            aRegion.setFound(true);
            // N-terminal fragment.
            int startLoc = location - aRegion.getNterminalResidueCount();
            if (startLoc < 0) {
                startLoc = 0;
            }
            String ntermAdd = aSequence.substring(startLoc, location).trim();
            if (ntermAdd.startsWith("\n")) {
                ntermAdd = ntermAdd.substring(1);
            }
            aRegion.setNterminalAddition(ntermAdd);
            // C-terminal fragment.
            startLoc = location + aRegion.getQuerySequence().length();
            int endLoc = startLoc + aRegion.getCterminalResidueCount();
            if (endLoc > aSequence.length()) {
                endLoc = aSequence.length();
            }
            String ctermAdd = aSequence.substring(startLoc, endLoc).trim();
            if (ctermAdd.endsWith("\n")) {
                ctermAdd = ctermAdd.substring(0, ctermAdd.length() - 1);
            }
            aRegion.setCterminalAddition(ctermAdd);
        }
    }
}
