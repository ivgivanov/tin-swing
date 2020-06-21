package info.ivgivanov.tin;

import info.ivgivanov.tin.ui.VcConnectionUi;

import javax.swing.*;

public class Tin {

    public static void main(String[] args) {

        if (args.length == 0) {
            VcConnectionUi vcConnectionUi = new VcConnectionUi();
            //JFrame appUi = vcConnectionUi.createUI();
        }

    }
}
