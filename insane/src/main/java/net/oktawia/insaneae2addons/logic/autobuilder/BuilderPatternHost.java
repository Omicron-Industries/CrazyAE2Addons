package net.oktawia.insaneae2addons.logic.autobuilder;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.storage.ISubMenuHost;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.defs.regs.InsaneItemRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneMenuRegistrar;
import net.oktawia.insaneae2addons.items.BuilderPatternItem;
import net.oktawia.insaneae2addons.util.ProgramExpander;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class BuilderPatternHost extends ItemMenuHost implements ISubMenuHost {

    private boolean code;

    public BuilderPatternHost(Player player, ItemStack itemStack, int inventorySlot) {
        super(player, inventorySlot, itemStack);
        this.code = BuilderPatternItem.getProgramId(getItemStack()) != null;
    }

    public String getProgram() {
        return loadProgramFromFile(this.getItemStack(), getPlayer().getServer());
    }

    public void setProgram(String program) {
        ProgramExpander.Result result = ProgramExpander.expand(program);

        if (result.success) {
            String id = BuilderPatternItem.getProgramId(getItemStack());
            if (id == null || id.isEmpty()) {
                id = UUID.randomUUID().toString();
            }

            saveProgramToFile(id, program, getPlayer().getServer());
            BuilderPatternItem.setProgramId(getItemStack(), id);
            this.code = true;
        } else {
            BuilderPatternItem.setProgramId(getItemStack(), null);
            this.code = false;
        }
    }

    public static String loadProgramFromFile(ItemStack stack, MinecraftServer server) {
        try {
            if (server == null || stack == null || stack.isEmpty()) {
                return "";
            }

            String id = BuilderPatternItem.getProgramId(stack);
            if (id == null || id.isEmpty()) {
                return "";
            }

            Path file = server.getWorldPath(new LevelResource("serverdata"))
                    .resolve("autobuilder")
                    .resolve(id);

            if (!Files.exists(file)) {
                return "";
            }

            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (Exception e) {
            InsaneAddons.LOGGER.debug("failed to read builder pattern file", e);
            return "";
        }
    }

    public static void saveProgramToFile(String id, String code, MinecraftServer server) {
        if (server == null || id == null || id.isEmpty()) {
            return;
        }

        Path file = server.getWorldPath(new LevelResource("serverdata"))
                .resolve("autobuilder")
                .resolve(id);

        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, code, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LogUtils.getLogger().info(e.toString());
        }
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(InsaneMenuRegistrar.BUILDER_PATTERN_MENU.get(), player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return InsaneItemRegistrar.BUILDER_PATTERN.get().getDefaultInstance();
    }
}