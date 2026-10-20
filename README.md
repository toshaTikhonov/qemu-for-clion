# QEMU for CLion

A QEMU run configuration plugin, modernized for **CLion 2026.2**.

## Build

Requirements: **JDK 25**, **Gradle 9+**, internet access to download the CLion SDK.

```sh
gradle buildPlugin
```

Install the resulting ZIP from `build/distributions/` using **Settings → Plugins → Install Plugin from Disk**.

## Running an Atocha Linux / Aqsi Cube ARM image

Select **Run → Edit Configurations → + → QEMU**. Set:

- Executable: `qemu-system-arm`
- Arguments (replace example paths with actual files):

```text
-M virt -cpu cortex-a7 -smp 2 -m 512M -nographic -kernel /path/to/zImage -initrd /path/to/cube-initramfs.cpio.gz -drive file=/path/to/cube-rootfs.squashfs,if=virtio,format=raw,readonly=on -drive file=/path/to/data.img,if=virtio,format=raw -append "console=ttyAMA0,115200 rdinit=/init"
```

These arguments match the QEMU ARM32 boot architecture used for the Aqsi Cube image; the kernel and initramfs must support the chosen virtual devices and root filesystem. The host QEMU version and paths remain user-configurable.

Enable **QEMU GDB server**, with **Pause CPU until debugger attaches**, for GDB stub access. The stub binds to `127.0.0.1` by default.

## SDK integration

The **Atocha SDK is used for cross-compilation**, independently of launching the emulator. Configure the SDK/sysroot through CLion's CMake toolchain or external build presets; use QEMU run configurations to start the resulting image. The plugin does not embed any proprietary SDK components.

## Limitations

This version intentionally removes old CLion internal CMake build hooks; build using CLion before running QEMU, or add a standard *Before launch: Build* task. CLion GDB debugger attachment is a separate IDE configuration; enabling the GDB stub does not automatically connect the IDE debugger.

Old configurations that relied on a CMake target must be recreated. ISO-based configurations can still use `-cdrom` in the arguments field.
