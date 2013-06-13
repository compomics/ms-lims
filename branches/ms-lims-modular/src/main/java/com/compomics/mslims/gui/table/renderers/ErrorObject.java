/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 24-nov-2004
 * Time: 12:29:47
 */
package com.compomics.mslims.gui.table.renderers;

import org.apache.log4j.Logger;

import com.compomics.util.interfaces.TableValueWrapper;

import java.awt.*;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2005/02/02 10:37:43 $
 */

/**
 * This class provides a wrapper around an Object that should be displayed in an error cell renderer.
 *
 * @author Lennart Martens
 * @version $Id: ErrorObject.java,v 1.2 2005/02/02 10:37:43 lennart Exp $
 */
public class ErrorObject implements TableValueWrapper {
    // Class specific log4j logger for ErrorObject instances.
    private static Logger logger = Logger.getLogger(ErrorObject.class);

    /**
     * The value to display on the cell.
     */
    private Object iValue = null;

    /**
     * The message (tooltip text) to display.
     */
    private String iMessage = null;

    /**
     * The color for the cell foreground (text).
     */
    private Color iForeground = null;

    /**
     * The color for the cell background (text).
     */
    private Color iBackGround = null;

    /**
     * This constructor takes the Object to display on the table.
     *
     * @param aValue Object with the value to display on the table.
     */
    public ErrorObject(Object aValue, String aMessage, Color aForeground, Color aBackground) {
        this.iValue = aValue;
        this.iMessage = aMessage;
        this.iForeground = aForeground;
        this.iBackGround = aBackground;
    }

    /**
     * This method returns the String value on this object.
     *
     * @return String with the value for this object.
     */
    public String toString() {
        return "" + this.iValue;
    }

    public Color getBackGround() {
        return iBackGround;
    }

    public Color getForeground() {
        return iForeground;
    }

    public String getMessage() {
        return iMessage;
    }

    public Object getValue() {
        return iValue;
    }
}
