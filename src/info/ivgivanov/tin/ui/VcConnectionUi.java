package info.ivgivanov.tin.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VcConnectionUi {
    private JPanel initView;
    private JLabel vcenterAddress;
    private JTextField vcAddrInput;
    private JPasswordField vcPwdInput;
    private JLabel vcenterPassword;
    private JButton btnConnect;
    private JLabel emptyLabel;
    private JLabel connectionStatus;

    public VcConnectionUi() {
        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String vcAddr = vcAddrInput.getText();
                char[] vcPwdCh = vcPwdInput.getPassword();
                String vcPwd = String.valueOf(vcPwdCh);
                connectionStatus.setText("Connecting...");

            }
        });
    }

    public JFrame createUI() {
        JFrame frame = new JFrame("TIN");
        frame.setContentPane(initView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        return frame;
    }
}