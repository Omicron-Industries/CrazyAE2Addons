package net.oktawia.insaneae2addons.compat.CC;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;

import net.oktawia.insaneae2addons.InsaneAddons;
import net.oktawia.insaneae2addons.entities.penrose.PenroseFrameBE;
import net.oktawia.insaneae2addons.entities.penrose.PortablePenroseSphereControllerBE;

public final class PenroseFramePeripheral implements GenericPeripheral {

    @Override
    public String id() {
        return new ResourceLocation(InsaneAddons.MODID, "penrose_frame").toString();
    }

    @LuaFunction(mainThread = true)
    public final boolean hasController(PenroseFrameBE frame) {
        return controllerOf(frame) != null;
    }

    @LuaFunction(mainThread = true)
    public final boolean isActive(PenroseFrameBE frame) {
        PortablePenroseSphereControllerBE controller = controllerOf(frame);
        return controller != null && controller.isBlackHoleActive();
    }

    @LuaFunction(mainThread = true)
    public final String getStoredEnergy(PenroseFrameBE frame) {
        return text(controllerOf(frame), PortablePenroseSphereControllerBE::getStoredEnergy);
    }

    @LuaFunction(mainThread = true)
    public final String getStoredEnergyInDisk(PenroseFrameBE frame) {
        return text(controllerOf(frame), PortablePenroseSphereControllerBE::getStoredEnergyInDisk);
    }

    @LuaFunction(mainThread = true)
    public final double getHeat(PenroseFrameBE frame) {
        PortablePenroseSphereControllerBE controller = controllerOf(frame);
        return controller == null ? 0.0 : controller.getHeat();
    }

    @LuaFunction(mainThread = true)
    public final String getMass(PenroseFrameBE frame) {
        return text(controllerOf(frame), PortablePenroseSphereControllerBE::getBlackHoleMass);
    }

    @LuaFunction(mainThread = true)
    public final String getLastGeneratedFePerTickGross(PenroseFrameBE frame) {
        return text(controllerOf(frame), PortablePenroseSphereControllerBE::getLastGeneratedFePerTick);
    }

    @LuaFunction(mainThread = true)
    public final String getLastConsumedFePerTick(PenroseFrameBE frame) {
        return text(controllerOf(frame), PortablePenroseSphereControllerBE::getLastConsumedFePerTick);
    }

    @LuaFunction(mainThread = true)
    public final String getLastSecondMassDelta(PenroseFrameBE frame) {
        return text(controllerOf(frame), PortablePenroseSphereControllerBE::getLastSecondMassDelta);
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getStatus(PenroseFrameBE frame) {
        PortablePenroseSphereControllerBE controller = controllerOf(frame);

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("attached", controller != null);
        if (controller == null) {
            return status;
        }

        status.put("active", controller.isBlackHoleActive());
        status.put("storedEnergy", Long.toString(controller.getStoredEnergy()));
        status.put("storedEnergyInDisk", Long.toString(controller.getStoredEnergyInDisk()));
        status.put("heat", controller.getHeat());
        status.put("mass", Long.toString(controller.getBlackHoleMass()));
        status.put("lastGeneratedFePerTickGross", Long.toString(controller.getLastGeneratedFePerTick()));
        status.put("lastConsumedFePerTick", Long.toString(controller.getLastConsumedFePerTick()));
        status.put("lastSecondMassDelta", Long.toString(controller.getLastSecondMassDelta()));
        return status;
    }

    private static @Nullable PortablePenroseSphereControllerBE controllerOf(@Nullable PenroseFrameBE frame) {
        return frame == null ? null : frame.getController();
    }

    private static String text(@Nullable PortablePenroseSphereControllerBE controller, LongReader reader) {
        return controller == null ? "0" : Long.toString(reader.read(controller));
    }

    private interface LongReader {
        long read(PortablePenroseSphereControllerBE controller);
    }
}
