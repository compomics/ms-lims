package com.compomics.mslims.gui.quantitation;

import com.compomics.mslims.db.accessors.Project;
import com.compomics.mslims.db.accessors.Protocol;
import com.compomics.mslims.gui.frames.PreviewQuantitationResultsFrame;
import com.compomics.mslims.util.enumeration.RatioSourceType;
import org.apache.log4j.Logger;

import com.compomics.util.interfaces.Flamable;
import com.compomics.util.interfaces.Connectable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA. User: niklaas Date: 17-mrt-2009 Time: 11:13:09
 */
public class iTraqMs_limsStorageGui extends JFrame implements Connectable, Flamable {
    // Class specific log4j logger for iTraqMs_limsStorageGui instances.
    private static Logger logger = Logger.getLogger(iTraqMs_limsStorageGui.class);
    private JComboBox cmbProject;
    private JTextArea txtDescription;
    private JLabel lblUser;
    private JLabel lblProtocol;
    private JLabel lblCreationdate;
    private JButton storeButton;
    private JLabel lblTitle;
    private JPanel jpanContent;

    /**
     * The connection to the ms_lims database
     */
    private Connection iConn;
    /**
     * All the ms_lims projects
     */
    private Project[] iProjects;
    /**
     * The selected project
     */
    private Project iProject;
    /**
     * The protocols from the database
     */
    private Protocol[] iProtocols;
    /**
     * Date-time format String.
     */
    private static final String iDateTimeFormat = "dd/MM/yyyy - HH:mm:ss";

    /**
     * The SimpleDateFormat formatter to display creationdates.
     */
    private static SimpleDateFormat iSDF = new SimpleDateFormat(iDateTimeFormat);
    /**
     * Boolean that indicates whether the tool is ran in stand-alone mode ('true') or not ('false').
     */
    private static boolean iStandAlone = true;

    /**
     * Constructor
     *
     * @param aConn Connection to the ms_lims database
     */
    public iTraqMs_limsStorageGui(Connection aConn) {
        super("Store quantitations for iTraq data in ms_lims");
        //set the connection
        this.iConn = aConn;
        //create the gui
        $$$setupUI$$$();
        //add item listeners
        cmbProject.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    iProject = (Project) e.getItem();
                    lblCreationdate.setText(iSDF.format(iProject.getCreationdate()));
                    lblUser.setText(iProject.getUsername());
                    for (int i = 0; i < iProtocols.length; i++) {
                        if (iProtocols[i].getProtocolid() == iProject.getL_protocolid()) {
                            lblProtocol.setText(iProtocols[i].getType());
                        }
                    }
                    txtDescription.setText(iProject.getDescription());
                    lblTitle.setText(iProject.getTitle());
                }
            }
        });
        storeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previewStorage();
                setVisible(false);
                dispose();
            }
        });
        //select the newest project
        iProject = iProjects[0];
        lblCreationdate.setText(iSDF.format(iProject.getCreationdate()));
        lblUser.setText(iProject.getUsername());
        for (int i = 0; i < iProtocols.length; i++) {
            if (iProtocols[i].getProtocolid() == iProject.getL_protocolid()) {
                lblProtocol.setText(iProtocols[i].getType());
            }
        }
        txtDescription.setText(iProject.getDescription());
        lblTitle.setText(iProject.getTitle());

        //create jframe
        this.setContentPane(jpanContent);
        this.setSize(500, 500);
        this.setLocation(100, 100);
        this.setVisible(true);
    }

    /**
     * This method will start the storage of the iTraq data
     */
    private void previewStorage() {
        //get an array of datfile ids linked to the selected project
        Vector lDatfileIdVector = new Vector();
        try {
            PreparedStatement prep = iConn.prepareStatement("select i.l_datfileid from identification as i , spectrum as s where i.l_spectrumid = s.spectrumid and s.l_projectid = ? group by i.l_datfileid");
            prep.setLong(1, iProject.getProjectid());
            ResultSet rs = prep.executeQuery();

            while (rs.next()) {
                lDatfileIdVector.add(rs.getLong(1));
            }
            rs.close();

            prep.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        Long[] lDatfileIds = new Long[lDatfileIdVector.size()];
        lDatfileIdVector.toArray(lDatfileIds);

        // Now display the selected quantiation information into the previewpane.
        new PreviewQuantitationResultsFrame(this, lDatfileIds, iConn, RatioSourceType.ITRAQ_MS_LIMS);
    }

    /**
     * Create UI components
     */
    private void createUIComponents() {

        this.loadProtocol();
        this.loadProjects();
        cmbProject = new JComboBox(iProjects);

    }

    /**
     * This method attempts to load the protocol types from the DB.
     */
    private void loadProjects() {
        try {
            iProjects = Project.getAllProjects(iConn);
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to load projects from DB!");
        }
    }

    /**
     * This method attempts to load the protocol types from the DB.
     */
    private void loadProtocol() {
        try {
            iProtocols = Protocol.getAllProtocols(iConn);
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to load protocol types from DB!");
        }
    }

    public void passConnection(Connection connection, String s) {
        iConn = connection;
    }

    /**
     * This method takes care of any unrecoverable exception or error, thrown by a child thread.
     *
     * @param aThrowable Throwable that represents the unrecoverable error or exception.
     */
    public void passHotPotato(Throwable aThrowable) {
        this.passHotPotato(aThrowable, aThrowable.getMessage());
    }

    /**
     * This method takes care of any unrecoverable exception or error, thrown by a child thread.
     *
     * @param aThrowable Throwable that represents the unrecoverable error or exception.
     * @param aMessage   String with an extra message to display.
     */
    public void passHotPotato(Throwable aThrowable, String aMessage) {
        logger.error(aThrowable.getMessage(), aThrowable);

        JOptionPane.showMessageDialog(this, new String[]{"An error occurred while attempting to read the data:", aMessage}, "Error occurred!", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method should be called when the application is not launched in stand-alone mode.
     */
    public static void setNotStandAlone() {
        iStandAlone = false;
    }

    public boolean isStandAlone() {
        return iStandAlone;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        jpanContent = new JPanel();
        jpanContent.setLayout(new GridBagLayout());
        final JLabel label1 = new JLabel();
        label1.setText("Project");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(label1, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(cmbProject, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        jpanContent.add(panel1, gbc);
        panel1.setBorder(BorderFactory.createTitledBorder(null, "Project description", TitledBorder.CENTER, TitledBorder.ABOVE_TOP));
        final JLabel label2 = new JLabel();
        label2.setText("User");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Protocol");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Creation date");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Description");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label5, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(scrollPane1, gbc);
        txtDescription = new JTextArea();
        scrollPane1.setViewportView(txtDescription);
        lblUser = new JLabel();
        lblUser.setText(" ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(lblUser, gbc);
        lblProtocol = new JLabel();
        lblProtocol.setText(" ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(lblProtocol, gbc);
        lblCreationdate = new JLabel();
        lblCreationdate.setText(" ");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(lblCreationdate, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Title");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label6, gbc);
        lblTitle = new JLabel();
        lblTitle.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(lblTitle, gbc);
        storeButton = new JButton();
        storeButton.setText("Preview quantitative iTraq data");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(storeButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanContent.add(spacer1, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jpanContent;
    }
}
