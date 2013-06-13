/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 2-aug-02
 * Time: 10:37:00
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.compomics.mslims.util.http.forms.parsers;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This is the interface for parsers that take HTML forms as their input, and generate a collection of InputInterfaces
 * implementations as output. <br /> <i>Typically, one would expect an implementation of this class to accept the source
 * HTML form as an argument to a constructor (or a factory-like static method) in the form of a String, or possibly a
 * Reader (i.e. directly linked to a URLConnection) to initialize all fields.<i>
 *
 * @author Lennart Martens
 */
public interface FormToObjectParser {
    /**
     * This method returns the complete Collection of InputInterface implementations found in the form.
     *
     * @return Colelction  with all the InputInterface implementations.
     */
    public Collection getAllInputs();

    /**
     * This method reports on the number of inputs found in the form.
     *
     * @return int the number of inputs found in the form.
     */
    public int getInputCount();

    /**
     * This method reports on the form parameters (parameters in the FORM tag) that have been found in the form.
     *
     * @return HashMap with the form parameters as key-value pairs as occurring in the FORM tag.
     */
    public HashMap getFormParams();
}
