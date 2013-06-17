/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.quantitation.ratios;

import com.compomics.mslimscore.util.IdentificationExtension;
import com.compomics.mslimscore.util.interfaces.Ratio;
import com.compomics.mslimscore.util.quantitation.QuantitativeValidationSingleton;
import java.awt.Color;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Davy
 */
public class MaxQuantRatioGroup  extends RatioGroup {
	// Class specific log4j logger for MaxQuantRatioGroup instances.
	 private static Logger logger = Logger.getLogger(MaxQuantRatioGroup.class);

    /**
     * The absolute intensities found in the DistillerRatioGroup in the distiller quantitation file.
     */
    private Double[] iRatioGroupAbsoluteIntensities;
     /**
     * This distiller validation singelton holds information for the calculation of the ratio
     */
    private QuantitativeValidationSingleton iQuantitativeValidationSingelton = QuantitativeValidationSingleton.getInstance();
    /**
     * The posterior erro probability score for this ratiogroup
     */
    private double iPEP;

    private int iId;

    /**
     * Creates a DistillerRatioGroup implementing the RatioGroup interfaces
     * as well as the logic to tie up the Distiller structure to ms_lims Identifcation instances.
     * @param aRatioGroupCollection The RatioGroupCollection wherein this DistillerRatioGroup resides.
     */
    public MaxQuantRatioGroup(final RatioGroupCollection aRatioGroupCollection, double aPEP, int lId){
        super(aRatioGroupCollection);
        this.iPEP = aPEP;
        this.iId = lId;
    }

    /**
     * Set the AbsoluteIntensities of this DistillerRatioGroup.
     * @param aRatioGroupAbsoluteIntensities The corresponding absolute intensities.
     */
    public void setRatioGroupAbsoluteIntensities(Double[] aRatioGroupAbsoluteIntensities) {
        this.iRatioGroupAbsoluteIntensities = aRatioGroupAbsoluteIntensities;
    }

    public int getId() {
        return iId;
    }

    /**
     * Getter for the absolute intensities
     * @return Double[] with the absolute intensities
     */
    public Double[] getAbsoluteIntensities(){
        return iRatioGroupAbsoluteIntensities;
    }

    /**
     * This method will link identifications to the ratios. This is done by comparing the query number.
     * These identifications were found in the database for a specific datfile linked to the mascot distiller rov file.
     * @param aIdentifications Identifications to match. <b>Hence, the final aim is to link ms_lims Identifications to Quantitation information</b>
     */
    public void linkIdentificationsAndQueries(IdentificationExtension[] aIdentifications){

        // The idea of this method is as following.
        // In general, the QueryNumber is the hub connnecting three objects all having their information.

        // First, the current DistillerRatioGroup and Identification are matched by QueryNumber (and peptide sequence)

        // Second, the DistillerPeptide and Identifcation are then also matched by QueryNumber.

        // Finally, the DistillerRatioGroup saves the relevant Identifications together with their Type.

        // The DistillerRatioGroup then reflects the many-to-many relationship in its data:
        // One or more Identifications, with known type - together with a series of Ratio's;


        // 1. Iterate over each of the given Identification instances.
        for(int i = 0; i<aIdentifications.length; i ++){

            if(aIdentifications[i].getQuantitationGroupId() == iId){
                //the file ref id is correct
                this.addIdentification(aIdentifications[i], aIdentifications[i].getType());
            }
        }
    }


     public double getIntensityForComponent(String aComponent){
        for(int i = 0; i<this.getParentCollection().getComponentTypes().size();  i ++){
            if(aComponent.equalsIgnoreCase(this.getParentCollection().getComponentTypes().get(i))){
                return this.getAbsoluteIntensities()[i];
            }
        }
        return 0.0;
    }

    public double getSummedIntensityForRatioType(String aType){
        Vector<RatioType> iRatioTypes =  iQuantitativeValidationSingelton.getMatchedRatioTypes();
        RatioType lType = null;

        for(int i = 0; i<iRatioTypes.size(); i ++){
            if(iRatioTypes.get(i).getType().equalsIgnoreCase(aType)){
                lType = iRatioTypes.get(i);
            }
        }

        double lSum = 0.0;

        for(int i = 0; i<iQuantitativeValidationSingelton.getComponentTypes().size(); i++){
            for(int j = 0; j<lType.getComponents().length; j ++){
                if(lType.getComponents()[j].equalsIgnoreCase(iQuantitativeValidationSingelton.getComponentTypes().get(i))){
                    lSum = lSum + iRatioGroupAbsoluteIntensities[i];
                }
            }
        }
        return lSum;
    }


    /**
     * This method create a JFreeChart for the absolute intensities
     * @return JFreeChart
     */
    public JFreeChart getIntensityChart() {
        //create dateset

        // row keys...
        Vector<String> lRowKeys = iParentCollection.getComponentTypes();

        // column keys...
        String lColumn = "Absolute intensity";
        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(int i = 0; i<lRowKeys.size(); i ++){

            dataset.addValue(iRatioGroupAbsoluteIntensities[i], lRowKeys.get(i), lColumn);
        }

         // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
             "Absolute intensities", // chart title
             "", // domain axis label
             "", // range axis label
             dataset, // data
             PlotOrientation.VERTICAL, // orientation
             true, // include legend
             false, // tooltips?
             false // URLs?
         );

         // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

         // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

         // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
         plot.setBackgroundPaint(Color.white);
         plot.setDomainGridlinePaint(Color.black);
         plot.setDomainGridlinesVisible(true);
         plot.setRangeGridlinePaint(Color.black);


         // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

         // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        return chart;

     }

    /**
     * To String method
     * @return String
     */
    public String toString(){
        String lTitle = "";
        // row keys...
        Vector<String> lTypes = iParentCollection.getRatioTypes();
        for(int i = 0; i<lTypes.size(); i ++){
            Ratio lRatio = getRatioByType(lTypes.get(i));
            if(lRatio !=  null){
                lTitle = lTitle + " " + lTypes.get(i) + ": " + this.getRatioByType(lTypes.get(i)).getRatio(iQuantitativeValidationSingelton.isLog2());
            } else {
                lTitle = lTitle + " " + lTypes.get(i) + ": /";
            }
        }
        return lTitle;
    }

    /**
     * This method is the getter for the PEP (posterior error probability score)
     * @return double with PEP
     */
    public double getPEP(){
        return iPEP;
    }

}
