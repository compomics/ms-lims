/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.fileio;

import com.compomics.mslimscore.util.IndexElementExtension;
import com.compomics.mslimscore.util.XmlElementExtension;
import com.compomics.mslimscore.util.enumeration.DataType;
import com.compomics.mslimscore.util.enumeration.QuantitationMetaType;
import com.compomics.mslimscore.util.quantitation.QuantitativeValidationSingleton;
import com.compomics.mslimscore.util.quantitation.ratios.DistillerHit;
import com.compomics.mslimscore.util.quantitation.ratios.DistillerPeptide;
import com.compomics.mslimscore.util.quantitation.ratios.DistillerRatio;
import com.compomics.mslimscore.util.quantitation.ratios.DistillerRatioGroup;
import com.compomics.mslimscore.util.quantitation.ratios.DistillerRatioGroupPartner;
import com.compomics.mslimscore.util.quantitation.ratios.RatioGroupCollection;
import com.compomics.util.interfaces.Flamable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;
import psidev.psi.tools.xxindex.StandardXpathAccess;
import psidev.psi.tools.xxindex.index.IndexElement;
import psidev.psi.tools.xxindex.index.StandardXpathIndex;
import psidev.psi.tools.xxindex.index.XmlXpathIndexer;

/**
 *
 * @author Davy
 */
public class QuantitationXmlReader {
	// Class specific log4j logger for QuantitationXmlReader instances.
	 private static Logger logger = Logger.getLogger(QuantitationXmlReader.class);

    /**
     * The RatioGroupCollection where all the ratios, hits, ... will be stored in
     */
    private RatioGroupCollection iRatioGroupCollection;
    /**
     * The flamable
     */
    private Flamable iFlamable;
    private QuantitativeValidationSingleton iQuantitativeValidationSingelton = QuantitativeValidationSingleton.getInstance();

