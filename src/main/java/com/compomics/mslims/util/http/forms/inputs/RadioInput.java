/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 19-jul-02
 * Time: 10:05:29
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.compomics.mslims.util.http.forms.inputs;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This class encapsulates the behaviour of a command-line interfaced radiobutton input.
 */
public class RadioInput extends AbstractInput {
    // Class specific log4j logger for RadioInput instances.
    private static Logger logger = Logger.getLogger(RadioInput.class);

    /**
     * This variable holds all possible choices for the radiobuttongroup.
     */
    private Vector choices = new Vector();

    /**
     * This constructor initializes only a name for the group.
     *
     * @param aName String with the name for the group.
     */
    public RadioInput(String aName) {
        this(aName, (String) null);
    }

    /**
     * This constructor initializes a name for the group and a default selection.
     *
     * @param aName    String with the name for the group.
     * @param aDefault String with the default selection for the group.
     */
    public RadioInput(String aName, String aDefault) {
        name = aName;
        value = aDefault;
    }

    /**
     * This constructor initializes a name for the group and a Vector of choices.
     *
     * @param aName    String with the name for the group.
     * @param aChoices Vector with the choices for the group.
     */
    public RadioInput(String aName, Vector aChoices) {
        this(aName, null, aChoices);
    }

    /**
     * This constructor initializes a name for the group, a default selection and a Vector of choices.
     *
     * @param aName    String with the name for the group.
     * @param aDefault String with the default selection for the group.
     * @param aChoices Vector with the choices for the group.
     */
    public RadioInput(String aName, String aDefault, Vector aChoices) {
        name = aName;
        value = aDefault;
        choices = aChoices;
    }

    /**
     * This method sets the default on this group. <br /> <b>Note</b> that there is no check on whether the default is
     * an element of the choices, so this is up to the caller to make sure. <br /> Failure to comply will result in a
     * submission String that is potentially wrong!
     *
     * @param aDefault String with the default
     */
    public void setDefault(String aDefault) {
        value = aDefault;
    }

    /**
     * This method reports on the available choices for this group.
     *
     * @return String[]    with all the available choices.
     */
    public String[] getChoices() {
        String[] returnStrings = new String[choices.size()];
        choices.toArray(returnStrings);
        return returnStrings;
    }

    /**
     * This method allows the addition of a choice to this group.
     *
     * @param aChoice String value for a choice to add to the group.
     */
    public void addChoice(String aChoice) {
        choices.addElement(aChoice);
    }

    /**
     * This method allows for the adding of a choice that automatically becomes the default choice, overriding all
     * earlier settings. <br /> This default has to be verified by the user before it is set as the definitive value.
     *
     * @param aChoice String value for the choice to add to a group and set it as the default.
     */
    public void addDefaultChoice(String aChoice) {
        this.addChoice(aChoice);
        value = aChoice;
    }

    public String getValue() {
        if (!valueConfirmed) {
            String toReturn = null;

            try {
                // Reader is cached in abstract ancestor for input redirection reasons.
                if (bReader == null) {
                    bReader = new BufferedReader(new InputStreamReader(System.in));
                }
                // This command-line input can become silent for input redirection reasons.
                if (!silent) {
                    logger.info("Please select one from the following choices, please");
                    logger.info((value != null ? (" (default is '" + value + "')?") : "?"));
                    // Print all choices.
                    int liSize = choices.size();
                    for (int i = 0; i < liSize; i++) {
                        logger.info(" - '" + choices.elementAt(i) + "'");
                    }
                }
                toReturn = bReader.readLine();
                if ((value != null) && ("".equals(toReturn))) {
                    toReturn = value;
                }
            } catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
            }

            value = toReturn;
            valueConfirmed = true;
        }
        return value;
    }

    public String getComment() {
        return "Radio with name '" + name + "'.";
    }

    /**
     * Returns a String representation of this RadioButton input.
     *
     * @return String  with the String representation of this RadioButton input.
     */
    public String toString() {
        StringBuffer lSB = new StringBuffer("This radio has name: '" + name + "'.\n");
        int liSize = choices.size();
        for (int i = 0; i < liSize; i++) {
            lSB.append(" - '" + choices.elementAt(i) + "'\n");
        }

        if (value == null) {
            lSB.append("No value currently selected.\n");
        } else {
            lSB.append("With current selected value: '" + value + "'.\n");
        }
        return lSB.toString();
    }

    public int getType() {
        return RADIOINPUT;
    }

    public String getHTTPPostString(final String BOUNDARY) {
        StringBuffer lBuf = new StringBuffer("--" + BOUNDARY + "\n");
        lBuf.append("Content-Disposition: form-data; name=\"" + this.getName() + "\"\n");
        lBuf.append("\n");
        lBuf.append(this.getValue() + "\n");

        return lBuf.toString();
    }
}
