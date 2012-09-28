/*
 * ScotlYard -- A software implementation of the Scotland Yard board game
 * Copyright (C) 2012  Jakob Schöttl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package kj.scotlyard.board.metadata;

import java.util.HashMap;
import java.util.Map;

import kj.scotlyard.game.model.Player;
import kj.scotlyard.game.model.item.Item;

import org.apache.log4j.Logger;

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
