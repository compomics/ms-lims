/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 15-mrt-03
 * Time: 11:34:28
 */
package com.compomics.mslims.util.mascot;

/*
 * CVS information:
 *
 * $Revision: 1.6 $
 * $Date: 2007/05/01 13:30:44 $
 */

/**
 * This class implements a single header for a Mascot identification.
 *
 * @author Lennart Martens
 */
public class MascotHeader implements Comparable {

    private String iAccession = null;
    private String iDescription = null;
    private int iStart = -1;
    private int iEnd = -1;
    private String iIsoformAccesions = null;

    /**
     * This constructor attempts to find a start and end location in the accession,
     * and will find these if they are formatted a ' (x-y)' in the end of the accession
     * number. If none are found, they are not set.
     *
     * @param   aAccession  String with the accession number, may contain coded start and end
     *                      location: ' (x-y)' at the end.
     * @param   aDescription    String with the description for the protein.
     */
    public MascotHeader(String aAccession, String aDescription) {
        this.iDescription = aDescription;

        // Try to parse the accession number for start and end locations.
        String test = aAccession;
        int open = aAccession.lastIndexOf(" (") + 1;
        int close = aAccession.lastIndexOf(")");
        int hyphen = aAccession.lastIndexOf("-");
        if((open >= 0) && (close >= 0) && (open < hyphen) && (hyphen < close)) {
            String start = aAccession.substring(open+1, hyphen).trim();
            String end = aAccession.substring(hyphen+1, close).trim();
            try {
                int startNr = Integer.parseInt(start);
                int endNr = Integer.parseInt(end);
                this.iStart = startNr;
                this.iEnd = endNr;
                // Open-1 because there is a space at -1.
                test = test.substring(0, open-1);
            } catch(Exception e) {
                // Just let it go, probably not a start-end location after all.
            }
        }

        this.iAccession = test;
    }

    /**
     * This constructor takes the formative elements of the header separately and it takes
     * these 'as is'. No processing is performed, so location information is considered
     * removed from the accession number.
     *
     * @param   aAccession  String with the accession number.
     * @param   aDescription    String with the description.
     * @param   aStart  int with the start index.
     * @param   aEnd    int with the end index.
     */
    public MascotHeader(String aAccession, String aDescription, int aStart, int aEnd) {
        this.iAccession = aAccession;
        this.iDescription = aDescription;
        this.iStart = aStart;
        this.iEnd = aEnd;
    }

    /**
     * This method checks the incoming start and end indices and updates
     * the current ones if applicable.
     *
     * @param aStart    int with the start position of the peptide in the parent protein.
     * @param aEnd  int with the end position of the peptide in the parent protein.
     */
    public void updateLocation(int aStart, int aEnd) {
        // Two checks, three avenues.
        //  a. If the current start and stop are both '-1', simply update.
        if(iStart < 0 && iEnd < 0) {
            iStart = aStart;
            iEnd = aEnd;
        }
        //  b. See if the start AND stop are both above 0.
        else if(aStart > 0 && aEnd > 0) {
            //  1. New delta (end-start) is smaller than the current one.
            //     In this case: replace.
            if((aEnd-aStart) < (iEnd-iStart)) {
               iStart = aStart;
               iEnd = aEnd;
            }
            //  2. Current start and end are both larger than the new start and end
            //     positions.
            //     In this case: replace (first occurrence has precedence).
            else if(aStart < iStart && aEnd < iEnd) {
                iStart = aStart;
                iEnd = aEnd;
            }
        }
        //  3. In all other cases, do nothing.
    }

    /**
     * This method returns only the accession number.
     * Localization information is never added here.
     *
     * @return  String  with only the accession number (no localization).
     */
    public String getAccession() {
        return this.iAccession;
    }

    /**
     * This method returns the accession number, completed with
     * ' (start-end)' if these are present. If no localization is
     * present, the result will be the same as calling 'getAccession()'.
     *
     * @return  String  with the accession number, appended with ' (start-end)'
     *                  if localization information is present.
     */
    public String getCompoundAccession() {
        String result = this.getAccession();
        if((iStart > 0) && (iEnd > 0)) {
            result += " (" + iStart + "-" + iEnd + ")";
        }

        return result;
    }

    /**
     * This method reports on the description for this protein.
     *
     * @return  String  with the description for this protein.
     */
    public String getDescription() {
        return iDescription;
    }

    /**
     * Simple setter for the description.
     *
     * @param   aDescription    String with the new description.
     */
    public void setDescription(String aDescription) {
        this.iDescription = aDescription;
    }

    /**
     * This method reports on the start index of the sequence.
     *
     * @return  int with the start index, -1 if not known.
     */
    public int getStart() {
        return iStart;
    }

    /**
     * This method reports on the end index of the sequence.
     *
     * @return  int with the end index, -1 if not known.
     */
    public int getEnd() {
        return iEnd;
    }


    public String getIsoformAccessions() {
        return iIsoformAccesions;
    }

    public void setIsoformAccessions(String aIosformAccesions) {
        iIsoformAccesions = aIosformAccesions;
    }

    /**
     * This method gives headers a score. Currently only works for IPI accession numbers.
     *
     * @return  int with the score.
     */
    public int getScore() {
        int score = 0;
        if(this.iAccession.toUpperCase().startsWith("IPI") && this.iDescription != null) {
            // IPI header. These we can score in detail.
            String temp = this.iDescription.toUpperCase();
            if(temp.indexOf("SWISS-PROT") >= 0) {
                score = 2;
            } else if(temp.indexOf("TREMBL") >= 0 || temp.indexOf("REFSEQ_NP") >= 0) {
                score = 1;
            }
        }
        return score;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     *
     * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)<p>
     *
     * The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.<p>
     *
     * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.<p>
     *
     * It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this Object.
     */
    public int compareTo(Object o) {
        MascotHeader mh = (MascotHeader)o;
        String myAccession = this.getAccession();
        String otherAccession = mh.getAccession();

        int compare = 0;
        // Accession starting with numbers should go before letters.
        if(Character.isDigit(myAccession.charAt(0)) && !Character.isDigit(otherAccession.charAt(0))) {
            compare = -1;
        } else if(!Character.isDigit(myAccession.charAt(0)) && Character.isDigit(otherAccession.charAt(0))) {
            compare = 1;
        } else {
            compare = myAccession.compareTo(otherAccession);
        }
        if(compare == 0) {
            int myStart = this.getStart();
            int otherStart = mh.getStart();

            if(myStart >= 0 && otherStart >= 0) {
                compare = myStart - otherStart;
            } else if(myStart >= 0 && otherStart < 0) {
                compare = -1;
            } else if(myStart < 0 && otherStart >= 0) {
                compare = 1;
            }
        }

        return compare;
    }
}
