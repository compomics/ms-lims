/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 4-okt-2005
 * Time: 14:39:13
 */
package com.compomics.mslims.util.fileio;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.interfaces.BrukerCompound;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;
/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2005/10/27 12:33:20 $
 */

/**
 * This class reads a Bruker (Ultraflex) compound list.
 *
 * @author Lennart Martens
 * @version $Id: BrukerCompoundListReader.java,v 1.4 2005/10/27 12:33:20 lennart Exp $
 */
public class BrukerCompoundListReader {
    // Class specific log4j logger for BrukerCompoundListReader instances.
    private static Logger logger = Logger.getLogger(BrukerCompoundListReader.class);

    /**
     * The XML Pull Parser Factory.
     */
    private static XmlPullParserFactory iFactory = null;

    /**
     * HashMap with the multiple SilePairReferences.
     */
    private HashMap iMultiplePairReferences = new HashMap();

    /**
     * HashMap with the couples.
     */
    private HashMap iCouples = new HashMap();

    /**
     * Collection with the singles.
     */
    private ArrayList iSingles = new ArrayList();

    /**
     * The number of compounds that were skipped for some reason or other.
     */
    private int iSkippedCompounds = 0;

    /**
     * Total number of compounds read.
     */
    private int iTotalCompoundsRead = 0;

    /**
     * Total number of single compounds.
     */
    private int iTotalSingles = 0;

    /**
     * Total number of compounds that were merged to pairs.
     */
    private int iTotalPairs = 0;

    // Static initializer for the XML parser factory.

    static {
        try {
            iFactory = XmlPullParserFactory.newInstance(System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
            iFactory.setNamespaceAware(true);
        } catch (XmlPullParserException xppe) {
            logger.error(xppe.getMessage(), xppe);
            System.exit(1);
        }
    }

    /**
     * Default constructor.
     */
    public BrukerCompoundListReader() {
    }

    /**
     * This constructor automatically parses the specified File.
     *
     * @param aFile File to parse.
     * @throws IOException when the parsing failed.
     */
    public BrukerCompoundListReader(File aFile) throws IOException {
        this.readList(aFile);
    }

    /**
     * This method will read a compound list (singles and couples).
     *
     * @param aInputFile File with the input data.
     * @throws IOException when the reading failed.
     */
    public void readList(File aInputFile) throws IOException {
        try {
            FileReader fr = new FileReader(aInputFile);
            XmlPullParser xpp = iFactory.newPullParser();
            xpp.setInput(fr);
            parseFile(xpp);
            fr.close();
        } catch (XmlPullParserException xppe) {
            throw new IOException("Error parsing XML file: " + xppe.getMessage());
        }
        // Start removing dirty stuff first.
        Iterator iter = iMultiplePairReferences.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            // If this key (which matches a peak that was assigned to multiple
            // couples) is found together with an assumed couple,
            // delete it.
            if (iCouples.containsKey(key)) {
                iCouples.remove(key);
                iSkippedCompounds++;
            }
        }
    }

