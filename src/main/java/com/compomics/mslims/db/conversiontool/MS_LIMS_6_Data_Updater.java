package com.compomics.mslims.db.conversiontool;

import com.compomics.mslims.db.accessors.Identification;
import com.compomics.mslims.db.accessors.Project;
import com.compomics.mslims.db.accessors.Fragmention;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.mascotdatfile.util.mascot.*;
import com.compomics.mascotdatfile.util.mascot.fragmentions.FragmentIonImpl;
import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import com.compomics.util.interfaces.Connectable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.sql.*;
import java.util.zip.GZIPInputStream;
import java.util.*;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: Niklaas Colaert
 * Date: 7-jan-2008
 * Time: 12:23:10
 */
public class MS_LIMS_6_Data_Updater extends JFrame implements Connectable {

    /**
     * Boolean that indicates whether the tool is ran in
     * stand-alone mode ('true') or not ('false').
     */
    private static boolean iStandAlone = true;

    //gui
    private JComboBox cmbProjects;
    private JProgressBar progress;
    private JTextArea txtInfo;
    private JButton btnStart;
    private JButton btnCancel;
    private JButton btnStartAllProjects;

    private String iDBName;
    private Connection iConnection;
    private String[] iIoncoverages;
    private String[] iMod_seqs;
    private Project[] iProjects = null;
    private Project iProject = null;
    private Identification iIdentification = null;
    private double[] fragmenterrors;
    private Collection[] fragmentions;
    private boolean[] iUsed;
    private Vector datfileIds;
    private Identification[] iIdentifications = null;
    private String[] iSpectrumfileNames;


    /**
     * This constructor takes a database connection and a database name
     * and automatically sets the frame title to the default value.
     *
     * @param aConn Connection with the database connection to use.
     * @param aDBName   String with the name of the database.
     */
    public MS_LIMS_6_Data_Updater(Connection aConn, String aDBName) {
        this("MS_LIMS 6 data updater - updates old ms_lims data to version 6 data", aConn, aDBName);
    }

    /**
     * This constructor takes a single argument with the title for the frame.
     *
     * @param aName String with the title for the frame.
     */
    public MS_LIMS_6_Data_Updater(String aName) {
        this(aName, null, null);
    }

    /**
     * @param title   String with the title for the frame.
     * @param aConn   Connection to the database.
     * @param aDBName String with the database name.
     */
    public MS_LIMS_6_Data_Updater(String title, Connection aConn, String aDBName) {
        super(title);
        if (aConn == null) {
            ConnectionDialog cd = new ConnectionDialog(this, this, "Connection for updater", "IdentificationGUI.properties");
            cd.setVisible(true);
        } else {
            passConnection(aConn, aDBName);
            // If we receive a connection from
            // the outside world,
            //  assume we're not stand-alone!
            setNotStandAlone();
        }
        if (iConnection == null) {
            this.dispose();
        }
        gatherData();
        initializeComponents();
        constructScreen();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 6;
        int y = (d.height - getSize().height) / 12;
        setLocation(x, y);
        this.pack();
        this.setVisible(true);

    }

    /**
     * This method will attempt to retrieve all relevant data from the
     * local filesystem and the DB connection.
     */
    private void gatherData() {
        JOptionPane.showMessageDialog(this, new String[]{"It takes a moment to load all non ms_lims 6 non empty projects"}, "Warning!", JOptionPane.WARNING_MESSAGE);
        this.findProjects();
    }

    /**
     * This method will use the data retrieved in 'gatherData()' to fill out
     * a few components.
     */
    private void initializeComponents() {
        cmbProjects = new JComboBox(iProjects);
        cmbProjects.setMaximumSize(new Dimension(cmbProjects.getPreferredSize().width, cmbProjects.getPreferredSize().height));

    }

