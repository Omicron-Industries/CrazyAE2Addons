package net.oktawia.crazyae2addons.mixins;

import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class Ae2Detection {

    private static final String CPU_SELECTION_LIST = "appeng/client/gui/widgets/CPUSelectionList";
    private static final String CRAFTING_CPU_SCREEN = "appeng/client/gui/me/crafting/CraftingCPUScreen";
    private static final String CRAFT_CONFIRM_MENU = "appeng/menu/me/crafting/CraftConfirmMenu";

    private static final String CPU_LIST_MENU_DESC = "Lappeng/menu/me/crafting/ICpuListMenu;";
    private static final String CRAFTING_SERVICE = "appeng/api/networking/crafting/ICraftingService";
    private static final String SUBMIT_JOB_FOLLOWING_DESC = "("
            + "Lappeng/api/networking/crafting/ICraftingPlan;"
            + "Lappeng/api/networking/crafting/ICraftingRequester;"
            + "Lappeng/api/networking/crafting/ICraftingCPU;"
            + "Z"
            + "Lappeng/api/networking/security/IActionSource;"
            + "Z"
            + ")Lappeng/api/networking/crafting/ICraftingSubmitResult;";

    public static final boolean SHARED_CPU_LIST_WIDGET = hasFieldOfType(CPU_SELECTION_LIST, "menu", CPU_LIST_MENU_DESC);

    public static final boolean CRAFTING_CPU_SCREEN_OVERRIDES_INIT = declaresMethod(CRAFTING_CPU_SCREEN, "()V", "init",
            "m_7856_");

    public static final boolean CRAFT_CONFIRM_BUILDS_CPU_LIST = declaresMethod(CRAFT_CONFIRM_MENU, null,
            "createCpuList");

    public static final boolean CRAFT_CONFIRM_SUBMITS_WITH_FOLLOWING = callsMethod(CRAFT_CONFIRM_MENU, CRAFTING_SERVICE,
            "submitJob", SUBMIT_JOB_FOLLOWING_DESC);

    private Ae2Detection() {
    }

    private static ClassNode read(String internalName) {
        try (InputStream in = Ae2Detection.class.getClassLoader().getResourceAsStream(internalName + ".class")) {
            if (in == null) {
                return null;
            }

            ClassNode node = new ClassNode();
            new ClassReader(in).accept(node, 0);
            return node;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean hasFieldOfType(String internalName, String fieldName, String descriptor) {
        ClassNode node = read(internalName);
        if (node == null || node.fields == null) {
            return false;
        }

        for (FieldNode field : node.fields) {
            if (fieldName.equals(field.name) && descriptor.equals(field.desc)) {
                return true;
            }
        }

        return false;
    }

    private static boolean declaresMethod(String internalName, String descriptor, String... names) {
        ClassNode node = read(internalName);
        if (node == null || node.methods == null) {
            return false;
        }

        for (MethodNode method : node.methods) {
            if (descriptor != null && !descriptor.equals(method.desc)) {
                continue;
            }

            for (String name : names) {
                if (name.equals(method.name)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean callsMethod(String internalName, String owner, String name, String descriptor) {
        ClassNode node = read(internalName);
        if (node == null || node.methods == null) {
            return false;
        }

        for (MethodNode method : node.methods) {
            if (method.instructions == null) {
                continue;
            }

            for (AbstractInsnNode insn : method.instructions) {
                if (!(insn instanceof MethodInsnNode call)) {
                    continue;
                }

                if (owner.equals(call.owner) && name.equals(call.name) && descriptor.equals(call.desc)) {
                    return true;
                }
            }
        }

        return false;
    }
}
