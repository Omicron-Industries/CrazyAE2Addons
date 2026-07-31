package net.oktawia.crazyae2addons.logic.interfaces;

import java.util.List;

import appeng.api.stacks.GenericStack;

import net.oktawia.crazyae2addons.menus.part.EmitterTerminalMenu;

public interface IEmitterTerminalHost {
    List<EmitterTerminalMenu.StorageEmitterInfo> getEmitters();

    List<EmitterTerminalMenu.StorageEmitterInfo> getEmitters(String filter);

    boolean setEmitterValue(String uuid, long value);

    boolean setEmitterConfig(String uuid, GenericStack stack);

    default void markDirty() {
    }
}
