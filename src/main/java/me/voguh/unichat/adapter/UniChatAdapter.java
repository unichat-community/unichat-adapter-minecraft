/*!******************************************************************************
 * Copyright (c) 2026 Voguh
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package me.voguh.unichat.adapter;

import me.voguh.unichat.adapter.event.UniChatEventUtils;
import me.voguh.unichat.adapter.network.NetworkBootstrap;
import me.voguh.unichat.adapter.server.ServerBootstrap;
import me.voguh.unichat.adapter.server.ServerConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(UniChatAdapter.MODID)
public final class UniChatAdapter {

    public static final String MODID = "unichat_adapter";

    public UniChatAdapter(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(NetworkBootstrap::register);

        container.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);

        NeoForge.EVENT_BUS.addListener(ServerBootstrap::onServerStarted);
        NeoForge.EVENT_BUS.addListener(ServerBootstrap::onServerStopping);
        NeoForge.EVENT_BUS.addListener(ServerBootstrap::onServerStopped);
        NeoForge.EVENT_BUS.addListener(ServerBootstrap::onPlayerLoggedIn);
    }

    /* ====================================================================== */

    private void commonSetup(FMLCommonSetupEvent event) {
        UniChatEventUtils.initialize();
    }

}
