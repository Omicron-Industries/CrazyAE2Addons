package net.oktawia.insaneae2addons.events;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

import lombok.Getter;

import net.oktawia.insaneae2addons.entities.penrose.PortablePenroseSphereControllerBE;

@Getter
public class PenroseSphereFormedEvent extends Event {

    private final ServerLevel level;
    private final BlockPos pos;
    private final PortablePenroseSphereControllerBE controller;

    @Nullable
    private final ServerPlayer player;

    public PenroseSphereFormedEvent(
            ServerLevel level,
            BlockPos pos,
            PortablePenroseSphereControllerBE controller,
            @Nullable ServerPlayer player) {
        this.level = level;
        this.pos = pos.immutable();
        this.controller = controller;
        this.player = player;
    }
}
