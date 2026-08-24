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

import me.voguh.unichat.adapter.config.CommonConfig;
import me.voguh.unichat.adapter.server.ServerBootstrap;
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
        ctx.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);

        ServerStartedEvent.BUS.addListener(ServerBootstrap::onServerStarted);
        ServerStoppingEvent.BUS.addListener(ServerBootstrap::onServerStopping);
    }

}
