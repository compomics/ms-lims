/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.quantitation.ratios;

import com.compomics.mslimscore.util.interfaces.Ratio;
import com.compomics.mslimscore.util.quantitation.QuantitativeValidationSingleton;
import com.compomics.statlib.descriptive.BasicStats;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Vector;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author Davy
 */
public class QuantitativeProtein {
	// Class specific log4j logger for QuantitativeProtein instances.
	 private static Logger logger = Logger.getLogger(QuantitativeProtein.class);

    /**
     * The protein accession
     */
    private String iAccession;
    /**
     * The different ratio types (L/H, M/H, ...)
     */
    private String[] iTypes;
    /**
     * This quantitative validation singelton holds information for the calculation of the ratio
     */
    private QuantitativeValidationSingleton iQuantitativeValidationSingelton = QuantitativeValidationSingleton.getInstance();
    /**
     * Boolean with the validated status of this protein
     */
    private boolean iValidated;
    /**
     * Boolean with the selected status of this protein
     */
    private boolean iSelected;
    /**
     * The protein sequence
     */
    private String iSequence;
    /**
     * A comment on the protein
     */
    private String iProteinComment;
    /**
     * Vector that stores all the peptide groups
     */
    private Vector<QuantitativePeptideGroup> iPeptideGroups = new Vector<QuantitativePeptideGroup>();
    /**
     * This boolean indicates if all the peptide groups are collapsed or are visible
     */
    private boolean iAllPeptideCollapsedStatus = false;
    /**
     * boolean that indicates if the peptide groups are ordened
     */
    private boolean iPeptideGroupsOrdened = false;
    /**
     * The protein sequence length
     */
    private int iSequenceLength = 0;
    private Vector<RatioGroup> iRatioGroups;


    /**
     * The conctructor
     *
     * @param aAccession The accession
     * @param aTypes     The ratio types
     */
    public QuantitativeProtein(String aAccession, String[] aTypes) {
        this.iAccession = aAccession;
        this.iTypes = aTypes;
    }

    /**
     * This method adds a ratio group to this protein.
     * The RatioGroups is also stored in the correct RatioGroup ArrayList based on the RatioGroup sequence
     * @param aRatioGroup The RatioGroup to add to this protein
     */
    public void addRatioGroup(RatioGroup aRatioGroup) {
        //check if it is a new peptide group
        boolean lNewPeptideGroup = true;
        for(int i = 0; i<iPeptideGroups.size(); i ++){
            if(iPeptideGroups.get(i).getSequence().equalsIgnoreCase(aRatioGroup.getPeptideSequence())){
                //it is not a new peptide group
                lNewPeptideGroup = false;
                //add this ratiogroup the this peptidegroup
                iPeptideGroups.get(i).addRatioGroup(aRatioGroup);
            }
        }
        if(lNewPeptideGroup){
            //it is a new peptide group
            //create a new peptide group and add it to the vector
            QuantitativePeptideGroup lPeptideGroup = new QuantitativePeptideGroup(aRatioGroup.getPeptideSequence(), true, false);
            lPeptideGroup.addRatioGroup(aRatioGroup);
            //add it
            iPeptideGroups.add(lPeptideGroup);
        }
    }

    /**
     * Getter for the ratio types (L/H, M/H, ...)
     *
     * @return String[] with the ratio types
     */
    public String[] getTypes() {
        return iTypes;
    }

    /**
     * Getter for the number of RatioGroups linked to this protein
     *
     * @return int with the number of RatioGroups linked to this protein
     */
    public int getNumberOfRatioGroups() {
        int lNumberOfRatioGroups = 0;
        for(int i = 0; i<iPeptideGroups.size(); i ++){
            lNumberOfRatioGroups = lNumberOfRatioGroups + iPeptideGroups.get(i).getRatioGroups().size();
        }
        return lNumberOfRatioGroups;
    }

    /**
     * Getter for the number Peptide Groups
     *
     * @return int with the number of different PeptideGroups linked to this protein
     */
    public int getNumberOfPeptideGroups() {
        return iPeptideGroups.size();
    }

    /**
     * Getter for the protein accession
     *
     * @return String protein accession
     */
    public String getAccession() {
        return iAccession;
    }


