package info.ivgivanov.tin;

import com.vmware.vim25.*;

import java.util.ArrayList;
import java.util.List;

public class VcInventoryCollector {
    private VcConnection vcConnection;

    private VcConnection getVcConnection() {
        return vcConnection;
    }

    private void setVcConnection(VcConnection vcConnection) {
        this.vcConnection = vcConnection;
    }

    public VcInventoryCollector(VcConnection vcConnection) {
        this.setVcConnection(vcConnection);
    }

    public List<String> getClusterNames() {

        VimPortType vimPort = this.vcConnection.getVimPort();
        ServiceContent serviceContent = this.vcConnection.getServiceContent();
        List<String> clusterNames = new ArrayList<String>();
        ManagedObjectReference propertyCollector = serviceContent.getPropertyCollector();

        ObjectSpec objectSpec = new ObjectSpec();
        objectSpec.setObj(serviceContent.getRootFolder());
        objectSpec.getSelectSet().addAll(buildFullTraversal());
        objectSpec.setSkip(false);

        PropertySpec propertySpecCluster = new PropertySpec();
        propertySpecCluster.setType("ClusterComputeResource");
        propertySpecCluster.getPathSet().add("name");
        propertySpecCluster.setAll(false);

        PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
        propertyFilterSpec.getObjectSet().add(objectSpec);
        propertyFilterSpec.getPropSet().add(propertySpecCluster);

        List<PropertyFilterSpec> propertyFilterSpecList = new ArrayList<PropertyFilterSpec>();
        propertyFilterSpecList.add(propertyFilterSpec);

        RetrieveOptions retrieveOptions = new RetrieveOptions();

        RetrieveResult result = null;
        try {
            result = vimPort.retrievePropertiesEx(propertyCollector, propertyFilterSpecList, retrieveOptions);
        } catch (InvalidPropertyFaultMsg invalidPropertyFaultMsg) {
            invalidPropertyFaultMsg.printStackTrace();
        } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
            runtimeFaultFaultMsg.printStackTrace();
        }

