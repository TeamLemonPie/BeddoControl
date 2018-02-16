package de.lemonpie.beddocontrol.midi;

public class MidiAction {

	public enum MidiActionType {
		PLAYER_FOLD,
		CONFIRM,
		LOCK_ALL_TOGGLE,
		UNLOCK_BOARD,
		NEW_ROUND,
		BOARD_CLEAR;
	}

	private int key;
	private MidiActionType type;
	private int additionalInfo = -1;

	public MidiAction(int key, MidiActionType type) {
		this.key = key;
		this.type = type;
	}

	public MidiAction(int key, MidiActionType type, int additionalInfo) {
		this.key = key;
		this.type = type;
		this.additionalInfo = additionalInfo;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public MidiActionType getType() {
		return type;
	}

	public void setType(MidiActionType type) {
		this.type = type;
	}

	public int getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(int additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
}
