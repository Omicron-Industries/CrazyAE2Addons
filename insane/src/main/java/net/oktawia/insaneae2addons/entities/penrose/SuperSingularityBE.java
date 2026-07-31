package net.oktawia.insaneae2addons.entities.penrose;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.blockentity.grid.AENetworkBlockEntity;

import net.oktawia.insaneae2addons.defs.regs.InsaneBlockEntityRegistrar;
import net.oktawia.insaneae2addons.defs.regs.InsaneBlockRegistrar;

public class SuperSingularityBE extends AENetworkBlockEntity implements IGridTickable {

    private static final int TICK_RATE = 5;
    private static final int HURT_RADIUS = 2;
    private static final float HURT_AMOUNT = 5f;

    public SuperSingularityBE(BlockPos pos, BlockState blockState) {
        super(InsaneBlockEntityRegistrar.SUPER_SINGULARITY_BE.get(), pos, blockState);

        getMainNode()
                .setIdlePowerUsage(8192)
                .addService(IGridTickable.class, this)
                .setVisualRepresentation(new ItemStack(InsaneBlockRegistrar.SUPER_SINGULARITY_BLOCK.get()));
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TICK_RATE, TICK_RATE, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        Level level = getLevel();
        if (level == null || level.isClientSide()) {
            return TickRateModulation.IDLE;
        }

        DamageSource source = level.damageSources().generic();
        AABB area = new AABB(getBlockPos()).inflate(HURT_RADIUS);

        for (Player player : level.getEntitiesOfClass(Player.class, area,
                p -> p.isAlive() && !p.isCreative())) {
            player.hurt(source, HURT_AMOUNT);
        }

        return TickRateModulation.IDLE;
    }
}
