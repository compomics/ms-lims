/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 25-feb-03
 * Time: 12:54:28
 */
package com.compomics.mslims.util.fileio;

import com.compomics.mslims.util.fileio.UltraflexXMLFile;
import junit.TestCaseLM;
import junit.framework.Assert;

import java.io.*;
import java.util.Arrays;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This class implements the test for the UltraflexXMLFile class.
 *
 * @author Lennart Martens
 * @see com.compomics.mslims.util.fileio.UltraflexXMLFile
 */
public class TestUltraflexXMLFile extends TestCaseLM {

    public TestUltraflexXMLFile() {
        this("Test for the UltraflexXMLFile class");
    }

    public TestUltraflexXMLFile(String aName) {
        super(aName);
    }

    /**
     * This method tests the error signalling when attempting to creation an Ultraflex XML
     * instance based on an aberrant file.
     */
    public void testAberrantCreation() {
        try {
            UltraflexXMLFile xml = new UltraflexXMLFile(super.getFullFilePath("testWrongPeaklist1.xml"));
            fail("No IOException thrown when attempting to create an UltraflexXMLFile based on a file without a <pklist> tag.");
        } catch(IOException ioe) {
            // This is okay.
        }
    }

    /**
     * This method test the implementation of comparable by
     * the UltraflexXMLFile class.
     */
    public void testOrdering() {
        try {
            // Read all five instances.
            UltraflexXMLFile[] xmls = new UltraflexXMLFile[5];
            for (int i = 0; i < xmls.length; i++) {
                xmls[i] = new UltraflexXMLFile(super.getFullFilePath("testPeaklist" + (i+1) + ".xml"));
                xmls[i].setPrecursorMZ(i+1024.56);
            }
            Arrays.sort(xmls);
            for (int i = 0; i < xmls.length; i++) {
                UltraflexXMLFile lXml = xmls[i];
                if(i > 0) {
                    Assert.assertTrue(lXml.getPrecursorMZ() > xmls[i-1].getPrecursorMZ());
                }
                if(i < (xmls.length-1)) {
                    Assert.assertTrue(lXml.getPrecursorMZ() < xmls[i+1].getPrecursorMZ());
                }
            }
        } catch(IOException ioe) {
            fail("Unable to test comparable implementation for UltraflexXMLFile due to IOException: " + ioe.getMessage() + "!");
        }
    }

    /**
     * This method test the writing of a PKL file to a file.
     */
    public void testWriting() {
        File input = new File(super.getFullFilePath("testPeaklist2.xml"));
        // Create a new folder to have a 'mass.LIFT.LIFT' name.
        File tempFolder = new File(input.getParentFile(), "1552.6900.LIFT.LIFT/");
        tempFolder.mkdir();
        tempFolder.deleteOnExit();
        File input2 = new File(tempFolder, "peaklist.xml");
        // Create the temp file and fill it with teh data form the original file.
        try {
            if(!input2.exists()) {
                input2.createNewFile();
            }
            input2.deleteOnExit();
            BufferedReader in = new BufferedReader(new FileReader(input));
            BufferedWriter out = new BufferedWriter(new FileWriter(input2));
            String line = null;
            while((line = in.readLine()) != null) {
                out.write(line + "\n");
            }
            out.flush();
            out.close();
            in.close();
        } catch(IOException ioe) {
            fail("Unable to create test file '" + input2.getAbsolutePath() + "' for testing reading/writing of UltraflexXMLFile: " + ioe.getMessage() + "!");
        }

        // First test write with the filename.
        try {
            // Reading original.
            UltraflexXMLFile file = new UltraflexXMLFile(input2);
            // Writing to outputstream.
            File output = new File(input.getParentFile(), "temp" + file.getFilename());
            output.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(output);
            file.writeToStream(fos);
            fos.flush();
            fos.close();

            // Comparing both.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("testPeaklist2_control.mgf")));
            BufferedReader test = new BufferedReader(new FileReader(output));
            String line = null;
            while((line = control.readLine()) != null) {
                Assert.assertEquals(line, test.readLine());
            }
            Assert.assertTrue(test.readLine() == null);

        } catch(IOException ioe) {
            fail("IOException during test of writing PKLFile to outputstream: " + ioe.getMessage() + "!");
        }

        try {
            // Reading original.
            UltraflexXMLFile file = new UltraflexXMLFile(input2);
            // Writing to file.
            file.setFilename("temp" + file.getFilename());
            file.writeToFile(input.getParentFile());

            // Comparing with original.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("testPeaklist2_control.mgf")));
            BufferedReader test = new BufferedReader(new FileReader(super.getFullFilePath(file.getFilename())));
            File tempFile = new File(super.getFullFilePath(file.getFilename()));
            tempFile.deleteOnExit();
            String line = null;
            // Second line has 'temp' appended to the filename.
            line = test.readLine();
            Assert.assertEquals(control.readLine(), line);
            line = test.readLine();
            int equalsLocation = line.lastIndexOf("=");
            Assert.assertEquals(control.readLine(), line.substring(0, equalsLocation+1) + line.substring(equalsLocation+5));
            while((line = control.readLine()) != null) {
                Assert.assertEquals(line, test.readLine());
            }
            Assert.assertTrue(test.readLine() == null);
        } catch(IOException ioe) {
            fail("IOException during test of writing PKLFile to file: " + ioe.getMessage() + "!");
        }
    }
}
