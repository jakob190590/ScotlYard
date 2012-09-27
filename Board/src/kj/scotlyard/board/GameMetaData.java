package kj.scotlyard.board;

import java.util.Map;

import org.apache.log4j.Logger;

import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Item;

public abstract class GameMetaData {
	
	private static final Logger logger = Logger.getLogger(GameMetaData.class);
	
	private static Map<Player, PlayerMetaData> playerMetaData;
	
	private static Map<Class<? extends Item>, ItemTypeMetaData> itemTypeMetaData;
	
	public static PlayerMetaData getPlayerMetaData(Player player) {
		PlayerMetaData pmd = playerMetaData.get(player);
		if (pmd == null) {
			pmd = new PlayerMetaData(player); // To prevent NullPointerExceptions
			logger.error("access to non-existing MetaData for Player " + player);
		}
		return pmd;
	}
	
	public static void setPlayerMetaData(Player player, PlayerMetaData playerMetaData) {
		GameMetaData.playerMetaData.put(player, playerMetaData);
	}
	
	
	public static ItemTypeMetaData getItemTypeMetaData(Class<? extends Item> itemType) {
		ItemTypeMetaData imd = itemTypeMetaData.get(itemType);
		if (imd == null) {
			imd = new ItemTypeMetaData(itemType);
			logger.error("access to non-existing MetaData for itemType " + itemType);
		}
		return imd;
	}
	
	public static void setItemTypeMetaData(Player player, PlayerMetaData playerMetaData) {
		GameMetaData.playerMetaData.put(player, playerMetaData);
	}

}
