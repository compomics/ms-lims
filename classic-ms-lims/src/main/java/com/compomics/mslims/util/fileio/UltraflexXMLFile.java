/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 9-feb-2004
 * Time: 18:01:22
 */
package com.compomics.mslims.util.fileio;

import com.compomics.mslims.util.workers.LoadUltraflexXMLWorker;
import org.apache.log4j.Logger;

import com.compomics.mslims.util.mascot.MascotIdentifiedSpectrum;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.*;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2004/06/30 08:46:23 $
 */

/**
 * This class represents a Bruker Ultraflex XML spectrum file.
 *
 * @author Lennart Martens
 * @version $Id: UltraflexXMLFile.java,v 1.4 2004/06/30 08:46:23 lennart Exp $
 */
public class UltraflexXMLFile extends SpectrumFileAncestor {
    // Class specific log4j logger for UltraflexXMLFile instances.
    private static Logger logger = Logger.getLogger(UltraflexXMLFile.class);

    /**
     * The Factory for the XmlPullParser. Note that it is static.
     */
    private static XmlPullParserFactory iFactory = null;

    /**
     * This constructor creates an in-memory representation of the specified Ultraflex XML file.
     *
     * @param aFilename String with the filename for the file.
     * @throws IOException whenever the XML file could not be read.
     */
    public UltraflexXMLFile(String aFilename) throws IOException {
        this(new File(aFilename));
    }

    /**
     * This constructor creates an in-memory representation of the specified Ultraflex XML file.
     *
     * @param aFile File with the XML file.
     * @throws IOException whenever the XML file could not be read.
     */
    public UltraflexXMLFile(File aFile) throws IOException {
        if (iFactory == null) {
            try {
                iFactory = XmlPullParserFactory.newInstance(System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
                iFactory.setNamespaceAware(true);
            } catch (XmlPullParserException xppe) {
                logger.error(xppe.getMessage(), xppe);
                throw new IOException("Unable to create XMLPullParserFactory: " + xppe.getMessage() + "!");
            }
        }
        if (!aFile.exists()) {
            throw new IOException("XML file '" + aFile.getCanonicalPath() + "' was not found!");
        } else {
            StringBuffer lsb = new StringBuffer();
            BufferedReader br = new BufferedReader(new FileReader(aFile));
            String line = null;
            while ((line = br.readLine()) != null) {
                lsb.append(line + "\n");
            }
            br.close();
            this.parseFromString(lsb.toString());
            // Get the precursor peak mass from the pathname.
            // It is located in the folder called '*.LIFT.LIFT',
            // where '*' is the mass. Note that the mass will
            // contain a decimal point.
            this.iPrecursorMz = this.extractPrecursorMZFromPath(aFile);
            // Charge for MALDI systems is '+1'.
            this.iCharge = 1;
            // Set the intensity arbitrarily to '1.0'.
            this.iIntensity = 1.0;

            this.iFilename = aFile.getName();
        }
    }

    /**
     * Compares this object with the specified object for order.  Returns a negative integer, zero, or a positive
     * integer as this object is less than, equal to, or greater than the specified object.<p>
     * <p/>
     * In the foregoing description, the notation <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>, <tt>0</tt>, or <tt>1</tt> according to
     * whether the value of <i>expression</i> is negative, zero or positive.
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

        UltraflexXMLFile file = (UltraflexXMLFile) o;
        // See if the run numbers differ.
        double delta = this.iPrecursorMz - file.iPrecursorMz;
        if (delta < 0.0) {
            compared = -1;
        } else if (delta > 0.0) {
            compared = 1;
        }

        return compared;
    }

    /**
     * This method checks whether the MascotIdentifiedSpectrum corresponds to this spectrum. The precise method for
     * comparison is up to the individual implementations.
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
     * This method allows the caller to write the spectrum file to the specified folder using its current filename.
     *
     * @param aParentDir File with the parent directory to put the file in.
     * @throws IOException whenever the write process failed.
     */
    public void writeToFile(File aParentDir) throws IOException {
        if (!aParentDir.exists() && !aParentDir.isDirectory()) {
            throw new IOException("Parent '" + aParentDir.getCanonicalPath() + "' does not exist or is not a directory!");
        }
        File output = new File(aParentDir, this.iFilename);
        FileOutputStream fos = new FileOutputStream(output);
        this.writeToStream(fos);
        fos.flush();
        fos.close();
    }

    /**
     * This method allows to write the spectrum file to the specified OutputStream.
     *
     * @param aOut OutputStream to write the file to. This Stream will <b>NOT</b> be closed by this method.
     * @throws IOException when the write operation fails.
     */
    public void writeToStream(OutputStream aOut) throws IOException {
        String content = this.getMGFFormat();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(aOut));
        bw.write(content);
        bw.flush();
        bw.close();
    }

