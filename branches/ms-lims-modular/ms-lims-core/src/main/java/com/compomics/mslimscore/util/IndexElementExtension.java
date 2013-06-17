/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;
import psidev.psi.tools.xxindex.StandardXpathAccess;
import psidev.psi.tools.xxindex.index.IndexElement;

/**
 *
 * @author Davy
 */
public class IndexElementExtension {
	// Class specific log4j logger for IndexElementExtension instances.
	 private static Logger logger = Logger.getLogger(IndexElementExtension.class);

    /**
     * The IndexElement
     */
    private IndexElement iIndexElement;
    /**
     * The accessor
     */
    private StandardXpathAccess iAccessor;
    /**
     * The title for the IndexElement
     */
    private String iTitlePath;

    /**
     * Constructor
     * @param aElement
     * @param aPath
     * @param aAccessor
     */
    public IndexElementExtension(IndexElement aElement, String aPath,  StandardXpathAccess aAccessor) {
        this.iIndexElement = aElement;
        this.iTitlePath = aPath;
        this.iAccessor = aAccessor;
    }

    /**
     * Getter for the XmlElementExtensions linked to the accessor and IndexElement
     * @return
     * @throws IOException
     */
    public Vector<XmlElementExtension> getExtendedXmlElement() throws IOException {
        Vector<XmlElementExtension> lElements = new Vector<XmlElementExtension>();

        List<String> lSnippet = iAccessor.getXmlSnippets(iTitlePath, iIndexElement.getStart(), iIndexElement.getStop());
        for(int i = 0; i<lSnippet.size(); i ++){
            XmlElementExtension axt = new XmlElementExtension(lSnippet.get(i), this, false);
            lElements.add(axt);
        }
        return lElements;
    }
}
