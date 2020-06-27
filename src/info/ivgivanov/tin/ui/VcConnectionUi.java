package info.ivgivanov.tin.ui;

import javax.swing.*;

public class VcConnectionUi {
    private JPanel initView;
    private JLabel vcenterAddress;
    public JTextField vcAddrInput;
    public JPasswordField vcPwdInput;
    private JLabel vcenterPassword;
    public JButton btnConnect;
    public JLabel statusLabel;
    public JTextField vcUsrNameInput;
    private JLabel usernameLabel;


    public JFrame drawConnectionUi(String title) {
        JFrame frame = new JFrame(title);
        frame.setContentPane(initView);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return frame;
    }

}