    /**
     * Getter for the PeptideGroups linked to this protein
     *
     * @param lOrdered boolean that indicates if the peptides must be ordered (start position)
     * @return Vector with the PeptideGroups linked to this protein
     */
    public Vector<QuantitativePeptideGroup> getPeptideGroups(boolean lOrdered) {
        if(iPeptideGroupsOrdened || !lOrdered){
            return iPeptideGroups;
        } else {
            if(iSequence != null){
                //get all the startpositions
                Vector<Integer> lStartPositions = new Vector<Integer>();
                for(int i = 0; i<iPeptideGroups.size(); i ++){
                    int lStartPosition = iSequence.indexOf(iPeptideGroups.get(i).getSequence());
                    if(lStartPosition == -1){
                        //if the start position is not found, we will set it to the sequence length
                        lStartPosition = iSequence.length();
                        //add the peptide start and end position
                        iPeptideGroups.get(i).setStartPosition(-1);
                        iPeptideGroups.get(i).setEndPosition(-1);
                    } else {
                        //we found a position, add the pre and post sequence to the peptide group
                        if(lStartPosition != 0){
                            iPeptideGroups.get(i).setPreSequence(String.valueOf(iSequence.charAt(lStartPosition - 1)));
                        } else {
                            iPeptideGroups.get(i).setPreSequence("-");
                        }
                        if(lStartPosition + iPeptideGroups.get(i).getSequence().length() < iSequence.length()){
                            iPeptideGroups.get(i).setPostSequence(String.valueOf(iSequence.charAt(lStartPosition + iPeptideGroups.get(i).getSequence().length())));
                        } else {
                            iPeptideGroups.get(i).setPostSequence("-");
                        }
                        //add the peptide start and end position
                        iPeptideGroups.get(i).setStartPosition(lStartPosition);
                        iPeptideGroups.get(i).setEndPosition(lStartPosition + iPeptideGroups.get(i).getSequence().length());
                    }
                    lStartPositions.add(lStartPosition);
                }
                Integer[] lStarts = new Integer[lStartPositions.size()];
                lStartPositions.toArray(lStarts);

                QuantitativePeptideGroup[] lOrderedPeptides = new QuantitativePeptideGroup[iPeptideGroups.size()];
                iPeptideGroups.toArray(lOrderedPeptides);


                //order the peptide groups
                int value;
                int position;
                for (int i = 0; i < lOrderedPeptides.length; i++){
                    value = lStarts[i];
                    position = i;
                    QuantitativePeptideGroup lGroupToOrder = lOrderedPeptides[i];
                    while ((position > 0) && (lStarts[position - 1] > value)){
                        lOrderedPeptides[position] = lOrderedPeptides[position - 1];
                        lStarts[position] = lStarts[position -1];
                        position--;
                    }
                    lOrderedPeptides[position] = lGroupToOrder;
                    lStarts[position] = value;
                }

                iPeptideGroups.removeAllElements();
                for(int i = 0; i<lOrderedPeptides.length; i ++){
                    iPeptideGroups.add(lOrderedPeptides[i]);
                }
                iPeptideGroupsOrdened = true;
            }
            return iPeptideGroups;
        }
    }



