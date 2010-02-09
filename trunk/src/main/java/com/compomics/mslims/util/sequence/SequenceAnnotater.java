/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 30-jun-2004
 * Time: 16:33:52
 */
package com.compomics.mslims.util.sequence;

import com.compomics.util.general.MassCalc;
import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.interfaces.SpectrumFile;
import com.compomics.util.protein.AASequenceImpl;

import java.awt.*;
import java.util.*;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2007/10/22 10:31:17 $
 */

/**
 * This class has methods to annotate a given sequence.
 * 
 * @author Lennart Martens
 * @version $Id: SequenceAnnotater.java,v 1.3 2007/10/22 10:31:17 lennart Exp $
 */
public class SequenceAnnotater {

    /**
     * This variable will hold the sequence and its modificzations.
     */
    private AASequenceImpl iSequence = null;

    /**
     * This variable holds the allowed error margin on the fragment ion peaks.
     */
    private double iErrorMargin = 0.0;

    /**
     * This mass calculator takes care of calculating the basic fragment
     * and parent ion masses.
     */
    private MassCalc iMassCalc = null;

    /**
     * This double[] contains the y-ions.
     */
    private double[] iYions = null;

    /**
     * This double[] contains the b-ions.
     */
    private double[] iBions = null;

    /**
     * This constructor takes the modified sequence for the peptide to annotate on
     * a spectrum.
     *
     * @param aModifiedSequence String with the modified sequence (eg. 'Ace-YSFVM&lt;Ox&gt;TAER-COOH').
     * @param aErrorMargin  double with the errormargin for the ion peaks.
     * @param aMassCalc MassCalc instance to use in calculating the mass.
     */
    public SequenceAnnotater(String aModifiedSequence, double aErrorMargin, MassCalc aMassCalc) {
        this.iMassCalc = aMassCalc;
        this.iErrorMargin = aErrorMargin;
        this.digestSequence(aModifiedSequence);
        this.calculateIonSeries();
    }

    /**
     * This method will report on all y-ions for this annotator.
     *
     * @return  double[] with the y-ions, ordered from low to high mass.
     */
    public double[] getYIons() {
        return this.iYions;
    }

    /**
     * This method will report on all b-ions for the given sequence.
     *
     * @return  double[] with the b-ions, ordered from low to high mass.
     */
    public double[] getBIons() {
        return this.iBions;
    }

    /**
     * This method will report on all a-ions for the given sequence.
     *
     * @return  double[] with the a-ions, ordered from low to high mass.
     */
    public double[] getAIons() {
        double[] aIons = new double[iBions.length];
        for (int i = 0; i < aIons.length; i++) {
            // a-ion is b-ion minus 'CO'
            aIons[i] = iBions[i] - 27.994915;
        }
        return aIons;
    }

    /**
     * This method will report on all c-ions for the given sequence.
     *
     * @return  double[] with the c-ions, ordered from low to high mass.
     */
    public double[] getCIons() {
        double[] cIons = new double[iBions.length];
        for (int i = 0; i < cIons.length; i++) {
            // c-ion is b-ion plus 'NH3'
            cIons[i] = iBions[i] + 17.026549;
        }
        return cIons;
    }

    /**
     * This method will report on all b-18 ions for the given sequence.
     *
     * @return  double[] with the b-18 ions, ordered from low to high mass.
     */
    public double[] getB18Ions() {
        double[] bIons = new double[iBions.length];
        for (int i = 0; i < bIons.length; i++) {
            // y-ion minus 'H2O'
            bIons[i] = iBions[i] - 18.010565;
        }
        return bIons;
    }

    /**
     * This method will report on all z-ions for the given sequence.
     *
     * @return  double[] with the z-ions, ordered from low to high mass.
     */
    public double[] getZIons() {
        double[] zIons = new double[iYions.length];
        for (int i = 0; i < zIons.length; i++) {
            // z-ion is y-ion minus 'NH3'
            zIons[i] = iYions[i] - 17.026549;
        }
        return zIons;
    }

