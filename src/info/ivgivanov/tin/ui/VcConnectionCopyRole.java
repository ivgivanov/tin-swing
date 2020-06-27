package info.ivgivanov.tin.ui;

import javax.swing.*;

public class VcConnectionCopyRole extends VcConnectionUi {

    public VcConnectionCopyRole() {
        btnConnect.setText("Copy");
        JFrame connectionUi = super.drawConnectionUi("Copy Role");
    }
}
