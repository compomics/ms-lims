package com.compomics.mslimscore.util;

import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import com.compomics.mascotdatfile.util.mascot.fragmentions.FragmentIonImpl;
import com.compomics.mslimsdb.accessors.Fragmention;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author Davy
 */
public class FragmentionMiddleMan {
    
        public static Vector getAllMascotDatfileFragmentIonImpl(Connection aConn, long aIdentificationID, long aIonType) throws SQLException {
        Collection<Fragmention> temp = Fragmention.getAllFragmentions(aConn, aIdentificationID);
        Vector<FragmentIon> result = new Vector(temp.size());
        for (Iterator<Fragmention> lIterator = temp.iterator(); lIterator.hasNext();) {
            Fragmention fi = lIterator.next();
             result.add(asFragmentIonImpl(fi));
        }

        return result;
    }
    public static FragmentIon asFragmentIonImpl(Fragmention fi) {
        FragmentIonImpl fii = new FragmentIonImpl(fi.getMz().doubleValue(), fi.getIntensity(), fi.getMasserrormargin().doubleValue(), (int) fi.getIontype(), (int) fi.getFragmentionnumber(), fi.getIonname());
        fii.setImportance((int) fi.getL_ionscoringid());
        return fii;
    }
    
        public static Vector getAllMascotDatfileFragmentIonImpl(Connection aConn, long aIdentificationID) throws SQLException {
        Collection temp = Fragmention.getAllFragmentions(aConn, aIdentificationID);
        Vector<FragmentIon> result = new Vector(temp.size());
        for (Iterator lIterator = temp.iterator(); lIterator.hasNext();) {
            Fragmention fi = (Fragmention) lIterator.next();
            result.add(asFragmentIonImpl(fi));
        }

        return result;
    }
    
}