        if (result != null) {
            for (ObjectContent objectContent : result.getObjects()) {


                System.out.println("First for");
                System.out.println(objectContent.getObj().getValue());
                List<DynamicProperty> properties = objectContent.getPropSet();
                for (DynamicProperty property : properties) {
                    System.out.println("Second for");
                    System.out.println(property.getName() + ": " + property.getVal());
                    clusterNames.add((String)property.getVal());
                }
            }
        }
        return  clusterNames;
    }


    public List<Cluster> getClusters() {

        VimPortType vimPort = this.vcConnection.getVimPort();
        ServiceContent serviceContent = this.vcConnection.getServiceContent();
        List<Cluster> clusters = new ArrayList<Cluster>();

        ManagedObjectReference propertyCollector = serviceContent.getPropertyCollector();

        ObjectSpec objectSpec = new ObjectSpec();
        objectSpec.setObj(serviceContent.getRootFolder());
        objectSpec.getSelectSet().addAll(buildFullTraversal());
        objectSpec.setSkip(false);

        PropertySpec propertySpecCluster = new PropertySpec();
        propertySpecCluster.setType("ClusterComputeResource");
        propertySpecCluster.getPathSet().add("name");
        propertySpecCluster.getPathSet().add("host");
        propertySpecCluster.setAll(false);

        PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
        propertyFilterSpec.getObjectSet().add(objectSpec);
        propertyFilterSpec.getPropSet().add(propertySpecCluster);

        List<PropertyFilterSpec> propertyFilterSpecList = new ArrayList<PropertyFilterSpec>();
        propertyFilterSpecList.add(propertyFilterSpec);

        RetrieveOptions retrieveOptions = new RetrieveOptions();

        RetrieveResult result = null;
        try {
            result = vimPort.retrievePropertiesEx(propertyCollector, propertyFilterSpecList, retrieveOptions);
        } catch (InvalidPropertyFaultMsg invalidPropertyFaultMsg) {
            invalidPropertyFaultMsg.printStackTrace();
        } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
            runtimeFaultFaultMsg.printStackTrace();
        }

        if (result != null) {
            for (ObjectContent objectContent : result.getObjects()) {
                Cluster cluster = new Cluster();
                cluster.setMoref(objectContent.getObj());
                //System.out.println("Cluster id: "+objectContent.getObj().getValue());
                List<DynamicProperty> properties = objectContent.getPropSet();
                for (DynamicProperty property : properties) {
                    if (property.getName().equals("name")) {
                        //System.out.println("Cluster name: "+property.getVal());
                        cluster.setName((String) property.getVal());
                    } else if (property.getName().equals("host")) {
                        List<HostSystem> hosts = new ArrayList<HostSystem>();
                        ArrayOfManagedObjectReference hostsMorefs = (ArrayOfManagedObjectReference)property.getVal();
                        if (hostsMorefs.getManagedObjectReference().size() > 0) {
                            //System.out.println("Cluster hosts:");
                            for (ManagedObjectReference moref : hostsMorefs.getManagedObjectReference()) {
                                //System.out.println(moref.getValue());
                                HostSystem host = new HostSystem();
                                host.setMoref(moref);
                                //hosts.add(host);
                                ///// to be refactored
                                ObjectSpec objectSpecHost = new ObjectSpec();
                                objectSpecHost.setObj(host.getMoref());
                                objectSpecHost.setSkip(false);

                                PropertySpec propertySpecHost = new PropertySpec();
                                propertySpecHost.setType("HostSystem");
                                propertySpecHost.getPathSet().add("name");
                                propertySpecHost.getPathSet().add("vm");
                                propertySpecHost.setAll(false);

                                PropertyFilterSpec propertyFilterSpecHost = new PropertyFilterSpec();
                                propertyFilterSpecHost.getObjectSet().add(objectSpecHost);
                                propertyFilterSpecHost.getPropSet().add(propertySpecHost);

                                List<PropertyFilterSpec> propertyFilterSpecListHost = new ArrayList<PropertyFilterSpec>();
                                propertyFilterSpecListHost.add(propertyFilterSpecHost);

                                RetrieveResult hostResult = null;
                                try {
                                    hostResult = vimPort.retrievePropertiesEx(propertyCollector, propertyFilterSpecListHost, retrieveOptions);
                                } catch (InvalidPropertyFaultMsg invalidPropertyFaultMsg) {
                                    invalidPropertyFaultMsg.printStackTrace();
                                } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
                                    runtimeFaultFaultMsg.printStackTrace();
                                }

                                if (hostResult != null) {
                                    for (ObjectContent objectContentHost : hostResult.getObjects()) {
                                        List<DynamicProperty> hostProperties = objectContentHost.getPropSet();
                                        for (DynamicProperty hostProperty : hostProperties) {
                                            if (hostProperty.getName().equals("name")) {
                                                host.setName((String)hostProperty.getVal());
                                            } else if (hostProperty.getName().equals("vm")) {
                                                List<VirtualMachine> vms = new ArrayList<VirtualMachine>();
                                                ArrayOfManagedObjectReference vmMorefs = (ArrayOfManagedObjectReference)hostProperty.getVal();
                                                if (vmMorefs.getManagedObjectReference().size() > 0) {
                                                    for (ManagedObjectReference vmMoref : vmMorefs.getManagedObjectReference()) {
                                                        VirtualMachine vm = new VirtualMachine();
                                                        vm.setMoref(vmMoref);
                                                        vms.add(vm);
                                                    }
                                                }
                                                host.setVms(vms);
                                            }
                                        }
                                    }
                                }

                                ////
                                //System.out.println("---- "+host.getName());
                                hosts.add(host);
                            }
                        }
                        cluster.setHosts(hosts);
                    }
                }
                clusters.add(cluster);
            }
        }

        return  clusters;
    }


    public static List<SelectionSpec> buildFullTraversal() {
        // Terminal traversal specs

        // RP -> VM
        TraversalSpec rpToVm = new TraversalSpec();
        rpToVm.setName("rpToVm");
        rpToVm.setType("ResourcePool");
        rpToVm.setPath("vm");
        rpToVm.setSkip(Boolean.FALSE);

        // vApp -> VM
        TraversalSpec vAppToVM = new TraversalSpec();
        vAppToVM.setName("vAppToVM");
        vAppToVM.setType("VirtualApp");
        vAppToVM.setPath("vm");

        // HostSystem -> VM
        TraversalSpec hToVm = new TraversalSpec();
        hToVm.setType("HostSystem");
        hToVm.setPath("vm");
        hToVm.setName("hToVm");
        hToVm.getSelectSet().add(getSelectionSpec("VisitFolders"));
        hToVm.setSkip(Boolean.FALSE);

        // DC -> DS
        TraversalSpec dcToDs = new TraversalSpec();
        dcToDs.setType("Datacenter");
        dcToDs.setPath("datastore");
        dcToDs.setName("dcToDs");
        dcToDs.setSkip(Boolean.FALSE);

        // DC -> DSFolder
        TraversalSpec dcToDsFolder = new TraversalSpec();
        dcToDsFolder.setType("Datacenter");
        dcToDsFolder.setPath("datastoreFolder");
        dcToDsFolder.setName("dcToDsFolder");
        dcToDsFolder.setSkip(Boolean.FALSE);

        // Recurse through all ResourcePools
        TraversalSpec rpToRp = new TraversalSpec();
        rpToRp.setType("ResourcePool");
        rpToRp.setPath("resourcePool");
        rpToRp.setSkip(Boolean.FALSE);
        rpToRp.setName("rpToRp");
        rpToRp.getSelectSet().add(getSelectionSpec("rpToRp"));

        TraversalSpec crToRp = new TraversalSpec();
        crToRp.setType("ComputeResource");
        crToRp.setPath("resourcePool");
        crToRp.setSkip(Boolean.FALSE);
        crToRp.setName("crToRp");
        crToRp.getSelectSet().add(getSelectionSpec("rpToRp"));

        TraversalSpec crToH = new TraversalSpec();
        crToH.setSkip(Boolean.FALSE);
        crToH.setType("ComputeResource");
        crToH.setPath("host");
        crToH.setName("crToH");

        TraversalSpec dcToHf = new TraversalSpec();
        dcToHf.setSkip(Boolean.FALSE);
        dcToHf.setType("Datacenter");
        dcToHf.setPath("hostFolder");
        dcToHf.setName("dcToHf");
        dcToHf.getSelectSet().add(getSelectionSpec("VisitFolders"));

        TraversalSpec vAppToRp = new TraversalSpec();
        vAppToRp.setName("vAppToRp");
        vAppToRp.setType("VirtualApp");
        vAppToRp.setPath("resourcePool");
        vAppToRp.getSelectSet().add(getSelectionSpec("rpToRp"));

        TraversalSpec dcToVmf = new TraversalSpec();
        dcToVmf.setType("Datacenter");
        dcToVmf.setSkip(Boolean.FALSE);
        dcToVmf.setPath("vmFolder");
        dcToVmf.setName("dcToVmf");
        dcToVmf.getSelectSet().add(getSelectionSpec("VisitFolders"));

        // For Folder -> Folder recursion
        TraversalSpec visitFolders = new TraversalSpec();
        visitFolders.setType("Folder");
        visitFolders.setPath("childEntity");
        visitFolders.setSkip(Boolean.FALSE);
        visitFolders.setName("VisitFolders");

        List<SelectionSpec> sspecarrvf = new ArrayList<SelectionSpec>();
        sspecarrvf.add(getSelectionSpec("crToRp"));
        sspecarrvf.add(getSelectionSpec("crToH"));
        sspecarrvf.add(getSelectionSpec("dcToVmf"));
        sspecarrvf.add(getSelectionSpec("dcToHf"));
        sspecarrvf.add(getSelectionSpec("vAppToRp"));
        sspecarrvf.add(getSelectionSpec("vAppToVM"));
        sspecarrvf.add(getSelectionSpec("dcToDs"));
        sspecarrvf.add(getSelectionSpec("dcToDsFolder"));
        sspecarrvf.add(getSelectionSpec("hToVm"));
        sspecarrvf.add(getSelectionSpec("rpToVm"));
        sspecarrvf.add(getSelectionSpec("VisitFolders"));

        visitFolders.getSelectSet().addAll(sspecarrvf);

        List<SelectionSpec> resultspec = new ArrayList<SelectionSpec>();
        resultspec.add(visitFolders);
        resultspec.add(crToRp);
        resultspec.add(crToH);
        resultspec.add(dcToVmf);
        resultspec.add(dcToHf);
        resultspec.add(vAppToRp);
        resultspec.add(vAppToVM);
        resultspec.add(dcToDs);
        resultspec.add(dcToDsFolder);
        resultspec.add(hToVm);
        resultspec.add(rpToVm);
        resultspec.add(rpToRp);

        return resultspec;
    }

    public static SelectionSpec getSelectionSpec(String name) {
        SelectionSpec genericSpec = new SelectionSpec();
        genericSpec.setName(name);
        return genericSpec;
    }
}
