package com.bfr.pluginandroidstudio.action;

import com.bfr.pluginandroidstudio.Actions;
import com.bfr.pluginandroidstudio.Common;
import com.bfr.pluginandroidstudio.tools.DeviceManager;
import com.intellij.execution.*;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.ide.macro.MacroManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.apache.tools.ant.taskdefs.Exec;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class BuildAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project _project = e.getRequiredData(CommonDataKeys.PROJECT);
        String _id = ActionManager.getInstance().getId(this);
        String[] _ids = _id.split("_");

        String _confName = "";

        if (_ids[1].equals("all"))
            _confName = "BuildEverything";
        else
            _confName = Common.APPS.get(_ids[1]).BuildConfig;

        runConf(_project, _confName, e);
    }

    void runConf(Project iProject, String iName, AnActionEvent iEvent) {
        RunManagerEx runManager = RunManagerEx.getInstanceEx(iProject);
        RunnerAndConfigurationSettings runConfig = runManager.findConfigurationByName(iName);

        if (runConfig == null) {
            System.out.println("Unable to find Run configuration with name: " + iName + " in project " + iProject.getName());
            return;
        }

        Executor executor = Executor.EXECUTOR_EXTENSION_NAME.getExtensionList().get(0);
        if (executor == null) {
            System.out.println("Unable to find Executor");
            return;
        }
        //String executorId = executor.getId();
        //String executionTargetId = ExecutionTargetManager.getInstance(iProject).getActiveTarget().getId();

        ExecutionTarget target = getExecutionTarget(iProject, runConfig, null);
        MacroManager.getInstance().cacheMacrosPreview(iEvent.getDataContext());
        ExecutionUtil.doRunConfiguration(runConfig, executor, target, null, iEvent.getDataContext());
    }

    @NotNull
    private ExecutionTarget getExecutionTarget(@NotNull Project project, @NotNull RunnerAndConfigurationSettings runConfig, String iTargetId) {
        ExecutionTargetManager targetManager = ExecutionTargetManager.getInstance(project);
        ExecutionTarget active = targetManager.getActiveTarget();
        if (iTargetId == null || iTargetId.equals(active.getId())) {
            return active;
        }

        List<ExecutionTarget> targets = targetManager.getTargetsFor(runConfig.getConfiguration());
        for (ExecutionTarget target : targets) {
            if (target.getId().equals(iTargetId)) {
                return target;
            }
        }
        return active;
    }

    @Override
    public void update(@NotNull AnActionEvent iEvent) {
        if (iEvent.getProject() == null) {
            iEvent.getPresentation().setEnabled(false);
            return;
        }

        iEvent.getPresentation().setEnabled(iEvent.getProject().getName().equals("BuddyCore"));
    }
}
