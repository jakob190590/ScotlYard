package kj.scotlyard.board.metadata;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Item;

public abstract class GameMetaData {
	
	private static final Logger logger = Logger.getLogger(GameMetaData.class);
	
	private static Map<Player, PlayerMetaData> playerMetaData = new HashMap<>();
	
	private static Map<Class<? extends Item>, ItemTypeMetaData> itemTypeMetaData = new HashMap<>();
	
	public static PlayerMetaData getForPlayer(Player player) {
		PlayerMetaData pmd = playerMetaData.get(player);
		if (pmd == null) {
			pmd = new PlayerMetaData(player); // To prevent NullPointerExceptions
			logger.warn("access to non-existing MetaData for Player " + player);
		}
		return pmd;
	}
	
	public static void setForPlayer(Player player, PlayerMetaData playerMetaData) {
		GameMetaData.playerMetaData.put(player, playerMetaData);
	}
	
	
	public static ItemTypeMetaData getForItemType(Class<? extends Item> itemType) {
		ItemTypeMetaData imd = itemTypeMetaData.get(itemType);
		if (imd == null) {
			imd = new ItemTypeMetaData(itemType);
			logger.warn("access to non-existing MetaData for itemType " + itemType);
		}
		return imd;
	}
	
	public static void setForItemType(Class<? extends Item> itemType, ItemTypeMetaData itemTypeMetaData) {
		GameMetaData.itemTypeMetaData.put(itemType, itemTypeMetaData);
	}

}
