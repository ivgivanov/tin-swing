package info.ivgivanov.tin;

import com.vmware.vim25.*;

import java.util.ArrayList;
import java.util.List;

public class AccessControlManager {
    private VcConnection vcConnection;

    private VcConnection getVcConnection() {
        return vcConnection;
    }

    private void setVcConnection(VcConnection vcConnection) {
        this.vcConnection = vcConnection;
    }

    public AccessControlManager (VcConnection vcConnection) {
        this.setVcConnection(vcConnection);
    }
    public List<AuthorizationRole> getRoles()
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {

        VimPortType vimPort = this.getVcConnection().getVimPort();
        ServiceContent serviceContent = this.getVcConnection().getServiceContent();
        List<AuthorizationRole> authRoles = new ArrayList<AuthorizationRole>();

        ManagedObjectReference authManager = serviceContent.getAuthorizationManager();
        ManagedObjectReference propertyCollector = serviceContent.getPropertyCollector();

        ObjectSpec objectSpec = new ObjectSpec();
        objectSpec.setObj(authManager);

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.setType(authManager.getType());
        propertySpec.getPathSet().add("roleList");

        PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
        propertyFilterSpec.getObjectSet().add(objectSpec);
        propertyFilterSpec.getPropSet().add(propertySpec);

        List<PropertyFilterSpec> propertyFilterSpecList = new ArrayList<PropertyFilterSpec>();
        propertyFilterSpecList.add(propertyFilterSpec);

        RetrieveOptions retrieveOptions = new RetrieveOptions();
        RetrieveResult result = vimPort.retrievePropertiesEx(propertyCollector, propertyFilterSpecList,
                retrieveOptions);

        if (result != null) {
            for (ObjectContent objectContent : result.getObjects()) {
                List<DynamicProperty> properties = objectContent.getPropSet();
                ArrayOfAuthorizationRole authRolesArray = (ArrayOfAuthorizationRole) properties.get(0).getVal();
                authRoles = authRolesArray.getAuthorizationRole();
            }
        }

        return authRoles;
    }
}
