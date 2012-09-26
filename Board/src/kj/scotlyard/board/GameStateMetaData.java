package kj.scotlyard.board;

import java.util.Map;

import kj.scotlyard.game.model.Player;

public abstract class GameStateMetaData {
	
	private static Map<Player, PlayerMetaData> playerMetaData;
	
	public static PlayerMetaData getPlayerMetaData(Player player) {
		return playerMetaData.get(player);
	}
	
	public static void setPlayerMetaData(Player player, PlayerMetaData playerMetaData) {
		GameStateMetaData.playerMetaData.put(player, playerMetaData);
	}

}
