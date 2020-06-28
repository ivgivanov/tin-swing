package info.ivgivanov.tin.ui;

import com.vmware.vim25.AuthorizationRole;
import com.vmware.vim25.InvalidLocaleFaultMsg;
import com.vmware.vim25.InvalidLoginFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import info.ivgivanov.tin.AccessControlManager;
import info.ivgivanov.tin.VcConnection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class VcConnectionCopyRole extends VcConnectionUi {
    private AuthorizationRole roleToCopy;

    private AuthorizationRole getRoleToCopy() {
        return roleToCopy;
    }

    private void setRoleToCopy(AuthorizationRole roleToCopy) {
        this.roleToCopy = roleToCopy;
    }

    public VcConnectionCopyRole(AuthorizationRole role) {
        btnConnect.setText("Copy");
        setRoleToCopy(role);
        JFrame connectionUi = super.drawConnectionUi("Copy Role");

        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String vcAddress = vcAddrInput.getText();
                String username = vcUsrNameInput.getText();
                char[] vcPwdCh = vcPwdInput.getPassword();
                String vcPwd = String.valueOf(vcPwdCh);

                VcConnection vcConnection = null;
                try {
                    vcConnection = new VcConnection("https://"+vcAddress+"/sdk/",username,vcPwd);
                    AccessControlManager acm = new AccessControlManager(vcConnection);
                    int roleId = acm.createRole(getRoleToCopy());
                    if (roleId != 0) {
                        statusLabel.setText("Role "+getRoleToCopy().getName()+" copied!");
                    }
                    vcConnection.getVimPort().logout(vcConnection.getServiceContent().getSessionManager());

                } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
                    runtimeFaultFaultMsg.printStackTrace();
                } catch (InvalidLoginFaultMsg invalidLoginFaultMsg) {
                    statusLabel.setText("Invalid username or password");
                    invalidLoginFaultMsg.printStackTrace();
                } catch (InvalidLocaleFaultMsg invalidLocaleFaultMsg) {
                    invalidLocaleFaultMsg.printStackTrace();
                } catch (KeyManagementException keyManagementException) {
                    keyManagementException.printStackTrace();
                } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    noSuchAlgorithmException.printStackTrace();
                }
            }
        });
    }
}
