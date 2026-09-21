package me.voguh.unichat.adapter;

import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;
import me.voguh.unichat.adapter.client.ClientBootstrap;
import me.voguh.unichat.adapter.client.ClientConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import javax.imageio.spi.IIORegistry;

@Mod(value = UniChatAdapter.MODID, dist = Dist.CLIENT)
public final class UniChatAdapterClient {

    public UniChatAdapterClient(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::commonSetup);

        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        modEventBus.addListener(ClientBootstrap::onRegisterKeyMappings);
        NeoForge.EVENT_BUS.addListener(ClientBootstrap::onClientTick);
        NeoForge.EVENT_BUS.addListener(ClientBootstrap::onLoggingOut);
    }

    /* ====================================================================== */

    private void commonSetup(FMLClientSetupEvent event) {
        IIORegistry.getDefaultInstance().registerServiceProvider(new WebPImageReaderSpi());
    }

}
