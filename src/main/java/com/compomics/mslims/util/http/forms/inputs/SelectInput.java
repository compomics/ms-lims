/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jul-02
 * Time: 15:09:36
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.compomics.mslims.util.http.forms.inputs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This class encapsulates the behaviour of a command-line interfaced list input. <br />
 * It works for true lists as well as for dropdownlists (multiple selects vs. single select).
 */
public class SelectInput extends AbstractInput {

    /**
     * This variable marks the difference between a true list (multiple == true) and
     * a dropdown list (multiple == false).
     */
    private boolean multiple = false;

    /**
     * The elements that compose the choices for this list.
     */
    private Vector elements = new Vector();

    /**
     * This constructor allows the setting of the name, comment, elements,
     * type and default selection of this list.
     *
     * @param   aName   String which holds the name for this list.
     * @param   aComment   String which holds the comment for this list.
     * @param   aElements   Vector with Strings that compose the choices in the list.
     * @param   aMultiple   boolean that indicates whether this is a true list
     *                      (multiple == true), or a dropdownlist (multiple == false).
     * @param   aDefault   String which holds the default choice for this list.
     */
    public SelectInput(String aName, String aComment, Vector aElements, boolean aMultiple, String aDefault) {
        name = aName;
        comment = aComment;
        elements = aElements;
        multiple = aMultiple;
        value = aDefault;
    }

    /**
     * This constructor allows the setting of the name, comment and elements,
     * of this list.
     *
     * @param   aName   String which holds the name for this list.
     * @param   aComment   String which holds the comment for this list.
     * @param   aElements   Vector with Strings that compose the choices in the list.
     */
    public SelectInput(String aName, String aComment, Vector aElements) {
        this(aName, aComment, aElements, false, null);
    }

    /**
     * This constructor allows the setting of the name of this list.
     *
     * @param   aName   String which holds the name for this list.
     */
    public SelectInput(String aName) {
        this.name = aName;
    }

    /**
     * This method sets the default on this list. <br />
     * <b>Note</b> that there is no check on whether the default is an
     * element of the choices, so this is up to the caller to make sure. <br />
     * Failure to comply will result in a submission String that is potentially wrong!
     *
     * @param   aDefault    String with the default
     */
    public void setDefault(String aDefault) {
        value = aDefault;
    }

    public String getValue() {
        if(!valueConfirmed) {
            String choice = null;
            // This command-line input can become silent for input redirection reasons.
            if(!silent) {
                int liSize = elements.size();
                System.out.print("In selection '" + comment + "', please select ");
                System.out.print((multiple?"all that apply (comma separated list)":"one from the list."));
                System.out.println((value != null?("(Default is '" + value + "')?"):"?"));
                // Print list.
                for(int i=0;i<liSize;i++) {
                    System.out.println(" - '" + elements.elementAt(i) + "'");
                }
            }
            try {
                // Reader is cached in abstract ancestor for input redirection reasons.
                if(bReader == null) {
                    bReader = new BufferedReader(new InputStreamReader(System.in));
                }
                choice = bReader.readLine();
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
            if(!("".equals(choice))) {
                value = choice;
            }
            valueConfirmed = true;
        }
        return value;
    }

    /**
     * This method reports on the available elements for this list.
     *
     * @return  String[]    with all the available elements.
     */
    public String[] getElements() {
        String[] returnStrings = new String[elements.size()];
        elements.toArray(returnStrings);
        return returnStrings;
    }

    /**
     * This method allows the caller to set the elements on this list.
     *
     * @param   aElements   vector with Strings, which will be the elements
     *                      in the list.
     */
    public void setElements(Vector aElements) {
        elements = aElements;
    }

    /**
     * This method allows the caller to add an element to the list.
     *
     * @param   aElement    String with the value that will be added to the list.
     */
    public void addElement(String aElement) {
        elements.addElement(aElement);
    }

    /**
     * This method reports on the type of the list.
     *
     * @return  boolean which is true for a true list, and false for a dropdown list.
     */
    public boolean getMultiple() {
        return multiple;
    }

    /**
     * This method allows the caller to set the type of this list.
     *
     * @param   aMultiple   boolean that is true for a true list, false for
     *                      a dropdown list.
     */
    public void setMultiple(boolean aMultiple) {
        multiple = aMultiple;
    }

    /**
     * Returns a String representation of this list input.
     *
     * @return  String  with the String representation of this list input.
     */
    public String toString() {
        StringBuffer lSB = new StringBuffer("This selection: '" + comment + "' and name: '" + name + "'.\n");
        int liSize = elements.size();
        for(int i=0;i<liSize;i++) {
            lSB.append(" - '" + elements.elementAt(i) + "'\n");
        }

        if(value == null) {
            lSB.append("No value currently selected.\n");
        } else {
            lSB.append("With current selected value: '" + value + "'.\n");
        }
        return lSB.toString();
    }

    public int getType() {
        return SELECTINPUT;
    }

    public String getHTTPPostString(final String BOUNDARY) {
        StringBuffer lBuf = new StringBuffer();
        // Depending on multiple or non-multiple, flow is different here!!
        // A multiple-selection list will print a header for each selection element!
        if(this.getMultiple()) {
            // Values are comma-separated list.
            String values = this.getValue();
            if(values == null || values.trim().equals("")) {
                return "";
            }
            StringTokenizer st = new StringTokenizer(values, ",");
            while(st.hasMoreTokens()) {
                lBuf.append("--"+BOUNDARY+"\n");
                lBuf.append("Content-Disposition: form-data; name=\"" + this.getName() + "\"\n");
                lBuf.append("\n");
                lBuf.append(st.nextToken().trim() + "\n");
            }
        } else {
            // Just one.
            lBuf.append("--"+BOUNDARY+"\n");
            lBuf.append("Content-Disposition: form-data; name=\"" + this.getName() + "\"\n");
            lBuf.append("\n");
            lBuf.append(this.getValue() + "\n");
        }
        return lBuf.toString();
    }
}