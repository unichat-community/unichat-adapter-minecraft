package me.voguh.unichat.adapter.network;

import me.voguh.unichat.adapter.network.packet.server.SendServerSettingsPayload;
import me.voguh.unichat.adapter.network.packet.server.SendWorkersPayload;
import me.voguh.unichat.adapter.server.MinecraftServerHolder;
import me.voguh.unichat.adapter.server.ServerPermissions;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class UniChatNetwork {

    /* <=======================================[ SERVER ]=======================================> */
    public static void sendToPlayers(CustomPacketPayload payload) {
        MinecraftServerHolder.execute((server) -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                sendToPlayer(player, payload);
            }
        });
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        if (!player.connection.hasChannel(payload)) {
            return;
        } else if (isOperatorsOnly(payload) && !ServerPermissions.canManage(player)) {
            return;
        }

        PacketDistributor.sendToPlayer(player, payload);
    }

    private static boolean isOperatorsOnly(CustomPacketPayload payload) {
        return payload instanceof SendServerSettingsPayload || payload instanceof SendWorkersPayload;
    }
    /* <=====================================[ END SERVER ]=====================================> */

    /* <=======================================[ CLIENT ]=======================================> */
    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }
    /* <=====================================[ END CLIENT ]=====================================> */

}
