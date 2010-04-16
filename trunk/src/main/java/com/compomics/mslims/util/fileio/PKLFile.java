/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 24-feb-03
 * Time: 14:35:53
 */
package com.compomics.mslims.util.fileio;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.mascot.MascotIdentifiedSpectrum;
import com.compomics.util.interfaces.SpectrumFile;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2007/10/22 10:31:17 $
 */

/**
 * This class maps a PKLFile to memory. It allows some searching and retrieval functionality.
 *
 * @author Lennart Martens
 */
public class PKLFile extends SpectrumFileAncestor implements SpectrumFile {
    // Class specific log4j logger for PKLFile instances.
    private static Logger logger = Logger.getLogger(PKLFile.class);

    /**
     * This variable holds the lowest intensity peak in the spectrum.
     */
    private double iLowIntensity = -1.0;

    /**
     * This variable holds the highest intensity peak in the spectrum.
     */
    private double iHighIntensity = -1.0;

    /**
     * This variable holds the lightest peak in the spectrum.
     */
    private double iLowMass = -1.0;

    /**
     * This variable holds the heaviest peak in the spectrum.
     */
    private double iHighMass = -1.0;


    /**
     * This constructor takes the PKLFile as a String as read from file or DB. The filename is specified separately here
     * and this filename overrides a filename already present in the pklfile.
     *
     * @param aFilename String with the filename for the PKLFile.
     * @param aContents String with the contents of the PKLFile.
     */
    public PKLFile(String aFilename, String aContents) {
        this.parseFromString(aContents);
        this.iFilename = aFilename;
    }

    /**
     * This constructor takes the PKLFile as a String as read from file or DB. The filename has to be present as the
     * fourth element in the first line!
     *
     * @param aContents String with the contents of the PKLFile.
     */
    public PKLFile(String aContents) {
        this.parseFromString(aContents);
    }


    /**
     * This constructor takes the filename of the PKLFile as argument and loads it form the hard drive. The filename
     * specified for loading the file overrides a filename present in the header of the pklfile.
     *
     * @param aFilename File with the pointer to the PKLFile.
     * @throws IOException when the file could not be read.
     */
    public PKLFile(File aFilename) throws IOException {
        if (!aFilename.exists()) {
            throw new IOException("PKLFile '" + aFilename.getCanonicalPath() + "' was not found!");
        } else {
            StringBuffer lsb = new StringBuffer();
            BufferedReader br = new BufferedReader(new FileReader(aFilename));
            String line = null;
            while ((line = br.readLine()) != null) {
                lsb.append(line + "\n");
            }
            br.close();
            this.parseFromString(lsb.toString());
            this.iFilename = aFilename.getName();
        }
    }

    public double getHighIntensity() {
        return iHighIntensity;
    }

    public void setHighIntensity(double aHighIntensity) {
        iHighIntensity = aHighIntensity;
    }

    public double getHighMass() {
        return iHighMass;
    }

    public void setHighMass(double aHighMass) {
        iHighMass = aHighMass;
    }

    public double getLowIntensity() {
        return iLowIntensity;
    }

    public void setLowIntensity(double aLowIntensity) {
        iLowIntensity = aLowIntensity;
    }

    public double getLowMass() {
        return iLowMass;
    }

    public void setLowMass(double aLowMass) {
        iLowMass = aLowMass;
    }

    /**
     * This method allows the caller to write the spectrum file to the specified folder using its current filename. The
     * filename is NOT mentioned in the PKL file when this method is used.
     *
     * @param aParentDir File with the parent directory to put the file in.
     * @throws IOException whenever the write process failed.
     */
    public void writeToFile(File aParentDir) throws IOException {
        this.writeToFile(aParentDir, false);
    }

    /**
     * This method allows to write the spectrum file to the specified OutputStream. The filename is NOT mentioned in the
     * PKL file when this method is used.
     *
     * @param aOut OutputStream to write the file to. This Stream will <b>NOT</b> be closed by this method.
     * @throws IOException when the write operation fails.
     */
    public void writeToStream(OutputStream aOut) throws IOException {
        this.writeToStream(aOut, false);
    }

