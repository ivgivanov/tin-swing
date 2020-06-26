package info.ivgivanov.tin;

import com.vmware.vim25.*;
import info.ivgivanov.tin.ui.VcConnectionUi;

import javax.xml.ws.BindingProvider;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class VcConnection {
    private String address;
    private VimPortType vimPort;
    private ServiceContent serviceContent;

    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    public VimPortType getVimPort() {
        return vimPort;
    }

    private void setVimPort(VimPortType vimPort) {
        this.vimPort = vimPort;
    }

    public ServiceContent getServiceContent() {
        return serviceContent;
    }

    private void setServiceContent(ServiceContent serviceContent) {
        this.serviceContent = serviceContent;
    }

    public VcConnection(String address, String username, String password) throws RuntimeFaultFaultMsg, InvalidLoginFaultMsg, InvalidLocaleFaultMsg, KeyManagementException, NoSuchAlgorithmException {

        VimService vimService = new VimService();
        VimPortType vimPort = vimService.getVimPort();

        Map<String, Object> ctxt = ((BindingProvider) vimPort).getRequestContext();
        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
        ctxt.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);

        // Disable all SSL trust security
        SslManager.trustEveryone();

        ManagedObjectReference serviceInstance = new ManagedObjectReference();
        serviceInstance.setType("ServiceInstance");
        serviceInstance.setValue("ServiceInstance");

        ServiceContent serviceContent = vimPort.retrieveServiceContent(serviceInstance);
        vimPort.login(serviceContent.getSessionManager(), username, password, null);
        this.setAddress(address);
        this.setVimPort(vimPort);
        this.setServiceContent(serviceContent);

    }

}