    /**
     * This method will report on all y-18 ions for the given sequence.
     *
     * @return  double[] with the y-18 ions, ordered from low to high mass.
     */
    public double[] getY18Ions() {
        double[] yIons = new double[iYions.length];
        for (int i = 0; i < yIons.length; i++) {
            // y-ion minus 'H2O'
            yIons[i] = iYions[i] - 18.010565;
        }
        return yIons;
    }

    /**
     * This method will report on all x-ions for the given sequence.
     *
     * @return  double[] with the x-ions, ordered from low to high mass.
     */
    public double[] getXIons() {
        double[] xIons = new double[iYions.length];
        for (int i = 0; i < xIons.length; i++) {
            // x-ion is y-ion plus 'CO', minus 'H2'.
            xIons[i] = iYions[i] + 25.979265;
        }
        return xIons;
    }

    /**
     * This method returns the parent mass.
     */
    public double getParent() {
        return this.iSequence.getMass();
    }

    /**
     * This method returns the bare sequence (no annotations) associated with this
     * annotator.
     *
     * @return  String with the sequence.
     */
    public String getSequence() {
        return this.iSequence.getSequence();
    }

    /**
     * This method returns a Vector with SpectrumAnnotation instances describing
     * all information that can be annotated on a spectrum graph.
     *
     * @return  Vector  with SpectrumAnnotation instances.
     */
    public Vector getSpectrumAnnotations() {
        Vector annotations = new Vector(50, 25);
        // The y ions.
        double[] ions = this.getYIons();
        double[] ions18 = this.getY18Ions();
        for (int i = 0; i < ions.length; i++) {
            annotations.add(new DefaultSpectrumAnnotation(ions[i], iErrorMargin, Color.blue, "y["+(i+1)+"]"));
            annotations.add(new DefaultSpectrumAnnotation(ions18[i], iErrorMargin, Color.blue, "y["+(i+1)) + "]-18");
        }
        // The b-ions.
        ions = this.getBIons();
        ions18 = this.getB18Ions();
        for (int i = 0; i < ions.length; i++) {
            annotations.add(new DefaultSpectrumAnnotation(ions[i], iErrorMargin, Color.black, "b["+(i+1)+"]"));
            annotations.add(new DefaultSpectrumAnnotation(ions18[i], iErrorMargin, Color.black, "b["+(i+1)+"]-18"));
        }
        // Three chareg states of the parent.
        annotations.add(new DefaultSpectrumAnnotation(getParent()+1.007825, iErrorMargin, Color.red, "Parent(+)"));
        annotations.add(new DefaultSpectrumAnnotation((getParent()+2.01565)/2, iErrorMargin, Color.red, "Parent(++)"));
        annotations.add(new DefaultSpectrumAnnotation((getParent()+3.023475)/3, iErrorMargin, Color.red, "Parent(+++)"));
        return annotations;
    }

    /**
     * This method compares all the masses of the precalculated, known ions to the ions that are stored in the
     * specified file. Whenever a hit is found within the specified mass delta, the correct mass will become the key
     * and the mass error the value in the HashMap.
     *
     * @param aFile SpectrumFile with the spectrum.
     * @return  HashMap with theoretical masses as keys (Double) and the measured deltas as values (Double as well).
     */
    public HashMap getMassDeltas(SpectrumFile aFile) {
        HashMap deltas = new HashMap();

        HashMap spectrum = aFile.getPeaks();

        // A TreeSet is ordered.
        TreeSet peaks = new TreeSet(spectrum.keySet());

        // First the y-ions.
        for (int i = 0; i < iYions.length; i++) {
            checkMassInSpectrum(iYions[i], peaks, spectrum, deltas);
        }
        // Now the b-ions.
        for (int i = 0; i < iBions.length; i++) {
            checkMassInSpectrum(iBions[i], peaks, spectrum, deltas);

        }

        return deltas;
    }



    public static void main(String[] args) {
        SequenceAnnotater sa = new SequenceAnnotater("NH2-M<Mox>ARTENS-COOH", 0.3, new MassCalc(MassCalc.MONOAA));
        String seq = sa.getSequence();
        int length = seq.length();
        double[] tempY = sa.getYIons();
        double[] tempB = sa.getBIons();
        System.out.println("Parent mass: " + sa.getParent());
        for (int i = 0; i < tempB.length; i++) {
            System.out.println(seq.substring(i, i+1) + " | b[" + (i+1) + "]: " + tempB[i] + " | y[" + (length-i) + "]: " + tempY[tempY.length-i-1]);
        }
    }

