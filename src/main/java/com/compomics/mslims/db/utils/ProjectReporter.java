/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 23-mrt-2005
 * Time: 12:10:46
 */
package com.compomics.mslims.db.utils;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.Protocol;
import com.compomics.mslims.db.accessors.Project;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/*
 * CVS information:
 *
 * $Revision: 1.13 $
 * $Date: 2009/03/11 13:57:45 $
 */

/**
 * This class actually creates the queries, executes them and writes up a report on the findings for a project report.
 * It takes into account the projects PROTOCOL type in generating certain detailed reports.
 *
 * @author Lennart
 * @version $Id: ProjectReporter.java,v 1.13 2009/03/11 13:57:45 niklaas Exp $
 */
public class ProjectReporter {
    // Class specific log4j logger for ProjectReporter instances.
    private static Logger logger = Logger.getLogger(ProjectReporter.class);

    /**
     * The PROTOCOL type.
     */
    private Protocol iProtocol = null;

    /**
     * The project instance.
     */
    private Project iProject = null;

    /**
     * The connection to retrieve the data from.
     */
    private Connection iConnection = null;

    /**
     * Setting this boolean to 'true' will result in output of performance statistics.
     */
    private static final boolean PERFOUTPUT = false;

    /**
     * The constructor fully initializes the reporter.
     *
     * @param aProject    Project to generate the report for.
     * @param aProtocol   Protocol type associated with the project.
     * @param aConnection Connection to retrieve the data from.
     */
    public ProjectReporter(Project aProject, Protocol aProtocol, Connection aConnection) {
        this.iProject = aProject;
        this.iProtocol = aProtocol;
        this.iConnection = aConnection;
    }

    /**
     * This method returns the number of individual SQL queries that will be executed against the database during the
     * generation of this report.
     *
     * @return int with the number of SQL queries to be executed against the database.
     */
    public int getNumberOfQueries() {
        int queries = 10;
        if (iProtocol.getType().toLowerCase().indexOf("nterm") >= 0) {
            queries += 11;
        } else if (iProtocol.getType().toLowerCase().indexOf("met") >= 0) {
            queries += 2;
        } else if (iProtocol.getType().toLowerCase().indexOf("cys") >= 0) {
            queries += 1;
        }

        return queries;
    }

