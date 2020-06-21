package info.ivgivanov.tin.ui;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainView {
    private JPanel panel1;
    private JLabel versionBuild;
    private JComboBox actionsList;
    private JButton runButton;
    private VimPortType vimPort;

    public VimPortType getVimPort() {

        return vimPort;
    }

    public void setVimPort(VimPortType vimPort) {

        this.vimPort = vimPort;
    }

    public JFrame createUI() {
        JFrame frame = new JFrame("TIN");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450,300);

        actionsList.addItem("Copy vCenter Role");
        actionsList.addItem("Check vMotion Compatibility");


        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        ManagedObjectReference serviceInstance = new ManagedObjectReference();
        serviceInstance.setType("ServiceInstance");
        serviceInstance.setValue("ServiceInstance");
        ServiceContent serviceContent = null;
        try {
            serviceContent = vimPort.retrieveServiceContent(serviceInstance);
            versionBuild.setText(" vCenter version: "+serviceContent.getAbout().getVersion()+", build "+serviceContent.getAbout().getBuild());
        } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
            runtimeFaultFaultMsg.printStackTrace();
        }


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
