/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.interfaces;

/**
 *
 * @author Davy
 */
public interface PeptideIdentification {

    /**
     * This method returns the value for the 'Datfile_query'
     *
     * @return long    with the value for the Datfile_query.
     */
    public long getDatfile_query();

    /**
     * This method returns the value for the 'Accession'
     *
     * @return String    with the value for the Accession.
     */
    public String getAccession();

    /**
     * This method returns the value for the 'Start'
     *
     * @return long    with the value for the Start.
     */
    public long getStart();

    /**
     * This method returns the value for the 'End'
     *
     * @return long    with the value for the End.
     */
    public long getEnd();

    /**
     * This method returns the value for the 'Enzymatic'
     *
     * @return String    with the value for the Enzymatic.
     */
    public String getEnzymatic();

    /**
     * This method returns the value for the 'Sequence'
     *
     * @return String    with the value for the Sequence.
     */
    public String getSequence();

    /**
     * This method returns the value for the 'Modified_sequence'
     *
     * @return String    with the value for the Modified_sequence.
     */
    public String getModified_sequence();

    /**
     * This method returns the value for the 'Score'
     *
     * @return long    with the value for the Score.
     */
    public long getScore();

    /**
     * This method returns the value for the 'Homology'
     *
     * @return double    with the value for the Homology.
     */
    public double getHomology();

    /**
     * This method returns the value for the 'Exp_mass'
     *
     * @return Number    with the value for the Exp_mass.
     */
    public Number getExp_mass();

    /**
     * This method returns the value for the 'Cal_mass'
     *
     * @return Number    with the value for the Cal_mass.
     */
    public Number getCal_mass();

    /**
     * This method returns the value for the 'Valid'
     *
     * @return int    with the value for the Valid.
     */
    public int getValid();

    /**
     * This method returns the value for the 'Description'
     *
     * @return String    with the value for the Description.
     */
    public String getDescription();

    /**
     * This method returns the value for the 'Identitythreshold'
     *
     * @return long    with the value for the Identitythreshold.
     */
    public long getIdentitythreshold();

    /**
     * This method returns the value for the 'Confidence'
     *
     * @return Number    with the value for the Confidence.
     */
    public Number getConfidence();

    /**
     * This method returns the value for the 'Db'
     *
     * @return String    with the value for the Db.
     */
    public String getDb();

    /**
     * This method returns the value for the 'Title'
     *
     * @return String    with the value for the Title.
     */
    public String getTitle();

    /**
     * This method returns the value for the 'Precursor'
     *
     * @return Number    with the value for the Precursor.
     */
    public Number getPrecursor();

    /**
     * This method returns the value for the 'Charge'
     *
     * @return int    with the value for the Charge.
     */
    public int getCharge();

    /**
     * This method returns the value for the 'Isoforms'
     *
     * @return String    with the value for the Isoforms.
     */
    public String getIsoforms();

    /**
     * This method returns the value for the 'Db_filename'
     *
     * @return String    with the value for the Db_filename.
     */
    public String getDb_filename();

    /**
     * This method returns the value for the 'Mascot_version'
     *
     * @return String    with the value for the Mascot_version.
     */
    public String getMascot_version();

    /**
     * This method returns the Type value.
     *
     * @return String   wiht the type of identification (light, medium, heavy)
     */
    public String getType();

    /**
     * This method returns the Type value.
     *
     * @return String   wiht the type of identification (light, medium, heavy)
     */
    public String getSpectrumFileName();

    /**
     * This method sets the value for the 'Datfile_query'
     *
     * @param    aDatfile_query    long with the value for the Datfile_query.
     */
    public void setDatfile_query(long aDatfile_query);

    /**
     * This method sets the value for the 'Accession'
     *
     * @param    aAccession    String with the value for the Accession.
     */
    public void setAccession(String aAccession);

    /**
     * This method sets the value for the 'Start'
     *
     * @param    aStart    long with the value for the Start.
     */
    public void setStart(long aStart);
    /**
     * This method sets the value for the 'End'
     *
     * @param    aEnd    long with the value for the End.
     */
    public void setEnd(long aEnd);

    /**
     * This method sets the value for the 'Enzymatic'
     *
     * @param    aEnzymatic    String with the value for the Enzymatic.
     */
    public void setEnzymatic(String aEnzymatic);

    /**
     * This method sets the value for the 'Sequence'
     *
     * @param    aSequence    String with the value for the Sequence.
     */
    public void setSequence(String aSequence);

    /**
     * This method sets the value for the 'Modified_sequence'
     *
     * @param    aModified_sequence    String with the value for the Modified_sequence.
     */
    public void setModified_sequence(String aModified_sequence);

    /**
     * This method sets the value for the 'Score'
     *
     * @param    aScore    long with the value for the Score.
     */
    public void setScore(long aScore);

    /**
     * This method sets the value for the 'Homology'
     *
     * @param    aHomology    double with the value for the Homology.
     */
    public void setHomology(double aHomology);

    /**
     * This method sets the value for the 'Exp_mass'
     *
     * @param    aExp_mass    Number with the value for the Exp_mass.
     */
    public void setExp_mass(Number aExp_mass);

    /**
     * This method sets the value for the 'Cal_mass'
     *
     * @param    aCal_mass    Number with the value for the Cal_mass.
     */
    public void setCal_mass(Number aCal_mass);

    /**
     * This method sets the value for the 'Valid'
     *
     * @param    aValid    int with the value for the Valid.
     */
    public void setValid(int aValid);

    /**
     * This method sets the value for the 'Description'
     *
     * @param    aDescription    String with the value for the Description.
     */
    public void setDescription(String aDescription);

    /**
     * This method sets the value for the 'Identitythreshold'
     *
     * @param    aIdentitythreshold    long with the value for the Identitythreshold.
     */
    public void setIdentitythreshold(long aIdentitythreshold);

    /**
     * This method sets the value for the 'Confidence'
     *
     * @param    aConfidence    Number with the value for the Confidence.
     */
    public void setConfidence(Number aConfidence);

    /**
     * This method sets the value for the 'Db'
     *
     * @param    aDb    String with the value for the Db.
     */
    public void setDb(String aDb);

    /**
     * This method sets the value for the 'Title'
     *
     * @param    aTitle    String with the value for the Title.
     */
    public void setTitle(String aTitle);

    /**
     * This method sets the value for the 'Precursor'
     *
     * @param    aPrecursor    Number with the value for the Precursor.
     */
    public void setPrecursor(Number aPrecursor);

    /**
     * This method sets the value for the 'Charge'
     *
     * @param    aCharge    int with the value for the Charge.
     */
    public void setCharge(int aCharge);

    /**
     * This method sets the value for the 'Isoforms'
     *
     * @param    aIsoforms    String with the value for the Isoforms.
     */
    public void setIsoforms(String aIsoforms);

    /**
     * This method sets the value for the 'Db_filename'
     *
     * @param    aDb_filename    String with the value for the Db_filename.
     */
    public void setDb_filename(String aDb_filename);

    /**
     * This method sets the value for the 'Mascot_version'
     *
     * @param    aMascot_version    String with the value for the Mascot_version.
     */
    public void setMascot_version(String aMascot_version);

    /**
     * This method sets the type value
     *
     * @param   aType   The type of this identification (light, medium, heavy)
     */
    public void setType(String aType);

    /**
     * This method sets the spectrumfilename value
     *
     * @param   aFileName   The spectrumfilename
     */
    public void setSpectrumFileName(String aFileName);
}