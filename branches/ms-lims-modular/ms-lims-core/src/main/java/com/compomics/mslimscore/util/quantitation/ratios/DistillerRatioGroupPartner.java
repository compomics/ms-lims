/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.quantitation.ratios;

import com.compomics.mslimscore.util.XmlElementExtension;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class DistillerRatioGroupPartner {
	// Class specific log4j logger for DistillerRatioGroupPartner instances.
	 private static Logger logger = Logger.getLogger(DistillerRatioGroupPartner.class);

    /**
     * The component type (Light, Medium, ...)
     */
    private String iType;
    /**
     * Boolean that indicates the identification status
     */
    private boolean iIdentified;
    /**
     * Sequence of this component
     */
    private String iSequence;
    /**
     * LabelFreeVarMods of this component
     */
    private String iLabelFreeVarMods;
    /**
     * Mass/Charge of this component
     */
    private double iMZ;
    /**
     * XIC peak start
     */
    private int iXicPeakStart;
    /**
     * XIC peak end
     */
    private int iXicPeakEnd;
    /**
     * XIC region start
     */
    private int iXicRegionStart;
    /**
     * XIC region end
     */
    private int iXicRegionEnd;
    /**
     * The intensities for the scans
     */
    private Double[] iIntensities;
    /**
     * The scans
     */
    private Integer[] iScans;
    /**
     * The mean scan
     */
    private String iMeanScan = "No mean scan";
    /**
     * The absolute intensity of this ratio group partner
     */
    private double iAbsoluteIntensity;
    /**
     * Ranks of the peptide identifications
     */
    private Integer[] iIdentificationRanks;
    /**
     * The query numbers of the peptide identifications
     */
    private Integer[] iQueries;

    /**
     * Constructor
     * @param aPartner XmlElementExtension 
     */
    public DistillerRatioGroupPartner(XmlElementExtension aPartner){

        //1.get the type
        iType = aPartner.getAttribute("component");
        //2.check if it is identified
        iIdentified = Boolean.valueOf(aPartner.getAttribute("partnerIdentified"));
        //3.get the sequence
        iSequence = aPartner.getAttribute("peptideString");
        //4.get the label Free Variable Modifications
        iLabelFreeVarMods = aPartner.getAttribute("labelFreeVariableModifications");
        //5.get the m/Z
        iMZ = Double.valueOf(aPartner.getAttribute("mOverZ"));
        //6.get the xic child
        Vector<XmlElementExtension> lXicVector = aPartner.getChildByTitle("xic");
        //normally there is only one xic, so take the first one
        XmlElementExtension lXic = lXicVector.get(0);
        //6.a get the XIC peak start
        iXicPeakStart = Integer.valueOf(lXic.getAttribute("XICPeakStart"));
        //6.b get the XIC peak end
        iXicPeakEnd = Integer.valueOf(lXic.getAttribute("XICPeakEnd"));
        //6.c get the XIC region start
        iXicRegionStart = Integer.valueOf(lXic.getAttribute("XICRegionStart"));
        //6.d get the XIC region end
        iXicRegionEnd = Integer.valueOf(lXic.getAttribute("XICRegionEnd"));
        //7.get the intensity children of the XIC
        Vector<XmlElementExtension> lIntensityElementsVector = lXic.getChildByTitle("intensity");
        //7.a create the vectors to store the the elements in
        Vector<Double> lIntensity = new Vector<Double>();
        //Vector<Double> lRetentionTime = new Vector<Double>();
        Vector<Integer> lScan = new Vector<Integer>();
        //7.b get the intensity, retention time and scan from the intensity element
        for(int i = 0; i<lIntensityElementsVector.size(); i++){
            XmlElementExtension lIntensityElement = lIntensityElementsVector.get(i);
            if(Integer.valueOf(lIntensityElement.getAttribute("scanid"))>= (iXicRegionStart - 2) && Integer.valueOf(lIntensityElement.getAttribute("scanid")) <= (iXicRegionEnd + 2)){
                lIntensity.add(Double.valueOf(lIntensityElement.getAttribute("v")));
                //lRetentionTime.add(Double.valueOf(lIntensityElement.getAttribute("rt")));
                lScan.add(Integer.valueOf(lIntensityElement.getAttribute("scanid")));
            }
        }
        //7.c get the elements form the vector and store them in an array
        iIntensities = new Double[lIntensity.size()];
        //iRetentionTimes = new Double[lRetentionTime.size()];
        iScans = new Integer[lScan.size()];
        lIntensity.toArray(iIntensities);
        //lRetentionTime.toArray(iRetentionTimes);
        lScan.toArray(iScans);
        //8.get the range child
        Vector<XmlElementExtension> lRangeVector = aPartner.getChildByTitle("range");
        //normally there is only one range, so take the first one
        //sometimes no range is given
        if(lRangeVector.size() != 0){
            //a range is given
            XmlElementExtension lRange = lRangeVector.get(0);
            //8.a get the mean retention time
            //iMeanRetentionTime = lRange.getAttribute("rt");
            //8.b get the mean scan
            iMeanScan = lRange.getAttribute("scan");
        }
        //9.get the absolute value child
        Vector<XmlElementExtension> lAbsoluteValueVector = aPartner.getChildByTitle("absoluteValue");
        //normally there is only one absolute value, so take the first one
        XmlElementExtension lAbsolute = lAbsoluteValueVector.get(0);
        //9.a get the absolute intensity
        iAbsoluteIntensity = Double.valueOf(lAbsolute.getAttribute("value"));
        //10.get the match child (only if this partner is identified)
        if(iIdentified){
            Vector<XmlElementExtension> lMatchVector = aPartner.getChildByTitle("matches");
            //10.a create the vectors to store the the elements in
            Vector<Integer> lQueryVector = new Vector<Integer>();
            Vector<Integer> lRankVector = new Vector<Integer>();
            for(int i = 0; i<lMatchVector.size(); i ++){
                XmlElementExtension lMatch = lMatchVector.get(0);
                //10.b get the query
                lQueryVector.add(Integer.valueOf(lMatch.getAttribute("query")));
                //10.c get the rank
                lRankVector.add(Integer.valueOf(lMatch.getAttribute("rank")));
            }
            //10.c get the elements form the vector and store them in an array
            iQueries = new Integer[lQueryVector.size()];
            iIdentificationRanks = new Integer[lRankVector.size()];
            lQueryVector.toArray(iQueries);
            lRankVector.toArray(iIdentificationRanks);
        }
    }

    /**
     * Getter for the 'iType' parameter
     * @return String with the type
     */
    public String getType() {
        return iType;
    }

    /**
     * Getter for the 'iIdentified' parameter.
     * @return boolean with the identification status
     */
    public boolean isIIdentified() {
        return iIdentified;
    }

    /**
     * Getter for the 'iSequence' parameter
     * @return String with the seqeunce
     */
    public String getSequence() {
        return iSequence;
    }

    /**
     * Getter for the 'iLabelFreeVarMods' parameter
     * @return String with the LabelFreeVariableModifications
     */
    public String getLabelFreeVarMods() {
        return iLabelFreeVarMods;
    }

    /**
     * Getter for the 'iMZ' parameter
     * @return double with the m/z value
     */
    public double getMZ() {
        return iMZ;
    }

    /**
     * Getter for the 'iXicPeakStart' parameter
     * @return int with the XIC peak start
     */
    public int getXicPeakStart() {
        return iXicPeakStart;
    }

    /**
     * Getter for the 'iXicPeakEnd' parameter
     * @return int with the XIC peak end
     */
    public int getXicPeakEnd() {
        return iXicPeakEnd;
    }

    /**
     * Getter for the 'iXicRegionStart' parameter
     * @return int with the XIC region start
     */
    public int getXicRegionStart() {
        return iXicRegionStart;
    }

    /**
     * Getter for the 'iXicRegionEnd' parameter
     * @return int with the XIC region start
     */
    public int getXicRegionEnd() {
        return iXicRegionEnd;
    }

    /**
     * Getter for the 'iIntensities' parameter
     * @return double[] with the intensities for the diffenent scans
     */
    public Double[] getIntensities() {
        return iIntensities;
    }

    /**
     * Getter for the 'iScans' parameter
     * @return Integer[] with the diffenent scans
     */
    public Integer[] getScans() {
        return iScans;
    }

    /**
     * Getter for the 'iMeanScan' parameter
     * @return String with the mean scans
     */
    public String getMeanScan() {
        return iMeanScan;
    }

    /**
     * Getter for the 'iAbsoluteIntensity' parameter
     * @return String with the mean scans
     */
    public double getAbsoluteIntensity() {
        return iAbsoluteIntensity;
    }

    /**
     * Getter for the 'iQueries' parameter
     * @return Integer[] with the query numbers of the identifications linked to this ratiogroup partner
     */
    public Integer[] getQueries() {
        return iQueries;
    }

    /**
     * Getter for the 'iIdentificationRanks' parameter
     * @return Integer[] with the ranks of the identifications linked to this ratiogroup partner
     */
    public Integer[] getIdentificationRanks() {
        return iIdentificationRanks;
    }
}