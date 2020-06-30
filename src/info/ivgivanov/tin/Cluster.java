package info.ivgivanov.tin;

import com.vmware.vim25.ManagedObjectReference;

import java.util.List;

public class Cluster {

    private String name;
    private ManagedObjectReference moref;
    private List<HostSystem> hosts;
    private List<VirtualMachine> vms;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ManagedObjectReference getMoref() {
        return moref;
    }

    public void setMoref(ManagedObjectReference moref) {
        this.moref = moref;
    }

    public List<HostSystem> getHosts() {
        return hosts;
    }

    public void setHosts(List<HostSystem> hosts) {
        this.hosts = hosts;
    }

    public List<VirtualMachine> getVms() {
        return vms;
    }

    public void setVms(List<VirtualMachine> vms) {
        this.vms = vms;
    }
}
