package pl.merskip.qemu;

import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/** Self-contained editor, without dependencies on CLion's internal CMake UI. */
public class QemuRunConfigurationEditor extends SettingsEditor<QemuRunConfiguration> {
    private final JPanel panel = new JPanel(new BorderLayout(8, 8));
    private final JTextField executable = new JTextField();
    private final JTextArea arguments = new JTextArea(9, 55);
    private final JCheckBox enableGdb = new JCheckBox("Enable QEMU GDB server");
    private final JTextField port = new JTextField("1234");
    private final JCheckBox waitForDebugger = new JCheckBox("Pause CPU until debugger attaches (-S)");

    public QemuRunConfigurationEditor() {
        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        fields.add(new JLabel("QEMU executable (absolute path or PATH command):"), c);
        c.gridy++;
        fields.add(executable, c);
        c.gridy++;
        fields.add(new JLabel("QEMU arguments (paths containing spaces must be quoted):"), c);
        c.gridy++;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        fields.add(new JScrollPane(arguments), c);
        c.gridy++;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        fields.add(enableGdb, c);
        c.gridy++;
        fields.add(new JLabel("GDB TCP port:"), c);
        c.gridy++;
        fields.add(port, c);
        c.gridy++;
        fields.add(waitForDebugger, c);
        panel.add(fields, BorderLayout.CENTER);
    }

    @Override
    protected void resetEditorFrom(@NotNull QemuRunConfiguration cfg) {
        executable.setText(cfg.getQemuCommand());
        arguments.setText(cfg.getArguments());
        enableGdb.setSelected(cfg.isEnableGDB());
        port.setText(Integer.toString(cfg.getTcpPort()));
        waitForDebugger.setSelected(cfg.isWaitForDebugger());
    }

    @Override
    protected void applyEditorTo(@NotNull QemuRunConfiguration cfg) {
        cfg.setQemuCommand(executable.getText().trim());
        cfg.setArguments(arguments.getText());
        cfg.setEnableGDB(enableGdb.isSelected());
        try { cfg.setTcpPort(Integer.parseInt(port.getText().trim())); }
        catch (NumberFormatException e) { cfg.setTcpPort(-1); }
        cfg.setWaitForDebugger(waitForDebugger.isSelected());
    }

    @NotNull
    @Override
    protected JComponent createEditor() { return panel; }
}
