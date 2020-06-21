package info.ivgivanov.tin;

import com.vmware.vim25.*;

import javax.xml.ws.BindingProvider;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class VcConnector {
    private String address;
    private String username;
    private String password;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public VimPortType login () throws KeyManagementException, NoSuchAlgorithmException, InvalidLocaleFaultMsg, InvalidLoginFaultMsg, RuntimeFaultFaultMsg {
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

        return vimPort;
    }

}