    /**
     * This method takes care of locating the specified mass in the set of peaks.
     * Whenever a hit is found, the specified mass will be added to the aDeltas HashMap as key
     * (Double) and the corresponding mass delta with the found spectrum peak will be the value
     * (Double).
     *
     * @param aMass double to find a match with within the spectrum peaks.
     * @param aPeaks    TreeSet with the m/z as present in the spectrum.
     * @param aSpectrum HashMap with the full spectrum data (necessary for retrieving
     *                          intensities in case of conflict).
     * @param aDeltas   HashMap that is treated as a reference parameter; found masses and their
     *                          deltas will be inserted in this HashMap as (Double, Double) pairs.
     */
    private void checkMassInSpectrum(double aMass, TreeSet aPeaks, HashMap aSpectrum, HashMap aDeltas) {
        Iterator iter = aPeaks.iterator();
        double intensity = -1.0;
        double delta = 0.0;
        // Try each candidate.
        while (iter.hasNext()) {
            double peak = ((Double)iter.next()).doubleValue();
            double candDelta = peak-aMass;
            double absDelta = Math.abs(candDelta);
            if(absDelta <= iErrorMargin) {
                // Okay, it is within the error margin!
                // Pick the one that has the highest intensity (starting intensty is -1.0).
                double candInt = ((Double)aSpectrum.get(new Double(peak))).doubleValue();
                // If more intense, replace,
                // if equally intense, take smallest error.
                // In all other cases, let it go.
                if( (candInt > intensity) || ((candInt == intensity) && (absDelta < Math.abs(delta))) ) {
                    intensity = candInt;
                    delta = candDelta;
                }
            } else if(absDelta> iErrorMargin && peak > aMass) {
                // So we're out of range, AND the candidate is larger than the
                // theoretical peak. SInce the candidates are sorted in ascending order,
                // all subsequent peaks will also be too big. So we break here.
                break;
            }
        }
        // See if we found a hit.
        if(intensity > 0) {
            aDeltas.put(new Double(aMass), new Double(delta));
        }
    }

    /**
     * This method takes apart the sequence proper and the annotated modifications.
     *
     * @param aModifiedSequence String with the modified sequence (eg. 'Ace-YSFVM&lt;Ox&gt;TAER-COOH').
     */
    private void digestSequence(String aModifiedSequence) {
        iSequence = AASequenceImpl.parsePeptideFromAnnotatedSequence(aModifiedSequence);
    }

    /**
     * This method will calculate all the ion series masses for the peptide.
     */
    private void calculateIonSeries() {
        String sequence = this.iSequence.getSequence();

        // Dimension the arrays.
        iYions = new double[sequence.length()];
        iBions = new double[sequence.length()];

        int length = sequence.length()+1;

        // b and y ions are done here.
        for(int i=1;i<length;i++) {
            double bMass = 0.0;
            double yMass = 0.0;

            // Subtract the mass of 'OH' for the b-ion.
            bMass = this.calculateIonWithMods(1, i+1)-17.00274;
            iBions[i-1] = bMass;
            // Add mass of 'H' to the y-ion.
            yMass = this.calculateIonWithMods(i, length) + 1.007825;
            iYions[i-1] = yMass;
        }
        Arrays.sort(iYions);
    }

    /**
     * This method calculates the fragment ion mass resulting from truncation at the specified
     * start and end location.
     *
     * @param aStart    int with the start location of the fragment ion
     * @param aEnd  int with the end location of the fragment ion (not inclusive)
     * @return  double with the fragment ion mass.
     */
    private double calculateIonWithMods(int aStart, int aEnd) {
        // Get the piece of sequence to calculate for.
        AASequenceImpl tempSeq = iSequence.getTruncatedSequence(aStart, aEnd);
        // Calculate the mass of the amino acids.
        double mass = tempSeq.getMass();
        // Done.
        return mass;
    }
}
