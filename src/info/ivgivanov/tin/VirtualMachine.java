package info.ivgivanov.tin;

import com.vmware.vim25.ManagedObjectReference;

public class VirtualMachine {

    private String name;
    private ManagedObjectReference moref;

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
}