    /**
     * This method returns this peaklist in MGF format.
     *
     * @return String with the MGF format for this spectrum.
     */
    public String getMGFFormat() {
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
     * This method parses the XML file from the content.
     *
     * @param aContents String with the XML file contents.
     * @throws IOException whenever the XML format could not be parsed.
     */
    private void parseFromString(String aContents) throws IOException {
        try {
            StringReader reader = new StringReader(aContents);
            XmlPullParser xpp = iFactory.newPullParser();
            xpp.setInput(reader);
            int eventType = xpp.getEventType();
            boolean validated = false;
            Double lastKey = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        eventType = xpp.next();
                        break;
                    case XmlPullParser.START_TAG:
                        String start = xpp.getName();
                        if (start.equals("pklist")) {
                            validated = true;
                            eventType = xpp.next();
                        } else if (start.equals("pk")) {
                            lastKey = processPeak(xpp);
                            eventType = xpp.getEventType();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String name = xpp.getName();
                        if (name.equals("pklist")) {
                            // Precursor mass is taken from the pathname now,
                            // and this is done in the constructor.
                            /*
                            // We're through.
                            // Delete the last key from the peaklist
                            // since this is the precursor, and
                            // init it as such.
                            if(lastKey != null) {
                                Double intensity = (Double)this.iPeaks.remove(lastKey);
                                // Always M+H for MALDI.
                                this.iCharge = 1;
                                this.iIntensity = intensity.doubleValue();
                                this.iPrecursorMz = lastKey.doubleValue();
                            }
                            */
                        }
                        eventType = xpp.next();
                        if (eventType == XmlPullParser.END_DOCUMENT) {
                        }
                        break;
                    case XmlPullParser.TEXT:
                        eventType = xpp.next();
                        break;
                    default:
                        eventType = xpp.next();
                        break;
                }
            }
            if (!validated) {
                throw new IOException("No root tag '<pklist>' found in the XML document!");
            }
        } catch (XmlPullParserException xppe) {
            throw new IOException(xppe.getMessage());
        }
    }

    /**
     * This method reads the data for a single peak.
     *
     * @param aXpp XmlPullParser to read the data from.
     * @return Double   with the last key that was added to the peak list. This peak is the precursor.
     * @throws IOException            when the XML parsing failed.
     * @throws XmlPullParserException when the XML pull parser encountered an error.
     */
    private Double processPeak(XmlPullParser aXpp) throws IOException, XmlPullParserException {
        int eventType = aXpp.next();
        boolean lbContinue = true;
        double mass = 0.0;
        double intensity = 0.0;
        while (lbContinue) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String start = aXpp.getName();
                    if (start.equals("absi")) {
                        intensity = this.getIntensity(aXpp);
                    } else if (start.equals("mass")) {
                        mass = this.getMass(aXpp);
                    }
                    eventType = aXpp.next();
                    break;
                case XmlPullParser.END_TAG:
                    if (aXpp.getName().equals("pk")) {
                        lbContinue = false;
                    }
                    eventType = aXpp.next();
                    break;
                default:
                    eventType = aXpp.next();
                    break;
            }
        }
        // Add the peak to the peaklist.
        this.iPeaks.put(new Double(mass), new Double(intensity));
        // Return the key.
        return new Double(mass);
    }

    /**
     * This method parses the intensity in the next tag.
     *
     * @param aXpp XMLPullParser to read the next tag and its value from.
     * @return double with the intensity of this peak.
     * @throws IOException            whenever the XML parsing failed.
     * @throws XmlPullParserException whenever the XML parsing failed.
     */
    private double getIntensity(XmlPullParser aXpp) throws IOException, XmlPullParserException {
        double result = 0.0;
        int eventType = aXpp.next();
        if (eventType == XmlPullParser.TEXT) {
            result = Double.parseDouble(aXpp.getText().trim());
        } else {
            throw new IOException("Expected text value after <absi> tag, but didn't find it!");
        }
        return result;
    }

    /**
     * This method parses the mass (M+H) in the next tag.
     *
     * @param aXpp XMLPullParser to read the next tag and its value from.
     * @return double with the mass (M+H) of this peak.
     * @throws IOException            whenever the XML parsing failed.
     * @throws XmlPullParserException whenever the XML parsing failed.
     */
    private double getMass(XmlPullParser aXpp) throws IOException, XmlPullParserException {
        double result = 0.0;
        int eventType = aXpp.next();
        if (eventType == XmlPullParser.TEXT) {
            result = Double.parseDouble(aXpp.getText().trim());
        } else {
            throw new IOException("Expected text value after <mass> tag, but didn't find it!");
        }
        return result;
    }

    /**
     * This method extracts the precursor mass from the pathname, based on the fact that the foldername with the mass
     * ends with '.LIFT.LIFT'.
     *
     * @param aFile File with the link to the original xml file.
     * @return double with the precursor M/Z
     * @throws IOException when the pathname could not be parsed.
     */
    private double extractPrecursorMZFromPath(File aFile) throws IOException {
        double result = 0.0;
        // Find the mass...
        File temp = aFile.getParentFile();
        boolean lbContinue = true;
        while (lbContinue && temp != null) {
            // Find the '.LIFT.*' signature.
            if(LoadUltraflexXMLWorker.isLiftFolder(temp)){
                String mass = temp.getName().toLowerCase().substring(0, temp.getName().toLowerCase().indexOf(".lift"));
                try {
                    result = Double.parseDouble(mass);
                    lbContinue = false;
                } catch (NumberFormatException nfe) {
                    throw new IOException("Unable to parse precursor mass from folder '" + temp.getName() + "' in file path " + aFile.getCanonicalPath() + "!");
                }
            } else {
                temp = temp.getParentFile();
            }
        }
        return result;
    }
}
