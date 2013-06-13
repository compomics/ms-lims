package com.compomics.mslims.gui;

import com.compomics.mslims.db.accessors.Project;
import com.compomics.mslims.db.utils.CopyProject;
import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.util.interfaces.Connectable;
import com.compomics.util.io.PropertiesManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Davy
 * Date: 30/09/11
 * Time: 9:34
 */
public class ProjectExtractor extends JFrame implements Connectable {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ProjectExtractor.class);

    private String propertiesfile;
    private JLabel lblcopyto;
    private JComboBox dropdownprojectnumber;
    private JLabel lblprojectnumber;
    private JButton btnexecutetransfer;
    private JButton btncopyto;
    private String iCurrentTitle = "Project Extractor";
    private static boolean iStandAlone;
    private Project[] iProjects = null;
    private long currentProject;
    private ProjectExtractor projectExtractor;
    private Connection fromconn;
    private Connection toconn;


    /**
     * Creates new form ProjectExtractorFrame
     *
     * @param aConn the connection to the MS_LIMS db
     * @throws java.sql.SQLException if the database is not available
     */
    public ProjectExtractor(Connection aConn) throws SQLException {
        this.fromconn = aConn;
        this.setTitle(iCurrentTitle);
        projectExtractor = this;
        initComponents();
        iProjects = Project.getAllProjects(fromconn);
        this.fillProjectPulldown();
        this.setContentPane(getContentPane());
        this.setLocation(150, 150);
        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form
     *
     * @throws java.sql.SQLException throws error
     */

    private void initComponents() throws SQLException {

        dropdownprojectnumber = new JComboBox();
        lblprojectnumber = new JLabel();
        btnexecutetransfer = new JButton();
        btncopyto = new JButton();
        lblcopyto = new JLabel();


        btncopyto.setText("open...");

        lblcopyto.setText("ms lims database to copy project to");

        lblprojectnumber.setText("project number");

        btnexecutetransfer.setText("execute");

        dropdownprojectnumber.setModel(new DefaultComboBoxModel(getAllProjects(fromconn)));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(lblprojectnumber)
                                        .addComponent(lblcopyto)
                                        .addComponent(btncopyto)
                                        .addComponent(btnexecutetransfer)
                                        .addComponent(dropdownprojectnumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblprojectnumber)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(dropdownprojectnumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblcopyto)
                                .addGap(18, 18, 18)
                                .addComponent(btncopyto)
                                .addGap(18, 18, 18)
                                .addComponent(btnexecutetransfer)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btncopyto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    getConnection();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        });
        btnexecutetransfer.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int questionpane = JOptionPane.showConfirmDialog(ProjectExtractor.this, "Please confirm project copying", "Really copy project '" + dropdownprojectnumber.getSelectedItem().toString() + "'?", JOptionPane.YES_NO_OPTION);
                if (questionpane == JOptionPane.YES_OPTION) {
                    CopyProject copyProjectExecute = new CopyProject(fromconn, toconn, currentProject);

                    copyProjectExecute = null;
                }
                } catch (SQLException e) {
                    logger.error(e);
                   JOptionPane.showMessageDialog(ProjectExtractor.this,"The following error has occured while copying: "+e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        });
        dropdownprojectnumber.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange() == ItemEvent.SELECTED) {
                    Project selected = (Project) ie.getItem();
                    stateChangedProject(selected);

                }
            }
        });

    }

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException means that one of the db's is not correct
     */
    public static void main(String args[]) throws SQLException {
        /* Set the Nimbus look and feel */
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProjectExtractor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProjectExtractor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProjectExtractor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProjectExtractor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
    }

    public static Project[] getAllProjects(Connection aConn) throws SQLException {
        PreparedStatement prep = aConn.prepareStatement("select projectid, l_userid, l_protocolid, title, description, username, creationdate, modificationdate from project order by creationdate desc, title asc");
        ResultSet rs = prep.executeQuery();
        Vector<Project> v = new Vector<Project>();
        while (rs.next()) {
            v.add(new Project(rs));
        }
        rs.close();
        prep.close();
        Project[] lProjects = new Project[v.size()];
        v.toArray(lProjects);

        return lProjects;
    }

    private void fillProjectPulldown() {
        dropdownprojectnumber.setModel(new DefaultComboBoxModel(iProjects));
        stateChangedProject((Project) dropdownprojectnumber.getSelectedItem());
    }

    private void stateChangedProject(Project aProject) {
        if (aProject != null) {
            currentProject = aProject.getProjectid();
        }
    }

    public void passConnection(Connection aConn, String aDB) {
        if (aConn == null) {
            this.close();
        }
        this.toconn = aConn;
        this.setTitle(iCurrentTitle + " (connected to '" + aDB + "')");
    }

    private void getConnection() throws SQLException {
        Properties lConnectionProperties = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, "ms-lims.properties");
        ConnectionDialog cd = new ConnectionDialog(this, this, "Establish DB connection for ms_lims", lConnectionProperties);
        cd.setVisible(true);
        DatabaseMetaData datatabasemetadata = fromconn.getMetaData();
        String connectionurl = datatabasemetadata.getURL();
        lConnectionProperties.setProperty("url",connectionurl);
    }

    private void closeConnection() {
        try {
            if (fromconn != null) {
                fromconn.close();
                logger.info("\nClosed DB connection.\n");
            }
            if (toconn != null) {
                toconn.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public boolean isStandAlone() {
        return iStandAlone;
    }

    public static void setNotStandAlone() {
        iStandAlone = false;
    }

    private void close() {

        if (isStandAlone()) {
            closeConnection();
        }
        this.setVisible(false);
        this.dispose();
    }
}
