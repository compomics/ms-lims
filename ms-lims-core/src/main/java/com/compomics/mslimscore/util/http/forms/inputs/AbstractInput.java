/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 21-jul-02
 * Time: 12:32:11
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.compomics.mslimscore.util.http.forms.inputs;

import java.io.BufferedReader;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This class is the abstraction for any input corresponding to the InputInterface contract. It deals with the
 * getter-and-setter topic and provides a neat variable for checking a 'set' value versus a default initialized value
 * (this can be useful when setting a field programmatically).
 */
public abstract class AbstractInput implements InputInterface {

    /**
     * When reading from redirected input, this reader is shared among all implementations. This averts some serious
     * 'stream closed' problems.
     */
    protected static BufferedReader bReader = null;

    /**
     * For text-mode (command-line) inputs, setting this boolean to true stops them from outputting their info and just
     * read a value. <br /> Comes in handy when input redirection to a file is used.
     */
    public static boolean silent = false;

    /**
     * This boolean allows to differentiate between a confirmed value (either directly by the user in a normal
     * input-sequence) or programmatically by a caller of the 'setValue()') and a default one (which has to be confirmed
     * and not blindly submitted, of course). <br /> <b>Please note that it is not a good idea to call 'setValue()' to
     * initialize a default value, as the user will no longer be prompted!!
     */
    protected boolean valueConfirmed = false;

    /**
     * The name of the input. <br /> This name is in fact the KEY for the POST submit!!
     */
    protected String name = null;

    /**
     * The comment is what the user will want to know about the input, such as what does it do and why do I want to set
     * anything. Consider it the label. <br /> RadioButtons do not have regular comments!
     */
    protected String comment = null;

    /**
     * The value is what the user (or the caller of the 'setValue()' method) has submitted to the input.
     */
    protected String value = null;

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String aComment) {
        comment = aComment;
    }

    public void setValue(String aValue) {
        value = aValue;
        valueConfirmed = true;
    }
}
