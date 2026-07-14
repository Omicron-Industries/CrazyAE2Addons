package net.oktawia.insaneae2addons.interfaces;

import appeng.api.networking.security.IActionSource;
import org.jetbrains.annotations.Nullable;

public interface IProviderSourceFilter {
    boolean insaneAE2Addons$allowSource(@Nullable IActionSource src);
}
