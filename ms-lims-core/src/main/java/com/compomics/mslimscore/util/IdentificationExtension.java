/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util;

import com.compomics.mslimscore.util.interfaces.PeptideIdentification;
import com.compomics.mslimscore.util.quantitation.QuantitativeValidationSingleton;
import com.compomics.mslimsdb.accessors.Identification;
import com.compomics.mslimsdb.accessors.Identification_to_quantitation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class IdentificationExtension extends Identification implements PeptideIdentification {
	// Class specific log4j logger for IdentificationExtension instances.
	 private static Logger logger = Logger.getLogger(IdentificationExtension.class);

    /**
     * An ArrayList with Identification_to_quantitations linked to this identification
     */
    private ArrayList<Identification_to_quantitation> iQuantitationLinkers = new ArrayList<Identification_to_quantitation>();
    /**
     * The quantitation file name
     */
    private String iQuantitationFileName;
    /**
     * The type of the identification (light, medium, heavy)
     */
    private String iType = null;
    /**
     * This is the spectrum file name
     */
    private String iSpectrumFileName;

    private long iQuanGroupid;

    /**
     * Default constructor.
     */
    public IdentificationExtension() {
        super();
    }

    /**
     * CONSTRUCTOR
     * @param aHM
     */
    public IdentificationExtension(HashMap aHM) {
        super(aHM);
        if (aHM.containsKey("FILENAME")) {
            this.iSpectrumFileName =  aHM.get("FILENAME").toString();
        }
    }

    /**
     * This method reads an Identification from the specified ResultSet. Th ResultSet should be positioned such that it
     * is directly readable, ie.: next should've been called already and should've returned 'true'. The columns should
     * be in this order: <br /> Column 1: identification ID <br /> Column 2: l_spectrumfile ID <br /> Column 3:
     * l_datfile ID <br /> Column 4: accession number <br /> Column 5: start position <br /> Column 6: end position <br
     * /> Column 7: enzymatic character <br /> Column 8: sequence <br /> Column 9: modified sequence <br /> Column 10:
     * ion coverage string <br /> Column 11: score <br /> Column 12: experimental mass <br /> Column 13: calculated mass
     * <br /> Column 14: light isotope intensity <br /> Column 15: heavy isotope intensity <br /> Column 16: valid flag
     * <br /> Column 17: description <br /> Column 18: identity threshold <br /> Column 19: confidence <br /> Column 20:
     * db <br /> Column 21: title <br /> Column 22: precursor mass <br /> Column 23: precursor charge <br /> Column 24:
     * isoforms <br /> Column 25: db filename <br /> Column 26: Mascot version <br /> Column 27: username <br /> Column
     * 28: creationdate <br /> Column 29: modificationdate.
     *
     * @param aRS ResultSet to read the Identification from. It should be directly readable, ie.: next should've been
     *            called already and should've returned 'true'.
     * @throws java.sql.SQLException when the ResultSet could not be read.
     */
    public IdentificationExtension(ResultSet aRS) throws SQLException {
        super(aRS);
        this.iSpectrumFileName = aRS.getString("filename");
    }

    /**
     * This methods reads all the IdentificationExtension associated with the specified Project ID, with an optional 'where'
     * clause addition. Note that the two tables you can use are 'identifciation' and 'spectrumfile'. The former is
     * aliased as 'i', the latter as 's'. So you can specify the addition as 'i.score>10 and i.identitithreshold < 30',
     * for instance. The addition will be added to the core query as 'AND (addition)'.
     *
     * @param aConn          Connection to read the identifications from.
     * @param aProjectID     long with the Project ID to select on.
     * @param aWhereAddition String with an optional where-clause addition. Can be 'null'.
     * @return IdentificationExtension[] with the DistillerQuantitativePeptide associated with the specified Project ID.
     * @throws SQLException when the retrieving of the identifications went wrong.
     */
    public static IdentificationExtension[] getIdentificationExtensionsforProject(Connection aConn, long aProjectID, String aWhereAddition) throws SQLException {
        String sql;
        QuantitativeValidationSingleton iQuantitationSingelton = QuantitativeValidationSingleton.getInstance();
         if(iQuantitationSingelton.getMsLimsPre7_2()){
             sql = "select i.*, s.filename from identification as i, spectrumfile as s where i.l_spectrumfileid = s.spectrumfileid and s.l_projectid=?";
         } else {
             sql = "select i.*, s.filename from identification as i, spectrum as s where i.l_spectrumid = s.spectrumid and s.l_projectid=?";
         }
        
        if (aWhereAddition != null) {
            sql += " AND " + aWhereAddition;
        }
        PreparedStatement prep = aConn.prepareStatement(sql);
        prep.setLong(1, aProjectID);
        ResultSet rs = prep.executeQuery();
        Vector v = new Vector();
        while (rs.next()) {
            v.add(new IdentificationExtension(rs));
        }
        rs.close();
        prep.close();
        IdentificationExtension[] lIDs = new IdentificationExtension[v.size()];
        v.toArray(lIDs);

        return lIDs;
    }

    public static Vector<Long> getUnvalidIdentificationdIdsForProject(Connection aConn, long aProjectID) throws SQLException {
        String sql;
        QuantitativeValidationSingleton iQuantitationSingelton = QuantitativeValidationSingleton.getInstance();
         if(iQuantitationSingelton.getMsLimsPre7_2()){
             sql = "select i.identificationid, s.filename from identification as i, spectrumfile as s, validation as v where i.identificationid = v.l_identificationid and v.status = 0 and i.l_spectrumfileid = s.spectrumfileid and s.l_projectid=?";
         } else if(iQuantitationSingelton.getMsLimsPre7_6()) {
             sql = "select i.identificationid from identification as i, spectrum as s, validation as v where i.identificationid = v.l_identificationid and v.status = 0 and i.l_spectrumid = s.spectrumid and s.l_projectid=?";
         } else {
             sql = "select i.identificationid from identification as i, spectrum as s, validation as v where i.identificationid = v.l_identificationid and v.l_validationtypeid < 0 and i.l_spectrumid = s.spectrumid and s.l_projectid=?";
         }
        PreparedStatement prep = aConn.prepareStatement(sql);
        prep.setLong(1, aProjectID);
        ResultSet rs = prep.executeQuery();
        Vector<Long> v = new Vector<Long>();
        while (rs.next()) {
            v.add(rs.getLong(1));
        }
        rs.close();
        prep.close();
        return v;
    }

    /**
     * This methods reads all the IdentificationExtension  with a 'where'
     * clause addition. Note that the two tables you can use are 'identifciation' and 'spectrumfile'. The former is
     * aliased as 'i', the latter as 's'. So you can specify the addition as 'i.score>10 and i.identitithreshold < 30',
     * for instance. The addition will be added to the core query as 'AND (addition)'.
     *
     * @param aConn          Connection to read the identifications from.
     * @param aWhereAddition String with an optional where-clause addition. Can be 'null'.
     * @return IdentificationExtension[]
     * @throws SQLException when the retrieving of the identifications went wrong.
     */
    public static HashMap<Integer,PeptideIdentification> getIdentificationExtensions(Connection aConn, String aWhereAddition) throws SQLException {

        String sql;
        QuantitativeValidationSingleton iQuantitationSingelton = QuantitativeValidationSingleton.getInstance();
         if(iQuantitationSingelton.getMsLimsPre7_2()){
             sql = "select i.*, s.filename from identification as i, spectrumfile as s ";
             if (aWhereAddition != null) {
                 sql += " where i.l_spectrumfileid = s.spectrumfileid and " + aWhereAddition;
             }
         } else {
             sql = "select i.*, s.filename from identification as i, spectrum as s ";
             if (aWhereAddition != null) {
                 sql += " where i.l_spectrumid = s.spectrumid and " + aWhereAddition;
             }
         }

        PreparedStatement prep = aConn.prepareStatement(sql);
        ResultSet rs = prep.executeQuery();
        HashMap<Integer,PeptideIdentification> v = new HashMap<Integer,PeptideIdentification>();
        while (rs.next()) {
            IdentificationExtension lEx = new IdentificationExtension(rs);
            v.put(Integer.valueOf(String.valueOf(lEx.getDatfile_query())), lEx);
        }
        rs.close();
        prep.close();
        return v;
    }


/**
     * Method to add a Identification_to_quantitation to this Identification
     * @param lQuantLinker The Identification_to_quantitatin to link
     */
    public void addIdentification_to_quantitation(Identification_to_quantitation lQuantLinker){
        iQuantitationLinkers.add(lQuantLinker);
    }

    /**
     * Setter for the quantitation file name
     * @param aFileName The quantitation file name
     *
     */
    public void setQuantitationFileName(String aFileName){
        this.iQuantitationFileName = aFileName;
    }

    /**
     * Getter for the quantitation file name linked to this identification
     * @return String with the quantitation file name linked to this identification
     */
    public String getQuantitationFileName(){
        return this.iQuantitationFileName;
    }

    /**
     * Getter for the ArrayList with Identfication_to_quantitations linked to this Identification
     * @return ArrayList<Identification_to_quantitation>
     */
    public ArrayList<Identification_to_quantitation> getQuantitationLinker(){
        return iQuantitationLinkers;
    }

    /**
     * This method gives the type of this identification (Light, Heavy, ...). If the type is not set, it will
     * try to find it in Identification_to_quantitations linked to this IdentificationExtension.
     * @return
     */
    public String getType() {
        if(iType == null){
            ArrayList<Identification_to_quantitation> lLinkers = this.getQuantitationLinker();
            for (int j = 0; j < lLinkers.size(); j++) {
                if (lLinkers.get(j).getL_identificationid() == this.getIdentificationid()) {
                    this.setType(lLinkers.get(j).getType());
                }
            }
        }
        return iType;
    }

    /**
     * Setter for the Type parameter
     * @param   aType   The type of this identification (light, medium, heavy)
     */
    public void setType(String aType) {
        iType = aType;
    }

    /**
     * Getter for the 'iSpectrumFileParameter'
     * @return String with the spectrumfile name
     */
    public String getSpectrumFileName() {
        return iSpectrumFileName;
    }

    /**
     * Setter for the 'iSpectrumFileParameter'
     * @param aFileName String with the spectrumfile name to set
     */
    public void setSpectrumFileName(String aFileName) {
        this.iSpectrumFileName = aFileName;
    }

    public long getQuantitationGroupId() {
        return iQuanGroupid;
    }

    public void setQuantitationGroupId(long lQuanGroupId) {
        this.iQuanGroupid = lQuanGroupId;
    }
}
