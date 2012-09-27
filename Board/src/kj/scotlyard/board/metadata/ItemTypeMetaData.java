package kj.scotlyard.board.metadata;

import javax.swing.Icon;

import kj.scotlyard.game.model.item.Item;

public class ItemTypeMetaData {
	
	private String name;

	private Icon icon;

	public ItemTypeMetaData(String name, Icon icon) {
		this.name = name;
		this.icon = icon;
	}

	public ItemTypeMetaData(Class<? extends Item> itemType) {
		name = itemType.getSimpleName();
	}

	public String getName() {
		return name;
	}

	public Icon getIcon() {
		return icon;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