    public void constructScreen() {
        //The project panel
        JLabel lblProject = new JLabel("   Select a project: ");
        lblProject.setPreferredSize(new Dimension(lblProject.getPreferredSize().width, lblProject.getPreferredSize().height));


        JPanel jpanProject = new JPanel();
        jpanProject.setLayout(new BoxLayout(jpanProject, BoxLayout.X_AXIS));
        jpanProject.add(lblProject);
        jpanProject.add(Box.createHorizontalStrut(5));
        jpanProject.add(cmbProjects);
        jpanProject.add(Box.createHorizontalGlue());

        //the progress panel
        progress = new JProgressBar(JProgressBar.HORIZONTAL);
        progress.setStringPainted(true);
        progress.setString("");
        progress.setMaximumSize(new Dimension(progress.getMaximumSize().width, progress.getPreferredSize().height));
        JPanel jpanProgress = new JPanel();
        jpanProgress.setLayout(new BoxLayout(jpanProgress, BoxLayout.X_AXIS));
        jpanProgress.setBorder(BorderFactory.createTitledBorder("Progress bar"));
        jpanProgress.add(Box.createHorizontalGlue());
        jpanProgress.add(progress);
        jpanProgress.add(Box.createHorizontalGlue());
        jpanProgress.setMaximumSize(new Dimension(jpanProgress.getMaximumSize().width, progress.getPreferredSize().height));

        //the info panel
        txtInfo = new JTextArea(20, 70);
        txtInfo.setEditable(false);
        txtInfo.setMaximumSize(txtInfo.getPreferredSize());
        JScrollPane scroll = new JScrollPane(txtInfo);
        JPanel jpanScroll = new JPanel();
        jpanScroll.setLayout(new BoxLayout(jpanScroll, BoxLayout.Y_AXIS));
        jpanScroll.setBorder(BorderFactory.createTitledBorder("Information ... "));
        jpanScroll.add(scroll);

        //the button panel
        btnStartAllProjects = new JButton("Update all projects");
        btnStartAllProjects.setMnemonic(KeyEvent.VK_A);
        btnStartAllProjects.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnStartAllProjectsPressed();
            }
        });

        btnStart = new JButton("Update selected project");
        btnStart.setMnemonic(KeyEvent.VK_U);
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnStartPressed();
            }
        });
        btnCancel = new JButton("Cancel");
        btnCancel.setMnemonic(KeyEvent.VK_C);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnStartAllProjects);
        jpanButtons.add(Box.createRigidArea(new Dimension(10, btnStart.getHeight())));
        jpanButtons.add(btnStart);
        jpanButtons.add(Box.createRigidArea(new Dimension(10, btnStart.getHeight())));
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createRigidArea(new Dimension(10, btnStart.getHeight())));

        JPanel jpanAll = new JPanel();
        jpanAll.setLayout(new BoxLayout(jpanAll, BoxLayout.Y_AXIS));
        jpanAll.add(Box.createVerticalStrut(10));
        jpanAll.add(jpanProject);
        jpanAll.add(Box.createVerticalStrut(10));
        jpanAll.add(jpanProgress);
        jpanAll.add(Box.createVerticalStrut(10));
        jpanAll.add(jpanScroll);
        jpanAll.add(Box.createVerticalStrut(10));
        jpanAll.add(jpanButtons);
        jpanAll.add(Box.createVerticalStrut(10));

        // Create a pane
        JScrollPane jscrollMain = new JScrollPane(jpanAll);
        this.getContentPane().add(jscrollMain, BorderLayout.CENTER);

    }

    /*
    * update the selected non ms_lims 6 project
    */
    public void updateProject(Project aProject) {
        iProject = aProject;
        progress.setIndeterminate(true);
        progress.setString("");

        com.compomics.util.sun.SwingWorker updater = new com.compomics.util.sun.SwingWorker() {
            private long startUpdating = System.currentTimeMillis();
            Boolean error = false;

            public Boolean construct() {
                try {
                    // Get all the datfileids for one project
                    long projectid = iProject.getProjectid();
                    PreparedStatement prepDatIds = null;
                    prepDatIds = iConnection.prepareStatement("select i.l_datfileid from identification as i, spectrumfile as s where i.l_spectrumfileid = s.spectrumfileid and s.l_projectid = ? group by i.l_datfileid");
                    prepDatIds.setLong(1, projectid);
                    ResultSet rsDatIds = prepDatIds.executeQuery();
                    datfileIds = new Vector();
                    while (rsDatIds.next()) {
                        datfileIds.add(rsDatIds.getLong(1));
                    }
                    prepDatIds.close();
                    rsDatIds.close();

                    // Analyse all different datfiles
                    for (int i = 0; i < datfileIds.size(); i++) {
                        // Get all the identifications for one project and for one datfileid
                        String addMysql = " i.l_datfileid = " + datfileIds.get(i);
                        iIdentifications = Identification.getAllIdentificationsforProject(iConnection, projectid, addMysql);
                        // Get for every identification the spectrumfilename and the threshold
                        iSpectrumfileNames = new String[iIdentifications.length];
                        iUsed = new boolean[iIdentifications.length];
                        for (int j = 0; j < iIdentifications.length; j++) {
                            iUsed[j] = false;
                            long spectrumfileid = iIdentifications[j].getL_spectrumfileid();
                            PreparedStatement prepSpec = null;
                            prepSpec = iConnection.prepareStatement("select s.filename from spectrumfile as s where s.spectrumfileid = ? ");
                            prepSpec.setLong(1, spectrumfileid);
                            ResultSet rsSpec = prepSpec.executeQuery();
                            while (rsSpec.next()) {
                                iSpectrumfileNames[j] = rsSpec.getString(1);
                            }
                            prepSpec.close();
                            rsSpec.close();

                        }

                        // Get datfile and create a MascotDatfile object
                        MascotDatfile mcdf = null;
                        PreparedStatement prepDat = null;
                        prepDat = iConnection.prepareStatement("select file, filename from datfile where datfileid = ?");
                        prepDat.setLong(1, (Long) datfileIds.get(i));
                        ResultSet rsDat = prepDat.executeQuery();
                        boolean errorDat = false;
                        while (rsDat.next()) {
                            byte[] zipped = rsDat.getBytes(1);
                            ByteArrayInputStream bais = new ByteArrayInputStream(zipped);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(bais));
                            BufferedOutputStream bos = new BufferedOutputStream(baos);
                            int read = -1;
                            while ((read = bis.read()) != -1) {
                                bos.write(read);
                            }
                            bos.flush();
                            baos.flush();
                            byte[] result = baos.toByteArray();
                            bos.close();
                            bis.close();
                            bais.close();
                            baos.close();
                            BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(result))));
                            try {
                                mcdf = new MascotDatfile(in);
                            } catch (IllegalArgumentException e) {
                                errorDat = true;
                                System.err.println("Error in reading datfile " + e + " for datfile: " + datfileIds.get(i) + " (" + rsDat.getString(2) + ")");
                                txtInfo.append("\nError in reading datfile " + e + " for datfile: " + datfileIds.get(i) + " (" + rsDat.getString(2) + ")");
                            }
                        }
                        prepDat.close();
                        rsDat.close();
                        try {
                            if (!errorDat) {
                                setUpdateParameters(mcdf);
                                boolean allIdsDone = true;

                                for (int j = 0; j < iUsed.length; j++) {
                                    if (!iUsed[j]) {
                                        allIdsDone = false;
                                    }
                                }
                                if (!allIdsDone) {
                                    setUpdateParameters2(mcdf);
                                }
                                for (int k = 0; k < iMod_seqs.length; k++) {
                                    if (iMod_seqs[k] == null) {
                                        for (int l = 0; l < iIdentifications.length; l++) {
                                            if ((iSpectrumfileNames[l].equalsIgnoreCase(iSpectrumfileNames[k])) && k != l) {
                                                iMod_seqs[k] = iMod_seqs[l];
                                                iIoncoverages[k] = iIoncoverages[l];
                                            }
                                        }
                                    }
                                    if (iMod_seqs[k] == null) {
                                        for (int l = 0; l < iIdentifications.length; l++) {
                                            if ((iIdentifications[l].getSequence().equalsIgnoreCase(iIdentifications[k].getSequence())) && (iIdentifications[l].getScore() == iIdentifications[k].getScore()) && (iIdentifications[l].getExp_mass().equals(iIdentifications[k].getExp_mass())) && k != l) {
                                                iMod_seqs[k] = iMod_seqs[l];
                                                iIoncoverages[k] = iIoncoverages[l];
                                            }
                                        }
                                    }
                                }
                                updateIdentifications();
                                storeFragmentions();
                            }

                        } catch (NumberFormatException n) {
                            errorDat = true;
                            txtInfo.append("\n" + "Number Format exception in datfile with datfileid = " + datfileIds.get(i) + " unable to update some identifications for project " + iProject.getProjectid());
                            System.out.println("Number Format exception in datfile with datfileid = " + datfileIds.get(i) + " unable to update some identifications for project " + iProject.getProjectid());
                        } catch (IllegalArgumentException e) {
                            System.out.println(e);
                            errorDat = true;
                            txtInfo.append("\n" + e);
                        }
                        prepDat.close();
                        rsDat.close();
                        txtInfo.append("\nUpdated identifications (and stored fragmentions) for datfile " + datfileIds.get(i));
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    error = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    error = true;
                }
                return error;
            }

            public void finished() {
                if (!error) {
                    long stopUpdating = System.currentTimeMillis();
                    double totalTime = 0.0;
                    boolean inSeconds = false;
                    totalTime = stopUpdating - startUpdating;
                    if (totalTime > 1000) {
                        totalTime /= 1000.0;
                        inSeconds = true;
                    }
                    String duration = new BigDecimal(totalTime).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + (inSeconds ? " seconds" : " milliseconds");
                    System.out.println(duration);
                    progress.setIndeterminate(false);
                    progress.setString("Updated project " + cmbProjects.getSelectedItem() + " in " + duration);
                    btnCancel.setText("Exit");
                    txtInfo.append("\nUpdated project " + cmbProjects.getSelectedItem() + " in " + duration);
                    txtInfo.updateUI();
                } else {
                    progress.setIndeterminate(false);
                    progress.setString("ERROR");
                }
                btnStart.setEnabled(false);
                btnStartAllProjects.setEnabled(false);

            }

        };
        updater.start();
    }

    /*
    * Update all the non ms_lims 6 projects
    */

    public void btnStartAllProjectsPressed() {
        progress.setIndeterminate(true);
        progress.setString("");
        txtInfo.setText("");

        //vector where all the projects that encoutered errors while updating will be stored
        final Vector errorProjects = new Vector();

        com.compomics.util.sun.SwingWorker updater = new com.compomics.util.sun.SwingWorker() {
            private long startUpdatingAll = System.currentTimeMillis();
            Boolean error = false;

            public Boolean construct() {

                for (int k = 0; k < iProjects.length; k++) {
                    boolean errorDat = false;
                    long startUpdating = System.currentTimeMillis();
                    iProject = iProjects[k];
                    progress.setIndeterminate(true);
                    progress.setString("");
                    try {
                        // Get all the datfileids for one project
                        long projectid = iProject.getProjectid();
                        PreparedStatement prepDatIds = null;
                        prepDatIds = iConnection.prepareStatement("select i.l_datfileid from identification as i, spectrumfile as s where i.l_spectrumfileid = s.spectrumfileid and s.l_projectid = ? group by i.l_datfileid");
                        prepDatIds.setLong(1, projectid);
                        ResultSet rsDatIds = prepDatIds.executeQuery();
                        datfileIds = new Vector();
                        while (rsDatIds.next()) {
                            datfileIds.add(rsDatIds.getLong(1));
                        }
                        prepDatIds.close();
                        rsDatIds.close();

                        // Analyse all different datfiles
                        for (int i = 0; i < datfileIds.size(); i++) {
                            // Get all the identifications for one project and for one datfileid
                            String addMysql = " i.l_datfileid = " + datfileIds.get(i);
                            iIdentifications = Identification.getAllIdentificationsforProject(iConnection, projectid, addMysql);
                            // Get for every identification the spectrumfilename and the threshold
                            iSpectrumfileNames = new String[iIdentifications.length];
                            iUsed = new boolean[iIdentifications.length];

                            for (int j = 0; j < iIdentifications.length; j++) {
                                iUsed[j] = false;
                                long spectrumfileid = iIdentifications[j].getL_spectrumfileid();
                                PreparedStatement prepSpec = null;
                                prepSpec = iConnection.prepareStatement("select s.filename from spectrumfile as s where s.spectrumfileid = ? ");
                                prepSpec.setLong(1, spectrumfileid);
                                ResultSet rsSpec = prepSpec.executeQuery();
                                while (rsSpec.next()) {
                                    iSpectrumfileNames[j] = rsSpec.getString(1);
                                }
                                prepSpec.close();
                                rsSpec.close();

                            }

                            // Get datfile and create a MascotDatfile object
                            MascotDatfile mcdf = null;
                            PreparedStatement prepDat = null;
                            prepDat = iConnection.prepareStatement("select file, filename from datfile where datfileid = ?");
                            prepDat.setLong(1, (Long) datfileIds.get(i));
                            ResultSet rsDat = prepDat.executeQuery();

                            while (rsDat.next()) {
                                byte[] zipped = rsDat.getBytes(1);
                                ByteArrayInputStream bais = new ByteArrayInputStream(zipped);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(bais));
                                BufferedOutputStream bos = new BufferedOutputStream(baos);
                                int read = -1;
                                while ((read = bis.read()) != -1) {
                                    bos.write(read);
                                }
                                bos.flush();
                                baos.flush();
                                byte[] result = baos.toByteArray();
                                bos.close();
                                bis.close();
                                bais.close();
                                baos.close();
                                BufferedReader in = new BufferedReader(new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(result))));
                                try {
                                    mcdf = new MascotDatfile(in);
                                } catch (IllegalArgumentException e) {
                                    // There was an error reading the datfile!!!!
                                    errorDat = true;
                                    System.err.println("Error in reading datfile " + e + " for datfile: " + datfileIds.get(i) + " (" + rsDat.getString(2) + ")");
                                    txtInfo.append("\nError in reading datfile " + e + " for datfile: " + datfileIds.get(i) + " (" + rsDat.getString(2) + ")");
                                }

                            }

                            try {

                                //only use the datfile if it didn't cause any problems
                                if (!errorDat) {
                                    if (mcdf != null) {
                                        //The update parameters will be searched
                                        setUpdateParameters(mcdf);
                                        boolean allIdsDone = true;

                                        //check if all the identifications have the parameters for an updata, if not=> not all ids are done
                                        for (int j = 0; j < iUsed.length; j++) {
                                            if (!iUsed[j]) {
                                                allIdsDone = false;
                                            }
                                        }
                                        //The update parameters will be searched for the identifications who don't have the parametes!
                                        if (!allIdsDone) {
                                            setUpdateParameters2(mcdf);
                                        }
                                        // if some of the modified sequence aren't found
                                        for (int m = 0; m < iMod_seqs.length; m++) {
                                            // if some of the modified sequence aren't found use the mod_seq for an identification with the same spectrumfilename
                                            if (iMod_seqs[m] == null) {
                                                for (int l = 0; l < iIdentifications.length; l++) {
                                                    if ((iSpectrumfileNames[l].equalsIgnoreCase(iSpectrumfileNames[m])) && m != l) {
                                                        iMod_seqs[m] = iMod_seqs[l];
                                                        iIoncoverages[m] = iIoncoverages[l];
                                                    }
                                                }
                                            }
                                            // if some of the modified sequence aren't found use the mod_seq for an identification with the same peptide, score and mass
                                            if (iMod_seqs[m] == null) {
                                                for (int l = 0; l < iIdentifications.length; l++) {
                                                    if ((iIdentifications[l].getSequence().equalsIgnoreCase(iIdentifications[m].getSequence())) && (iIdentifications[l].getScore() == iIdentifications[m].getScore()) && (iIdentifications[l].getExp_mass().equals(iIdentifications[m].getExp_mass())) && m != l) {
                                                        iMod_seqs[m] = iMod_seqs[l];
                                                        iIoncoverages[m] = iIoncoverages[l];
                                                    }
                                                }
                                            }
                                        }
                                        updateIdentifications();
                                        storeFragmentions();
                                    }

                                }
                            } catch (NumberFormatException n) {
                                errorDat = true;
                                txtInfo.append("\n" + "Number Format exception in datfile with datfileid = " + datfileIds.get(i) + " unable to update some identifications for project " + iProject.getProjectid());
                                System.out.println("Number Format exception in datfile with datfileid = " + datfileIds.get(i) + " unable to update some identifications for project " + iProject.getProjectid());
                            } catch (IllegalArgumentException e) {
                                System.out.println(e);
                                errorDat = true;
                                txtInfo.append("\n" + e);
                            }
                            prepDat.close();
                            rsDat.close();
                            //txtInfo.append("\n Updated identifications (and stored fragmentions) for datfile " + datfileIds.get(i));
                            //txtInfo.updateUI();
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    long stopUpdating = System.currentTimeMillis();
                    double totalTime = 0.0;
                    boolean inSeconds = false;
                    totalTime = stopUpdating - startUpdating;
                    if (totalTime > 1000) {
                        totalTime /= 1000.0;
                        inSeconds = true;
                    }
                    String duration = new BigDecimal(totalTime).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + (inSeconds ? " seconds" : " milliseconds");
                    System.out.println(duration);
                    if (errorDat) {
                        txtInfo.append("\nUpdated project with errors! " + iProject + " in " + duration + " with " + datfileIds.size() + " datfiles ");
                        errorProjects.add(iProject);
                    } else {
                        txtInfo.append("\nUpdated project " + iProject + " in " + duration + " with " + datfileIds.size() + " datfiles");
                    }

                }
                return error;
            }

            public void finished() {
                if (!error) {
                    long stopUpdatingAll = System.currentTimeMillis();
                    double totalTime = 0.0;
                    boolean inSeconds = false;
                    totalTime = stopUpdatingAll - startUpdatingAll;
                    if (totalTime > 1000) {
                        totalTime /= 1000.0;
                        inSeconds = true;
                    }
                    String duration = new BigDecimal(totalTime).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + (inSeconds ? " seconds" : " milliseconds");
                    progress.setIndeterminate(false);
                    progress.setString("Updated all projects in " + duration);
                    btnCancel.setText("Exit");
                    txtInfo.append("\nUpdated all projects in " + duration);
                    txtInfo.append("\nAn error occured in the following projects : ");
                    for (int i = 0; i < errorProjects.size(); i++) {
                        Project pro = (Project) errorProjects.get(i);
                        txtInfo.append((Long) pro.getProjectid() + " , ");
                    }
                } else {
                    progress.setIndeterminate(false);
                    progress.setString("ERROR");
                }
                btnStart.setEnabled(false);
                btnStartAllProjects.setEnabled(false);
            }

        };
        updater.start();


    }

    public void btnStartPressed() {
        updateProject((Project) cmbProjects.getSelectedItem());
    }

    private void setUpdateParameters(MascotDatfile aMDF) throws IllegalArgumentException, NumberFormatException {
        // create the arrays
        iMod_seqs = new String[iIdentifications.length];
        iIoncoverages = new String[iIdentifications.length];
        fragmenterrors = new double[iIdentifications.length];
        fragmentions = new Collection[iIdentifications.length];

        // Get the generic parameters, and the masses for the search
        Parameters parameters = aMDF.getParametersSection();
        String searchTitle = parameters.getCom();
        if (searchTitle == null) {
            searchTitle = "!No title specified";
        } else {
            int location = searchTitle.indexOf("|");
            if (location >= 0) {
                searchTitle = searchTitle.substring(0, location).trim();
            }
        }
        Masses masses = aMDF.getMasses();

        // Rank of the hit (only highest ranking hits
        // (i.e.: rank = 1)) are considered,
        int rank = 1;

        // Get all the queries...
        Vector queries = aMDF.getQueryList();
        // Map to transfer query ID into peptidehits.
        QueryToPeptideMap queryToPepMap = aMDF.getQueryToPeptideMap();
        Iterator iter = queries.iterator();
        while (iter.hasNext()) {
            // Get the query.
            Query query = (Query) iter.next();
            //check if it's a query we need
            boolean queryNeeded = false;
            int position = 0;           // the position in the different arrays
            //check wich query is needed
            for (int i = 0; i < iSpectrumfileNames.length; i++) {
                if (iSpectrumfileNames[i].equalsIgnoreCase(query.getTitle())) {
                    queryNeeded = true;
                    position = i;
                    iUsed[i] = true;
                }
            }

            if (queryNeeded) {
                // We can use this query!
                // Get the first ranking peptide hit, if any.
                PeptideHit ph = queryToPepMap.getPeptideHitOfOneQuery(query.getQueryNumber(), rank);
                if (ph != null && ph.scoresAboveIdentityThreshold(iIdentifications[position].getConfidence().doubleValue())) {

                    iMod_seqs[position] = ph.getModifiedSequence();

                    // If a modified sequence contain's a '#' character, this means the modification was not included in the modificationConversion.txt file.
                    // Throw an error since we don't want to have multiple names for identical modifications.
                    if (iMod_seqs[position].indexOf('#') != -1) {
                        throw new IllegalArgumentException("\n\nModificationConversion.txt does not contain enough information to parse the following identification:\n\t" + iMod_seqs[position] + "\nPlease add the modification into modificationcoverions.txt. ");
                    }

                    // Add the ion coverage String.
                    PeptideHitAnnotation pha = ph.getPeptideHitAnnotation(masses, parameters, query.getPrecursorMZ(), query.getChargeString());
                    iIoncoverages[position] = getIonCoverage(ph, query, pha);

                    // Calling this method will initialize all mass deltas between matched peaks.
                    pha.getMatchedBYions(query.getPeakList());
                    // Calling this method will initialize the ion importance as determined by Mascot.
                    fragmentions[position] = pha.getFusedMatchedIons(query.getPeakList(), ph.getPeaksUsedFromIons1(), query.getMaxIntensity(), 0.10);

                    fragmenterrors[position] = Double.parseDouble(parameters.getITOL());
                    String fragmentErrorUnit = parameters.getITOLU();
                    if (fragmentErrorUnit.trim().toLowerCase().equals("ppm")) {
                        fragmenterrors[position] = query.getPrecursorMZ() * fragmenterrors[position] * 1e-6;
                    }
                }
            }
        }
    }

    /*
    *This method will be used id not all the identifications could be updated with the method setUpdateParamters
    */
    private void setUpdateParameters2(MascotDatfile aMDF) throws IllegalArgumentException, NumberFormatException {
        // create the arrays
        iMod_seqs = new String[iIdentifications.length];
        iIoncoverages = new String[iIdentifications.length];
        fragmenterrors = new double[iIdentifications.length];
        fragmentions = new Collection[iIdentifications.length];

        // Get the generic parameters, and the masses for the search
        Parameters parameters = aMDF.getParametersSection();
        String searchTitle = parameters.getCom();
        if (searchTitle == null) {
            searchTitle = "!No title specified";
        } else {
            int location = searchTitle.indexOf("|");
            if (location >= 0) {
                searchTitle = searchTitle.substring(0, location).trim();
            }
        }
        Masses masses = aMDF.getMasses();

        // Rank of the hit (only highest ranking hits
        // (i.e.: rank = 1)) are considered,
        int rank = 1;

        // Get all the queries...
        Vector queries = aMDF.getQueryList();
        // Map to transfer query ID into peptidehits.
        QueryToPeptideMap queryToPepMap = aMDF.getQueryToPeptideMap();
        Iterator iter = queries.iterator();
        while (iter.hasNext()) {
            // Get the query.
            Query query = (Query) iter.next();
            //check if it's a query we need

            // Get the first ranking peptide hit, if any.
            PeptideHit ph = queryToPepMap.getPeptideHitOfOneQuery(query.getQueryNumber(), rank);
            if (ph != null) {
                // the position in the different arrays
                int position = 0;
                boolean phFound = false;
                for (int i = 0; i < iIdentifications.length; i++) {
                    if (!iUsed[i]) {
                        double idPre = iIdentifications[i].getExp_mass().doubleValue();
                        double quPre = query.getPrecursorMass();
                        boolean mass = false;
                        if ((idPre > quPre - 0.00009) && (idPre < quPre + 0.00009)) {
                            mass = true;
                        }

                        if ((iIdentifications[i].getScore() > (ph.getIonsScore() - 1)) && (iIdentifications[i].getScore() < (ph.getIonsScore() + 1)) && (iIdentifications[i].getSequence().equalsIgnoreCase(ph.getSequence())) && mass) {
                            phFound = true;
                            position = i;
                            iUsed[i] = true;
                        }
                    }
                }

                if (ph.scoresAboveIdentityThreshold(iIdentifications[position].getConfidence().doubleValue()) && phFound) {

                    iMod_seqs[position] = ph.getModifiedSequence();

                    // If a modified sequence contain's a '#' character, this means the modification was not included in the modificationConversion.txt file.
                    // Throw an error since we don't want to have multiple names for identical modifications.
                    if (iMod_seqs[position].indexOf('#') != -1) {
                        throw new IllegalArgumentException("\n\nModificationConversion.txt does not contain enough information to parse the following identification:\n\t" + iMod_seqs[position] + "\nPlease add the modification into modificationcoverions.txt. ");
                    }

                    // Add the ion coverage String.
                    PeptideHitAnnotation pha = ph.getPeptideHitAnnotation(masses, parameters, query.getPrecursorMZ(), query.getChargeString());
                    iIoncoverages[position] = getIonCoverage(ph, query, pha);

                    // Calling this method will initialize all mass deltas between matched peaks.
                    pha.getMatchedBYions(query.getPeakList());
                    // Calling this method will initialize the ion importance as determined by Mascot.
                    fragmentions[position] = pha.getFusedMatchedIons(query.getPeakList(), ph.getPeaksUsedFromIons1(), query.getMaxIntensity(), 0.10);

                    fragmenterrors[position] = Double.parseDouble(parameters.getITOL());
                    String fragmentErrorUnit = parameters.getITOLU();
                    if (fragmentErrorUnit.trim().toLowerCase().equals("ppm")) {
                        fragmenterrors[position] = query.getPrecursorMZ() * fragmenterrors[position] * 1e-6;
                    }
                }
            }
        }
    }


    /*
    * Store the new fragmentions
    */
    private void storeFragmentions() throws SQLException {

        for (int i = 0; i < iIdentifications.length; i++) {
            long identificationid = iIdentifications[i].getIdentificationid();
            // check if there aren't any fragmentations stored for this identification
            boolean fragmentionFound = false;
            PreparedStatement prepFra = null;
            prepFra = iConnection.prepareStatement("select f.fragmentionid from fragmention as f where f.l_identificationid = ? ");
            prepFra.setLong(1, identificationid);
            ResultSet rsFra = prepFra.executeQuery();
            while (rsFra.next()) {
                fragmentionFound = true;
            }
            prepFra.close();
            rsFra.close();

            //only update if there are no fragmentions already in the database
            if (!fragmentionFound) {
                // no fragmentions found => store the new ones
                if (fragmentions[i] != null) {
                    Iterator iter = fragmentions[i].iterator();
                    while (iter.hasNext()) {
                        FragmentIonImpl fi = (FragmentIonImpl) iter.next();
                        HashMap hm = new HashMap();
                        hm.put(Fragmention.FRAGMENTIONNUMBER, new Long(fi.getNumber()));
                        hm.put(Fragmention.INTENSITY, new Long(new Double(fi.getIntensity()).longValue()));
                        hm.put(Fragmention.IONNAME, fi.getType());
                        hm.put(Fragmention.IONTYPE, new Long(fi.getID()));
                        hm.put(Fragmention.L_IDENTIFICATIONID, new Long(identificationid));
                        hm.put(Fragmention.L_IONSCORINGID, new Long(fi.getImportance()));
                        hm.put(Fragmention.MASSDELTA, new Double(new BigDecimal(fi.getTheoreticalExperimantalMassError()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()));
                        hm.put(Fragmention.MASSERRORMARGIN, new Double(fragmenterrors[i]));
                        hm.put(Fragmention.MZ, new Double(new BigDecimal(fi.getMZ()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()));

                        Fragmention fi_db = new Fragmention(hm);
                        fi_db.persist(iConnection);
                    }
                }
            }
        }
    }

    /*
    * update the identifications
    */
    private void updateIdentifications() throws SQLException {
        for (int i = 0; i < iIdentifications.length; i++) {
            iIdentification = iIdentifications[i];
            long identificationid = iIdentification.getIdentificationid();
            // check if there is no ioncoverage stored for this identification 
            boolean ioncoverageFound = false;
            PreparedStatement prepIon = null;
            prepIon = iConnection.prepareStatement("select i.ion_coverage from identification as i where i.identificationid = ? ");
            prepIon.setLong(1, identificationid);
            ResultSet rsIon = prepIon.executeQuery();
            while (rsIon.next()) {
                String ioncoverage = rsIon.getString(1);
                if (ioncoverage != null) {
                    ioncoverageFound = true;
                }
            }
            prepIon.close();
            rsIon.close();
            // only update if there is no ioncoverage is found and the mod_seq and the ioncoverage to update is not null
            if (!ioncoverageFound) {
                if (iMod_seqs[i] != null && iIoncoverages[i] != null) {
                    iIdentification.setModified_sequence(iMod_seqs[i]);
                    iIdentification.setIon_coverage(iIoncoverages[i]);
                    iIdentification.update(iConnection);
                }

            }

        }

    }

    /*
    * Get a html string with the ion coverage
    */
    private String getIonCoverage(PeptideHit ph, Query query, PeptideHitAnnotation pha) {
        // Match Mascot ions.
        Vector ions = pha.getMatchedIonsByMascot(query.getPeakList(), ph.getPeaksUsedFromIons1());
        // Peptide sequence + length.
        String sequence = ph.getSequence();
        int length = sequence.length();
        // Create Y and B boolean arrays.
        boolean[] yIons = new boolean[length];
        boolean[] bIons = new boolean[length];
        // Fill out arrays.
        for (int i = 0; i < ions.size(); i++) {
            FragmentIon lFragmentIon = (FragmentIon) ions.elementAt(i);
            switch (lFragmentIon.getID()) {
                case FragmentIon.Y_ION:
                    yIons[lFragmentIon.getNumber() - 1] = true;
                    if (yIons.length == lFragmentIon.getNumber() + 1) {
                        yIons[yIons.length - 1] = true;
                    }
                    break;

                case FragmentIon.Y_DOUBLE_ION:
                    yIons[lFragmentIon.getNumber() - 1] = true;
                    if (yIons.length == lFragmentIon.getNumber() + 1) {
                        yIons[yIons.length - 1] = true;
                    }
                    break;

                case FragmentIon.B_ION:
                    bIons[lFragmentIon.getNumber() - 1] = true;
                    if (bIons.length == lFragmentIon.getNumber() + 1) {
                        bIons[bIons.length - 1] = true;
                    }
                    break;

                case FragmentIon.B_DOUBLE_ION:
                    bIons[lFragmentIon.getNumber() - 1] = true;
                    if (bIons.length == lFragmentIon.getNumber() + 1) {
                        bIons[bIons.length - 1] = true;
                    }
                    break;

                default:
                    // Skip other fragmentions.
            }
        }
        // Now simply add formatting.
        String[] modifiedAA = ph.getModifiedSequenceComponents();
        StringBuffer formattedSequence = new StringBuffer("<html>");
        // Cycle the amino acids (using b-ions indexing here).
        for (int i = 0; i < bIons.length; i++) {
            boolean italic = false;
            boolean bold = false;
            // First and last one only have 50% coverage anyway
            if (i == 0) {
                if (bIons[i]) {
                    italic = true;
                }
                if (yIons[yIons.length - (i + 1)] && yIons[yIons.length - (i + 2)]) {
                    if (yIons[yIons.length - (i + 3)]) {
                        bold = true;
                    }
                }
            } else if (i == (length - 1)) {
                if (bIons[i] && bIons[i - 1]) {
                    if (bIons[i - 2]) {
                        italic = true;
                    }
                }
                if (yIons[yIons.length - (i + 1)]) {
                    bold = true;
                }
            } else {
                // Aha, two ions needed here.
                if (bIons[i] && bIons[i - 1]) {
                    italic = true;
                }
                if (yIons[yIons.length - (i + 1)] && yIons[yIons.length - (i + 2)]) {
                    bold = true;
                }
            }
            // Actually add the next char.
            formattedSequence.append(
                    (italic ? "<u>" : "") +
                            (bold ? "<font color=\"red\">" : "") +
                            modifiedAA[i].replaceAll("<", "&lt;").replaceAll(">", "&gt;") +
                            (italic ? "</u>" : "") +
                            (bold ? "</font>" : "")
            );
        }
        // Finalize HTML'ized label text.
        formattedSequence.append("</html>");

        return formattedSequence.toString();
    }


    /**
     * This method finds all non empty, non ms_lims 6 project entries currently stored in the DB
     * and fills out the relevant arrays with info.
     */
    private void findProjects() {
        try {
            //iProjects = com.compomics.mslims.db.accessors.Project.getAllProjects(iConnection);
            Project[] projectsAll = com.compomics.mslims.db.accessors.Project.getAllProjects(iConnection);
            Vector selectedProjects = new Vector();
            //check if the projects are projcets that needs to be updated
            for (int i = 0; i < projectsAll.length; i++) {

                PreparedStatement prep = null;
                prep = iConnection.prepareStatement("select i.ion_coverage from identification as i , spectrumfile as s where i.l_spectrumfileid = s.spectrumfileid and s.l_projectid = ?");
                prep.setLong(1, projectsAll[i].getProjectid());
                ResultSet rs = prep.executeQuery();

                boolean oldProject = false;
                boolean emptyProject = true;
                while (rs.next()) {
                    emptyProject = false;
                    if (rs.getString(1) == null) {
                        oldProject = true;
                    }
                    //rs.last();
                }
                if (oldProject && !emptyProject) {
                    selectedProjects.add(projectsAll[i]);
                }
                rs.close();
                prep.close();
                System.out.println("Analysed project : " + projectsAll[i]);
            }
            iProjects = new Project[selectedProjects.size()];
            selectedProjects.toArray(iProjects);

        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, new String[]{"Unable to find projects:  ", sqle.getMessage()}, "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method will be called by the class actually making the connection.
     * It will pass the connection and an identifier String for that connection
     * (typically the name of the database connected to).
     *
     * @param aConn   Connection with the DB connection.
     * @param aDBName String with an identifier for the connection, typically the
     *                name of the DB connected to.
     */
    public void passConnection(Connection aConn, String aDBName) {
        if (aConn == null) {
            close();
        } else {
            this.iConnection = aConn;
            this.iDBName = aDBName;
        }
    }

    /**
     * This method is called when the frame is closed. It shuts down the JVM.
     */
    private void close() {
        this.dispose();
        if(iStandAlone) {
            if(iConnection != null) {
                try {
                    iConnection.close();
                    System.out.println("DB connection closed.");
                } catch(Exception e) {
                    System.err.println("\n\nUnable to close DB connection: " + e.getMessage() + "\n\n");
                }
            }
            System.exit(0);
        }
    }

    /**
     * This method should be called when the application is
     * not launched in stand-alone mode.
     */
    public static void setNotStandAlone() {
        iStandAlone = false;
    }

    public boolean isStandAlone() {
        return iStandAlone;
    }

    /**
     * The main
     */
    public static void main(String[] args) {
        MS_LIMS_6_Data_Updater updater = new MS_LIMS_6_Data_Updater("MS_LIMS 6 data updater - updates old ms_lims data to version 6 data");
    }
}


