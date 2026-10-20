package pl.merskip.qemu;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Persistent configuration for a user-selected QEMU executable and arguments. */
public class QemuRunConfiguration extends RunConfigurationBase<Object> {
    public enum DiskImageSource { File, CMakeTarget }

    private String qemuCommand = "qemu-system-arm";
    private String arguments = "-M virt -cpu cortex-a7 -m 512M -nographic";
    // Retained fields so old file-based configurations remain readable.
    private DiskImageSource diskImageSource = DiskImageSource.File;
    private String cdromFile = "";
    private boolean enableGDB = false;
    private int tcpPort = 1234;
    private boolean waitForDebugger = true;
    private boolean deamonize = false;

    public QemuRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new QemuRunConfigurationEditor();
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return DefaultRunExecutor.EXECUTOR_ID.equals(executor.getId()) ? new QemuCommandLineState(environment, this) : null;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (qemuCommand.isBlank()) throw new RuntimeConfigurationException("QEMU executable is required");
        try {
            QemuArguments.parse(arguments);
        } catch (IllegalArgumentException e) {
            throw new RuntimeConfigurationException(e.getMessage());
        }
        if (enableGDB && (tcpPort < 1 || tcpPort > 65535)) {
            throw new RuntimeConfigurationException("GDB port must be between 1 and 65535");
        }
    }

    public String getQemuCommand() { return qemuCommand; }
    public void setQemuCommand(String value) { qemuCommand = value; }
    public String getArguments() { return arguments; }
    public void setArguments(String value) { arguments = value; }
    public DiskImageSource getDiskImageSource() { return diskImageSource; }
    public void setDiskImageSource(DiskImageSource value) { diskImageSource = value; }
    public String getCdromFile() { return cdromFile; }
    public void setCdromFile(String value) { cdromFile = value; }
    public boolean isEnableGDB() { return enableGDB; }
    public void setEnableGDB(boolean value) { enableGDB = value; }
    public int getTcpPort() { return tcpPort; }
    public void setTcpPort(int value) { tcpPort = value; }
    public boolean isWaitForDebugger() { return waitForDebugger; }
    public void setWaitForDebugger(boolean value) { waitForDebugger = value; }
    public boolean isDeamonize() { return deamonize; }
    public void setDeamonize(boolean value) { deamonize = value; }
}
