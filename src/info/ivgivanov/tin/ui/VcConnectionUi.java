package info.ivgivanov.tin.ui;

import com.vmware.vim25.InvalidLocaleFaultMsg;
import com.vmware.vim25.InvalidLoginFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.VimPortType;
import info.ivgivanov.tin.VcConnector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class VcConnectionUi {
    private JPanel initView;
    private JLabel vcenterAddress;
    private JTextField vcAddrInput;
    private JPasswordField vcPwdInput;
    private JLabel vcenterPassword;
    private JButton btnConnect;
    private JLabel statusLabel;
    private JTextField vcUsrNameInput;
    private JLabel usernameLabel;

    public VcConnectionUi() {
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
                    MainView mainView = new MainView();
                    mainView.setVimPort(vimPort);
                    mainView.createUI();
                } catch (KeyManagementException keyManagementException) {
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

    public JFrame createUI() {
        JFrame frame = new JFrame("TIN");
        frame.setContentPane(initView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return frame;
    }

}