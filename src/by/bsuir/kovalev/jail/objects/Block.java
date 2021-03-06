package by.bsuir.kovalev.jail.objects;

import java.awt.Graphics;
import java.util.LinkedList;

import by.bsuir.kovalev.jail.framework.GameObject;
import by.bsuir.kovalev.jail.framework.ObjectId;
import by.bsuir.kovalev.jail.framework.Texture;
import by.bsuir.kovalev.jail.window.Game;

public class Block extends GameObject{
	
	Texture texture = Game.getTextureInstance();
	private int type;
	public static final int BRICK_BLOCK = 0;
	public static final int TEXTURE_SIZE = 32;

	public Block(int x, int y, int width, int height, ObjectId objectId, int type ) {
		super(x, y, width, height, objectId);
		this.type = type;
	}

	public void tick(LinkedList<GameObject> object) {
	}

	public void render(Graphics graphics) {
		if(type == 0)
			graphics.drawImage(texture.block[0], x, y, null);
	}

}
 