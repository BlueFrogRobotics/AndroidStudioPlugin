package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.DeviceManager;
import com.bfr.pluginandroidstudio.ProjectManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.annotations.NotNull;

public class ConnectedAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        ProjectManager.setProject(iEvent);

        boolean _isDevice = DeviceManager.isDevice();
        Presentation _presentation = iEvent.getPresentation();
        _presentation.setEnabled(false);
        if (!_isDevice) {
            _presentation.setText("No robot connected");
        } else {
            String _name = DeviceManager.getRobotName(DeviceManager.CURRENT_IP);
            _presentation.setText("Connected " + _name);
        }
    }
}
