/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jul-02
 * Time: 14:24:47
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.compomics.mslimscore.util.http.forms;

import org.apache.log4j.Logger;

import com.compomics.mslimscore.util.http.forms.inputs.InputInterface;
import com.compomics.mslimscore.util.http.forms.parsers.FormToObjectParser;
import com.compomics.mslimscore.util.http.forms.parsers.FormToObjectParserForMascotCallBackImpl;

import javax.swing.text.html.HTML;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * In theory, this class is usable to parse any HTML form into a collection of InputInterface implementations that allow
 * for the selection of values in a programmatic context rather than an HTML context. <br /> Value initialization can be
 * done automatically when one requests the HTTP post header, used to submit this form. <br /> If the correct content
 * types etc. are set (at your leisure in the caller of this class), the POST String can be submitted as the body of the
 * HTTP request to complete the cycle. <br /> The class behaves like a Factory for itself (static methods for getting an
 * instance), but it has public constructors as well if you would like to use them (for instance, when you write your
 * own form parser). <b>PLEASE NOTE</b> that the current underlying parser specializes in Mascot search forms for the
 * retrieval of appropriate 'comment' lines for the inputs. This is an issue of the parser, not the form!! This format
 * consists of a table, where each cell holds an input and possibly a comment (as an ANCHOR tag!!). Only RadioButtons
 * escape this.
 *
 * @author Lennart Martens
 */
public class HTTPForm {
    // Class specific log4j logger for HTTPForm instances.
    private static Logger logger = Logger.getLogger(HTTPForm.class);

    /**
     * All the InputInterface implementations found on the form.
     */
    private Vector inputFields = null;

    /**
     * The parameters of the FORM tag.
     */
    private HashMap formParams = null;

    /**
     * This boundary is what will seperate fields in the POST request (MIME encoding). Note that it is FINAL! I guess it
     * suffices for most applications, since the only real condition is that this String does not appear anywhere in the
     * request, save as boundary.
     */
    private static final String BOUNDARY = "---------------ArCo5BaLeNo4DoVe3Il2VaTiCaNo1?";

    /**
     * Default constructor which just initializes the vars to empty, yet usable values.
     */
    public HTTPForm() {
        this(new Vector(), new HashMap(4));
    }

    /**
     * Constructor to initialize a Form with the given InputInterface implementations, yet without any Form parameters.
     *
     * @param aFields a Vector with InputInterface implementations.
     */
    public HTTPForm(Vector aFields) {
        this(aFields, new HashMap(4));
    }

    /**
     * This constructor initializes the InputInterface implementations on this HTTPForm as well as setting all the
     * parameters for the form. <br /> Typically, this constructor is called by the parser, as you will probably call
     * the static factory methods (which will invoke the parser for you).
     */
    public HTTPForm(Vector aFields, HashMap aParams) {
        inputFields = aFields;
        formParams = aParams;
    }

    /**
     * Probably the factory method of choice for getting an HTTPForm object from an actual form. Just give it the URl
     * and let the default parser sort it out.
     *
     * @param anURL The URL where the form is located that will be parsed.
     * @return HTTPForm    the HTTPForm instance which results from the parsing.
     * @throws IOException when reading the stream causes unexpected problems.
     */
    public static HTTPForm parseHTMLForm(URL anURL) throws IOException {
        // The HTTPForm to return.
        HTTPForm lForm = null;
        // The Connection to the form HTML.
        URLConnection lConn = anURL.openConnection();
        InputStream in = lConn.getInputStream();
        // Delegate the parsing to the other member function.
        lForm = HTTPForm.parseHTMLForm(in);
        // Close the stream to the HTML form.
        in.close();
        // Return result.
        return lForm;
    }

    /**
     * Probably not the easiest factory method to use. It allows you to parse a form from an InputStream, using the
     * default parser.
     *
     * @param is InputStream from which the form will be parsed.
     * @return HTTPForm    the HTTPForm instance which results from the parsing.
     * @throws IOException when reading the stream causes unexpected problems.
     */
    public static HTTPForm parseHTMLForm(InputStream is) throws IOException {
        BufferedReader lBuf = new BufferedReader(new InputStreamReader(is));
        FormToObjectParser fparser = new FormToObjectParserForMascotCallBackImpl(lBuf);
        return new HTTPForm((Vector) fparser.getAllInputs(), fparser.getFormParams());
    }

    /**
     * This method adds an InputInterface implementation to the form.
     *
     * @param aField InputInterface implementation to add to the form.
     */
    public void addInput(InputInterface aField) {
        inputFields.addElement(aField);
    }

