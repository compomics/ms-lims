/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jul-02
 * Time: 15:25:55
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.compomics.mslims.util.http.forms.inputs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This class encapsulates the behaviour of a command-line interfaced textfield input.
 */
public class TextFieldInput extends AbstractInput {

    /**
     * This boolean indicates whether the textfield is hidden.
     */
    private boolean hidden = false;

    /**
     * This boolean indicates whether the textfield is a file textfield.
     */
    private boolean file = false;

    /**
     * This constructor creates a new textfield with the specified name.
     *
     * @param   aName   String with the name for this textfield.
     */
    public TextFieldInput(String aName) {
        this(aName, null, null, false);
    }

    /**
     * This constructor creates a new textfield with the specified name
     * and comment.
     *
     * @param   aName   String with the name for this textfield.
     * @param   aComment   String with the comment for this textfield.
     */
    public TextFieldInput(String aName, String aComment) {
        this(aName, aComment, null, false);
    }

    /**
     * This constructor creates a new textfield with the specified name,
     * comment and default value.
     *
     * @param   aName   String with the name for this textfield.
     * @param   aComment   String with the comment for this textfield.
     * @param   aDefault   String with the default value for this textfield.
     */
    public TextFieldInput(String aName, String aComment, String aDefault) {
        this(aName, aComment, aDefault, false);
    }

    /**
     * This constructor creates a new textfield with the specified name and
     * default value and allows to set it hidden.
     *
     * @param   aName   String with the name for this textfield.
     * @param   aDefault   String with the default for this textfield.
     * @param   aHidden   boolean that indicates whether this field is hidden.
     */
    public TextFieldInput(String aName, String aDefault, boolean aHidden) {
        this(aName, null, aDefault, aHidden);
    }

    /**
     * This constructor creates a new textfield with the specified name,
     * comment, default value and allows to set it hidden.
     *
     * @param   aName   String with the name for this textfield.
     * @param   aComment   String with the comment for this textfield.
     * @param   aDefault   String with the default for this textfield.
     * @param   aHidden   boolean that indicates whether this field is hidden.
     */
    public TextFieldInput(String aName, String aComment, String aDefault, boolean aHidden) {
        name = aName;
        comment = aComment;
        value = aDefault;
        hidden = aHidden;
    }

    public String getValue() {
        String toReturn = null;
        if(!valueConfirmed) {
            if(this.isHidden()) {
                toReturn = value;
            } else {
                try {
                    // Reader is cached in abstract ancestor for input redirection reasons.
                    if(bReader == null) {
                        bReader = new BufferedReader(new InputStreamReader(System.in));
                    }
                    // This command-line input can become silent for input redirection reasons.
                    if(!silent) {
                        System.out.print("Value for textfield '" + comment + "', please");
                        System.out.println((value != null?(" (default is '" + value + "')?"):"?"));
                    }
                    toReturn = bReader.readLine();
                    if( (value != null) && ("".equals(toReturn)) ) {
                        toReturn = value;
                    }
                    value = toReturn;
            } catch(IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            valueConfirmed = true;
        } else {
            toReturn = value;
        }
        return toReturn;
    }

    /**
     * This method returns whether this textfield input is hidden.
     *
     * @return  boolean true for hidden textfields, false otherwise.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * This method allows the caller to indicate whether this textfield
     * should be hidden. <br />
     * <b>Please note</b> that a hidden textfield will not prompt the user
     * for input!
     *
     * @param   aHidden boolean that is true for a hidden textfield, false
     *                          otherwise.
     */
    public void setHidden(boolean aHidden) {
        hidden = aHidden;
    }

    /**
     * Returns a String representation of this textfield input.
     *
     * @return  String  with the String representation of this textfield input.
     */
    public String toString() {
        StringBuffer lSB = new StringBuffer("This TextField: '" + comment + "' and name: '" + name + "'.\n");
        lSB.append("This field is ");
        lSB.append((hidden?"":"not "));
        lSB.append("hidden.\n");
        if(value == null) {
            lSB.append("No value is currently entered.\n");
        } else {
            lSB.append("The field currently holds the value '" + value + "'.\n");
        }
        return lSB.toString();
    }

    /**
     * This method allows the caller to set the default value on the input without
     * side-effects. <br />
     * <b>Note</b> that this value should in any case be confirmed by the user.
     *
     * @param   aDefault    String with the default for this textfield.
     */
    public void setDefaultValue(String aDefault) {
        value = aDefault;
    }

    public int getType() {
        return TEXTFIELDINPUT;
    }

    /**
     * This method returns whether this textfield is a file input field.
     *
     * @return  boolean true for a file input field, false otherwise.
     */
    public boolean isFile() {
        return this.file;
    }

    /**
     * This method allows the user to indicate whether this textfield should
     * be a file input field.
     *
     * @param   abFile  boolean that is true for file input fields, false otherwise.
     */
    public void setFile(boolean abFile) {
        this.file = abFile;
    }

    /**
     * This method will use parameter filename to retrieve the file and transfer the
     * contents to the HTTP request. <br />
     * <b>Please note</b> that it only supports textfiles for the time being!
     *
     * @param   aFilename   the textfile to be read and whose contents should
     *                      be returned.
     * @return  String  with the contents of the textfile.
     * @exception   IOException when the file cannot be read.
     */
    private String processFile(String aFilename) throws IOException {
        StringBuffer result = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(aFilename)));
        String line = null;
        while((line = br.readLine()) != null) {
            result.append(line + "\n");
        }
        return result.toString();
    }

    /**
     * This method will report on the HTTP POST header for this filefield.
     *
     * @param   asFileName  the filename of the file that will be submitted.
     * @return  String  with the HTTP POST header for this filefield.
     */
    private String getNameForPostHeader(String asFileName) {
        StringBuffer result = new StringBuffer("name=\"" + this.name + "\"");

        // If this is a file, we'll need some extra stuff.
        // Namely: the extra filename and a new line with the
        // extra header info:
        // 'Content-Type: application/octet-stream'.
        if(this.file) {
            result.append("; filename=\"" + asFileName + "\"\n");
            result.append("Content-Type: application/octet-stream");
        }
        return result.toString();
    }

    public String getHTTPPostString(final String BOUNDARY) {
        StringBuffer lBuf = new StringBuffer("--"+BOUNDARY+"\n");
        //lBuf.append("Content-Disposition: form-data; name=\"" + iiField.getName() + "\"\n");
        // PROBLEM HERE!!!!!
        String lsTemp = this.getValue();
        lBuf.append("Content-Disposition: form-data; " + this.getNameForPostHeader(lsTemp) + "\n");
        lBuf.append("\n");
        if(this.file) {
            try {
                String temp = processFile(lsTemp);
                lBuf.append(temp);
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            lBuf.append(lsTemp + "\n");
        }

        return lBuf.toString();
    }
}