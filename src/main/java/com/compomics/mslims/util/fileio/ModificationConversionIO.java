package com.compomics.mslims.util.fileio;

import com.compomics.mslims.db.accessors.Modification_conversion;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Niklaas
 * Date: 29-Jun-2010
 * Time: 11:54:08
 * To change this template use File | Settings | File Templates.
 */
public class ModificationConversionIO {
    // Class specific log4j logger for ModificationConversionIO instances.
    private static Logger logger = Logger.getLogger(MascotGenericFile.class);

    public ModificationConversionIO(){

    }


    public void writeModificationConversionFile(int lNewVersion, Vector<Modification_conversion> lConversions){
        try{
            String path = "" + this.getClass().getProtectionDomain().getCodeSource().getLocation();
            path = path.substring(5, path.lastIndexOf("/"));
            path = path + "/resources/modificationConversion.txt";
            path = path.replace("%20", " ");
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            out.write("#version=" + lNewVersion + "\n");
            for(int  m = 0; m<lConversions.size(); m ++){
                out.write(lConversions.get(m).getModification() + "=" + lConversions.get(m).getConversion() + "\n");
            }
            out.flush();
            out.close();

        } catch (IOException e) {
            logger.error(e);
        }

    }

    public int getLocalModificationConversionVersion(){
        int lVersion = 0;
        BufferedReader lBuf = null;
        try {
            // First, try to find the modificationconversion file in the "resources" jar launcher folder.
            String path = "" + this.getClass().getProtectionDomain().getCodeSource().getLocation();
            path = path.substring(5, path.lastIndexOf("/"));
            path = path + "/resources/modificationConversion.txt";
            path = path.replace("%20", " ");

            File lFile = new File(path);
            if (lFile.exists()) {
                lBuf = new BufferedReader(new InputStreamReader(new FileInputStream(lFile)));
            } else {
                // Second, if not found - try to find the file in the classpath.
                InputStreamReader lReader = new InputStreamReader(ClassLoader.getSystemResourceAsStream("modificationConversion.txt"));

                if (lReader == null) {
                    lReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("modificationConversion.txt"));
                }

                lBuf = new BufferedReader(lReader);
            }


                String line = null;
                while ((line = lBuf.readLine()) != null) {
                    // Skip comments and empty lines.
                    if(line.trim().startsWith("#version")){
                        lVersion = Integer.valueOf(line.substring(line.indexOf("=") + 1).trim());
                    }
                }

            lBuf.close();
        } catch (IOException e) {
            logger.error(e);
        }

        return lVersion;
    }
}
