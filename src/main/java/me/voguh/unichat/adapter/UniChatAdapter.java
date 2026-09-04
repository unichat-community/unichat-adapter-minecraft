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

import me.voguh.unichat.adapter.client.ClientConfig;
import me.voguh.unichat.adapter.command.CommandsBootstrap;
import me.voguh.unichat.adapter.event.UniChatEventUtils;
import me.voguh.unichat.adapter.network.UniChatNetwork;
import me.voguh.unichat.adapter.server.ServerBootstrap;
import me.voguh.unichat.adapter.server.ServerConfig;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(UniChatAdapter.MODID)
public final class UniChatAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UniChatAdapter.class);

    public static final String MODID = "unichat_adapter";

    public UniChatAdapter(FMLJavaModLoadingContext ctx) {
        UniChatEventUtils.initialize();
        ctx.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        ctx.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        UniChatNetwork.INSTANCE.register();

        RegisterCommandsEvent.BUS.addListener(CommandsBootstrap::register);
        ServerStartedEvent.BUS.addListener(ServerBootstrap::onServerStarted);
        ServerStoppingEvent.BUS.addListener(ServerBootstrap::onServerStopping);
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(ServerBootstrap::onPlayerLoggedIn);
    }

}
