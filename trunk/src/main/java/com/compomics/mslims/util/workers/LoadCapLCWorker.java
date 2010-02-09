/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 16-jul-2003
 * Time: 16:06:27
 */
package com.compomics.mslims.util.workers;

import com.compomics.mslims.db.accessors.LCRun;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.io.FilenameExtensionFilter;
import com.compomics.util.sun.SwingWorker;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2005/03/04 08:36:12 $
 */

/**
 * This class loads all the LCRuns with PKL files from the directories
 * in the specified folder listing.
 *
 * @author Lennart Martens
 */
public class LoadCapLCWorker extends SwingWorker {

    private File[] list = null;
    private Vector iStoredCaplc = null;
    private Vector names=  null;
    private Flamable outer = null;
    private DefaultProgressBar progress = null;

    /**
     * This hash will contain the mapping of the LCRun to all the constituent files.
     * Keys are Strings with the LCRun name, values are Vectors of files.
     */
    private HashMap iCapLCtoFiles = null;

    /**
     * This constructor takes all parameters that allow the setting up of this LoadCapLCWorker.
     *
     * @param aList File[] with the listing of the directory to process.
     * @param aStored   Vector with the already stored LCRuns. PKL files from these runs will be skipped.
     * @param aNames    Vector that will contain the LCRuns that were picked up as new.
     *                  <b>Please note</b> that this is a reference parameter.
     * @param aParent   Flamable with the parent to inform of grievous errors.
     * @param aProgress DefaultProgressBar to show the progress of the loader on.
     * @param aCapLCtoFiles HashMap which will contain the LCRun names as Strings as keys, and Vectors
     *                      with File instances as values. It thus provides the list of files per LCRun.
     *                      <b>Please note</b> that this is a reference parameter.
     */
    public LoadCapLCWorker(File[] aList, Vector aStored, Vector aNames, Flamable aParent, DefaultProgressBar aProgress, HashMap aCapLCtoFiles) {
        list = aList;
        iStoredCaplc = aStored;
        names = aNames;
        outer = aParent;
        progress = aProgress;
        iCapLCtoFiles = aCapLCtoFiles;
    }

    /**
     * Compute the value to be returned by the <code>get</code> method.
     */
    public Object construct() {
        for (int i = 0; i < list.length; i++) {
            File file = list[i];
            if(file.isDirectory()) {
                recurseForPKLs(file);
            }
            int progressCount = i+1;
            if(progressCount < progress.getMaximum()) {
                progress.setValue(progressCount);
            }
        }
        // See if we found anything at all...
        if(iCapLCtoFiles.size() == 0 && list.length > 0) {
            // Maybe they pointed to the last folder in the hierarchy,
            // and the PKL files are in fact the listing we received...
            recurseForPKLs(list[0].getParentFile());
        }
        // Create the necessary LCRuns.
        Iterator iter = iCapLCtoFiles.keySet().iterator();
        while (iter.hasNext()) {
            String lcName = (String)iter.next();
            Vector tempVec = (Vector)iCapLCtoFiles.get(lcName);
            int lcSize = tempVec.size();
            LCRun tempRun = new LCRun(lcName, 1, lcSize);
            names.add(tempRun);
        }
        progress.setValue(progress.getValue()+1);
        return "";
    }

    /**
     * This method recurses through the specified folder until it finds
     * pkl files or can no longer find lower directories.
     *
     * @param aFile File with the folder to recurse through.
     */
    private void recurseForPKLs(File aFile) {
        File[] tempList = aFile.listFiles(new FilenameExtensionFilter("pkl"));
        if(tempList != null && tempList.length > 0) {
            // Now find all the LCRuns that this folder is really composed of.
            for(int j = 0; j < tempList.length; j++) {
                String s = tempList[j].getName().toLowerCase();
                int end = s.indexOf(".");
                if(end > 0) {
                    String key = s.toLowerCase().substring(0, end);
                    if(verifyNewOne(key, iStoredCaplc)) {
                        if(iCapLCtoFiles.containsKey(key)) {
                            Vector temp = (Vector)iCapLCtoFiles.get(key);
                            temp.add(tempList[j]);
                        } else {
                            Vector temp = new Vector();
                            temp.add(tempList[j]);
                            iCapLCtoFiles.put(key, temp);
                        }
                    }
                }
            }
        } else {
            File[] files = aFile.listFiles();
            if(files != null && files.length > 0) {
                // Cycle all found items.
                for (int i = 0; i < files.length; i++) {
                    File lFile = files[i];
                    // Only look at directories.
                    if(lFile.isDirectory()) {
                        this.recurseForPKLs(lFile);
                    }
                }
            }
        }
    }

    /**
     * This method checks whether the lcrun which builds this filename is new,
     * regardless of case.
     *
     * @param aLCRunName    String with the name of the lcrun to check against what has
     *                      been stored before.
     * @param aStoredCaplc  Vector with the stored LCRuns that have been stored before.
     * @return  boolean that is 'true' when the lcrun name is new, 'false' when
     *                  it is already present in the DB.
     */
    public static boolean verifyNewOne(String aLCRunName, Vector aStoredCaplc) {
        boolean newLCRun = true;
        for (int i = 0; i < aStoredCaplc.size(); i++) {
            String dbName = (String)aStoredCaplc.elementAt(i);
            if(dbName.toLowerCase().equals(aLCRunName.toLowerCase())) {
                newLCRun = false;
                break;
            }
        }
        return newLCRun;
    }
}
