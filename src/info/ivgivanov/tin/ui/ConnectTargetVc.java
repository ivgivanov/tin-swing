package info.ivgivanov.tin.ui;

import com.vmware.vim25.*;
import info.ivgivanov.tin.VcConnector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class ConnectTargetVc {
    private JPanel initView;
    private JLabel vcenterAddress;
    private JTextField vcAddrInput;
    private JPasswordField vcPwdInput;
    private JLabel vcenterPassword;
    private JButton btnConnect;
    private JLabel statusLabel;
    private JTextField vcUsrNameInput;
    private JLabel usernameLabel;

    public ConnectTargetVc() {
        JFrame appUi = createUI();
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String vcAddress = vcAddrInput.getText();
                String username = vcUsrNameInput.getText();
                char[] vcPwdCh = vcPwdInput.getPassword();
                String vcPwd = String.valueOf(vcPwdCh);

                VcConnector vcConnector = new VcConnector();
                vcConnector.setAddress("https://"+vcAddress+"/sdk/");
                vcConnector.setUsername(username);
                vcConnector.setPassword(vcPwd);

                try {
                    VimPortType vimPort = vcConnector.login();
                    appUi.setVisible(false);

                    ManagedObjectReference serviceInstance = new ManagedObjectReference();
                    serviceInstance.setType("ServiceInstance");
                    serviceInstance.setValue("ServiceInstance");
                    ServiceContent serviceContent = vimPort.retrieveServiceContent(serviceInstance);

                    vimPort.logout(serviceContent.getSessionManager());

                }  catch (KeyManagementException keyManagementException) {
                    keyManagementException.printStackTrace();
                } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    noSuchAlgorithmException.printStackTrace();
                } catch (InvalidLocaleFaultMsg invalidLocaleFaultMsg) {
                    invalidLocaleFaultMsg.printStackTrace();
                } catch (InvalidLoginFaultMsg invalidLoginFaultMsg) {
                    statusLabel.setText("Invalid username or password");
                    invalidLoginFaultMsg.printStackTrace();
                } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
                    runtimeFaultFaultMsg.printStackTrace();
                }
            }
        });
    }



    private JFrame createUI() {
        JFrame frame = new JFrame("TIN");
        frame.setContentPane(initView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return frame;
    }
}
