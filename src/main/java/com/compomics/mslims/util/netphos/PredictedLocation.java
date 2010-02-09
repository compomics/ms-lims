/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 20-jul-2003
 * Time: 16:29:45
 */
package com.compomics.mslims.util.netphos;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This class holds the netphos prediction for a single phosphorylation.
 *
 * @author Lennart Martens
 */
public class PredictedLocation {

    /**
     * The location within the parent sequence for the phosphorylation.
     */
    private int iLocation = 0;
    /**
     * The context of the predicted phosphorylation.
     */
    private String iContext = null;
    /**
     * The residue that could be phosphorylated (typically 'S', 'T' or 'Y').
     */
    private String iResidue = null;
    /**
     * The Netphos score for this phosphorylation.
     */
    private double iScore = 0d;

    /**
     * This construct takes all relevant information for a phosphorylation predicted
     * as reported by Netphos.
     *
     * @param aLocation int with the residue location of the phosphorylation
     * @param aContext  String with the context of the phosphorylation.
     * @param aResidue  String with the phosphorylated residue (typically 'S', 'T' or 'Y')
     * @param aScore    double with the netphos score for the prediction
     */
    public PredictedLocation(int aLocation, String aContext, String aResidue, double aScore) {
        this.iLocation = aLocation;
        this.iContext = aContext;
        this.iResidue = aResidue;
        this.iScore = aScore;
    }
    /**
     * Reports on the location of the phosphorylation.
     *
     * @return  int with the location of the phosphorylation.
     */
    public int getLocation() {
        return iLocation;
    }
    /**
     * This method sets the location of the phosphorylation.
     *
     * @param aLocation int with the phosphorylaed location.
     */
    public void setLocation(int aLocation) {
        iLocation = aLocation;
    }
    /**
     * This method gets the residue that is phosphorylated.
     *
     * @return  String with the phosphorylated residue.
     */
    public String getResidue() {
        return iResidue;
    }
    /**
     * this method sets the residue that is phosphorylated.
     *
     * @param aResidue  String with the residue that is phosphorylated.
     */
    public void setResidue(String aResidue) {
        iResidue = aResidue;
    }
    /**
     * This method sets the context of the phosphorylation.
     *
     * @param aContext  String with the context for the phosphorylation.
     */
    public void setContext(String aContext) {
        this.iContext = aContext;
    }
    /**
     * This method returns the context for the phosphorylation.
     */
    public String getContext() {
        return this.iContext;
    }
    /**
     * This method returns the predicted phosphorylation site's Netphos score.
     *
     * @return  doule with the Netphos score.
     */
    public double getScore() {
        return iScore;
    }
    /**
     * This method sets the Netphos score.
     *
     * @param aScore    double with the Netphos score.
     */
    public void setScore(double aScore) {
        iScore = aScore;
    }
    /**
     * This method returns a clone of this Object.
     *
     * @return  Object with a clone of this object.
     */
    public Object clone() {
        PredictedLocation pl = new PredictedLocation(this.iLocation, this.iContext, this.iResidue, this.iScore);
        return pl;
    }
    /**
     * This method returns a String representation of this object.
     *
     * @return  String with a String representation of this object.
     */
    public String toString() {
        return iResidue + " at " + iLocation + " in " + iContext + " with a score of " + iScore + "\n";
    }

    /**
     * This method test whether the Object specified is the same
     * predictedlocation as this one.
     *
     * @return boolean to indicate equality in the predicted location.
     */
    public boolean equals(Object o) {
        PredictedLocation pl = (PredictedLocation)o;
        boolean same = false;
        if(this.iContext.equals(pl.iContext) && this.iLocation == pl.iLocation && this.iResidue.equals(pl.iResidue) && this.iScore == pl.iScore) {
            same = true;
        }
        return same;
    }
}
