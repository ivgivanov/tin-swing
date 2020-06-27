package info.ivgivanov.tin.ui;

import com.vmware.vim25.*;
import info.ivgivanov.tin.AccessControlManager;
import info.ivgivanov.tin.VcConnection;
import info.ivgivanov.tin.VcInventoryCollector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainView {
    private VcConnection vcConnection;
    private JPanel panel1;
    private JLabel versionBuild;
    private JComboBox actionsList;
    private JButton executeMethod;
    private JList vcRoles;

    public VcConnection getVcConnection() {
        return vcConnection;
    }

    private void setVcConnection(VcConnection vcConnection) {
        this.vcConnection = vcConnection;
    }

    public MainView(VcConnection vcConnection) {
        this.setVcConnection(vcConnection);
        executeMethod.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (executeMethod.getText().equals("Copy to...")) {

                    VcConnectionCopyRole vcConnectionCopyRole = new VcConnectionCopyRole();

                }
            }
        });
        actionsList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRole = (String) actionsList.getSelectedItem();
                switch (selectedRole) {
                    case "Copy vCenter Role":
                        ((DefaultListModel)vcRoles.getModel()).clear();
                        executeMethod.setVisible(false);
                        AccessControlManager acm = new AccessControlManager(vcConnection);
                        try {
                            List<AuthorizationRole> roles = acm.getRoles();
                            for (AuthorizationRole role : roles) {
                                ((DefaultListModel)vcRoles.getModel()).addElement(role.getName());
                            }
                        } catch (InvalidPropertyFaultMsg invalidPropertyFaultMsg) {
                            invalidPropertyFaultMsg.printStackTrace();
                        } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
                            runtimeFaultFaultMsg.printStackTrace();
                        }
                        executeMethod.setText("Copy to...");
                        executeMethod.setVisible(true);
                        break;
                    case "Check vMotion Compatibility":
                        ((DefaultListModel)vcRoles.getModel()).clear();
                        executeMethod.setVisible(false);
                        VcInventoryCollector vcInventoryCollector = new VcInventoryCollector(vcConnection);
                        for  (String clusterName : vcInventoryCollector.getClusterNames()) {
                            ((DefaultListModel)vcRoles.getModel()).addElement(clusterName);
                        }
                        executeMethod.setText("Check vMotion...");
                        executeMethod.setVisible(true);
                        break;
                    default:
                        ((DefaultListModel)vcRoles.getModel()).clear();
                        ((DefaultListModel)vcRoles.getModel()).addElement("empty state");
                        break;
                }

                vcRoles.setVisible(true);
            }
        });

        createUI();
    }

    public JFrame createUI() {
        JFrame frame = new JFrame("TIN");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450,300);

        executeMethod.setVisible(false);
        vcRoles.setVisible(false);

        actionsList.addItem("Copy vCenter Role");
        actionsList.addItem("Check vMotion Compatibility");


        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        VimPortType vimPort = vcConnection.getVimPort();
        ServiceContent serviceContent = vcConnection.getServiceContent();

        versionBuild.setText(" vCenter version: "+serviceContent.getAbout().getVersion()+", build "+serviceContent.getAbout().getBuild());


        ServiceContent finalServiceContent = serviceContent;
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("Exiting...");
                try {
                    vimPort.logout(finalServiceContent.getSessionManager());
                } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
                    runtimeFaultFaultMsg.printStackTrace();
                }
            }
        });

        return frame;
    }
}
