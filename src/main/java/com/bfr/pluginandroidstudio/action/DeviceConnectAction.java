package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.bfr.pluginandroidstudio.DeviceManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import se.vidstige.jadb.ConnectionToRemoteDeviceException;
import se.vidstige.jadb.JadbException;

import java.io.IOException;
import java.net.InetSocketAddress;

public class DeviceConnectAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String _id = ActionManager.getInstance().getId(this);
        Project _project = e.getRequiredData(CommonDataKeys.PROJECT);
        if (_id.equals("device_remember")) {
            String _name = Messages.showInputDialog(_project, "The name or model for the robot", "Robot name", null, "Untitled", null);
            DeviceManager.addOrReplace(DeviceManager.CURRENT_IP, _name);
            DeviceManager.saveDevices();
        } else {
            String[] _names = DeviceManager.getNames();
            String _robot = Messages.showEditableChooseDialog("", "Choose a robot", null,
                    _names, _names[0], null);
            if (_robot != null && !_robot.isEmpty()) {
                if (_id.equals("devices_connect")) {
                    String _IP = DeviceManager.getRobotIP(_robot);
                    try {
                        DeviceManager.ADB.connectToTcpDevice(new InetSocketAddress(_IP, 5555));
                    } catch (IOException | JadbException | ConnectionToRemoteDeviceException ex) {
                        ex.printStackTrace();
                    }
                } else if (_id.equals("device_forget")) {
                    DeviceManager.remove(_robot);
                    DeviceManager.saveDevices();
                }
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {

        String _id = ActionManager.getInstance().getId(this);

        if (_id.equals("device_remember")) {
            Actions.setMenuEnabledIfDevice(iEvent);
        } else if (_id.equals("devices_connect")) {
            if (DeviceManager.ROBOTS.size() > 0)
                iEvent.getPresentation().setEnabled(!DeviceManager.isDevice());
            else
                iEvent.getPresentation().setEnabled(false);
        } else if (_id.equals("device_forget")) {
            iEvent.getPresentation().setEnabled(DeviceManager.ROBOTS.size() > 0);
        }
    }
}
