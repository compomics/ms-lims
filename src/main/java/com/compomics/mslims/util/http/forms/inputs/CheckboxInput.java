/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 19-jul-02
 * Time: 10:56:45
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.compomics.mslims.util.http.forms.inputs;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This class encapsulates the behaviour of a command-line interfaced checkbox input.
 */
public class CheckboxInput extends AbstractInput {
    // Class specific log4j logger for CheckboxInput instances.
    private static Logger logger = Logger.getLogger(CheckboxInput.class);

    /**
     * The state for a checkbox can be represented by a single boolean: either on, or off.
     */
    private boolean ibChecked = false;

    /**
     * This constructor initializes the checkbox with a name and a value. <br /> The value will be submitted if the
     * checkbox state is 'true'; nothing will happen when the state is 'false' (the input is mute).
     *
     * @param aName  this String represents the name of the input.
     * @param aValue this String represents the value of the input.
     */
    public CheckboxInput(String aName, String aValue) {
        this(aName, aValue, null);
    }

    /**
     * This constructor initializes the checkbox with a name, a value and a comment. <br /> It is advised to use this
     * constructor as the comment will allow you to tell the user why this checkbox is here and what the value will do.
     * <br /> The value will be submitted if the checkbox state is 'true'; nothing will happen when the state is 'false'
     * (the input is mute).
     *
     * @param aName    this String represents the name of the input.
     * @param aValue   this String represents the value of the input.
     * @param aComment this String represents the comment for the input.
     */
    public CheckboxInput(String aName, String aValue, String aComment) {
        this(aName, aValue, aComment, false);
    }

    /**
     * This constructor initializes the checkbox with a name, a value, a comment and set the state simultaneously. <br
     * /> The value will be submitted if the checkbox state is 'true'; nothing will happen when the state is 'false'
     * (the input is mute).
     *
     * @param aName     this String represents the name of the input.
     * @param aValue    this String represents the value of the input.
     * @param aComment  this String represents the comment for the input.
     * @param abChecked a boolean that represents the state of this input.
     */
    public CheckboxInput(String aName, String aValue, String aComment, boolean abChecked) {
        name = aName;
        value = aValue;
        comment = aComment;
        ibChecked = abChecked;
    }

    public String getValue() {
        if (!valueConfirmed) {
            try {
                // Reader is cached in abstract ancestor for input redirection reasons.
                if (bReader == null) {
                    bReader = new BufferedReader(new InputStreamReader(System.in));
                }
                // This command-line input can become silent for input redirection reasons.
                if (!silent) {
                    logger.info("Do you wish to check checkbox '" + comment + "' (default is '");
                    logger.info((ibChecked ? ("yes')?") : "no')?"));
                }
                String input = bReader.readLine();
                if (input.trim().equals("")) {
                    // Do nothing, as the user requested the default.
                } else ibChecked = input.trim().equalsIgnoreCase("yes") || input.trim().equalsIgnoreCase("y");
            } catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
            }
            valueConfirmed = true;
        }
        if (ibChecked) {
            return value;
        } else {
            return null;
        }
    }

    /**
     * This method reports on the state of this checkbox input.
     *
     * @return boolean that indicates whether this checkbox is checked.
     */
    public boolean isChecked() {
        return ibChecked;
    }

    /**
     * This method allows the caller to set the state of this checkbox without any side effects. It basically sets the
     * default.
     *
     * @param abChecked boolean to idicate whether this checkbox input should be checked by default.
     */
    public void setChecked(boolean abChecked) {
        ibChecked = abChecked;
    }

    /**
     * Returns a String representation of this checkbox input.
     *
     * @return String  with the String representation of this checkbox input.
     */
    public String toString() {
        StringBuffer lSB = new StringBuffer("This Checkbox: '" + comment + "' and name: '" + name + "'.\n");
        if (ibChecked) {
            lSB.append("Checkbox is selected!\n");
        } else {
            lSB.append("Checkbox is NOT selected.\n");
        }
        return lSB.toString();
    }

    public int getType() {
        return CHECKBOX;
    }

    public String getHTTPPostString(final String BOUNDARY) {
        String toReturn = "";

        // In a checkbox, you either give the value, or you give nothing at all.
        // Meaning: not even a header!!
        if (this.getValue() != null) {
            StringBuffer lBuf = new StringBuffer("--" + BOUNDARY + "\n");
            lBuf.append("Content-Disposition: form-data; name=\"" + this.getName() + "\"\n");
            lBuf.append("\n");
            lBuf.append(this.getValue() + "\n");
            toReturn = lBuf.toString();
        }

        return toReturn;
    }
}
