/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 20-jul-2003
 * Time: 16:23:47
 */
package com.compomics.mslimscore.util.netphos;

import org.apache.log4j.Logger;

import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This class wraps a netphos prediction for a given protein.
 *
 * @author Lennart Martes
 */
public class NetphosPrediction {
    // Class specific log4j logger for NetphosPrediction instances.
    private static Logger logger = Logger.getLogger(NetphosPrediction.class);

    /**
     * The accession number of the protein.
     */
    private String iAccession = null;

    /**
     * The Vector with all the PredictedLocations.
     */
    private Vector iPhosphorylations = new Vector();

    /**
     * Constructor that takes the accession String for the protein.
     *
     * @param aAccession String with the accession String for the protein.
     */
    public NetphosPrediction(String aAccession) {
        if (aAccession.startsWith("sp_")) {
            iAccession = aAccession.substring(aAccession.indexOf("_") + 1, aAccession.lastIndexOf("_"));
        } else {
            iAccession = aAccession;
        }
    }

    /**
     * This method adds a new predicted phosphorylation location to the protein.
     *
     * @param aLocation PredcitedLocation with the location, residue and score information for the predicted location.
     */
    public void addPrediction(PredictedLocation aLocation) {
        iPhosphorylations.add(aLocation);
    }

    /**
     * This method adds a new predicted phosphorylation location to the protein.
     *
     * @param aLocation int with the residue location of the phosphorylation
     * @param aContext  String with the context of the phosphorylation.
     * @param aResidue  String with the phosphorylated residue (typically 'S', 'T' or 'Y')
     * @param aScore    double with the netphos score for the prediction
     */
    public void addPrediction(int aLocation, String aContext, String aResidue, double aScore) {
        iPhosphorylations.add(new PredictedLocation(aLocation, aContext, aResidue, aScore));
    }

    /**
     * This method returns a clone (aka deep copy) of this object.
     *
     * @return Object with a clone of this object.
     */
    public Object clone() {
        NetphosPrediction clone = new NetphosPrediction(this.iAccession);
        clone.iPhosphorylations = (Vector) this.iPhosphorylations.clone();

        return clone;
    }

    /**
     * This method reports on the accession number of the protein.
     *
     * @return String  with the accession number.
     */
    public String getAccession() {
        return this.iAccession;
    }

    /**
     * This method allows the caller to specify a new accession number for the protein.
     *
     * @param aAccession String  with the new accession number.
     */
    public void setAccession(String aAccession) {
        this.iAccession = aAccession;
    }

    /**
     * This method returns a String representation of this object.
     *
     * @param aThreshold double with the threshold required for printing.
     * @return String with a String representation of this object.
     */
    public String toString(double aThreshold) {
        StringBuffer temp = new StringBuffer();
        temp.append("Netphos predictions for protein '" + iAccession + "'" + ((aThreshold > 0) ? " (threshold score is " + aThreshold + ")" : "") + ":\n");
        int liSize = this.iPhosphorylations.size();
        for (int i = 0; i < liSize; i++) {
            PredictedLocation pl = (PredictedLocation) this.iPhosphorylations.get(i);
            if (pl.getScore() >= aThreshold) {
                temp.append("  " + pl.toString());
            }
        }
        return temp.toString();
    }

    /**
     * This method returns a String representation of this object.
     *
     * @return String with a String representation of this object.
     */
    public String toString() {
        return this.toString(0.0);
    }

    /**
     * This method returns all the predicted phosphorylation locations for this protein.
     *
     * @return Vector  with all the PredictedLocations for this protein.
     */
    public Vector getLocations() {
        return this.getLocations(0.0);
    }

    /**
     * This method returns all the predicted phosphorylation locations for this protein that score higher than the
     * provided threshold.
     *
     * @param aThreshold double with threshold score.
     * @return Vector  with all the PredictedLocations that have a score higher than aThreshold.
     */
    public Vector getLocations(double aThreshold) {
        Vector result = null;

        // First see if checking the threshold makes sense.
        if (aThreshold == 0) {
            result = this.iPhosphorylations;
        } else {
            // Okay, we need to check each score against the threshold.
            result = new Vector();
            int liSize = this.iPhosphorylations.size();
            for (int i = 0; i < liSize; i++) {
                PredictedLocation pl = (PredictedLocation) this.iPhosphorylations.elementAt(i);
                if (pl.getScore() >= aThreshold) {
                    result.add(pl);
                }
            }
        }
        // Voila.
        return result;
    }
}
