package com.compomics.mslimscore.util.fileio.filters;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by IntelliJ IDEA.
 * User: kennyhelsens
 * Date: Oct 25, 2010
 * Time: 1:43:25 PM
 */
public class MascotGenericFileFilter implements FileFilter {
    private String iExtension = ".mgf";


    /**
     * Tests whether or not the specified abstract aFile should be
     * included in a aFile list.
     *
     * @param aFile The abstract aFile to be tested
     * @return <code>true</code> if and only if <code>aFile</code>
     *         should be included
     */
    public boolean accept(File aFile) {
        String lFileName = aFile.getName().toLowerCase();
        if (lFileName.endsWith(iExtension)) {
            return true;
        } else {
            return false;
        }
    }
}
