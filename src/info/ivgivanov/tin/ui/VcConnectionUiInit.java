package info.ivgivanov.tin.ui;

import com.vmware.vim25.InvalidLocaleFaultMsg;
import com.vmware.vim25.InvalidLoginFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import info.ivgivanov.tin.VcConnection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class VcConnectionUiInit extends VcConnectionUi{
    public VcConnectionUiInit() {
        JFrame connectionUi = super.drawConnectionUi("TIN");
        connectionUi.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                    connectionUi.setVisible(false);
                    MainView mainView = new MainView(vcConnection);
                    //mainView.createUI();


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
