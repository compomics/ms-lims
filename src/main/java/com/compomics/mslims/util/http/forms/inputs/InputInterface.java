/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jul-02
 * Time: 15:50:28
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.compomics.mslims.util.http.forms.inputs;

import org.apache.log4j.Logger;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This interface encompasses the contract for any input that can be located on an HTTP form. <br /> <b>Please note</b>
 * that the individual implementations often will need to be accessed directly in order to construct and initialize them
 * correctly. <br /> But this should only be the issue of somebody writing a new parser or a new implementation of this
 * interface. <br /> <i>A user of this interface, who has been given an instance from a parser of form should be able to
 * cope with what's presented here.</i>
 */
public interface InputInterface {

    /**
     * Can be checked to see if type of implementation is Checkbox.
     */
    int CHECKBOX = 1;

    /**
     * Can be checked to see if type of implementation is RadioButton.
     */
    int RADIOINPUT = 2;

    /**
     * Can be checked to see if type of implementation is a kind of list (dropdown or full).
     */
    int SELECTINPUT = 3;

    /**
     * Can be checked to see if type of implementation is TextField (including hidden and file fields).
     */
    int TEXTFIELDINPUT = 4;

    /**
     * This method will return an int, which is an element of the public static final variables in this class.
     *
     * @return int which can be matched against the variables in this interface to find the type of the implementation.
     */
    int getType();

    /**
     * The name of an input is used in the construction of the URL parameters.
     *
     * @return String  the name of this particular input field.
     */
    String getName();

    /**
     * The name of an input is used in the construction of the URL parameters.
     *
     * @param aName the String with the name for this particular input field.
     */
    void setName(String aName);

    /**
     * The value of the inputfield is combined with its name to yield the final submit URL.
     *
     * @return String  with the value for this particular input field.
     */
    String getValue();

    /**
     * This method allows for programmatic setting of the value. <br /> Note that calling this method will result in the
     * discarding of previously entered values and will disable user input from now on!
     *
     * @param aValue the String with the value that has to be set.
     */
    void setValue(String aValue);

    /**
     * The comment for an input field often contains an elucidation for the user, which is more verbose than the name,
     * which is machine readable.
     *
     * @return String  with the comment for this particular input field.
     */
    String getComment();

    /**
     * The comment for an input field often contains an elucidation for the user, which is more verbose than the name,
     * which is machine readable.
     *
     * @param aComment the String with the comment for this particular input field.
     */
    void setComment(String aComment);

    /**
     * This method will return the contents of this input in a HTTP post parameter style.
     *
     * @param BOUNDARY the delimiter used in the post String.
     * @return String  with the name and value of this input, structured in a HHTP post format.
     */
    String getHTTPPostString(final String BOUNDARY);
}