    /**
     * This method generates the report for the current project, showing it's progress on the progressbar if it is
     * specified.
     *
     * @param aProgress JProgressBar on which to show the progress. Each executed query will set the value of the
     *                  progressbar to (getValue()+1). Note that this parameter can be 'null'.
     * @return String with the report.
     * @throws java.sql.SQLException when the report generation failed.
     */
    public String getReport(JProgressBar aProgress) throws SQLException {
        ArrayList perfAuditing = new ArrayList();
        // Report basic stuff.
        StringBuffer report = new StringBuffer("Report for project " + iProject.getProjectid() + ". " + iProject.getTitle() + ":\n\n");
        report.append("\t- This report employs the following PROTOCOL technique: " + iProtocol.getType() + "\n\n");

        // The statement.
        Statement stat = iConnection.createStatement();

        // Write and execute the queries.
        // 1. Standard queries.
        //  1.a. Number of spectra in the DB.
        String numSpectraQuery = "select count(*) from spectrum where l_projectid = " + iProject.getProjectid();
        long startTime = System.currentTimeMillis();
        ResultSet rs = stat.executeQuery(numSpectraQuery);
        rs.next();
        int numSpectra = rs.getInt(1);
        rs.close();
        long endTime = System.currentTimeMillis();
        perfAuditing.add(new Object[]{numSpectraQuery, new Long(endTime - startTime)});
        report.append("\t- " + numSpectra + " submitted");
        if (aProgress != null) {
            aProgress.setIndeterminate(false);
            aProgress.setValue(aProgress.getMinimum());
            advanceProgressBar(aProgress);
        }
        //  1.b. Group spectra count per instrument.
        String grpSpectraPerInstrumentQuery = "select i.name, count(*) from spectrum as f, instrument as i where f.l_instrumentid = i.instrumentid and f.l_projectid = " + iProject.getProjectid() + " group by i.instrumentid";
        startTime = System.currentTimeMillis();
        rs = stat.executeQuery(grpSpectraPerInstrumentQuery);
        report.append(" (");
        int iteration = 0;
        while (rs.next()) {
            String instrument = rs.getString(1);
            int count = rs.getInt(2);
            if (iteration > 0) {
                report.append(", ");
            }
            report.append(count + " from " + instrument);
            iteration++;
        }
        rs.close();
        endTime = System.currentTimeMillis();
        perfAuditing.add(new Object[]{grpSpectraPerInstrumentQuery, new Long(endTime - startTime)});
        report.append(").\n");
        if (aProgress != null) {
            advanceProgressBar(aProgress);
        }
        //  1.c. Number of identified spectra.
        String numIdedSpectraQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0";
        startTime = System.currentTimeMillis();
        rs = stat.executeQuery(numIdedSpectraQuery);
        rs.next();
        int numIdedSpectra = rs.getInt(1);
        rs.close();
        endTime = System.currentTimeMillis();
        perfAuditing.add(new Object[]{numIdedSpectraQuery, new Long(endTime - startTime)});
        String percent = new BigDecimal((((double) numIdedSpectra) / ((double) numSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
        report.append("\t- " + numIdedSpectra + " spectra identified (" + percent + "%)\n\n");
        if (aProgress != null) {
            advanceProgressBar(aProgress);
        }
        // Only do the rest if we have in fact got identifications.
        if (numIdedSpectra > 0) {
            //  1.d. Enzymicity of the identifications.
            String notFeCountQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.enzymatic <> 'FE' and i.valid > 0";
            startTime = System.currentTimeMillis();
            rs = stat.executeQuery(notFeCountQuery);
            rs.next();
            int notFeCount = rs.getInt(1);
            rs.close();
            endTime = System.currentTimeMillis();
            perfAuditing.add(new Object[]{notFeCountQuery, new Long(endTime - startTime)});
            if (notFeCount == 0) {
                report.append("\t- All peptides were the result of correct enzymatic cleavage\n\n");
                if (aProgress != null) {
                    advanceProgressBar(aProgress, 3);
                }
            } else {
                percent = new BigDecimal((((double) notFeCount) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\t- " + notFeCount + " (" + percent + "%) did not correspond fully with enzymatic cleavage:\n");
                // 1.d.1. N-terminally correct ones.
                String ntermCorrectCountQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.enzymatic = 'NE' and i.valid > 0";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(ntermCorrectCountQuery);
                rs.next();
                int ntermCorrectCount = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{ntermCorrectCountQuery, new Long(endTime - startTime)});
                percent = new BigDecimal((((double) ntermCorrectCount) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                String detailPercent = new BigDecimal((((double) ntermCorrectCount) / ((double) notFeCount)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\t\t+ " + ntermCorrectCount + " N-terminally correct peptides (" + percent + "% of total, " + detailPercent + "% of non-enzymatic identifications)\n");
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
                // 1.d.2. C-terminally correct ones.
                String ctermCorrectCountQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.enzymatic = 'CE' and i.valid > 0";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(ctermCorrectCountQuery);
                rs.next();
                int ctermCorrectCount = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{ctermCorrectCountQuery, new Long(endTime - startTime)});
                percent = new BigDecimal((((double) ctermCorrectCount) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                detailPercent = new BigDecimal((((double) ctermCorrectCount) / ((double) notFeCount)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\t\t+ " + ctermCorrectCount + " C-terminally correct peptides (" + percent + "% of total, " + detailPercent + "% of non-enzymatic identifications)\n");
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
                // 1.d.3. Fully incorrect ones.
                String inCorrectCountQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.enzymatic = 'EE' and i.valid > 0";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(inCorrectCountQuery);
                rs.next();
                int inCorrectCount = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{inCorrectCountQuery, new Long(endTime - startTime)});
                percent = new BigDecimal((((double) inCorrectCount) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                detailPercent = new BigDecimal((((double) inCorrectCount) / ((double) notFeCount)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\t\t+ " + inCorrectCount + " fully incorrect peptides (" + percent + "% of total, " + detailPercent + "% of non-enzymatic identifications)\n\n");
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
            }
            if (aProgress != null) {
                advanceProgressBar(aProgress, 1);
            }

            // 2. Specific PROTOCOL technique queries isolation efficiency.
            if (iProtocol.getType().toLowerCase().indexOf("nterm") >= 0) {
                //  2.a. N-terminal COFRADIC
                //   2.a.1. Count the number of N-terminal peptides.
                String numNtermQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.start<=2 and i.start>0";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(numNtermQuery);
                rs.next();
                int numNterm = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{numNtermQuery, new Long(endTime - startTime)});
                percent = new BigDecimal((((double) numNterm) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\t- " + numNterm + " of the identified spectra contained N-terminal peptides (" + percent + "%)\n\n");
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
                report.append("\t- Modifications summary (against total number of identified spectra):\n");
                //   2.a.2. Count the number of pryo-glu peptides.
                String numPygQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.modified_sequence regexp '^NH2-Q<Pyr>.*'";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(numPygQuery);
                rs.next();
                int numPyg = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{numPygQuery, new Long(endTime - startTime)});
                percent = new BigDecimal((((double) numPyg) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\t\t+ " + numPyg + " pyro-glutamate peptides (" + percent + "%)\n");
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
                //   2.a.3. Count the number of pyro-Cmc peptides.
                String numPycQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.modified_sequence regexp '^NH2-C<Pyc>.*'";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(numPycQuery);
                rs.next();
                int numPyc = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{numPycQuery, new Long(endTime - startTime)});
                percent = new BigDecimal((((double) numPyc) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\t\t+ " + numPyc + " pyro-Cmc peptides (" + percent + "%)\n");
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
                //   2.a.4. Count the number of proline-starting peptides (3 queries).
                String numProQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.sequence regexp '^P.*'";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(numProQuery);
                rs.next();
                int numPro = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{numProQuery, new Long(endTime - startTime)});
                if (numPro > 0) {
                    percent = new BigDecimal((((double) numPro) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                    numProQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.modified_sequence regexp '^Ace-P.*'";
                    String numProQuery2 = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.modified_sequence regexp '^AcD3-P.*'";
                    startTime = System.currentTimeMillis();
                    rs = stat.executeQuery(numProQuery);
                    rs.next();
                    int numProAce = rs.getInt(1);
                    rs.close();
                    endTime = System.currentTimeMillis();
                    perfAuditing.add(new Object[]{numProQuery, new Long(endTime - startTime)});
                    startTime = System.currentTimeMillis();
                    rs = stat.executeQuery(numProQuery2);
                    rs.next();
                    int numProAcD3 = rs.getInt(1);
                    rs.close();
                    endTime = System.currentTimeMillis();
                    perfAuditing.add(new Object[]{numProQuery2, new Long(endTime - startTime)});
                    String percentAce = new BigDecimal((((double) numProAce) / ((double) numPro)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                    String percentAcD3 = new BigDecimal((((double) numProAcD3) / ((double) numPro)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                    String percentAceTotal = new BigDecimal((((double) numProAce) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                    String percentAcD3Total = new BigDecimal((((double) numProAcD3) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                    report.append("\t\t+ " + numPro + " proline-starting peptides (" + percent + "%), containing a total (tri-deutero + normal) of " + (numProAcD3 + numProAce) + " acetylated peptides, of which:\n");
                    report.append("\t\t\t° " + numProAce + " were acetylated (" + percentAceTotal + "% of total, " + percentAce + "% of proline-starting peptides.)\n");
                    report.append("\t\t\t° " + numProAcD3 + " were tri-deutero acetylated (" + percentAcD3Total + "% of total, " + percentAcD3 + "% of proline-starting peptides.)\n");
                } else {
                    report.append("\t\t+ 0 proline-starting peptides found (0.0%)\n");
                }
                if (aProgress != null) {
                    advanceProgressBar(aProgress, 2);
                }
                //   2.a.5. Count the number of internal (non pyro, non-proline starting) peptides.
                String numInternalQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.modified_sequence regexp '^NH2-.*' and i.sequence not regexp '^P.*' and i.modified_sequence not regexp '^NH2-Q<Pyr>.*' and i.modified_sequence not regexp '^NH2-C<Pyc>.*'";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(numInternalQuery);
                rs.next();
                int numInternal = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{numInternalQuery, new Long(endTime - startTime)});
                percent = new BigDecimal((((double) numInternal) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\t\t+ " + numInternal + " internal (non-acetylated N-terminus, non-Pro starting, non-PyroGlu) peptides (" + percent + "%)\n");
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
                //   2.a.6. Count the number of N-terminal acetylated peptides.
                String numNtermAceQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.modified_sequence regexp '^Ace-.*' and (start=1 or start=2)";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(numNtermAceQuery);
                rs.next();
                int numNtermAce = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{numNtermAceQuery, new Long(endTime - startTime)});
                String percentNtermAce = new BigDecimal((((double) numNtermAce) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
                //   2.a.7. Count the number of internal acetylated peptides.
                String numInternalAceQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.modified_sequence regexp '^Ace-.*' and start > 2";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(numInternalAceQuery);
                rs.next();
                int numInternalAce = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{numInternalAceQuery, new Long(endTime - startTime)});
                String percentInternalAce = new BigDecimal((((double) numInternalAce) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                int sum = numNtermAce + numInternalAce;
                percent = new BigDecimal((((double) sum) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                // Report part for all acetylated.
                report.append("\t\t+ " + sum + " acetylated peptides (" + percent + "%), of which:\n");
                // Report part for N-term acetylated.
                String detailPercent = new BigDecimal((((double) numNtermAce) / ((double) sum)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\t\t\t° " + numNtermAce + " were N-terminal (starting at position 1 or 2) (" + percentNtermAce + "% of total, " + detailPercent + "% of acetylated peptides)\n");
                // Report part for internal acetylated.
                detailPercent = new BigDecimal((((double) numInternalAce) / ((double) sum)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\t\t\t° " + numInternalAce + " were internal (starting at position > 2) (" + percentInternalAce + "% of total, " + detailPercent + "% of acetylated peptides)\n");
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
                //   2.a.8. Count the number of N-terminal 3-deutero acetylated peptides.
                String numNtermAcD3Query = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.modified_sequence regexp '^AcD3-.*' and (start=1 or start=2)";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(numNtermAcD3Query);
                rs.next();
                int numNtermAcD3 = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{numNtermAcD3Query, new Long(endTime - startTime)});
                String percentNtermAcD3 = new BigDecimal((((double) numNtermAcD3) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
                //   2.a.9. Count the number of internal 3-deutero acetylated peptides.
                String numInternalAcD3Query = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.modified_sequence regexp '^AcD3-.*' and start > 2";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(numInternalAcD3Query);
                rs.next();
                int numInternalAcD3 = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{numInternalAcD3Query, new Long(endTime - startTime)});
                String percentInternalAcD3 = new BigDecimal((((double) numInternalAcD3) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                int sumD3 = numNtermAcD3 + numInternalAcD3;
                if (sumD3 > 0) {
                    percent = new BigDecimal((((double) sumD3) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                    // Report part for all D3 acetylated.
                    report.append("\t\t+ " + sumD3 + " tri-deutero acetylated peptides (" + percent + "%), of which:\n");
                    // Report part for N-term D3 acetylated.
                    String detailPercentD3 = new BigDecimal((((double) numNtermAcD3) / ((double) sumD3)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                    report.append("\t\t\t° " + numNtermAcD3 + " were N-terminal (starting at position 1 or 2) (" + percentNtermAcD3 + "% of total, " + detailPercentD3 + "% of tri-deutero acetylated peptides)\n");
                    // Report part for internal acetylated.
                    detailPercentD3 = new BigDecimal((((double) numInternalAcD3) / ((double) sumD3)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                    report.append("\t\t\t° " + numInternalAcD3 + " were internal (starting at position > 2) (" + percentInternalAcD3 + "% of total, " + detailPercentD3 + "% of tri-deutero acetylated peptides)\n");
                } else {
                    report.append("\t\t+ 0 tri-deutero acetylated peptides (0%)\n");
                }
                int totalSum = sumD3 + sum;
                String totalPercent = new BigDecimal((((double) totalSum) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\n\t\t+ " + (totalSum) + " acetylated peptides in total (tri-deutero + normal) (" + totalPercent + "%)\n");
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
                //   2.a.10. Show all N-terminal modifications.
                String nterMods = "select substring(i.modified_sequence, 1, locate('-', i.modified_sequence)-1) as 'N-terminus', count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 group by 'N-terminus'";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(nterMods);
                HashMap ntermModsTable = new HashMap();
                while (rs.next()) {
                    ntermModsTable.put(rs.getString(1), new Integer(rs.getInt(2)));
                }
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{nterMods, new Long(endTime - startTime)});
                TreeSet keys = new TreeSet(ntermModsTable.keySet());
                Iterator iter = keys.iterator();
                report.append("\n\t\t+ Summary table of all N-terminal modifications and their prevalence:\n");
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    int value = ((Integer) ntermModsTable.get(key)).intValue();
                    percent = new BigDecimal((((double) value) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                    report.append("\t\t\t° " + key + "\t" + value + "\t" + percent + "%\n");
                }
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
            } else if (iProtocol.getType().toLowerCase().indexOf("met") >= 0) {
                //  2.b. MetOx COFRADIC
                //   2.b.1. Count the number of met-containing peptides.
                String numMetQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.sequence regexp '.*M.*'";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(numMetQuery);
                rs.next();
                int numMet = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{numMetQuery, new Long(endTime - startTime)});
                percent = new BigDecimal((((double) numMet) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\t- " + numMet + " of the identified spectra contained methionine peptides (" + percent + "%)");
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
                //ToDo change the met ox counter
                //   2.b.2. Count the number of met-containing peptides that have at least one oxidized methioine.
                String numMetOxQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.modified_sequence regexp '.*M<.{0,6}\\(ox\\){1,1}.{0,4}\\(>\\){1,1}.*'"; // This regular expression takes labeled methionine oxidations as well. M<Mox>, M<C13N15ox>, M<MoxC13>
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(numMetOxQuery);
                rs.next();
                int numMetOx = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{numMetOxQuery, new Long(endTime - startTime)});
                percent = new BigDecimal((((double) numMetOx) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                String detailPercent = new BigDecimal((((double) numMetOx) / ((double) numMet)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append(" " + numMetOx + " had at least one oxidized methionine (" + percent + "% of total, " + detailPercent + "% of met-containing identifications)\n");
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
            } else if (iProtocol.getType().toLowerCase().indexOf("cys") >= 0) {
                //  2.c. Cys COFRADIC
                String numCysQuery = "select count(*) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0 and i.sequence regexp '.*C.*'";
                startTime = System.currentTimeMillis();
                rs = stat.executeQuery(numCysQuery);
                rs.next();
                int numCys = rs.getInt(1);
                rs.close();
                endTime = System.currentTimeMillis();
                perfAuditing.add(new Object[]{numCysQuery, new Long(endTime - startTime)});
                percent = new BigDecimal((((double) numCys) / ((double) numIdedSpectra)) * 100).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                report.append("\t- " + numCys + " of the identified spectra contained cysteine peptides (" + percent + "%)\n");
                if (aProgress != null) {
                    advanceProgressBar(aProgress);
                }
            } else {
                // 2.e. Non-COFRADIC
                report.append("\t- No COFRADIC technique specified.\n");
            }

            // 3. Final, shared summary.
            //  3.a. Number of unique peptides.
            String numUniquePeptidesQuery = "select count(distinct i.sequence) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0";
            startTime = System.currentTimeMillis();
            rs = stat.executeQuery(numUniquePeptidesQuery);
            rs.next();
            int numUniquePeptides = rs.getInt(1);
            rs.close();
            endTime = System.currentTimeMillis();
            perfAuditing.add(new Object[]{numUniquePeptidesQuery, new Long(endTime - startTime)});
            report.append("\n\t- " + numUniquePeptides + " unique peptide sequences have been identified.\n");
            if (aProgress != null) {
                advanceProgressBar(aProgress);
            }
            // 3.b. Number of unique proteins (no isoforms).
            String numUniqueAccessionsQuery = "select count(distinct substring(i.accession, 1, if((locate('.', i.accession)-1) >0, locate('.', i.accession)-1, length(i.accession)))) from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0";
            startTime = System.currentTimeMillis();
            rs = stat.executeQuery(numUniqueAccessionsQuery);
            rs.next();
            int numUniqueAccessions = rs.getInt(1);
            rs.close();
            endTime = System.currentTimeMillis();
            perfAuditing.add(new Object[]{numUniqueAccessionsQuery, new Long(endTime - startTime)});
            report.append("\t- " + numUniqueAccessions + " unique, versionless protein accession numbers have been identified (no isoforms).\n");
            if (aProgress != null) {
                advanceProgressBar(aProgress);
            }
            // 3.c. Number of unique proteins (with isoforms).
            String accessionsQuery = "select i.accession, i.isoforms from identification as i, spectrum as f where i.l_spectrumid=f.spectrumid and f.l_projectid=" + iProject.getProjectid() + " and i.valid > 0";
            startTime = System.currentTimeMillis();
            rs = stat.executeQuery(accessionsQuery);
            HashMap allAccessions = new HashMap();
            while (rs.next()) {
                String accession = rs.getString(1);
                // See if there is a version number.
                // If so, strip it.
                if (accession.indexOf(".") > 0) {
                    accession = accession.substring(0, accession.indexOf("."));
                }
                allAccessions.put(accession, "");
                String isoforms = rs.getString(2);
                if (isoforms != null && !isoforms.trim().equals("")) {
                    // Delete leading '^A', if any.
                    if (isoforms.startsWith("^A")) {
                        isoforms = isoforms.substring(2);
                    }
                    isoforms = isoforms.trim();
                    int start = -1;
                    while ((start = isoforms.indexOf("^A")) >= 0) {
                        String isoform = isoforms.substring(0, start).trim();
                        // See if there is an ' (12-24)' location specifier.
                        int numberpos = isoform.indexOf(" (");
                        // If so, remove it.
                        if (numberpos > 0) {
                            isoform = isoform.substring(0, numberpos).trim();
                        }
                        // See if there is a version number.
                        // If so, strip it.
                        if (isoform.indexOf(".") > 0) {
                            isoform = isoform.substring(0, isoform.indexOf(".")).trim();
                        }
                        allAccessions.put(isoform, "");
                        isoforms = isoforms.substring(start + 2);
                    }
                    // Inverted fence-post here. Don't forget the last one.
                    String isoform = isoforms.trim();
                    // See if there is an ' (12-24)' location specifier.
                    int numberpos = isoform.indexOf(" (");
                    // If so, remove it.
                    if (numberpos > 0) {
                        isoform = isoform.substring(0, numberpos).trim();
                    }
                    allAccessions.put(isoform, "");
                }
            }
            rs.close();
            endTime = System.currentTimeMillis();
            perfAuditing.add(new Object[]{accessionsQuery, new Long(endTime - startTime)});
            report.append("\t- " + allAccessions.size() + " unique, versionless protein accession numbers have been identified (with isoforms).\n");
            if (aProgress != null) {
                advanceProgressBar(aProgress);
            }
        } else {
            advanceProgressBar(aProgress, this.getNumberOfQueries() - 6);
        }
        // Print out the performance report, if desired.
        if (PERFOUTPUT) {
            logger.info("Query performance report:");
            logger.info("Query;Time (milliseconds):");
            int liSize = perfAuditing.size();
            for (int i = 0; i < liSize; i++) {
                Object[] temp = (Object[]) perfAuditing.get(i);
                logger.info(temp[0] + ";" + temp[1]);
            }
        }

        stat.close();
        return report.toString();


    }

    /**
     * This method takes care of advancing the progress bar. It simply sets the new value to current value + 1 and
     * updates the label to indicate the new percentage of progress.
     *
     * @param aProgress JProgressBar to advance with one 'tick'.
     */
    private void advanceProgressBar(JProgressBar aProgress) {
        this.advanceProgressBar(aProgress, 1);
    }

    /**
     * This method takes care of advancing the progress bar. It simply sets the new value to current value +
     * aNumberOfTicks and updates the label to indicate the new percentage of progress.
     *
     * @param aProgress JProgressBar to advance with one 'tick'.
     */
    private void advanceProgressBar(JProgressBar aProgress, int aNumberOfTicks) {
        aProgress.setValue(aProgress.getValue() + aNumberOfTicks);
        // Calculate percentage.
        double percent = (double) aProgress.getValue() / (double) aProgress.getMaximum();
        int value = (int) (new BigDecimal(percent).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100);
        aProgress.setString(value + "%");
    }
}
