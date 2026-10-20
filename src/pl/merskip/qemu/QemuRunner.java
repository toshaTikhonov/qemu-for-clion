package pl.merskip.qemu;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QemuRunner extends GenericProgramRunner<RunProfileState> {
    @NotNull
    @Override
    public String getRunnerId() { return "QemuRunConfigurationRunner"; }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return DefaultRunExecutor.EXECUTOR_ID.equals(executorId) && profile instanceof QemuRunConfiguration;
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state,
                                             @NotNull ExecutionEnvironment environment) throws ExecutionException {
        ExecutionResult result = state.execute(environment.getExecutor(), this);
        return result == null ? null : new RunContentBuilder(result, environment).showRunContent(environment.getContentToReuse());
    }
}
