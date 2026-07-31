package net.oktawia.crazyae2addons.logic.interfaces;

import java.util.List;

import net.oktawia.crazyae2addons.menus.part.RedstoneTerminalMenu;

public interface IRedstoneTerminalHost {
    List<RedstoneTerminalMenu.EmitterInfo> getEmitters();

    List<RedstoneTerminalMenu.EmitterInfo> getEmitters(String filter);

    void toggle(String name);
}
