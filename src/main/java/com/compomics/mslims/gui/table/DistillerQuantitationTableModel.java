package com.compomics.mslims.gui.table;

import com.compomics.mslims.db.accessors.Identification;
import org.apache.log4j.Logger;

import com.compomics.rover.general.enumeration.QuantitationMetaType;
import com.compomics.rover.general.quantitation.RatioGroupCollection;
import com.compomics.rover.general.quantitation.source.distiller.DistillerRatio;
import com.compomics.rover.general.quantitation.source.distiller.DistillerRatioGroup;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 20-nov-2008 Time: 13:45:14
 * <p/>
 * The 'QuantitationTableModel ' this class was created to model Distiller quantitation results. Dynamic based on the
 * quantitation protocol! 1. Ratio GroupMetaData (ex: The parent DistillerHit number) 2. Collection MetaData (ex: The
 * filename) 3. The ratio's (ex: L/H) 4. The Identification types (ex: L)
 */
public class DistillerQuantitationTableModel extends AbstractTableModel {
    // Class specific log4j logger for DistillerQuantitationTableModel instances.
    private static Logger logger = Logger.getLogger(DistillerQuantitationTableModel.class);

    /**
     * The Array of Distiller Output Hit Ratio's to be shown in the table.
     */
    private DistillerRatioGroup[] iGroups = null;
    private int iNumberOfCollectionMetaData;
    private int iNumberOfRatioGroupMetaData; // Reference for the hit within the Mascot Distiller file.
    private int iNumberOfRatios;
    private int iNumberOfComponents;
    private Vector<RatioGroupCollection> iCollections;


    /**
     * Construct a new tablemodel for the given set of RatioGroupCollections.
     *
     * @param aCollections The Vector with one or more RatioGroupCollection instances.
     */
    public DistillerQuantitationTableModel(final Vector<RatioGroupCollection> aCollections) {
        setRatioGroupCollections(aCollections);
    }

    /**
     * Setter for property 'ratioGroupCollections'.
     *
     * @param aCollections Value to set for property 'ratioGroupCollections'.
     */
    public void setRatioGroupCollections(Vector<RatioGroupCollection> aCollections) {
        iCollections = aCollections;
        createData();
    }

