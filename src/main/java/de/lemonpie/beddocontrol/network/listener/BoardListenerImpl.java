package de.lemonpie.beddocontrol.network.listener;

import java.net.SocketException;

import de.lemonpie.beddocontrol.listener.BoardListener;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.network.ControlSocket;
import de.lemonpie.beddocontrol.network.command.send.ReaderSendCommand;
import de.lemonpie.beddocontrol.network.command.send.ReaderSendCommand.ReaderType;

public class BoardListenerImpl implements BoardListener {

    private ControlSocket socket;

    public BoardListenerImpl(ControlSocket socket) {
        this.socket = socket;
    }

	@Override
	public void cardDidChangeAtIndex(int index, Card card)
	{
		
	}

	@Override
	public void boardReaderIdDidChange(int index, int readerId)
	{
		ReaderSendCommand cmd = new ReaderSendCommand(ReaderType.BOARD, readerId);
        try {
            socket.write(cmd);
        } catch (SocketException e) {
            e.printStackTrace();
        }
	}
}
