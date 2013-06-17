/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.quantitation.ratios;

import com.compomics.mslimscore.util.interfaces.PeptideIdentification;
import com.compomics.mslimscore.util.interfaces.Ratio;
import com.compomics.mslimscore.util.quantitation.QuantitativeValidationSingleton;
import java.awt.Color;
import java.io.IOException;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author Davy
 */
public class DistillerRatioGroup extends RatioGroup {
	// Class specific log4j logger for DistillerRatioGroup instances.
	 private static Logger logger = Logger.getLogger(DistillerRatioGroup.class);

    /**
     * The querynumbers from as found in the DistillerRatioGroup in the distiller quantitation file.
     */
    private Integer[] iRatioGroupQueryNumbers;
    /**
     * The Parent DistillerHit wherefrom this DistillerRatioGroup origins.
     */
    private DistillerHit iParentDistillerHit = null;
    /**
     * The absolute intensities found in the DistillerRatioGroup in the distiller quantitation file.
     */
    private Double[] iRatioGroupAbsoluteIntensities;
    /**
     * The correlation of the ion envelops with the theoretical ion envelops
     * found in the DistillerRatioGroup in the distiller quantitation file.
     */
    private double iCorrelation;
    /**
     * A score for the fraction of the peaks that are noise
     * found in the DistillerRatioGroup in the distiller quantitation file.
     */
    private double iFraction;
    /**
     * The ratiogroup partner
     */
    private Vector<DistillerRatioGroupPartner> iRatioGroupPartners = new Vector<DistillerRatioGroupPartner>();
        /**
     * This distiller validation singelton holds information for the calculation of the ratio
     */
    private QuantitativeValidationSingleton iQuantitativeValidationSingelton = QuantitativeValidationSingleton.getInstance();

    /**
     * Creates a DistillerRatioGroup implementing the RatioGroup interfaces
     * as well as the logic to tie up the Distiller structure to ms_lims Identifcation instances.
     * @param aDistillerHit The parent DistillerHit wherein this DistillerRatioGroup origins.
     * @param aRatioGroupCollection The RatioGroupCollection wherein this DistillerRatioGroup resides.
     */
    public DistillerRatioGroup(DistillerHit aDistillerHit, final RatioGroupCollection aRatioGroupCollection){
        super(aRatioGroupCollection);
        iParentDistillerHit = aDistillerHit;
    }

    /**
     * This method will link identifications to the ratios. This is done by comparing the query number.
     * These identifications were found in the database for a specific datfile linked to the mascot distiller rov file.
     * @param aIdentifications Identifications to match. <b>Hence, the final aim is to link ms_lims Identifications to Quantitation information</b>
     */
    public void linkIdentificationsAndQueries(PeptideIdentification[] aIdentifications){

        // The idea of this method is as following.
        // In general, the QueryNumber is the hub connnecting three objects all having their information.

        // First, the current DistillerRatioGroup and Identification are matched by QueryNumber (and peptide sequence)

        // Second, the DistillerPeptide and Identifcation are then also matched by QueryNumber.

        // Finally, the DistillerRatioGroup saves the relevant Identifications together with their Type.

        // The DistillerRatioGroup then reflects the many-to-many relationship in its data:
        // One or more Identifications, with known type - together with a series of Ratio's;


        // 1. Iterate over each of the given Identification instances.
        for(int i = 0; i<aIdentifications.length; i ++){

            // 2. Iterate over each of the QueryNumber of this RatioGroup.
            for(int q = 0; q< iRatioGroupQueryNumbers.length; q ++){
                // 2A. Assert the querynumber of the group equals the querynumber of one of the identifications.
                if(iRatioGroupQueryNumbers[q].intValue() == aIdentifications[i].getDatfile_query()){
                    // 2B. Assert the sequence of the match is equal!
                    if(iPeptideSequence.equals(aIdentifications[i].getSequence())){

                        // 3. Get the DistillerPeptides from the parent hit.
                        Vector<DistillerPeptide> lDistillerPeptides = iParentDistillerHit.getDistillerPeptides();
                        for(int p =0; p<lDistillerPeptides.size(); p++){

                            // 4. Find the peptide type for this peptide (ex: Heavy or Light)
                            if(lDistillerPeptides.get(p).getQuery() == aIdentifications[i].getDatfile_query()){
                                this.addIdentification(aIdentifications[i], lDistillerPeptides.get(p).getComposition());
                            }
                        }
                    }
                }
            }
        }
    }
    