    /**
     * Construct the data upon construction
     */
    private void createData() {

        if (iCollections.size() > 0) {

            ArrayList<DistillerRatioGroup> list = new ArrayList<DistillerRatioGroup>();

            // First, process each distinct file.
            for (int i = 0; i < iCollections.size(); i++) {

                RatioGroupCollection lRatioGroupCollection = iCollections.get(i);
                // Cache the number of ratio's and components for determining the number of columns.
                if (i == 0) {
                    iNumberOfRatios = lRatioGroupCollection.getRatioTypes().size();
                    iNumberOfComponents = lRatioGroupCollection.getComponentTypes().size();
                    iNumberOfCollectionMetaData = lRatioGroupCollection.getMetaKeys().size();
                    iNumberOfRatioGroupMetaData = 1;
                }

                // Second process all RatioGroups.
                for (int j = 0; j < lRatioGroupCollection.size(); j++) {

                    DistillerRatioGroup lRatioGroup = (DistillerRatioGroup) lRatioGroupCollection.get(j);
                    // Last, if any ms_lims Identification instances are connected to this
                    // RatioGroup, only then display the RatioGroup in the table.
                    if (lRatioGroup.getNumberOfIdentifications() > 0) {
                        list.add(lRatioGroup);
                    }
                }
            }

            // Convert the ArrayList into a array of references to all groups. Each group is a distinct line
            iGroups = new DistillerRatioGroup[list.size()];
            list.toArray(iGroups);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        // An empty table upon failure!
        if (iGroups == null) {
            return 0;
        } else {
            return iGroups.length;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getColumnName(final int column) {
        String lHeader = null;

        if (column < iNumberOfCollectionMetaData) {
            // We are in the Collection meta data!!

            int lZeroIndex = column;
            RatioGroupCollection lCollection = iCollections.get(0);
            QuantitationMetaType lType = (QuantitationMetaType) lCollection.getMetaKeys().toArray()[lZeroIndex];

            lHeader = lType.toString();
        } else if (column < iNumberOfRatioGroupMetaData + iNumberOfCollectionMetaData) {
            // We are in the RatioGroup meta data!!
            lHeader = "Hit";

        } else if (column < iNumberOfRatios + iNumberOfRatioGroupMetaData + iNumberOfCollectionMetaData) {
            // Columns for the ratio's
            int lZeroIndex = column - iNumberOfRatioGroupMetaData - iNumberOfCollectionMetaData;

            RatioGroupCollection lCollection = iCollections.get(0);
            Vector<String> lRatioTypes = lCollection.getRatioTypes();
            lHeader = lRatioTypes.get(lZeroIndex);

        } else if (column < iNumberOfComponents + iNumberOfRatios + iNumberOfRatioGroupMetaData + iNumberOfCollectionMetaData) {
            // Components in the end!

            int lZeroIndex = column - iNumberOfRatios - iNumberOfRatioGroupMetaData - iNumberOfCollectionMetaData;

            RatioGroupCollection lCollection = iCollections.get(0);
            Vector<String> lComponentTypes = lCollection.getComponentTypes();
            lHeader = lComponentTypes.get(lZeroIndex);

        }
        return lHeader;
    }

    /**
     * {@inheritDoc}
     */
    public Class getColumnClass(final int column) {

        Class lClass = null;

        if (column < iNumberOfRatioGroupMetaData + iNumberOfCollectionMetaData) {
            // Object - String.class
            lClass = Object.class;
        } else if (column < iNumberOfRatios + iNumberOfRatioGroupMetaData + iNumberOfCollectionMetaData) {
            // Columns for the ratio's
            lClass = DistillerRatio.class;
        } else if (column < iNumberOfComponents + iNumberOfRatios + iNumberOfRatioGroupMetaData + iNumberOfCollectionMetaData) {
            // Components in the end!
            lClass = Identification.class;
        }
        return lClass;
    }


    /**
     * {@inheritDoc}
     */
    public int getColumnCount() {
        // Dynamic based on the quantitation protocol!
        // 1. Ratio GroupMetaData (ex: The parent DistillerHit number)
        // 2. Collection MetaData (ex: The filename)
        // 3. The ratio's (ex: L/H)
        // 4. The Identification types (ex: L)

        return iNumberOfRatioGroupMetaData + iNumberOfCollectionMetaData + iNumberOfRatios + iNumberOfComponents;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The 'QuantitationTableModel ' this class was created to model Distiller quantitation results. Dynamic based on
     * the quantitation protocol! 1. Collection MetaData (ex: The filename) 2. Ratio GroupMetaData (ex: The parent
     * DistillerHit number) 3. The ratio's (ex: L/H) 4. The Identification types (ex: L)
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {

        // Note: The ZeroIndex is the 0-based columnIndex for the type of information.

        Object o = null;

        // 1. Collection MetaData (ex: The filename)
        if (columnIndex < iNumberOfCollectionMetaData) {
            int lZeroIndex = columnIndex;
            RatioGroupCollection lCollection = iCollections.get(0);
            QuantitationMetaType lType = (QuantitationMetaType) lCollection.getMetaKeys().toArray()[lZeroIndex];
            o = iGroups[rowIndex].getParentCollection().getMetaData(lType);

            // 2. Ratio GroupMetaData (ex: The parent DistillerHit number)
        } else if (columnIndex < (iNumberOfRatioGroupMetaData + iNumberOfCollectionMetaData)) {
            o = iGroups[rowIndex].getReferenceOfParentHit();

            // 3. The ratio's (ex: L/H)
        } else if (columnIndex < (iNumberOfRatios + iNumberOfRatioGroupMetaData + iNumberOfCollectionMetaData)) {
            int lZeroIndex = columnIndex - iNumberOfRatioGroupMetaData - iNumberOfCollectionMetaData;
            RatioGroupCollection lCollection = iCollections.get(0);
            o = iGroups[rowIndex].getRatio(lZeroIndex);

            // 4. The Identification types (ex: L)
        } else if (columnIndex < (iNumberOfComponents + iNumberOfRatios + iNumberOfRatioGroupMetaData + iNumberOfCollectionMetaData)) {
            int lZeroIndex = columnIndex - iNumberOfRatios - iNumberOfRatioGroupMetaData - iNumberOfCollectionMetaData;
            RatioGroupCollection lCollection = iCollections.get(0);
            Vector<String> lComponentTypes = lCollection.getComponentTypes();
            String lType = lComponentTypes.get(lZeroIndex);

            o = iGroups[rowIndex].getIdentificationForType(lType);

        }

        return o;
    }
}
