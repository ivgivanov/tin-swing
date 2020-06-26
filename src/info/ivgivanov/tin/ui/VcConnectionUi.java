package info.ivgivanov.tin.ui;

import com.vmware.vim25.*;
import info.ivgivanov.tin.VcConnection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

                VcConnection vcConnection = null;
                try {
                    vcConnection = new VcConnection("https://"+vcAddress+"/sdk/",username,vcPwd);
                    appUi.setVisible(false);
                    MainView mainView = new MainView(vcConnection);
                    mainView.createUI();

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