    /**
     * Getter for the protein ratio mean (the ratio is the mean of the ratios for the different RatioGroupes grouped by RatioGroup sequence)
     *
     * @param aType Ratio type
     * @return double The protein ratio mean (grouped by RatioGroup sequence)
     */
    public double getGroupedProteinRatio(String aType) {

        double lGroupedProteinRatioMean = 0.0;
        int lNumberOfRatiosUsed = 0;
        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Double lMeanRatio = iPeptideGroups.get(i).getMeanRatioForGroup(aType);
                if(lMeanRatio != null){
                    lGroupedProteinRatioMean = lGroupedProteinRatioMean + lMeanRatio;
                    lNumberOfRatiosUsed = lNumberOfRatiosUsed + 1;
                }
            }
        }
        double lResult = lGroupedProteinRatioMean / (double)lNumberOfRatiosUsed;
        return Math.round(lResult*10000.0)/10000.0;
    }

    /**
     * Getter for the protein ratio mean for a specific ratio type
     *
     * @param aType Ratio type
     * @return double The protein ratio mean
     */
    public double getProteinRatio(String aType) {
        return this.getProteinRatio(aType, -1, iQuantitativeValidationSingelton.isLog2());

    }

     /**
     * Getter for the protein ratio mean for a specific ratio type
     *
     * @param aType Ratio type
     * @param aMultipleSourceIndex Int that indicates the index of a specific source, if this int is -1 all the sources will be used
     * @param isLog2 boolean that indicates if the log2 values of the ratios must be used
     * @return double The protein ratio mean
     */
    public double getProteinRatio(String aType, int aMultipleSourceIndex, boolean isLog2) {

        double lProteinRatioMean = 0.0;
        int lNumberOfRatiosUsed = 0;

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Double> lRatios = iPeptideGroups.get(i).getRatioValuesForType(aType, isLog2, aMultipleSourceIndex);
                for(int j = 0; j<lRatios.size(); j ++){
                    lProteinRatioMean = lProteinRatioMean + lRatios.get(j);
                    lNumberOfRatiosUsed = lNumberOfRatiosUsed + 1;

                }
            }
        }

        double lResult = lProteinRatioMean / (double)lNumberOfRatiosUsed;
        return Math.round(lResult*10000.0)/10000.0;

    }

    /**
     * Getter for the sum of all the peptide intensities for the components linked to a specific ratio type
     * @param aType Ratio type
     * @param aMultipleSourceIndex Int that indicates the index of a specific source, if this int is -1 all the sources will be used
     * @param isLog2 boolean that indicates if the log2 values of the ratios must be used
     * @return double The summed intensity
     */
    public double getSummedProteinIntensities(String aType, int aMultipleSourceIndex, boolean isLog2) {

        double lProteinIntensitySum = 0.0;

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Double> lIntensities = iPeptideGroups.get(i).getIntensitiesForType(aType, isLog2, aMultipleSourceIndex);
                for(int j = 0; j<lIntensities.size(); j ++){
                    lProteinIntensitySum = lProteinIntensitySum + lIntensities.get(j);
                }
            }
        }
        double lResult = lProteinIntensitySum;
        return Math.round(lResult*10000.0)/10000.0;

    }

    /**
     * Getter for the SD of all the peptide intensities for the components linked to a specific ratio type
     * @param aType Ratio type
     * @param aMultipleSourceIndex Int that indicates the index of a specific source, if this int is -1 all the sources will be used
     * @param isLog2 boolean that indicates if the log2 values of the ratios must be used
     * @return double The SD of the intensities
     */
    public double getStandardDeviationProteinIntensities(String aType, int aMultipleSourceIndex, boolean isLog2) {
        DescriptiveStatistics lIntensityHolder = new DescriptiveStatistics();

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Double> lIntensities = iPeptideGroups.get(i).getIntensitiesForType(aType, isLog2, aMultipleSourceIndex);
                for(int j = 0; j<lIntensities.size(); j ++){
                    lIntensityHolder.addValue(lIntensities.get(j));
                }
            }
        }

        double lResultSD = lIntensityHolder.getStandardDeviation();
        return Math.round(lResultSD*10000.0)/10000.0;

    }

    /**
     * Getter for the mean of all the peptide intensities for the components linked to a specific ratio type
     * @param aType Ratio type
     * @param aMultipleSourceIndex Int that indicates the index of a specific source, if this int is -1 all the sources will be used
     * @param isLog2 boolean that indicates if the log2 values of the ratios must be used
     * @return double The mean of the intensities
     */
    public double getMeanProteinIntensity(String aType, int aMultipleSourceIndex, boolean isLog2) {

        double lProteinIntensitySum = 0.0;
        int lNumberOfRatiosUsed = 0;

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Double> lIntensities = iPeptideGroups.get(i).getIntensitiesForType(aType, isLog2, aMultipleSourceIndex);
                for(int j = 0; j<lIntensities.size(); j ++){
                    lProteinIntensitySum = lProteinIntensitySum + lIntensities.get(j);
                    lNumberOfRatiosUsed = lNumberOfRatiosUsed + 1;

                }
            }
        }

        double lResult = lProteinIntensitySum / (double)lNumberOfRatiosUsed;
        return Math.round(lResult*10000.0)/10000.0;

    }

    /**
     * Getter for the median of all the peptide intensities for the components linked to a specific ratio type
     * @param aType Ratio type
     * @param aMultipleSourceIndex Int that indicates the index of a specific source, if this int is -1 all the sources will be used
     * @param isLog2 boolean that indicates if the log2 values of the ratios must be used
     * @return double The median of the intensities
     */
    public double getMedianProteinIntensity(String aType, int aMultipleSourceIndex, boolean isLog2) {

        Vector<Double> lPeptideIntensities = new Vector<Double>();

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Double> lIntensities = iPeptideGroups.get(i).getIntensitiesForType(aType, isLog2, aMultipleSourceIndex);
                for(int j = 0; j<lIntensities.size(); j ++){
                    lPeptideIntensities.add(lIntensities.get(j));
                }
            }
        }

        double[] lInts = new double[lPeptideIntensities.size()];
        for(int i = 0; i<lPeptideIntensities.size(); i ++){
            lInts[i] = lPeptideIntensities.get(i);
        }

        double lResult = 0.0;
        if(lInts.length > 0){
            lResult = BasicStats.median(lInts, false);
        }
        return Math.round(lResult*10000.0)/10000.0;

    }


    /**
     * This method calculates the number of ratios that are used for the calculation of a specific ratio type
     * @param aType String that indicates the ratio type
     * @return int with the number of ratios used
     */
    public int getNumberOfRatiosUsedForProteinMean(String aType) {
        return this.getNumberOfRatiosUsedForProteinMean(aType, -1);
    }

    /**
     * This method calculates the number of ratios that are used for the calculation of a specific ratio type
     * @param aType String that indicates the ratio type
     * @param aMultipleSourceIndex Int that indicates the index of a specific source, if this int is -1 all the sources will be used
     * @return int with the number of ratios used
     */
    public int getNumberOfRatiosUsedForProteinMean(String aType, int aMultipleSourceIndex) {
        int lNumberOfRatiosUsed = 0;
        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Double> lRatios = iPeptideGroups.get(i).getRatioValuesForType(aType, aMultipleSourceIndex);
                for(int j = 0; j<lRatios.size(); j ++){
                    lNumberOfRatiosUsed = lNumberOfRatiosUsed + 1;

                }
            }
        }
        return lNumberOfRatiosUsed;
    }

    /**
     * Method that calculates the protein coverage
     * @return double with the protein coverage
     */
    public double getProteinCoverage(){
        double lLenghtPeptides = 0.0;
        if(iSequenceLength != 0){
            for (int i = 0; i < iPeptideGroups.size(); i++) {
                lLenghtPeptides = lLenghtPeptides + iPeptideGroups.get(i).getSequence().length();
            }
            return lLenghtPeptides / (double) iSequenceLength;
        }
        return  lLenghtPeptides; 
    }

    //todo comment
    public double getProteinPvalue(String aType, int aMultipleSourceIndex){
        double lZScore = Math.abs(getProteinZScore(aType, aMultipleSourceIndex));
        return iQuantitativeValidationSingelton.calculateTwoSidedPvalueForZvalue(lZScore);
    }

    //todo comment
    /*
    public double getPower(String aType, int aMultipleSourceIndex, double lZscore){
        double lResult;
        double lProteinRatioMean = 0.0;
        int lNumberOfRatiosUsed = 0;

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Double> lRatios = iPeptideGroups.get(i).getRatioValuesForType(aType,true, aMultipleSourceIndex);
                for(int j = 0; j<lRatios.size(); j ++){
                    lProteinRatioMean = lProteinRatioMean + lRatios.get(j);
                    lNumberOfRatiosUsed = lNumberOfRatiosUsed + 1;

                }
            }
        }

        double lMean = lProteinRatioMean / (double)lNumberOfRatiosUsed;
        ReferenceSet lReferenceSet = iQuantitativeValidationSingelton.getReferenceSet();
        HashMap lHuber = lReferenceSet.getHuberEstimatorsForType(aType);
        Double lRefMean = (Double) lHuber.get("mean");
        Double lRefSD = (Double) lHuber.get("stdev");

        //lResult = ((lZscore*(lRefSD / Math.sqrt(lNumberOfRatiosUsed)))-(lMean - lRefMean)) / (lRefSD/Math.sqrt(lNumberOfRatiosUsed));
        lResult = ((lZscore*(lRefSD / Math.sqrt(lNumberOfRatiosUsed)))-Math.abs(lMean - lRefMean)) / (lRefSD/Math.sqrt(lNumberOfRatiosUsed));
        double lPvalueResult = 1.0 - iQuantitativeValidationSingelton.calculateTwoSidedPvalueForZvalue(lResult);
        //System.out.println(lNumberOfRatiosUsed+","+lRefSD + "," + (lMean- lRefMean) +  "," + 1.96 + "," + lResult + "," + lPvalueResult);
        return lPvalueResult;
    }      */

    /**
     * This method calculates the Z-score for this protein based on the SD and mean of the reference set, the protein ratio and the
     * number of peptide ratios used in the calculation of the protein ratio
     * @param aType String that indicates the ratio type
     * @param aMultipleSourceIndex Int that indicates the index of a specific source, if this int is -1 all the sources will be used
     * @return double with the protein Z-score
     */
    public double getProteinZScore(String aType, int aMultipleSourceIndex){
        double lResult;
        double summedRatios = 0.0;
        int lNumberOfRatiosUsed = 0;

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Double> lRatios = iPeptideGroups.get(i).getRatioValuesForType(aType,true, aMultipleSourceIndex);
                for(int j = 0; j<lRatios.size(); j ++){
                    summedRatios = summedRatios + lRatios.get(j);
                    lNumberOfRatiosUsed = lNumberOfRatiosUsed + 1;

                }
            }
        }

        double lMean = summedRatios / (double)lNumberOfRatiosUsed;
        ReferenceSet lReferenceSet = iQuantitativeValidationSingelton.getReferenceSet();
        HashMap lHuber = lReferenceSet.getHuberEstimatorsForType(aType);
        Double lRefMean = (Double) lHuber.get("mean");
        Double lRefSD = (Double) lHuber.get("stdev");
        lResult = (lMean - lRefMean)/(lRefSD/Math.sqrt(lNumberOfRatiosUsed));
        if(lNumberOfRatiosUsed == 0){
            lResult = 0.0;
        }
        return lResult;
    }

    /**
     * Getter for the SD of the peptide ratios linked to this protein for a specific ratio type
     *
     * @param aType Ratio type
     * @return double The protein ratio mean
     */
    public double getProteinRatioStandardDeviationForType(String aType) {
        DescriptiveStatistics lRatioHolder = new DescriptiveStatistics();

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Double> lRatios = iPeptideGroups.get(i).getRatioValuesForType(aType, true, -1);
                for(int j = 0; j<lRatios.size(); j ++){
                    lRatioHolder.addValue(lRatios.get(j));
                }
            }
        }

        double lResultSD = lRatioHolder.getStandardDeviation();
        return Math.round(lResultSD*10000.0)/10000.0;

    }

    /**
     * Getter for the MAD of the peptide ratios linked to this protein for a specific ratio type
     *
     * @param aType Ratio type
     * @return double The protein ratio mean
     */
    public double getProteinRatioMADForType(String aType) {
        Vector<Ratio> lRatioHolder = new Vector<Ratio>();

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Ratio> lRatios = iPeptideGroups.get(i).getRatiosForType(aType, true, -1);
                for(int j = 0; j<lRatios.size(); j ++){
                    lRatioHolder.add(lRatios.get(j));
                }
            }
        }

        double lResultSD = calculateMAD(lRatioHolder);
        return Math.round(lResultSD*10000.0)/10000.0;

    }


    /**
     * Method to calculate the MAD for ratios
     * @param lRatios Vector with ratios
     * @return double with the resulting MAD
     */
     public double calculateMAD(Vector<Ratio> lRatios) {
        double[] lRatioDoubles = new double[lRatios.size()];
        for (int i = 0; i < lRatios.size(); i++) {
            lRatioDoubles[i] = lRatios.get(i).getRatio(true);
        }
        if(lRatioDoubles.length == 0){
            return 0.0;
        }
        return BasicStats.mad(lRatioDoubles, false);
    }


     /**
     * Getter for the SD of the original MAD linked to the ratios of this protein for a specific ratio type
     *
     * @param aType Ratio type
     * @return double The protein ratio mean
     */
    public double getOriginalMadSD(String aType) {
        DescriptiveStatistics lMadHolder = new DescriptiveStatistics();

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Ratio> lRatios = iPeptideGroups.get(i).getRatiosForType(aType, true, -1);
                for(int j = 0; j<lRatios.size(); j ++){
                    lMadHolder.addValue(lRatios.get(j).getPreNormalizedMAD());
                }
            }
        }

        double lResultSD = lMadHolder.getStandardDeviation();
        return Math.round(lResultSD*10000.0)/10000.0;

    }

    /**
     * Getter for the SD of the MAD linked to the ratios of this protein for a specific ratio type
     *
     * @param aType Ratio type
     * @return double The protein ratio mean
     */
    public double getNormalizedMadSD(String aType) {
        DescriptiveStatistics lMadHolder = new DescriptiveStatistics();

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Ratio> lRatios = iPeptideGroups.get(i).getRatiosForType(aType, true, -1);
                for(int j = 0; j<lRatios.size(); j ++){
                    lMadHolder.addValue(lRatios.get(j).getNormalizedMAD());
                }
            }                                                  
        }

        double lResultSD = lMadHolder.getStandardDeviation();
        return Math.round(lResultSD*10000.0)/10000.0;

    }

    /**
     * Getter for the mean of the original MAD linked to the ratios of this protein for a specific ratio type
     *
     * @param aType Ratio type
     * @return double The protein ratio mean
     */
    public double getOriginalMadMean(String aType) {
        DescriptiveStatistics lMadHolder = new DescriptiveStatistics();

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Ratio> lRatios = iPeptideGroups.get(i).getRatiosForType(aType, true, -1);
                for(int j = 0; j<lRatios.size(); j ++){
                    lMadHolder.addValue(lRatios.get(j).getPreNormalizedMAD());
                }
            }
        }

        double lResultSD = lMadHolder.getMean();
        return Math.round(lResultSD*10000.0)/10000.0;

    }

    /**
     * Getter for the mean of the MAD linked to the ratios of this protein for a specific ratio type
     *
     * @param aType Ratio type
     * @return double The protein ratio mean
     */
    public double getNormalizedMadMean(String aType) {
        DescriptiveStatistics lMadHolder = new DescriptiveStatistics();

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Ratio> lRatios = iPeptideGroups.get(i).getRatiosForType(aType, true, -1);
                for(int j = 0; j<lRatios.size(); j ++){
                    lMadHolder.addValue(lRatios.get(j).getNormalizedMAD());
                }
            }
        }

        double lResultSD = lMadHolder.getMean();
        return Math.round(lResultSD*10000.0)/10000.0;

    }

    /**
     * Getter for the protein ratio calculated with peptides linked to only this protein
     *
     * @param aType Ratio type
     * @return double The protein ratio mean
     */
    public double getUniqueProteinRatio(String aType) {

        double lProteinRatioMean = 0.0;
        int lNumberOfRatiosUsed = 0;

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Double> lRatios = iPeptideGroups.get(i).getRatioValuesForType(aType);
                if(!iPeptideGroups.get(i).isLinkedToMoreProteins()){
                    for(int j = 0; j<lRatios.size(); j ++){
                        lProteinRatioMean = lProteinRatioMean + lRatios.get(j);
                        lNumberOfRatiosUsed = lNumberOfRatiosUsed + 1;
                    }
                }
            }
        }

        double lResult = lProteinRatioMean / (double)lNumberOfRatiosUsed;
        return Math.round(lResult*10000.0)/10000.0;

    }


    /**
     * This method gives a boolean that indicates if a peptide sequence
     * can be used for the calculation of the protein mean.
     * @param aSequence
     * @return Boolean
     */
    public Boolean getUsageForSequence(String aSequence){
        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).getSequence().equalsIgnoreCase(aSequence)){
                return iPeptideGroups.get(i).isUsedInCalculations();
            }
        }
        //if we get here, we could not find the correct peptidegroup
        //so don't use it
        return false;
    }

    /**
     * This method gives a boolean that indicates if a ratiogroups that
     * are linked to a sequence must be collapsed or visible
     * @param aSequence
     * @return Boolean
     */
    public Boolean getCollapsedStatusForSequence(String aSequence){
        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).getSequence().equalsIgnoreCase(aSequence)){
                return iPeptideGroups.get(i).isCollapsed();
            }
        }
        //if we get here, we could not find the correct peptidegroup
        //so collapse it
        return true;
    }

    /**
     * To string method
     *
     * @return String with the protein accession
     */
    public String toString() {
        if(iProteinComment != null){
            if(iProteinComment.length() == 0){
                return iAccession;
            }
            return iAccession + " - " + iProteinComment;
        }
        return iAccession;
    }

    /**
     * This method create a JFreeChart with the distribution of the ratio of the reference set (in red) and the ratio (for a specific ratio type)
     * for every RatioGroup linked to this protein (in blue). The protein mean (and the peptide grouped protein mean) is shown on the chart (in green).
     * A red area can be displayed (showRealDistribution = true) that represent the [2.5;97.5] of the reference set.
     * A blue area can be displayed (showHuberDistribution = true and log2 (in QuantitationValidationSingelton) = true) that represent the [-1.96;1.96] stDev interval of the Huber estimated distribution of the reference set.
     * @param aReferenceSet DistillerProteinReferenceSet
     * @param aType Ratio type
     * @param showHuberDistribution
     * @param showRealDistribution
     *@param @return JFreeChart
     */
    public JFreeChart getChart(ReferenceSet aReferenceSet, String aType, boolean showHuberDistribution, boolean showRealDistribution) {

        //get the reference set
        XYDataset lReferenceDataSet;
        lReferenceDataSet = aReferenceSet.getChartDataSet(aType,100,iQuantitativeValidationSingelton.getLeftGraphBorder(),iQuantitativeValidationSingelton.getRightGraphBorder(), showHuberDistribution, showRealDistribution);


        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
                "", // chart title
                "Distribution for " + aType, // x axis label
                "#", // y axis label
                lReferenceDataSet, // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true, // tooltips
                true // urls
        );
        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);
        // get a reference to the plot for further customisation...
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        if(showRealDistribution){
            //paint interval
            Marker area = new IntervalMarker(aReferenceSet.getPercentileReal(aType, 2.5), aReferenceSet.getPercentileReal(aType, 97.5));
            area.setPaint(Color.RED);
            area.setAlpha(0.08f);
            area.setLabel("95% of the data");
            area.setLabelAnchor(RectangleAnchor.BOTTOM);
            plot.addDomainMarker(area, org.jfree.ui.Layer.BACKGROUND);
        }

        if(showHuberDistribution){
            Marker area2 = new IntervalMarker(aReferenceSet.getPercentileHuber(aType, -1.96), aReferenceSet.getPercentileHuber(aType, 1.96));
            area2.setPaint(Color.BLUE);
            area2.setAlpha(0.08f);
            area2.setLabel("[-1,96;1,96]");
            area2.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
            area2.setLabelAnchor(RectangleAnchor.BOTTOM);
            plot.addDomainMarker(area2, org.jfree.ui.Layer.BACKGROUND);

        }
        //paint protein ratios
        Marker proteinMean = new ValueMarker(getProteinRatio(aType));
        proteinMean.setPaint(Color.GREEN);
        proteinMean.setLabel("Protein ratio mean");
        proteinMean.setLabelAnchor(RectangleAnchor.TOP_LEFT);
        proteinMean.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addDomainMarker(proteinMean);

        Marker proteinGroupedMean = new ValueMarker(getGroupedProteinRatio(aType));
        proteinGroupedMean.setPaint(Color.GREEN);
        proteinGroupedMean.setLabel("Peptide grouped protein ratio mean");
        proteinGroupedMean.setLabelAnchor(RectangleAnchor.CENTER);
        proteinGroupedMean.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        plot.addDomainMarker(proteinGroupedMean);
        

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            //paint them if they are used in the calculation
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                //get the ratiogroups
                Vector<RatioGroup> lRatioGroups = iPeptideGroups.get(i).getRatioGroups();
                for(int j = 0; j<lRatioGroups.size(); j ++){
                    //get the ratio for the specific ratio type
                    Ratio lRatio = lRatioGroups.get(j).getRatioByType(aType);
                    if(lRatio != null){

                        //paint it
                        Marker lRatioMarker = new ValueMarker(lRatio.getRatio(iQuantitativeValidationSingelton.isLog2()));
                        if(lRatio.getParentRatioGroup().getProteinAccessions().length == 1){
                            lRatioMarker.setPaint(Color.BLUE);
                        } else if(lRatio.getParentRatioGroup().getRazorProteinAccession().equalsIgnoreCase(iAccession)){
                            lRatioMarker.setPaint(Color.RED);
                        } else {
                            lRatioMarker.setPaint(Color.ORANGE);
                        }
                        if(iQuantitativeValidationSingelton.isUseOnlyValidRatioForProteinMean()){
                            if(lRatio.getValid()){
                                if(iQuantitativeValidationSingelton.isUseOnlyUniqueRatioForProteinMean() && lRatioGroups.get(j).getProteinAccessions().length != 1){
                                    //don't add it, it must be unique but it is not
                                } else {
                                    //add it, it doesn't have to be unique
                                    //it could also be that in must be unique and it is
                                    plot.addDomainMarker(lRatioMarker);
                                }
                            }
                        } else {
                            if(iQuantitativeValidationSingelton.isUseOnlyUniqueRatioForProteinMean() && lRatioGroups.get(j).getProteinAccessions().length != 1){
                                //don't add it, it must be unique but it is not
                            } else {
                                //add it, it doesn't have to be unique
                                //it could also be that in must be unique and it is
                                plot.addDomainMarker(lRatioMarker);
                            }
                        }
                    }
                }
            }
        }


        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setShapesVisible(false);
        renderer.setShapesFilled(false);


        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return chart;
    }

    public void setValidated(boolean aValidated) {
        iValidated = aValidated;
    }

    public boolean getValidated() {
        return iValidated;
    }

    public void setSelected(boolean aSelected) {
        iSelected = aSelected;
    }

    public boolean getSelected() {
        return iSelected;
    }

    public String getSequence() {
        return iSequence;
    }

    public void setSequence(String aSequence) {
        this.iSequence = aSequence;
    }

    public int getSequenceLength() {
        return iSequenceLength;
    }

    public void setSequenceLength(int iSequenceLength) {
        this.iSequenceLength = iSequenceLength;
    }

    public String getProteinComment() {
        if(iProteinComment == null){
            return "";
        }
        return iProteinComment;
    }

    public void setProteinComment(String aProteinComment) {
        this.iProteinComment = aProteinComment;
    }

    public void setAllPeptideGroupsCollapsed(boolean aStatus) {
        this.iAllPeptideCollapsedStatus = aStatus;
        for(int i = 0; i<iPeptideGroups.size(); i++){
            iPeptideGroups.get(i).setCollapsed(aStatus);
        }
    }


    public boolean isAllPeptideCollapsedStatus() {
        return iAllPeptideCollapsedStatus;
    }

    /**
     * This method gives the number of identifications linked to this protein
     * @return Int with the number of identifications
     */
    public int getNumberOfIdentifications() {
        int lNumberOfIdentifications = 0;
        for(int i = 0; i<iPeptideGroups.size(); i ++){
            for(int j = 0; j<iPeptideGroups.get(i).getRatioGroups().size(); j ++){
                lNumberOfIdentifications = lNumberOfIdentifications + iPeptideGroups.get(i).getRatioGroups().get(j).getNumberOfIdentifications();
            }
        }
        return lNumberOfIdentifications;
    }

    /**
     * This method gives the number of valid ratios for a specific ratio type linked to this protein
     * @param lType String with the ratio type
     * @return Int with tht number of the valid ratios
     */
    public int getNumberOfValidRatioByType(String lType) {
        int lNumberOfValids = 0;
        for(int i = 0; i<iPeptideGroups.size(); i ++){
            for(int j = 0; j<iPeptideGroups.get(i).getRatioGroups().size(); j ++){
                if(iPeptideGroups.get(i).getRatioGroups().get(j).getRatioByType(lType) != null){
                    if(iPeptideGroups.get(i).getRatioGroups().get(j).getRatioByType(lType).getValid()){
                        lNumberOfValids = lNumberOfValids + 1;
                    }
                }
            }
        }
        return lNumberOfValids;
    }


    /**
     * This method gives the number distinct peptides with one specific ratio that is valid
     * @param lType String with the ratio type
     * @return Int with tht number of the valid ratios
     */
    public int getNumberOfDistinctPeptidesWithOneValidRatioByType(String lType) {
        int lNumberOfValids = 0;
        for(int i = 0; i<iPeptideGroups.size(); i ++){
            for(int j = 0; j<iPeptideGroups.get(i).getRatioGroups().size(); j ++){
                if(iPeptideGroups.get(i).getRatioGroups().get(j).getRatioByType(lType) != null){
                    if(iPeptideGroups.get(i).getRatioGroups().get(j).getRatioByType(lType).getValid()){
                        lNumberOfValids = lNumberOfValids + 1;
                        j = iPeptideGroups.get(i).getRatioGroups().size();
                    }
                }
            }
        }
        return lNumberOfValids;
    }

    /**
     * This method will get all the ratiogroups from the different peptide groups
     * @return Vector with the ratiogroups
     */
    public Vector<RatioGroup> getRatioGroups() {
        if(iRatioGroups != null){
            return iRatioGroups;
        } else {
            iRatioGroups = new Vector<RatioGroup>();
            for(int i = 0; i<iPeptideGroups.size(); i++){
                for(int j = 0; j<iPeptideGroups.get(i).getRatioGroups().size(); j++){
                    iRatioGroups.add(iPeptideGroups.get(i).getRatioGroups().get(j));
                }
            }
        }

        return iRatioGroups;
    }



    /**
     * This method will get the number of uniquely identified peptide groups
     * @return int The number of uniquely identified peptides groups
     */
    public int getNumberOfUniquePeptidesGroups(){
        int lCounter = 0;
        for(int i = 0; i<iPeptideGroups.size(); i++){
            if(!iPeptideGroups.get(i).isLinkedToMoreProteins()){
                lCounter = lCounter + 1;
            }
        }
        return lCounter;
    }

    /**
     * This method will get the number of uniquely identified peptide
     * @return int The number of uniquely identified peptides
     */
    public int getNumberOfUniquePeptides(){
        int lCounter = 0;
        for(int i = 0; i<iPeptideGroups.size(); i++){
            if(!iPeptideGroups.get(i).isLinkedToMoreProteins()){
                lCounter = lCounter + iPeptideGroups.get(i).getRatioGroups().size();
            }
        }
        return lCounter;
    }

    //todo comment
    /*public double getRatioIndexMeanForType(String lType){
        DescriptiveStatistics lIndexHolder = new DescriptiveStatistics();

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Ratio> lRatios = iPeptideGroups.get(i).getRatiosForType(lType, true, -1);
                for(int j = 0; j<lRatios.size(); j ++){
                    double lIndex = -1.0;
                    if(iQuantitativeValidationSingelton.isUseOriginalRatio()){
                        lIndex = lRatios.get(j).getOriginalIndex();
                    } else {
                        lIndex = lRatios.get(j).getIndex();
                    }
                    if(lIndex == -1.0){
                        lIndex = iQuantitativeValidationSingelton.getReferenceSet().getIndexCloseToRatio(lRatios.get(j), lType);
                        if(iQuantitativeValidationSingelton.isUseOriginalRatio()){
                            lRatios.get(j).setOriginalIndex(lIndex);
                        } else {
                            lRatios.get(j).setIndex(lIndex);
                        }
                        System.out.println(lIndex);
                    }
                    lIndexHolder.addValue(lIndex);
                }
            }
        }

        double lResult = lIndexHolder.getMean();
        return Math.round(lResult*10000.0)/10000.0;
    }

    public double getRatioIndexSDForType(String lType){
        DescriptiveStatistics lIndexHolder = new DescriptiveStatistics();

        for (int i = 0; i < iPeptideGroups.size(); i++) {
            if(iPeptideGroups.get(i).isUsedInCalculations()){
                Vector<Ratio> lRatios = iPeptideGroups.get(i).getRatiosForType(lType, true, -1);
                for(int j = 0; j<lRatios.size(); j ++){
                    double lIndex = -1.0;
                    if(iQuantitativeValidationSingelton.isUseOriginalRatio()){
                        lIndex = lRatios.get(j).getOriginalIndex();
                    } else {
                        lIndex = lRatios.get(j).getIndex();
                    }
                    if(lIndex == -1.0){
                        lIndex = iQuantitativeValidationSingelton.getReferenceSet().getIndexCloseToRatio(lRatios.get(j), lType);
                        if(iQuantitativeValidationSingelton.isUseOriginalRatio()){
                            lRatios.get(j).setOriginalIndex(lIndex);
                        } else {
                            lRatios.get(j).setIndex(lIndex);
                        }
                    }
                    lIndexHolder.addValue(lIndex);
                }
            }
        }

        double lResult = lIndexHolder.getStandardDeviation();
        return Math.round(lResult*10000.0)/10000.0;
    }  

    /**
     * This method will calculate in a multiple source environment the maximum difference between the combined protein ratio
     * and the source specific protein ratio
     * @param iType
     * @return double with the difference
     */
    public double getDiffProteinRatios(String iType) {
        double lProteinRatio = this.getProteinRatio(iType);
        double lDiff = 0.0;
        for(int i = 0; i<iQuantitativeValidationSingelton.getTitles().size(); i ++){
            if(this.getProteinRatio(iType, i, iQuantitativeValidationSingelton.isLog2()) != 0.0){
                double lTempDiff = Math.abs(this.getProteinRatio(iType, i, iQuantitativeValidationSingelton.isLog2()) - lProteinRatio);
                if(lTempDiff> lDiff){
                    lDiff = lTempDiff;
                }
            }
        }
        return lDiff;  
    }
}