    /**
     * This method parses the XML file.
     *
     * @param aPP XmlPullParser to read the data from.
     * @throws IOException            when the reading failed.
     * @throws XmlPullParserException when the XML parsing failed.
     */
    private void parseFile(XmlPullParser aPP) throws IOException, XmlPullParserException {
        int eventType = aPP.getEventType();
        boolean validated = false;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    eventType = aPP.next();
                    break;
                case XmlPullParser.START_TAG:
                    String start = aPP.getName();
                    if (start.equals("CompoundList")) {
                        validated = true;
                        eventType = aPP.next();
                    } else if (start.equals("Compound")) {
                        iTotalCompoundsRead++;
                        processCompound(aPP);
                        eventType = aPP.next();
                    } else {
                        eventType = aPP.next();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    eventType = aPP.next();
                    break;
                case XmlPullParser.TEXT:
                    eventType = aPP.next();
                    break;
                default:
                    eventType = aPP.next();
                    break;
            }
        }
        if (!validated) {
            throw new IOException("No root tag '<CompoundList>' found in the XML document!");
        }
    }

    /**
     * Read all the details for a compound tag.
     *
     * @param aPP XmlPullParser to read the data from.
     * @throws IOException            when the data could not be read, or the XML was deemed malformatted.
     * @throws XmlPullParserException when the parsing failed.
     */
    private void processCompound(XmlPullParser aPP) throws IOException, XmlPullParserException {
        // Area.
        String areaString = aPP.getAttributeValue(null, "RelativeArea");
        double area = Double.parseDouble(areaString);
        // Intensity
        String intensityString = aPP.getAttributeValue(null, "AbsoluteIntensity");
        double intensity = Double.parseDouble(intensityString);
        // Regulation.
        String regulationString = aPP.getAttributeValue(null, "Regulation");
        double regulation = Double.parseDouble(regulationString);
        // Correct the fact that Bruker uses heavy/light and we use light/heavy.
        regulation = 1 / regulation;
        String massString = aPP.getAttributeValue(null, "mass");
        double mass = Double.parseDouble(massString);
        // Convert from M+H to mass.
        mass -= 1.007825;
        String reference = null;
        aPP.next();
        // Try to find SilePairReferences (if any).
        int pairReferencesFound = 0;
        while (!(aPP.getEventType() == XmlPullParser.START_TAG && aPP.getName().equals("Ranking"))) {
            // Now we should have a SilePairReference element.
            int type = aPP.getEventType();
            String name = aPP.getName();
            // Check type & name.
            if (type == XmlPullParser.START_TAG && name.equals("SilePairReference")) {
                // See if there is a first reference already before we overwrite it
                // with a new one.
                if (pairReferencesFound == 1) {
                    // So the reference we are about to read is the second one of a
                    // multiple-pair matcher. We'll first need to add the first reference to
                    // the 'most-unwanted' list first.
                    iMultiplePairReferences.put(reference, "");
                }
                // Get 'SPReferenceID' attribute.
                reference = aPP.getAttributeValue(null, "SPReferenceID");
                // See if there already was a SilePairReference!
                if (pairReferencesFound > 0) {
                    // Something wicked this way comes!
                    pairReferencesFound++;
                    // Add it to the bad guys HashMap.
                    iMultiplePairReferences.put(reference, "");
                    reference = null;
                } else {
                    pairReferencesFound += 1;
                }
            }
            // Skip to next tag.
            aPP.next();
        }
        // Now we should have the 'Ranking' element.
        int type = aPP.getEventType();
        String name = aPP.getName();
        // Read the total score.
        String totalScoreString = aPP.getAttributeValue(null, "TotalScore");
        double totalScore = Double.parseDouble(totalScoreString);
        // Skip the closing tag.
        aPP.next();
        // Skip empty stuff.
        aPP.next();
        int componentCount = 0;
        // This should be the first 'CompoundComponent'.
        type = aPP.next();
        if (type != XmlPullParser.START_TAG) {
            throw new IOException("Expected starting tag <CompoundComponent>, but found something else instead at line " + aPP.getLineNumber() + "!");
        }
        name = aPP.getName();
        if (!name.equals("CompoundComponent")) {
            throw new IOException("Expected tag <CompoundComponent>, but found <" + name + "> instead at line " + aPP.getLineNumber() + "!");
        }
        // Found the first component.
        componentCount++;
        String position = aPP.getAttributeValue(null, "Pos_on_Scout");
        // Now read the s2n (signal-to-noise) for this first CompoundComponent from
        // the 'Ranking' tag.
        // Skip empty stuff.
        aPP.next();
        type = aPP.next();
        if (type != XmlPullParser.START_TAG) {
            throw new IOException("Expected starting tag <Ranking>, but found something else instead at line " + aPP.getLineNumber() + "!");
        }
        name = aPP.getName();
        if (!name.equals("Ranking")) {
            throw new IOException("Expected tag <Ranking>, but found <" + name + "> instead at line " + aPP.getLineNumber() + "!");
        }
        String s2nString = aPP.getAttributeValue(null, "s2n");
        double s2n = 0.0;
        try {
            s2n = Double.parseDouble(s2nString);
        } catch (NumberFormatException nfe) {
            throw new IOException("Expected a double with the S/N ratio but got '" + s2nString + "' at line " + aPP.getLineNumber() + "!");
        }
        // Continue reading until we find the last
        while (!(type == XmlPullParser.END_TAG && name.equals("Compound"))) {
            type = aPP.next();
            name = aPP.getName();
            if (type == XmlPullParser.START_TAG && name.equals("CompoundComponent")) {
                componentCount++;
            }
        }
        // Okay, create a new Compound.
        BrukerCompoundSingle bcs = new BrukerCompoundSingle(mass, position, regulation, area, intensity / ((double) componentCount), totalScore, s2n);
        // See if it is part of a couple.
        if (reference != null && pairReferencesFound == 1 && !iMultiplePairReferences.containsKey(reference)) {
            // See if the other guy was already found.
            if (iCouples.containsKey(reference)) {
                BrukerCompoundSingle other = (BrukerCompoundSingle) iCouples.get(reference);
                // Find out which is the heavy one.
                BrukerCompoundCouple bcc = null;
                if (other.getMass() > bcs.getMass()) {
                    bcc = new BrukerCompoundCouple(bcs, other);
                } else if (other.getMass() < bcs.getMass()) {
                    bcc = new BrukerCompoundCouple(other, bcs);
                } else {
                    throw new IOException("Found a differential couple with identical masses!");
                }
                iCouples.put(reference, bcc);
                iTotalPairs++;
            } else {
                iCouples.put(reference, bcs);
            }
        } else if (reference == null && pairReferencesFound == 0) {
            iSingles.add(bcs);
            iTotalSingles++;
        } else {
            // Skip peak.
            iSkippedCompounds++;
        }
    }

    public int getTotalCompoundsRead() {
        return iTotalCompoundsRead;
    }

    public int getTotalPairs() {
        return iTotalPairs;
    }

    public int getTotalSingles() {
        return iTotalSingles;
    }

    public int getSkippedCompounds() {
        return iSkippedCompounds;
    }

    public ArrayList getSingles() {
        return iSingles;
    }

    public HashMap getMultiplePairReferences() {
        return iMultiplePairReferences;
    }

    public HashMap getCouples() {
        return iCouples;
    }
}