    /**
     * The constructor
     * @param aFile The rov file to parse
     */
    public QuantitationXmlReader(File aFile ,Flamable aFlamable, String aFileName){
        this.iFlamable = aFlamable;
        File lFile = aFile;
        String lPath = lFile.getAbsolutePath();

        try {

            //O.Create some objects
            //get the indexer for this file
            StandardXpathIndex index = XmlXpathIndexer.buildIndex( new FileInputStream( lPath ) );
            //get the accessor for this file
            StandardXpathAccess access = new StandardXpathAccess(lFile);
            //create the RatioGroupCollection
            iRatioGroupCollection = new RatioGroupCollection(DataType.MDQT_ROV);

            //1.Get the hits from the xml file
            Vector<XmlElementExtension> lHitElements = getXmlElements(index, access, "/quantitationResults/peptideGrouping/hit");

            //2.Get the peptides from the xml file
            Vector<XmlElementExtension> lPeptideElements = getXmlElements(index, access, "/quantitationResults/peptide");

            //create distiller hits
            DistillerHit[] lDistillerHits = new DistillerHit[lHitElements.size()];
            for(int i = 0; i<lHitElements.size(); i ++){
                DistillerHit lDistillerHit = new DistillerHit(Integer.valueOf(lHitElements.get(i).getAttribute("h")));
                //get peptides
                Vector<XmlElementExtension> lChildren = lHitElements.get(i).getChildren();
                for(int c = 0; c<lChildren.size(); c ++){
                    XmlElementExtension lChild = lChildren.get(c);
                    DistillerPeptide lPeptide = new DistillerPeptide(Integer.valueOf(lChild.getAttribute("q")),Integer.valueOf(lChild.getAttribute("r")),lChild.getAttribute("pepStr"),lChild.getAttribute("varMods"),lChild.getAttribute("comp"),lChild.getAttribute("status"));
                    for(int p = 0; p<lPeptideElements.size(); p ++){
                        //get the first child (queryRank) from the peptide
                        XmlElementExtension lQueryRank = lPeptideElements.get(p).getChildren().get(0);
                        if(lPeptide.getQuery() == Integer.valueOf(lQueryRank.getAttribute("query"))){
                            lPeptide.setCallMass(Double.valueOf(lPeptideElements.get(p).getAttribute("mrCalc")));
                            lPeptide.setDeltalMass(Double.valueOf(lPeptideElements.get(p).getAttribute("delta")));
                            lPeptide.setObsMass(Double.valueOf(lPeptideElements.get(p).getAttribute("obs")));
                            lPeptide.setScore(Double.valueOf(lPeptideElements.get(p).getAttribute("score")));
                            lPeptide.setType(lPeptideElements.get(p).getAttribute("component"));
                            lPeptide.setVarMods(lPeptideElements.get(p).getAttribute("varModsStr"));
                        }
                    }
                    lDistillerHit.addDistillerPeptide(lPeptide);
                }
                lDistillerHits[i] = lDistillerHit;
            }

            //3.Get the peptide matches from the xml file
            Vector<XmlElementExtension> lPeptideMatchElements = getXmlElements(index, access, "/quantitationResults/peptideMatch");
            //look at the child (hitRatios) of the match to find what is the parent hit number
            for(int i = 0; i <lPeptideMatchElements.size(); i++ ){
                //get all the hitRatios from the match (normally one)
                Vector<XmlElementExtension> lHitRatios = lPeptideMatchElements.get(i).getChildByTitle("hitRatios");
                for(int j = 0; j <lHitRatios.size(); j++ ){
                    XmlElementExtension lHitRatio = lHitRatios.get(j);
                    int lHitNumber = Integer.valueOf(lHitRatio.getAttribute("hitNumber"));
                    for(int k = 0; k<lDistillerHits.length; k ++){
                        //add the ratios to the correct hit
                        if(lDistillerHits[k].getDistillerHitNumber() == lHitNumber){
                            //add every ratio to the ratio group
                            DistillerRatioGroup lDistillerRatioGroup = new DistillerRatioGroup(lDistillerHits[k], iRatioGroupCollection);

                            //A. get the queryNumbers for the identifications linked to this ratiogroup
                            Vector<XmlElementExtension> lQueryMatches = lPeptideMatchElements.get(i).getChildByTitle("partner/matches");
                            Vector<Integer> lPeptideQueryVector = new Vector<Integer>();
                            for(int l = 0; l<lQueryMatches.size(); l ++){
                                lPeptideQueryVector.add(Integer.valueOf(lQueryMatches.get(l).getAttribute("query")));
                            }
                            //create an array with the peptide queries
                            Integer[] peptideQueries = new Integer[lPeptideQueryVector.size()];
                            lPeptideQueryVector.toArray(peptideQueries);
                            //set the queries in the ratio group
                            lDistillerRatioGroup.setRatioGroupQueryNumbers(peptideQueries);

                            //B. get the ratio sequence
                            lDistillerRatioGroup.setPeptideSequence(lPeptideMatchElements.get(i).getAttribute("pepStr"));

                            //C. get the ratios
                            Vector<XmlElementExtension> lRatios = lHitRatio.getChildren();
                            for(int l = 0; l<lRatios.size(); l ++){
                                DistillerRatio lRatio = new DistillerRatio(Double.valueOf(lRatios.get(l).getAttribute("value")), lRatios.get(l).getAttribute("ratio"), Double.valueOf(lRatios.get(l).getAttribute("quality")), lRatios.get(l).getAttribute("valid"), lDistillerRatioGroup);
                                if(!lRatio.getValid()){
                                    //this ratio is not valid
                                    //try to store the reasons
                                    Vector<XmlElementExtension> lRatioStatus = lRatios.get(l).getChildByTitle("ratioStatus");
                                    for(int s = 0; s<lRatioStatus.size(); s ++){
                                        lRatio.addNonValidReason(lRatioStatus.get(s).getAttribute("state"), lRatioStatus.get(s).getAttribute("info"));
                                    }
                                }
                                lDistillerRatioGroup.addRatio(lRatio);
                            }

                            //D. get the absolute intensities
                            Vector<XmlElementExtension> lAbsoluteValueElements = lPeptideMatchElements.get(i).getChildByTitle("partner/absoluteValue");
                            Vector<Double> lAbsoluteValueVector = new Vector<Double>();
                            for(int l = 0; l<lAbsoluteValueElements.size(); l ++){
                                lAbsoluteValueVector.add(Double.valueOf(lAbsoluteValueElements.get(l).getAttribute("value")));
                            }
                            //create an array with the absolute intensities
                            Double[] absoluteIntensities = new Double[lAbsoluteValueVector.size()];
                            lAbsoluteValueVector.toArray(absoluteIntensities);
                            //set the intensities in the ratio group
                            lDistillerRatioGroup.setRatioGroupAbsoluteIntensities(absoluteIntensities);

                            //E.get the correction
                            XmlElementExtension lchargeState = lPeptideMatchElements.get(i).getChildByTitle("chargeStateData").get(0);
                            lDistillerRatioGroup.setCorrelation(Double.valueOf(lchargeState.getAttribute("matchedRho")));
                            lDistillerRatioGroup.setFraction(Double.valueOf(lchargeState.getAttribute("matchedFraction")));

                            //F.add the ratio group to the hit and the ratio group collection
                            lDistillerHits[k].addHitRatioGroup(lDistillerRatioGroup);
                            iRatioGroupCollection.add(lDistillerRatioGroup);

                            //G.find and add the ratiogroup partners
                            Vector<DistillerRatioGroupPartner> lPartners = new Vector<DistillerRatioGroupPartner>();
                            //get all the information from the file
                            Vector<XmlElementExtension> tse = lPeptideMatchElements.get(i).getIndexElement().getExtendedXmlElement();
                            for(int x = 0; x<tse.size(); x ++){
                                XmlElementExtension xmlElement = tse.get(x);
                                Vector<XmlElementExtension> partnerChildren = xmlElement.getChildByTitle("partner");
                                for(int p = 0; p<partnerChildren.size(); p ++){
                                    lPartners.add(new DistillerRatioGroupPartner(partnerChildren.get(p)));
                                }
                            }
                            lDistillerRatioGroup.setRatioGroupPartners(lPartners);

                        }
                    }
                }
            }

            // Get overall information such as the 'component' name (light, heavy, ..)
            // and the 'ratio' (L/H, ..) name from the quantitation file.

            //4.Get the component types from the xml file
            Vector<XmlElementExtension> lComponentElements = getXmlElements(index, access, "/quantitationResults/mqm:quantitation/mqm:method/mqm:component");
            Vector lComponentList = new Vector();
            for(int i = 0; i<lComponentElements.size(); i ++){
                lComponentList.add(lComponentElements.get(i).getAttribute("name"));
            }
            iRatioGroupCollection.setComponentTypes(lComponentList);

            //5.Get the ratio types from the xml file
            Vector<XmlElementExtension> lRatioElements = getXmlElements(index, access, "/quantitationResults/mqm:quantitation/mqm:method/mqm:report_ratio");
            Vector lRatioList = new Vector();
            for(int i = 0; i<lRatioElements.size(); i ++){
                String lRatioType = lRatioElements.get(i).getAttribute("name");
                lRatioList.add(lRatioType);
                Vector<XmlElementExtension> lRatioTypeChildren = lRatioElements.get(i).getChildren();
                Vector<String> lComponents = new Vector<String>();
                for(int j = 0; j<lRatioTypeChildren.size(); j ++){
                    lComponents.add(lRatioTypeChildren.get(j).getAttribute("name"));
                }
                String[] lComponentsArray = new String[lComponents.size()];
                lComponents.toArray(lComponentsArray);
            }
            iRatioGroupCollection.setRatioTypes(lRatioList);

            //6.set the meta data
            String lRunName = null;
            //check if the quantitation is from a multi mascot distiller file
            Vector<XmlElementExtension> lRawFiles = getXmlElements(index, access, "/quantitationResults/rawfiles/rawfile");
            if(lRawFiles.size() > 1){
                //it's a multi file
                XmlElementExtension lRawFile = lRawFiles.get(0);
                Vector<XmlElementExtension> lFileNameElements = lRawFile.getChildByTitle("info");
                for(int j = 0;  j<lFileNameElements.size(); j ++){
                    if(lFileNameElements.get(j).getAttribute("name").equalsIgnoreCase("FILENAME")){
                        aFileName = lFileNameElements.get(j).getAttribute("val");
                        aFileName = aFileName.substring(aFileName.lastIndexOf("%5c") + 3, aFileName.lastIndexOf("."));
                        try{
                            lRunName = aFileName.substring(0,aFileName.indexOf("_"));   //get the lRunName from the file name
                        } catch( IndexOutOfBoundsException e){
                            lRunName = aFileName;
                        }
                    }
                }
            } else {
                //get the lRunName from the file name
                try{
                    lRunName = aFileName.substring(aFileName.lastIndexOf("~") + 1, aFileName.indexOf("_", aFileName.lastIndexOf("~")));
                } catch( IndexOutOfBoundsException e){
                    lRunName = aFileName.substring(0, aFileName.indexOf(".rov"));
                }
            }

            // Set the lRunName and filename to the 'RatioGroupCollection'
            iRatioGroupCollection.putMetaData(QuantitationMetaType.RUNNAME, lRunName);
            iRatioGroupCollection.putMetaData(QuantitationMetaType.FILENAME, aFileName);
            if(iRatioGroupCollection.size() == 0){
                iFlamable.passHotPotato(new Throwable("No information could be extracted from distiller .rov file information for  '" + iRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME) + "' !"));
            }

        } catch (IOException e) {
            iFlamable.passHotPotato(new Throwable("Problem in reading distiller .rov file information for  '" + lPath + "' !"));
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * This method will get the XmlElementExtension for a specific path from the xml file.
     * @param lIndex StandardXpathIndex
     * @param lAccess StandardXpathAccess
     * @param lXmlPath String with the xml tag name
     * @return Vector<XmlElementExtension> with the XmlElementExtensions
     * @throws IOException error reading the xml file.
     */
    public Vector<XmlElementExtension> getXmlElements(StandardXpathIndex lIndex, StandardXpathAccess lAccess ,String lXmlPath) throws IOException {
        List<IndexElement> lIndexElements = lIndex.getElements(lXmlPath);
        Vector<IndexElementExtension> lIndexedElements = new Vector<IndexElementExtension>();
        for (IndexElement element : lIndexElements) {
            lIndexedElements.add( new IndexElementExtension(element,lXmlPath,lAccess ));
        }
        //get all the information from the file
        Vector<XmlElementExtension> lElements = new Vector<XmlElementExtension>();
        for(int i = 0; i<lIndexedElements.size(); i++){
            Vector<XmlElementExtension> tse = lIndexedElements.get(i).getExtendedXmlElement();
            for(int x = 0; x<tse.size(); x ++){
                lElements.add(tse.get(x));
            }
        }
        return lElements;
    }

    /**
     * The getter for the RatioGroupCollection
     * @return RatioGroupCollection
     */
    public RatioGroupCollection getRatioGroupCollection() {
        return iRatioGroupCollection;
    }

}
