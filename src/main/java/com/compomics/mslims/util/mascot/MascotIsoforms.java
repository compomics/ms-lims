/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 15-mrt-03
 * Time: 11:27:42
 */
package com.compomics.mslims.util.mascot;

import java.util.*;

/*
 * CVS information:
 *
 * $Revision: 1.7 $
 * $Date: 2007/05/01 13:30:44 $
 */

/**
 * This class is used to store the headers of isoforms from a Mascot results file
 * using MascotID.
 *
 * @author Lennart Martens
 */
public class MascotIsoforms {

    /**
     * This HashMap stores the isoform information.
     */
    private HashMap iIsoforms = null;

    /**
     * Default constructor.
     */
    public MascotIsoforms() {
        iIsoforms = new HashMap();
    }

    /**
     * This method stores an isoform in the collection if it is new
     * (based on accession), or will update start and end if already present.
     *
     * @param   aAccession  String with the accession number (may contain location
     *                      information: ' (x-y)' at the end).
     * @param   aDescription    String with the description.
     */
    public void addIsoform(String aAccession, String aDescription) {
        MascotHeader mh = new MascotHeader(aAccession, aDescription);
        String key = mh.getAccession();
        Object tempValue = null;
        if((tempValue = iIsoforms.get(key)) != null) {
            MascotHeader value = (MascotHeader)tempValue;
            value.updateLocation(mh.getStart(), mh.getEnd());
        } else {
            iIsoforms.put(key, mh);
        }
    }

    /**
     * This method stores an isoform in the collection if it is new
     * (based on accession), or will update start and end if already present.
     *
     * @param   aAccession  String with the accession number (may contain location
     *                      information: ' (x-y)' at the end).
     * @param   aDescription    String with the description.
     * @param   aStart  int with the start index.
     * @param   aEnd    int with the end index.
     */
    public void addIsoform(String aAccession, String aDescription, int aStart, int aEnd) {
        MascotHeader mh = new MascotHeader(aAccession, aDescription, aStart, aEnd);
        String key = mh.getAccession();
        Object tempValue = null;
        if((tempValue = iIsoforms.get(key)) != null) {
            MascotHeader value = (MascotHeader)tempValue;
            value.updateLocation(mh.getStart(), mh.getEnd());
        } else {
            iIsoforms.put(key, mh);
        }
    }

    /**
     * This method simply returns all known isoforms for this identification.
     *
     * @return  HashMap with all the known isoforms.
     */
    public HashMap getIsoforms() {
        return this.iIsoforms;
    }

    /**
     * This method will return a MascotHeader with the accession that was found first from the
     * HashMap (if available) and the first (natural ordering courtesy of MascotHeader class)
     * otherwise. If the specified accession numbers are present, but they score less than another
     * accession number present, the highest scoring one is returned.
     *
     * @return  MascotHeader    with the main header for this identification.
     */
    public MascotHeader getMainHeader(String[] aAllMainAccessions) {
        MascotHeader result = null;

        // Start off by sorting the headers alphabetically.
        Iterator iter = iIsoforms.values().iterator();
        ArrayList headers = new ArrayList(iIsoforms.size());
        while (iter.hasNext()) {
            headers.add(iter.next());
        }
        Collections.sort(headers);

        // Now find the highest scoring header (starting from the first entry in the
        // sorted collection.
        iter = headers.iterator();
        // Fencepost.
        result = (MascotHeader)iter.next();
        int maxScore = result.getScore();
        while(iter.hasNext()) {
            MascotHeader candidate = (MascotHeader)iter.next();
            // If this header has a higher score than the previous one,
            // change it.
            if(candidate.getScore() > maxScore) {
                result = candidate;
                maxScore = result.getScore();
            }
        }

        // Now see if we have a preferred hit among this collection.
        if(aAllMainAccessions != null) {
            // First see if any of these headers are known.
            for(int i = 0; i < aAllMainAccessions.length; i++) {
                String lAccession = aAllMainAccessions[i];
                Object tempObj = null;
                if((tempObj = iIsoforms.get(lAccession)) != null) {
                    MascotHeader newCandidate = (MascotHeader)tempObj;
                    if(newCandidate.getScore() >= maxScore) {
                        result = newCandidate;
                        maxScore = result.getScore();
                    }
                }
            }
        }

        // Finally, collect all other headers and
        // append them.
        StringBuffer isoforms = new StringBuffer("");
        String accession = result.getAccession();
        iter = new TreeSet(this.iIsoforms.keySet()).iterator();
        while(iter.hasNext()) {
            String s = (String)iter.next();
            if(!s.equals(accession)) {
                isoforms.append(((MascotHeader)iIsoforms.get(s)).getCompoundAccession());
                isoforms.append("^A");
            }
        }
        if ((isoforms.length() >= 2) && (isoforms.charAt(isoforms.length()-2) == '^')) {
            isoforms.delete(isoforms.length()-2, isoforms.length());
        }
        result.setIsoformAccessions(isoforms.toString());

        return result;
    }

    /**
     * This method returns the MascotHeader for the specified accession number,
     * or 'null' if no match found.
     *
     * @param   aAccession  String with the accession number to retrieve.
     * @return  MascotHeader    with the mascot header for the specified accession
     *                          number, or 'null' if no match.
     */
    public MascotHeader getHeader(String aAccession) {
        MascotHeader mh = null;
        Object loTemp = this.iIsoforms.get(aAccession);
        if(loTemp != null) {
            mh = (MascotHeader)loTemp;
        }
        return mh;
    }

    /**
     * This method returns all known accession numbers.
     *
     * @return  String[]    with all the accession numbers.
     */
    public String[] getAccessionNumbers() {
        String[] result = new String[iIsoforms.size()];
        Iterator iter = iIsoforms.keySet().iterator();
        int counter = 0;
        while(iter.hasNext()) {
            String s = (String)iter.next();
            result[counter] = s;
            counter++;
        }
        return result;
    }
}
