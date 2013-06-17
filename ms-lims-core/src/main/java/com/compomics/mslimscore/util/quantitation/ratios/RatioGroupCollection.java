/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.quantitation.ratios;

import com.compomics.mslimscore.util.enumeration.DataType;
import com.compomics.mslimscore.util.enumeration.QuantitationMetaType;
import com.compomics.mslimscore.util.enumeration.QuantitationSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class RatioGroupCollection extends ArrayList{
	// Class specific log4j logger for RatioGroupCollection instances.
	 private static Logger logger = Logger.getLogger(RatioGroupCollection.class);

    /**
     * The Meta information on the collection.
     */
    private HashMap<QuantitationMetaType, Object> iMetaData = new HashMap<QuantitationMetaType, Object>();

    /**
     * The distinct components of this collection. (ex: heavy, light, ..)
     */
    private Vector<String> iComponentTypes;

    /**
     * The distinct ratios of this collection (ex: H/L)
     */
    private Vector<String> iRatioTypes;
    /**
     * DataType with the type (can be ITRAQ_ROV, MDQT_ROV, MAXQUANT, ...) of this ratiogroup collection
     */
    private DataType iType;

    private int iIndex;
    
    private QuantitationSource quantitationSource;

    /**
     * Constructs a RatioGroupCollection. <br>Note this class extends the <i>ArrayList</i>.
     * @param aType DataTypewith the type (can be ITRAQ_ROV, MDQT_ROV, MAXQUANT, ...)
     */
    public RatioGroupCollection(DataType aType) {
        super();
        this.iType = aType;
    }

    /**
     * Add a RatioGroup to the
     * @param aRatioGroup The RatioGroup to be added to this collection.
     * @return boolean on succes status
     */
    public boolean add(final RatioGroup aRatioGroup) {
        return super.add(aRatioGroup);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Returns the RatioGroup for the given index.
     * @param index The index of the requested RatioGroup.
     * @return RatioGroup at the given position.
     */
    public RatioGroup get(final int index) {
        return (RatioGroup) super.get(index);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Returns the list with component types.
     * @return ArrayList with component types. (ex: heavy, light)
     */
    public Vector<String> getComponentTypes() {
        return iComponentTypes;
    }

    /**
     * Set the list with component types.
     * @param aComponentTypes The ArrayList with component types. (ex:heavy, light)
     */
    public void setComponentTypes(final Vector aComponentTypes) {
        iComponentTypes = aComponentTypes;
    }


    /**
     * Returns the list with ratio types.
     * @return ArrayList with ratio types. (ex: L/H, ..)
     */
    public Vector<String> getRatioTypes() {
        return iRatioTypes;
    }


    /**
     * Set the list with ratio types.
     * @param aRatioTypes The ArrayList with ratio types. (ex:heavy, light)
     */
    public void setRatioTypes(final Vector aRatioTypes) {
        iRatioTypes = aRatioTypes;
    }

    /**
     * Store metadata for the collection.
     * @param aType The QuantitationMetaType enum describes the type of meta information.
     * @param o The Object of meta information.
     */
    public void putMetaData(QuantitationMetaType aType, Object o){
        iMetaData.put(aType, o);
    }

    /**
     * Returns the Object with Meta information/
     * @param aType The QuantitationMetaType enum describes the type of meta information.
     * @return  The Object of meta information.
     */
    public Object getMetaData(QuantitationMetaType aType){
        return iMetaData.get(aType);
    }

    /**
     * Returns the Set with MetaKeys that reside on the RatioGroupCollection.
     * @return Set with MetaKeys.
     */
    public Set<QuantitationMetaType> getMetaKeys(){
        return iMetaData.keySet();
    }

    /**
     * Returns the type of this ratiogroupcollection.
     * @return DataType the type
     */
    public DataType getRatioGroupCollectionType(){
        return iType;
    }

    /**
     * Setter for the RatioGroupCollectionType
     * @param aType The RatioGroupCollectionType to set
     */
    public void setRatioGroupCollectionType(DataType aType){
        iType = aType;
    }

    /**
     * Getter for the roversource
     * @return The roversource
     */
    public QuantitationSource getQuantitationSource() {
        return quantitationSource;
    }

    /**
     * Setter for the rover source
     * @param iRoverSource The rover source
     */
    public void setRatioSource(QuantitationSource quantitationSource) {
        this.quantitationSource = quantitationSource;
    }

    /**
     * Getter for the index
     * @return int with the index
     */
    public int getIndex() {
        return iIndex;
    }

    /**
     * Setter for the index
     * @param iIndex the index
     */
    public void setIndex(int iIndex) {
        this.iIndex = iIndex;
    }
}
