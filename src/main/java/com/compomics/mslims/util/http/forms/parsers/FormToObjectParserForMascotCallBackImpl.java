/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 16-jul-02
 * Time: 14:59:48
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.compomics.mslims.util.http.forms.parsers;

import com.compomics.mslims.util.http.forms.inputs.*;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This Class is the implementation for a FormToObjectParser specifically for Mascot
 * search forms. It uses the ParserCallBack and the ParserDelegator from Swing for
 * these purposes.
 *
 * @author  Lennart Martens
 */
public class FormToObjectParserForMascotCallBackImpl extends HTMLEditorKit.ParserCallback implements FormToObjectParser {

    /**
     * If we're parsing data for a select, it will be this one.
     */
    private SelectInput currentSelect = null;
    /**
     * Caches the default option for the select we're currently parsing.
     */
    private boolean selectDefault = false;

    /**
     * The final Collection of inputs will be stored here.
     */
    private Vector inputs = new Vector();

    /**
     * This HashMap will hold the FORM tag parameters.
     */
    private HashMap formParams = new HashMap(4);

    /**
     * Helper var voor radiobuttons.
     */
    private HashMap mapRadioNameToIndex = new HashMap();

    /**
     * Comment cache. Works only for Mascot forms!
     */
    private String prevComment = null;

    /**
     * Localization helper.
     */
    private boolean inTD = false;

    /**
     * Localization helper.
     */
    private boolean inAnchor = false;

    /**
     * Localization helper.
     */
    private boolean ibCat = false;


    /**
     * Localization helper.
     */
    private int state = NO_STATE;
    private static final int NO_STATE = -1;
    private static final int IN_OPTION = 1;


