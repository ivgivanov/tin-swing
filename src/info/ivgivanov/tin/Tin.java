package info.ivgivanov.tin;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;
import info.ivgivanov.tin.ui.MainView;
import info.ivgivanov.tin.ui.VcConnectionUi;
import info.ivgivanov.tin.ui.VcConnectionUiInit;

import javax.swing.*;

public class Tin {

    public static void main(String[] args) {

        if (args.length == 0) {
            VcConnectionUiInit vcConnectionUiInit = new VcConnectionUiInit();
            //JFrame appUi = vcConnectionUi.createUI();
        }

    }
}
