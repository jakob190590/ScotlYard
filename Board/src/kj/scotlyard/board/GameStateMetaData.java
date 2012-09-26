package kj.scotlyard.board;

import java.util.Map;

import org.apache.log4j.Logger;

import kj.scotlyard.game.model.Player;

public abstract class GameStateMetaData {
	
	private static final Logger logger = Logger.getLogger(GameStateMetaData.class);
	
	private static Map<Player, PlayerMetaData> playerMetaData;
	
	public static PlayerMetaData getPlayerMetaData(Player player) {
		PlayerMetaData pmd = playerMetaData.get(player);
		if (pmd == null) {
			pmd = new PlayerMetaData("", null); // To prevent NullPointerExceptions
			logger.error("access to non-existing PlayerMetaData for Player " + player);
		}
		return pmd;
	}
	
	public static void setPlayerMetaData(Player player, PlayerMetaData playerMetaData) {
		GameStateMetaData.playerMetaData.put(player, playerMetaData);
	}

}