    /**
     * This method allows you to set the parameters on the form.
     *
     * @param aParams Form parameters by key-value pairs in the HashMap.
     */
    public void setFormParameters(HashMap aParams) {
        formParams = aParams;
    }

    /**
     * This method will report on the parameters set in the form tag.
     *
     * @return HashMap with the form parameters as key-value pairs.
     */
    public HashMap getFormParameters() {
        return formParams;
    }

    /**
     * This method returns all InputInterface implementations on the form.
     *
     * @return Vector  with InputInterface implementations.
     */
    public Vector getInputs() {
        return this.inputFields;
    }

    /**
     * This method allows you to search for an InputInterface implementation by its name.
     *
     * @param aName String with the name of the input
     * @return InputInterface  which is the implementation if found, 'null' otherwise.
     */
    public InputInterface getInputByName(String aName) {
        InputInterface result = null;
        int liSize = this.inputFields.size();
        for (int i = 0; i < liSize; i++) {
            InputInterface lTemp = (InputInterface) this.inputFields.get(i);
            if (aName.equalsIgnoreCase(lTemp.getName())) {
                result = lTemp;
                break;
            }
        }
        return result;
    }

    /**
     * This method returns a String representation of the current form. <br /> Most notably, reporting on the form
     * parameters and all the inputs.
     *
     * @return String  with the String representation of this form.
     */
    public String toString() {
        // Initial info that it concerns a form.
        StringBuffer lSB = new StringBuffer("This object represents a form with the following input fields:\n\n");
        // Add toStrings from all inputs.
        int liSize = inputFields.size();
        for (int i = 0; i < liSize; i++) {
            lSB.append(inputFields.elementAt(i).toString() + "\n");
        }
        // Add all form parameters.
        lSB.append("The form parameters are:\n");
        Iterator iter = formParams.keySet().iterator();
        while (iter.hasNext()) {
            Object loTemp = iter.next();
            lSB.append(" - '" + loTemp + "': '" + formParams.get(loTemp) + "'\n");
        }
        return lSB.toString();
    }

    /**
     * This method reports on the current boundary.
     *
     * @return String with the current boundary.
     */
    public String getBoundary() {
        return BOUNDARY;
    }

    /**
     * This method returns header settings as generated by Microsoft Internet Explorer 6.0, nl-be locale, on Windows XP
     * Professional.
     *
     * @return HashMap with key-value pairs for the HTTP headers.
     * @deprecated This method has no serious use.
     */
    public HashMap getHeaders() {
        HashMap toReturn = new HashMap();
        // Now assemble the header.
        // Largely copied from MS IExplore 6.0 on my machine (Win XP Pro, be-nl locale).
        toReturn.put("Accept", " image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
        toReturn.put("Accept-Language", " nl-be");
        // This boundary is always the same.
        // Check it out - it's not like you're going to ever include that, are you?
        toReturn.put("Content-Type", " multipart/form-data; boundary=" + BOUNDARY);
        toReturn.put("Accept-Encoding", " gzip, deflate");
        toReturn.put("User-Agent", " Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
        toReturn.put("Host", " " + formParams.get(HTML.Attribute.ACTION));
        toReturn.put("Connection", " Keep-Alive");
        toReturn.put("Cache-Control", " no-cache");

        return toReturn;
    }

    /**
     * This method reports on the submission String you an use as the body for an HTTP POST request. <br /> It
     * automatically polls the inputs for their values, and the inputs will now whether to call the user for input.<br
     * /> <i>Note that the header settings are up to you, the caller!!</i>
     *
     * @return String with the POST body String.
     */
    public String getSubmissionString() {
        // This will always be a POST.
        StringBuffer lBuf = new StringBuffer();
        // Cycle all inputs and query them for their values.
        // they now where to get it from.
        int liSize = inputFields.size();
        for (int i = 0; i < liSize; i++) {
            InputInterface iiField = (InputInterface) inputFields.elementAt(i);
            lBuf.append(iiField.getHTTPPostString(BOUNDARY));
        }
        // Signal end of POST.
        lBuf.append("--" + BOUNDARY + "--\n");

        // Now we've assembled this thing in full, return it.
        return lBuf.toString();
    }

    /**
     * This method is present for testing purposes only. <br /> The arguments are NOT used and you probably don't want
     * to call this method as it will not find the file it's looking for and simply exit.
     */
    public static void main(String[] args) {
        try {
            HTTPForm hf = HTTPForm.parseHTMLForm(new FileInputStream("G:/temp.html"));
            logger.info(hf.getSubmissionString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
