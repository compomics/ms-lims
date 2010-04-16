package com.compomics.mslims.util.fileio;

import org.apache.log4j.Logger;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 15-okt-2008 Time: 11:40:51 The 'FileExtensionFilter ' class was created
 * for file extensions.
 */
public class FileExtensionFilter extends FileFilter {
    // Class specific log4j logger for FileExtensionFilter instances.
    private static Logger logger = Logger.getLogger(FileExtensionFilter.class);
    /**
     * The extendsion to filter.
     */
    private String iExtension;

    /**
     * Constructs a new FileExtensionFilter
     *
     * @param aExtension The extension to filter for.
     */
    public FileExtensionFilter(final String aExtension) {
        iExtension = aExtension;
    }


    /**
     * {@inheritDoc}
     */
    public boolean accept(final File f) {
        return (f.getName().toLowerCase().endsWith(iExtension));
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return "." + iExtension + " file filter";
    }
}
