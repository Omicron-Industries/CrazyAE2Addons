package net.oktawia.insaneae2addons.interfaces;

import org.jetbrains.annotations.Nullable;

import appeng.api.networking.security.IActionSource;

public interface IProviderSourceFilter {
    boolean insaneAE2Addons$allowSource(@Nullable IActionSource src);
}
