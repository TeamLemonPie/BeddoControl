package de.lemonpie.beddocontrol.network.listener;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.model.listener.PlayerListener;
import de.lemonpie.beddocontrol.network.ControlSocket;
import de.lemonpie.beddocontrol.network.command.send.PlayerNameSendCommand;

import java.net.SocketException;

public class PlayerListenerImpl implements PlayerListener {

    private ControlSocket socket;

    public PlayerListenerImpl(ControlSocket socket) {
        this.socket = socket;
    }

    @Override
    public void nameDidChange(Player player, String name) {
        PlayerNameSendCommand cmd = new PlayerNameSendCommand(PlayerNameSendCommand.NameType.NAME, player.getId(), name);
        try {
            socket.write(cmd);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void twitchNameDidChange(Player player, String twitchName) {
        PlayerNameSendCommand cmd = new PlayerNameSendCommand(PlayerNameSendCommand.NameType.TWITCH, player.getId(), twitchName);
        try {
            socket.write(cmd);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cardDidChangeAtIndex(Player player, int index, Card card) {
    }

    @Override
    public void chipsDidChange(Player player, int chips) {

    }
}
