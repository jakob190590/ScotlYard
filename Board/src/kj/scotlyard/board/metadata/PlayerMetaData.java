package kj.scotlyard.board.metadata;

import java.awt.Color;

import kj.scotlyard.game.model.Player;

public class PlayerMetaData {

	private String name;

	private Color color;
	
	public PlayerMetaData(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	
	public PlayerMetaData(Player player) {
		this("Unknown " + player.getClass().getSimpleName(), Color.ORANGE);
	}

	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}

}
