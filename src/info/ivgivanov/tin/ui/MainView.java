package info.ivgivanov.tin.ui;

import com.vmware.vim25.*;
import info.ivgivanov.tin.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

public class MainView {
    private VcConnection vcConnection;
    private JPanel panel1;
    private JLabel versionBuild;
    private JComboBox actionsList;
    private JButton executeMethod;
    private JList vcRoles;
    private JLabel vmotinStatus;

    public VcConnection getVcConnection() {
        return vcConnection;
    }

    private void setVcConnection(VcConnection vcConnection) {
        this.vcConnection = vcConnection;
    }

    public MainView(VcConnection vcConnection) {
        List<AuthorizationRole> roles=null;
        AccessControlManager acm = new AccessControlManager(vcConnection);
        try {
            roles = acm.getRoles();
        } catch (InvalidPropertyFaultMsg invalidPropertyFaultMsg) {
            invalidPropertyFaultMsg.printStackTrace();
        } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
            runtimeFaultFaultMsg.printStackTrace();
        }
        VcInventoryCollector vcInventoryCollector = new VcInventoryCollector(vcConnection);
        List<Cluster> clusters = vcInventoryCollector.getClusters();

        this.setVcConnection(vcConnection);
        List<AuthorizationRole> finalRoles = roles;
        executeMethod.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (executeMethod.getText().equals("Copy to...")) {

                    for (AuthorizationRole role : finalRoles) {
                        if (role.getName().equals((String)vcRoles.getSelectedValue())){
                            VcConnectionCopyRole vcConnectionCopyRole = new VcConnectionCopyRole(role);
                            break;
                        }
                    }
                } else if (executeMethod.getText().equals("Check vMotion...")) {

                    Cluster selectedCluster = null;
                    for (Cluster cluster : clusters) {
                        if (cluster.getName().equals((String)vcRoles.getSelectedValue())) {
                            selectedCluster = cluster;
                            break;
                        }
                    }

                    List<ManagedObjectReference> listVms = new ArrayList<ManagedObjectReference>();
                    List<ManagedObjectReference> listHosts = new ArrayList<ManagedObjectReference>();

                    System.out.println("Selected cluster name: "+selectedCluster.getName());
                    System.out.println("Cluster MoRef: "+selectedCluster.getMoref().getValue());
                    System.out.println("Hosts count: "+selectedCluster.getHosts().size());
                    if (selectedCluster.getHosts().size() > 0) {
                        System.out.println("Hosts:");
                        for (HostSystem host : selectedCluster.getHosts()) {
                            listHosts.add(host.getMoref());
                            System.out.println("---> "+host.getName());
                            if (host.getVms().size() > 0) {
                                System.out.println("VMs:");
                                for (VirtualMachine vm : host.getVms()) {
                                    listVms.add(vm.getMoref());
                                    System.out.println("-------> "+vm.getMoref().getValue());
                                }
                            } else {
                                System.out.println("VMs: 0");
                            }
                        }
                    }

                    //// check vMotion


                    ManagedObjectReference myVm = new ManagedObjectReference();
                    myVm.setType("VirtualMachine");
                    myVm.setValue("vm-16");

                    ManagedObjectReference myHost = new ManagedObjectReference();
                    myHost.setType("HostSystem");
                    myHost.setValue("host-10");

                    List<ManagedObjectReference> vmList = new ArrayList<ManagedObjectReference>();
                    vmList.add(myVm);
                    List<ManagedObjectReference> hostList = new ArrayList<ManagedObjectReference>();
                    hostList.add(myHost);

                    System.out.println("Checking vMotion compatibility for "+listHosts.size() + " hosts and "+listVms.size()+" VMs");
                    VimPortType vimPort = vcConnection.getVimPort();
                    ServiceContent serviceContent = vcConnection.getServiceContent();

                    ManagedObjectReference vmProvChecker = serviceContent.getVmProvisioningChecker();

                    try {
                        ManagedObjectReference task = vimPort.queryVMotionCompatibilityExTask(vmProvChecker, listVms, listHosts);
                        System.out.println(task.getValue());

                        ManagedObjectReference propertyCollector = serviceContent.getPropertyCollector();

                        ObjectSpec objectSpec = new ObjectSpec();
                        objectSpec.setObj(task);
                        objectSpec.setSkip(false);

                        PropertySpec propertySpecCluster = new PropertySpec();
                        propertySpecCluster.setType("Task");
                        propertySpecCluster.getPathSet().add("info");
                        propertySpecCluster.setAll(false);

                        PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
                        propertyFilterSpec.getObjectSet().add(objectSpec);
                        propertyFilterSpec.getPropSet().add(propertySpecCluster);

                        List<PropertyFilterSpec> propertyFilterSpecList = new ArrayList<PropertyFilterSpec>();
                        propertyFilterSpecList.add(propertyFilterSpec);

                        RetrieveOptions retrieveOptions = new RetrieveOptions();

                        RetrieveResult result = vimPort.retrievePropertiesEx(propertyCollector, propertyFilterSpecList, retrieveOptions);

                        Set<String> problematicVms = new HashSet<String>();

                        if (result != null) {
                            for (ObjectContent objectContent : result.getObjects()) {
                                List<DynamicProperty> properties = objectContent.getPropSet();
                                for (DynamicProperty property : properties) {
                                    TaskInfo taskInfo = (TaskInfo)property.getVal();
                                    System.out.println(taskInfo.getState().value());
                                    ArrayOfCheckResult checkRes = (ArrayOfCheckResult) taskInfo.getResult();
                                    for (CheckResult checkResult : checkRes.getCheckResult()) {
                                        if (checkResult.getError().size() > 0) {
                                            problematicVms.add(checkResult.getVm().getValue());
                                            System.out.println("Unable to vMotion VM "+checkResult.getVm().getValue()+" to host "+checkResult.getHost().getValue());
                                            System.out.println("Reasons:");
                                            for (LocalizedMethodFault error : checkResult.getError()) {
                                                System.out.println(error.getLocalizedMessage());
                                            }
                                        }
                                    }

                                    System.out.println("vMotion not possible for VMs: "+problematicVms);
                                    vmotinStatus.setText("<html>vMotion not possible for:<br/>"+String.join("<br/>",problematicVms)+"</html>");
                                    vmotinStatus.setVisible(true);
                                }
                            }
                        }

                    } catch (RuntimeFaultFaultMsg | InvalidPropertyFaultMsg runtimeFaultFaultMsg) {
                        runtimeFaultFaultMsg.printStackTrace();
                    }
                }
            }
        });
        List<AuthorizationRole> finalRoles1 = roles;
        actionsList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRole = (String) actionsList.getSelectedItem();
                switch (selectedRole) {
                    case "Copy vCenter Role":
                        ((DefaultListModel)vcRoles.getModel()).clear();
                        executeMethod.setVisible(false);
                        vmotinStatus.setVisible(false);
                        for (AuthorizationRole role : finalRoles1) {
                            ((DefaultListModel)vcRoles.getModel()).addElement(role.getName());
                        }
                        executeMethod.setText("Copy to...");
                        executeMethod.setVisible(true);
                        break;
                    case "Check vMotion Compatibility":
                        ((DefaultListModel)vcRoles.getModel()).clear();
                        executeMethod.setVisible(false);
                        vmotinStatus.setVisible(false);
                        //VcInventoryCollector vcInventoryCollector = new VcInventoryCollector(vcConnection);
                        for  (Cluster cluster : clusters) {
                            ((DefaultListModel)vcRoles.getModel()).addElement(cluster.getName());
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
        vmotinStatus.setVisible(false);

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