    /**
     * This constructor will automatically parse the form from the Reader
     * passed as a parameter. <br />
     * It uses the parsers from the Swing library.
     *
     * @param   aBuf    BufferedReader from which the form HTML may be read and parsed.
     * @exception   IOException when the Reader fails.
     */
    public FormToObjectParserForMascotCallBackImpl(BufferedReader aBuf) throws IOException {
        new ParserDelegator().parse(aBuf, this, true);
    }

    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        // Hidden textfields need no comments. So they're easy.
        if((HTML.Tag.INPUT.equals(t)) && (((String)a.getAttribute(HTML.Attribute.TYPE)).equalsIgnoreCase("hidden"))) {
            this.handleHidden(t, a);
            this.prevComment = null;
        } else if((HTML.Tag.INPUT.equals(t)) && (((String)a.getAttribute(HTML.Attribute.TYPE)).equalsIgnoreCase("text"))) {
            this.handleTextField(t, a);
            this.prevComment = null;
        } else if(HTML.Tag.INPUT.equals(t) && ((String)a.getAttribute(HTML.Attribute.TYPE)).equalsIgnoreCase("radio")) {
            this.handleRadio(t, a);
            this.prevComment = null;
        } else if(HTML.Tag.INPUT.equals(t) && ((String)a.getAttribute(HTML.Attribute.TYPE)).equalsIgnoreCase("checkbox")) {
            this.handleCheckbox(t, a);
            this.prevComment = null;
        } else if(HTML.Tag.INPUT.equals(t) && ((String)a.getAttribute(HTML.Attribute.TYPE)).equalsIgnoreCase("file")) {
            this.handleFile(t, a);
        } else if(HTML.Tag.BR.equals(t) && inTD && inAnchor) {
            ibCat = true;
        }
    }

    public void handleEndOfLineString(String eol) {
        super.handleEndOfLineString(eol);
    }

    public void handleError(String errorMsg, int pos) {
        super.handleError(errorMsg, pos);
    }

    public void handleEndTag(HTML.Tag t, int pos) {
        if(t.equals(HTML.Tag.SELECT) && this.currentSelect != null) {
            inputs.addElement(currentSelect);
            this.currentSelect = null;
            this.prevComment = null;
        } else if(t.equals(HTML.Tag.OPTION)) {
            state = NO_STATE;
            selectDefault = false;
        } else if(t.equals(HTML.Tag.TD)) {
            inTD = false;
        } else if(t.equals(HTML.Tag.A)) {
            inAnchor = false;
            ibCat = false;
        }
    }

    public void handleComment(char[] data, int pos) {
        super.handleComment(data, pos);
    }

    public void handleText(char[] data, int pos) {
        switch(state) {
            case IN_OPTION:
                if(this.currentSelect == null) {
                    System.err.println("Option found outside of SELECT!");
                    break;
                } else {
                    currentSelect.addElement(new String(data).trim());
                    // Check for selected.
                    if(selectDefault) {
                        currentSelect.setDefault(new String(data).trim());
                    }
                }
                break;
            case NO_STATE:
            default:
                if(inTD && inAnchor) {
                    if(ibCat) {
                        this.prevComment += " " + new String(data).trim();
                    } else {
                        this.prevComment = new String(data).trim();
                    }
                }
        }
    }

    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        if(t.equals(HTML.Tag.SELECT)) {
            this.currentSelect = new SelectInput((String)a.getAttribute(HTML.Attribute.NAME));

            // Check fo available comment.
            if(prevComment != null) {
                this.currentSelect.setComment(prevComment);
            }

            // Check for multiple select
            if(a.getAttribute(HTML.Attribute.MULTIPLE) != null) {
                this.currentSelect.setMultiple(true);
            }
        } else if(t.equals(HTML.Tag.OPTION)) {
            state = this.IN_OPTION;
            if(a.getAttribute(HTML.Attribute.SELECTED) != null) {
                selectDefault = true;
            } else {
                selectDefault = false;
            }
        } else if(t.equals(HTML.Tag.TD)) {
            inTD = true;
        } else if(t.equals(HTML.Tag.A)) {
            inAnchor = true;
        } else if(t.equals(HTML.Tag.FORM)) {
            this.formParams.put(HTML.Attribute.NAME, a.getAttribute(HTML.Attribute.NAME));
            this.formParams.put(HTML.Attribute.ACTION, a.getAttribute(HTML.Attribute.ACTION));
            this.formParams.put(HTML.Attribute.ENCTYPE, a.getAttribute(HTML.Attribute.ENCTYPE));
            this.formParams.put(HTML.Attribute.METHOD, a.getAttribute(HTML.Attribute.METHOD));
        }
    }

    public Collection getAllInputs() {
        return inputs;
    }

    public int getInputCount() {
        return inputs.size();
    }

    /**
     * Since this specific implementation stores all inputs in a Vector,
     * you can access them by index as well.
     *
     * @param   aIndex   the int with the index in the Vector for the desired input.
     * @return  InputInterface  the implementation requested.
     */
    public InputInterface getInputAt(int aIndex) {
        return (InputInterface)inputs.elementAt(aIndex);
    }

    public HashMap getFormParams() {
        return formParams;
    }

    /**
     * This method handles radiobuttons.
     */
    private void handleRadio(HTML.Tag t, MutableAttributeSet a) {
        String name = (String)a.getAttribute(HTML.Attribute.NAME);
        Object loTemp = null;
        if((loTemp = mapRadioNameToIndex.get(name)) != null) {
            // Radio already exists. Just add the item as a selection
            // in the corresponding RadioInput object (set it as default if
            // necessary).
            RadioInput ri = (RadioInput)inputs.elementAt(((Integer)loTemp).intValue());
            // Check for default.
            if(a.getAttribute(HTML.Attribute.CHECKED) != null) {
                ri.addDefaultChoice((String)a.getAttribute(HTML.Attribute.VALUE));
            } else {
                ri.addChoice((String)a.getAttribute(HTML.Attribute.VALUE));
            }
        } else {
            // Radio is a new one. Initialize it, add it to the inputs Vector and
            // link the index tot the name via the HashMap.

            // 1. Initialize radioInput object.
            RadioInput ri = new RadioInput(name);
            // 1.b. Check for the value, see if it's default...
            if(a.getAttribute(HTML.Attribute.CHECKED) != null) {
                ri.addDefaultChoice((String)a.getAttribute(HTML.Attribute.VALUE));
            } else {
                ri.addChoice((String)a.getAttribute(HTML.Attribute.VALUE));
            }

            // 2. Add it to the inputs Vector.
            int index = inputs.size();
            inputs.addElement(ri);

            // 3. Link name and index via the HashMap.
            mapRadioNameToIndex.put(name, new Integer(index));
        }
    }

    /**
     * This method handles TextFields.
     */
    private void handleTextField(HTML.Tag t, MutableAttributeSet a) {
        String name = (String)a.getAttribute(HTML.Attribute.NAME);
        Object loTemp = a.getAttribute(HTML.Attribute.VALUE);
        TextFieldInput tfi = new TextFieldInput(name);

        // Check for a default.
        if( (loTemp != null) && (!((String)loTemp).trim().equals("")) ) {
            String value = ((String)loTemp).trim();
            tfi.setDefaultValue(value);
        }

        // Check for a comment.
        if(prevComment != null) {
            tfi.setComment(prevComment);
        }

        inputs.addElement(tfi);
    }

    /**
     * This method handles hidden fields.
     */
    private void handleHidden(HTML.Tag t, MutableAttributeSet a) {
        String name = (String)a.getAttribute(HTML.Attribute.NAME);
        String value = ((String)a.getAttribute(HTML.Attribute.VALUE)).trim();
        inputs.addElement(new TextFieldInput(name, value, true));
    }

    /**
     * This method handles Checkboxes.
     */
    private void handleCheckbox(HTML.Tag t, MutableAttributeSet a) {
        String name = (String)a.getAttribute(HTML.Attribute.NAME);
        String value = ((String)a.getAttribute(HTML.Attribute.VALUE)).trim();
        CheckboxInput ci = new CheckboxInput(name, value);

        // See if it is checked.
        if(a.getAttribute(HTML.Attribute.CHECKED) != null) {
            ci.setChecked(true);
        }

        // See if we have a comment available.
        if(this.prevComment != null) {
            ci.setComment(prevComment);
        }
        inputs.addElement(ci);
    }

    /**
     * This method handles file-inputs.
     */
    private void handleFile(HTML.Tag t, MutableAttributeSet a) {
        TextFieldInput tfi = new TextFieldInput((String)a.getAttribute(HTML.Attribute.NAME));
        tfi.setFile(true);
        // See if we have a comment available.
        if(prevComment != null) {
            tfi.setComment(prevComment);
        }
        inputs.addElement(tfi);
    }
}