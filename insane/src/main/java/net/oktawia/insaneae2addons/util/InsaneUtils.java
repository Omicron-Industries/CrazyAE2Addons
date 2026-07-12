package net.oktawia.insaneae2addons.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class InsaneUtils {

    public static final NavigableMap<Long, String> voltagesMap = Collections.unmodifiableNavigableMap(new TreeMap<>(Map.ofEntries(
            Map.entry((long) Math.pow(2, 3), "ULV"),
            Map.entry((long) Math.pow(2, 5), "LV"),
            Map.entry((long) Math.pow(2, 7), "MV"),
            Map.entry((long) Math.pow(2, 9), "HV"),
            Map.entry((long) Math.pow(2, 11), "EV"),
            Map.entry((long) Math.pow(2, 13), "IV"),
            Map.entry((long) Math.pow(2, 15), "LuV"),
            Map.entry((long) Math.pow(2, 17), "ZPM"),
            Map.entry((long) Math.pow(2, 19), "UV"),
            Map.entry((long) Math.pow(2, 21), "UHV"),
            Map.entry((long) Math.pow(2, 23), "UEV"),
            Map.entry((long) Math.pow(2, 25), "UIV"),
            Map.entry((long) Math.pow(2, 27), "UXV"),
            Map.entry((long) Math.pow(2, 29), "OpV"),
            Map.entry((long) Math.pow(2, 31), "MAX")
    )));

    public static Direction getRightDirection(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return facing.getClockWise(Direction.Axis.Y);
    }

    public static Direction getLeftDirection(BlockState state) {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        return facing.getCounterClockWise(Direction.Axis.Y);
    }

    private InsaneUtils() {
    }
}