    /**
     * This method returns the runnumber as indicated in the filename of the PKL file (it is the number after 'caplc'
     * and before the first '.').
     *
     * @return long    with the runnumber, or -1 if no filename was set or the filename was not formatted correctly.
     */
    public long getRunNumber() {
        long result = -1l;
        if (this.iFilename != null) {
            int start = this.iFilename.toLowerCase().indexOf("caplc") + 5;
            int stop = this.iFilename.indexOf('.');
            try {
                result = Long.parseLong(this.iFilename.substring(start, stop));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * This method returns the scannumber as indicated in the filename of the PKL file (it is the number between the
     * first and second '.').
     *
     * @return long    with the scannumber, or -1 if no filename was set or the filename was not formatted correctly.
     */
    public long getScanNumber() {
        long result = -1l;
        if (this.iFilename != null) {
            int start = this.iFilename.indexOf('.') + 1;
            int stop = this.iFilename.indexOf('.', start);
            try {
                result = Long.parseLong(this.iFilename.substring(start, stop));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * This method checks whether the following variables correspond: <ul> <li>Precursor M/Z (exact)</li> <li>Precursor
     * charge state</li> </ul>
     *
     * @param aMIS MascotIdentifiedSpectrum to compare to.
     * @return boolean which indicates whether these objects correspond.
     */
    public boolean corresponds(MascotIdentifiedSpectrum aMIS) {
        boolean result = false;
        if ((aMIS.getPrecursorMZ() == this.iPrecursorMz) &&
                (aMIS.getChargeState() == this.iCharge)
                ) {
            result = true;
        }

        return result;
    }

    /**
     * This method performs a 'coarser' check of the precurosr mass, meaning that it allows for an error of 0.0001 Da on
     * the precursor mass. <ul> <li>Precursor M/Z (exact)</li> <li>Precursor charge state</li> </ul>
     *
     * @param aMIS MascotIdentifiedSpectrum to compare to.
     * @return boolean which indicates whether these objects correspond.
     */
    public boolean coarseCheck(MascotIdentifiedSpectrum aMIS) {
        boolean result = false;
        if (((Math.abs(aMIS.getPrecursorMZ() - this.iPrecursorMz)) <= 0.0001) &&
                (aMIS.getChargeState() == this.iCharge)
                ) {
            result = true;
        }

        return result;
    }

    /**
     * This method is an elaboration of the 'corresponds' check and should only be used when the corresponds method
     * returns multiple hits, since there are some problems concerning MAscot precision in reporting masses! <br /> It
     * checks the following parameters: <ul> <li>Precursor M/Z (exact)</li> <li>Precursor charge state</li> <li>lowest
     * peak mass with an allowed error of 0.2 Da</li> <li>highest peak mass with an allowed error of 0.2 Da</li> </ul>
     *
     * @param aMIS MascotIdentifiedSpectrum to compare to.
     * @return boolean which indicates whether these objects correspond.
     */
    public boolean deepCheck(MascotIdentifiedSpectrum aMIS) {
        boolean result = false;
        if ((aMIS.getPrecursorMZ() == this.iPrecursorMz) &&
                (aMIS.getChargeState() == this.iCharge) &&
                (Math.abs(this.iLowMass - aMIS.getLowestMass()) <= 0.2) &&
                (Math.abs(this.iHighMass - aMIS.getHighestMass()) <= 0.2)
                ) {
            result = true;
        }

        return result;
    }

    /**
     * This method returns a String representation of the PKLFile object.
     *
     * @return String  with a String representation of the PKL file.
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        // The precursor M/Z, intensity and charge and the filename.
        result.append(this.iPrecursorMz + " " + this.iIntensity + " " + this.iCharge + " " + this.iFilename + "\n");
        // Lowest and highest mass and lowest and highest intensities.
        result.append("Massrange: " + this.iLowMass + "-" + this.iHighMass + "\n");
        result.append("Intensityrange: " + this.iLowIntensity + "-" + this.iHighIntensity + "\n");
        // The individual peaks.
        Object[] keys = this.iPeaks.keySet().toArray();
        Arrays.sort(keys);
        for (int i = 0; i < keys.length; i++) {
            Object key = keys[i];
            result.append(key.toString() + " " + this.iPeaks.get(key).toString() + "\n");
        }
        // Voila.
        return result.toString();
    }

    /**
     * Compares this object with the specified object for order.  Returns a negative integer, zero, or a positive
     * integer as this object is less than, equal to, or greater than the specified object.<p>
     * <p/>
     * The implementor must ensure <tt>sgn(x.compareTo(y)) == -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and
     * <tt>y</tt>.  (This implies that <tt>x.compareTo(y)</tt> must throw an exception iff <tt>y.compareTo(x)</tt>
     * throws an exception.)<p>
     * <p/>
     * The implementor must also ensure that the relation is transitive: <tt>(x.compareTo(y)&gt;0 &amp;&amp;
     * y.compareTo(z)&gt;0)</tt> implies <tt>x.compareTo(z)&gt;0</tt>.<p>
     * <p/>
     * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt> implies that <tt>sgn(x.compareTo(z)) ==
     * sgn(y.compareTo(z))</tt>, for all <tt>z</tt>.<p>
     * <p/>
     * It is strongly recommended, but <i>not</i> strictly required that <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.
     * Generally speaking, any class that implements the <tt>Comparable</tt> interface and violates this condition
     * should clearly indicate this fact.  The recommended language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object.
     * @throws ClassCastException if the specified object's type prevents it from being compared to this Object.
     */
    public int compareTo(Object o) {
        int compared = 0;

        PKLFile file = (PKLFile) o;
        // See if the run numbers differ.
        long run = file.getRunNumber();
        compared = (int) (this.getRunNumber() - run);
        if (compared == 0) {
            long scan = file.getScanNumber();
            compared = (int) (this.getScanNumber() - scan);
        }

        return compared;
    }

    /**
     * This method allows to write the PKLFile to the specified OutputStream.
     *
     * @param aOut           OutputStream to write the file to. This Stream will <b>NOT</b> be closed by this method.
     * @param aWriteFilename if this boolean is true, the filename is appended as the fourth element in the header. If
     *                       it is false, the filename is ommitted.
     * @throws IOException when the write operation fails.
     */
    public void writeToStream(OutputStream aOut, boolean aWriteFilename) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(aOut));
        bw.write(new BigDecimal(this.iPrecursorMz).setScale(4, BigDecimal.ROUND_HALF_UP) + " " + this.iIntensity + " " + this.iCharge);
        if (aWriteFilename) {
            bw.write(" " + this.iFilename);
        }
        bw.write("\n");
        SortedSet ss = new TreeSet(this.iPeaks.keySet());
        Iterator it = ss.iterator();
        while (it.hasNext()) {
            Double tempKey = (Double) it.next();
            BigDecimal lDouble = new BigDecimal(tempKey.doubleValue()).setScale(4, BigDecimal.ROUND_HALF_UP);
            bw.write(lDouble.toString() + " " + new BigDecimal(((Double) this.iPeaks.get(tempKey)).doubleValue()).setScale(4, BigDecimal.ROUND_HALF_UP).toString() + "\n");
        }
        bw.flush();
    }

    /**
     * This method allows the caller to write the pklfile to the specified folder using its current filename.
     *
     * @param aParentDir     File with the parent directory to put the file in.
     * @param aWriteFilename if this boolean is true, the filename is appended as the fourth element in the header. If
     *                       it is false, the filename is ommitted.
     * @throws IOException whenever the write process failed.
     */
    public void writeToFile(File aParentDir, boolean aWriteFilename) throws IOException {
        if (!aParentDir.exists() && !aParentDir.isDirectory()) {
            throw new IOException("Parent '" + aParentDir.getCanonicalPath() + "' does not exist or is not a directory!");
        }
        File output = new File(aParentDir, this.iFilename);
        FileOutputStream fos = new FileOutputStream(output);
        this.writeToStream(fos, aWriteFilename);
        fos.flush();
        fos.close();
    }

    /**
     * This method reports on the contents of this PKLFile in Mascot Generic Format.
     *
     * @return String with the Mascot Generic Formatted contents of this PKLFile.
     */
    public String getMGFContents() {
        StringBuffer content = new StringBuffer();

        // First start with the 'BEGIN IONS'.
        content.append("BEGIN IONS\n");
        // Next up: the title. We just use the original (PKL) filename here.
        content.append("TITLE=" + this.iFilename + "\n");
        // Now the parent mass and intensity.
        content.append("PEPMASS=" + this.iPrecursorMz + " " + this.iIntensity + "\n");
        // Now the charge. Note the extra processing for MGF notation (eg. 1+).
        content.append("CHARGE=" + Math.abs(this.iCharge) + (this.iCharge > 0 ? "+" : "-") + "\n");
        // Now all the peaks.
        SortedSet ss = new TreeSet(this.iPeaks.keySet());
        Iterator it = ss.iterator();
        while (it.hasNext()) {
            Double tempKey = (Double) it.next();
            BigDecimal lDouble = new BigDecimal(tempKey.doubleValue()).setScale(4, BigDecimal.ROUND_HALF_UP);
            content.append(lDouble.toString() + " " + new BigDecimal(((Double) this.iPeaks.get(tempKey)).doubleValue()).setScale(4, BigDecimal.ROUND_HALF_UP).toString() + "\n");
        }

        // Finally, 'END IONS'.
        content.append("END IONS\n");

        return content.toString();
    }

    /**
     * This method will parse the data from the specified String with the contents of the PKL file.
     *
     * @param aPKLContents String with the contents of the PKL file.
     */
    private void parseFromString(String aPKLContents) {
        this.iPeaks = new HashMap();
        try {
            BufferedReader br = new BufferedReader(new StringReader(aPKLContents));
            String line = null;
            // First read the header line.
            line = br.readLine();
            while (line.trim().equals("")) {
                line = br.readLine();
            }
            StringTokenizer firstLine = new StringTokenizer(line, " ");
            this.iPrecursorMz = new BigDecimal(firstLine.nextToken().trim()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
            this.iIntensity = new BigDecimal(firstLine.nextToken().trim()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
            this.iCharge = Integer.parseInt(firstLine.nextToken().trim());
            if (firstLine.hasMoreTokens()) {
                this.iFilename = firstLine.nextToken().trim();
            }
            // Now read the entire file, keep an eye open for the lowest intensity,
            // highest intensity, low mass and high mass.
            double lowMass = Double.MAX_VALUE;
            double highMass = Double.MIN_VALUE;
            double lowIntensity = Double.MAX_VALUE;
            double highIntensity = Double.MIN_VALUE;
            // Cycle file.
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("")) {
                    continue;
                }
                StringTokenizer peakLine = new StringTokenizer(line, " ");
                double mz = new BigDecimal(peakLine.nextToken().trim()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                double intensity = new BigDecimal(peakLine.nextToken().trim()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                if (mz > highMass) {
                    highMass = mz;
                }
                if (mz < lowMass) {
                    lowMass = mz;
                }
                if (intensity > highIntensity) {
                    highIntensity = intensity;
                }
                if (intensity < lowIntensity) {
                    lowIntensity = intensity;
                }
                this.iPeaks.put(new Double(mz), new Double(intensity));
            }
            br.close();

            this.iLowMass = lowMass;
            this.iHighMass = highMass;
            this.iLowIntensity = lowIntensity;
            this.iHighIntensity = highIntensity;
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
        }
    }
}
