package pl.merskip.qemu;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Starts and owns a QEMU process; stopping the run configuration stops QEMU. */
public class QemuCommandLineState extends CommandLineState {
    private final QemuRunConfiguration configuration;

    public QemuCommandLineState(ExecutionEnvironment environment, QemuRunConfiguration configuration) {
        super(environment);
        this.configuration = configuration;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        final List<String> args;
        try {
            args = QemuArguments.parse(configuration.getArguments());
        } catch (IllegalArgumentException e) {
            throw new ExecutionException("Invalid QEMU arguments: " + e.getMessage(), e);
        }
        GeneralCommandLine commandLine = new GeneralCommandLine(configuration.getQemuCommand());
        commandLine.addParameters(args);
        // Compatibility with legacy configurations that selected an ISO instead of free-form arguments.
        if (configuration.getArguments().isBlank() && !configuration.getCdromFile().isBlank()) {
            commandLine.addParameters("-cdrom", configuration.getCdromFile());
        }
        if (configuration.isEnableGDB()) {
            int port = configuration.getTcpPort();
            if (port < 1 || port > 65535) throw new ExecutionException("Invalid GDB TCP port: " + port);
            commandLine.addParameters("-gdb", "tcp:127.0.0.1:" + port);
            if (configuration.isWaitForDebugger()) commandLine.addParameter("-S");
        }
        // Intentionally avoid -daemonize and a shared PID file: the IDE owns this process.
        return ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine);
    }
}
