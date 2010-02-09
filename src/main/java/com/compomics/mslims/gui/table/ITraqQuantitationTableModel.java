package com.compomics.mslims.gui.table;

import com.compomics.mslims.db.accessors.Identification;
import com.compomics.rover.general.quantitation.RatioGroupCollection;
import com.compomics.rover.general.quantitation.RatioGroup;
import com.compomics.rover.general.quantitation.DefaultRatio;
import com.compomics.rover.general.enumeration.QuantitationMetaType;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import java.util.ArrayList;

/**
    * Created by IntelliJ IDEA.
    * User: niklaas
    * Date: 24-mrt-2009
    * Time: 16:57:19
    *
    * The 'QuantitationTableModel ' this class was created to model Distiller quantitation results.
    * Dynamic based on the quantitation protocol!
    * 1. Ratio GroupMetaData (ex: The parent DistillerHit number)
    * 2. Collection MetaData (ex: The filename)
    * 3. The ratio's (ex: L/H)
    * 4. The Identification types (ex: L)
 */
public class ITraqQuantitationTableModel extends AbstractTableModel{

    /**
     * The Array of Distiller Output Hit Ratio's to be shown in the table.
     */
    private RatioGroup[] iGroups = null;
    private int iNumberOfCollectionMetaData;
    private int iNumberOfRatios;
    private int iNumberOfComponents;
    private Vector<RatioGroupCollection> iCollections;


    /**
     * Construct a new tablemodel for the given set of RatioGroupCollections.
     * @param aCollections The Vector with one or more RatioGroupCollection instances.
     */
    public ITraqQuantitationTableModel(final Vector<RatioGroupCollection> aCollections) {
        setRatioGroupCollections(aCollections);
    }

    /**
     * Setter for property 'ratioGroupCollections'.
     *
     * @param aCollections Value to set for property 'ratioGroupCollections'.
     */
    public void setRatioGroupCollections(Vector<RatioGroupCollection> aCollections){
        iCollections = aCollections;
        createData();
    }

    /**
     * Construct the data upon construction
     */
    private void createData() {

        if (iCollections.size() > 0) {

            ArrayList<RatioGroup> list = new ArrayList<RatioGroup>();

            // First, process each distinct file.
            for (int i = 0; i < iCollections.size(); i++) {

                RatioGroupCollection lRatioGroupCollection = iCollections.get(i);
                // Cache the number of ratio's and components for determining the number of columns.
                if(i==0){
                    iNumberOfRatios = lRatioGroupCollection.getRatioTypes().size();
                    iNumberOfComponents = 1;
                    iNumberOfCollectionMetaData = lRatioGroupCollection.getMetaKeys().size();
                }

                // Second process all RatioGroups.
                for (int j = 0; j < lRatioGroupCollection.size(); j++) {

                    RatioGroup lRatioGroup = (RatioGroup) lRatioGroupCollection.get(j);
                    // Last, if any ms_lims Identification instances are connected to this
                    // RatioGroup, only then display the RatioGroup in the table.
                    if(lRatioGroup.getNumberOfIdentifications() > 0){
                        list.add(lRatioGroup);
                    }
                }
            }

            // Convert the ArrayList into a array of references to all groups. Each group is a distinct line
            iGroups = new RatioGroup[list.size()];
            list.toArray(iGroups);
        }
    }

    /** {@inheritDoc} */
    public int getRowCount() {
        // An empty table upon failure!
        if(iGroups == null){
            return 0;
        }else{
            return iGroups.length;
        }
    }

    /** {@inheritDoc} */
    public String getColumnName(final int column) {
        String lHeader = null;

        if(column < iNumberOfCollectionMetaData){
            // We are in the Collection meta data!!

           int lZeroIndex = column;
           RatioGroupCollection lCollection = iCollections.get(0);
           QuantitationMetaType lType = (QuantitationMetaType) lCollection.getMetaKeys().toArray()[lZeroIndex];

            lHeader = lType.toString();
        }else if(column < iNumberOfRatios + iNumberOfCollectionMetaData){
            // Columns for the ratio's
           int lZeroIndex = column - iNumberOfCollectionMetaData;

           RatioGroupCollection lCollection = iCollections.get(0);
           Vector<String> lRatioTypes = lCollection.getRatioTypes();
           lHeader = (String) lRatioTypes.get(lZeroIndex);

        }else if(column < iNumberOfComponents + iNumberOfRatios + iNumberOfCollectionMetaData){
           //get the quantitation type from the first identification from the first RatioGroupCollection
           RatioGroupCollection lCollection = iCollections.get(0);
           lHeader = lCollection.get(0).getIdentification(0).getType() + " identificationid";


        }
        return lHeader;
    }

    /** {@inheritDoc} */
    public Class getColumnClass(final int column) {

        Class lClass = null;

        if(column < iNumberOfCollectionMetaData){
            // Object - String.class
            lClass = Object.class;
        }else if(column < iNumberOfRatios + iNumberOfCollectionMetaData){
            // Columns for the ratio's
            lClass = DefaultRatio.class;
        }else if(column < iNumberOfComponents + iNumberOfRatios  + iNumberOfCollectionMetaData){
           // Components in the end!
            lClass = Identification.class;
        }
        return lClass;
    }


    /** {@inheritDoc} */
    public int getColumnCount() {
        // Dynamic based on the quantitation protocol!
        // 1. Collection MetaData (ex: The filename)
        // 2. The ratio's (ex: L/H)
        // 3. The Identification types (ex: L)

        return iNumberOfCollectionMetaData + iNumberOfRatios + iNumberOfComponents;
    }

   /** {@inheritDoc}
    *
    * The 'QuantitationTableModel ' this class was created to model Distiller quantitation results.
    * Dynamic based on the quantitation protocol!
    * 1. Collection MetaData (ex: The filename)
    * 2. Ratio GroupMetaData (ex: The parent DistillerHit number)
    * 3. The ratio's (ex: L/H)
    * 4. The Identification types (ex: L)
    *
    */
    public Object getValueAt(final int rowIndex, final int columnIndex) {

       // Note: The ZeroIndex is the 0-based columnIndex for the type of information.

       Object o = null;

       // 1. Collection MetaData (ex: The filename)
       if(columnIndex < iNumberOfCollectionMetaData){
            int lZeroIndex = columnIndex;
            RatioGroupCollection lCollection = iCollections.get(0);
            QuantitationMetaType lType = (QuantitationMetaType) lCollection.getMetaKeys().toArray()[lZeroIndex];
            o = iGroups[rowIndex].getParentCollection().getMetaData(lType);
           // 2. The ratio's (ex: L/H)
        }else if(columnIndex < (iNumberOfRatios + iNumberOfCollectionMetaData)){
            int lZeroIndex = columnIndex -  iNumberOfCollectionMetaData;
            o = iGroups[rowIndex].getRatio(lZeroIndex);
           // 3. The Identification
        }else if(columnIndex < (iNumberOfComponents + iNumberOfRatios + iNumberOfCollectionMetaData)){
            o = iGroups[rowIndex].getIdentification(0);
        }

        return o;
    }
}