    /**
     * Set the QueryNumbers of this DistillerRatioGroup.
     * @param aRatioGroupQueryNumbers The corresponding querynumbers.
     */
    public void setRatioGroupQueryNumbers(Integer[] aRatioGroupQueryNumbers) {
        this.iRatioGroupQueryNumbers = aRatioGroupQueryNumbers;
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
     * Return the distiller project file reference
     * of the Parent DistillerHit wherein this DistillerRatioGroup resides.
     * @return
     */
    public int getReferenceOfParentHit(){
        return iParentDistillerHit.getDistillerHitNumber();
    }

    /**
     * Return the Parent DistillerHit wherein this DistillerRatioGroup resides.
     * @return
     */
    public DistillerHit getParentHit(){
        return iParentDistillerHit;
    }


    /**
     * Set the AbsoluteIntensities of this DistillerRatioGroup.
     * @param aRatioGroupAbsoluteIntensities The corresponding absolute intensities.
     */
    public void setRatioGroupAbsoluteIntensities(Double[] aRatioGroupAbsoluteIntensities) {
        this.iRatioGroupAbsoluteIntensities = aRatioGroupAbsoluteIntensities;
    }


    /**
     * Set the correlation of this DistillerRatioGroup.
     * @param aCorrelation The corresponding correction.
     */
    public void setCorrelation(double aCorrelation) {
        this.iCorrelation = aCorrelation;
    }

    /**
     * Set the fraction of this DistillerRatioGroup.
     * @param aFraction The corresponding fraction.
     */
    public void setFraction(double aFraction) {
        this.iFraction = aFraction;
    }

    /**
     * Getter for the absolute intensities
     * @return Double[] with the absolute intensities
     */
    public Double[] getAbsoluteIntensities(){
        return iRatioGroupAbsoluteIntensities;
    }

    /**
     * Setter for the ratiogroup partners
     * @param aRatioGroupPartners Vector with DistillerRatioGroupPartner
     */
    public void setRatioGroupPartners(Vector<DistillerRatioGroupPartner> aRatioGroupPartners){
        this.iRatioGroupPartners = aRatioGroupPartners;
    }

    /**
     * Getter for the RatioGroupPartners
     * @return Vector<DistillerRatioGroupPartner>
     * @throws IOException
     */
    public Vector<DistillerRatioGroupPartner> getRatioGroupPartners() throws IOException {
        return iRatioGroupPartners;
    }

    /**
     * Getter for the fraction
     * @return double with the fraction
     */
    public double getFraction(){
        return this.iFraction;
    }

    /**
     * Getter for the correlation
     * @return double with the correlation
     */
    public double getCorrelation(){
        return this.iCorrelation;
    }

    /**
     * This method create a JFreeChart for the XICs
     * @return JFreeChart
     * @throws IOException
     */
    public JFreeChart getXicChart() throws IOException {
        //create dataset
        Vector<DistillerRatioGroupPartner> lPartners = getRatioGroupPartners();
        XYSeriesCollection dataset = new XYSeriesCollection();
        for(int i = 0; i<lPartners.size(); i ++){
            DistillerRatioGroupPartner lPartner = lPartners.get(i);
            Double[] lIntensities = lPartner.getIntensities();
            Integer[] lScans = lPartner.getScans();
            XYSeries series = new XYSeries(lPartner.getType());
            for(int j = 0; j<lIntensities.length; j ++){
                series.add(lScans[j],lIntensities[j]);
            }
            dataset.addSeries(series);
        }

        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart(
                "XIC", // chart title
                "scan nr.", // x axis label
                "", // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                false, // tooltips
                false // urls
        );
        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);
        // get a reference to the plot for further customisation...
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.black);
        plot.setRangeGridlinePaint(Color.black);

        //paint confident interval
        Marker area = new IntervalMarker(lPartners.get(0).getXicPeakStart(), lPartners.get(0).getXicPeakEnd());
        area.setPaint(Color.pink);
        area.setAlpha(0.5f);
        area.setLabel("Area used to calculate ratio");
        area.setLabelAnchor(RectangleAnchor.BOTTOM);
        plot.addDomainMarker(area);


        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setShapesVisible(true);
        renderer.setShapesFilled(true);


        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.
        return chart;
    }


    /**
     * This method creates a JFreeChart for the absolute intensities
     * @return
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
     * Getter for the datfile queries
     * @return Array with integers
     */
    public Integer[] getDatfileQueries() {
        return iRatioGroupQueryNumbers;
    }
}
