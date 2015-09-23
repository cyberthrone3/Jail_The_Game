package by.bsuir.kovalev.jail.window;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import by.bsuir.kovalev.jail.framework.KeyInput;
import by.bsuir.kovalev.jail.framework.MouseInput;
import by.bsuir.kovalev.jail.framework.ObjectId;
import by.bsuir.kovalev.jail.framework.Texture;
import by.bsuir.kovalev.jail.objects.Block;
import by.bsuir.kovalev.jail.objects.Player;
import by.bsuir.kovalev.jail.objects.Security;

public class Game extends Canvas implements Runnable{

	private static final int ONE_SECOND_IN_NANOS = 1000000000;
	private static final long serialVersionUID = -3957124689436603261L;
	private boolean isRunning = false;
	private Thread thread;
	protected Handler handler;
	protected Camera camera;
	static Texture texture;
	private BufferedImage levelImage = null;
	private BufferedImage background = null;
	
	public static void main(String args[]){
		new Window(800, 600, "Jail The Game", new Game());
	}
	
	public synchronized void start(){
		if(isRunning)
			return;
		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void run() {
		initializeGameObjects();
		this.requestFocus();
		displayGameObjects();
	}
	
	private void initializeGameObjects(){
		handler = new Handler();
		camera = new Camera();
		texture = new Texture();
		loadLevel();
		this.addKeyListener(new KeyInput(handler));
		this.addMouseListener(new MouseInput(handler));
	}
	
	private void displayGameObjects(){
		long lastTime = System.nanoTime();
		double amountOfTicksPerSecond = 60;
		double timeForOneTickInNanos = ONE_SECOND_IN_NANOS / amountOfTicksPerSecond;
		double delta = 0;
		while(isRunning){
			long currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / timeForOneTickInNanos;
			lastTime = currentTime;
			while(delta >= 1){
				tick();
				delta--;
			}	
			renderGame();
		}
	}
	
	private void tick(){
		handler.tick();
		for(int i = 0; i < handler.objectList.size(); i++){
			if(handler.objectList.get(i).getObjectId() == ObjectId.Player){
				camera.tick(handler.objectList.get(i));
			}
		}	
	}
	
	private void renderGame(){
		BufferStrategy bufferStrategy = this.getBufferStrategy();
		if(bufferStrategy == null){
			this.createBufferStrategy(3);
			return;
		}
		drawGame(bufferStrategy);
		bufferStrategy.show();
	}
	
	private void drawGame(BufferStrategy bufferStrategy){
		Graphics2D graphics2D = (Graphics2D) bufferStrategy.getDrawGraphics();
		graphics2D.fillRect(0, 0, getWidth(), getHeight());
		graphics2D.drawImage(background, 0, 0, this);
		graphics2D.translate((int)camera.getX(), (int)camera.getY());
		handler.render(graphics2D);
		graphics2D.dispose();
	}
	
	private void loadLevel(){
		BufferedImageLoader loader = new BufferedImageLoader();
		levelImage = loader.loadImage("/level.png");
		background = loader.loadImage("/background.png");
		loadLevelFromImage(levelImage);
	}
	
	private void loadLevelFromImage(BufferedImage image){
		for(int x = 0; x < image.getHeight(); x++){
			for(int y = 0; y < image.getWidth(); y++){
				int pixel = image.getRGB(x, y); 
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;
				checkForWhitePixel(x, y, red, green, blue);
				checkForBluePixel(x, y, red, green, blue);
				checkForRedPixel(x, y, red, green, blue);
			}
		}
	}
	
	private void checkForWhitePixel(int x, int y, int red, int green, int blue){
		if(red == 255 && green == 255 && blue == 255){
			handler.addObject(new Block(x*Block.TEXTURE_SIZE, y*Block.TEXTURE_SIZE, 32, 32, ObjectId.Block, Block.BRICK_BLOCK));
		}
	}
	
	private void checkForBluePixel(int x, int y, int red, int green, int blue){
		if(red == 0 && green == 0 && blue == 255){
			handler.addObject(new Player(x*Block.TEXTURE_SIZE, y*Block.TEXTURE_SIZE, 48, 96, ObjectId.Player, handler));
		}
	}
	
	private void checkForRedPixel(int x, int y, int red, int green, int blue){
		if(red == 255 && green == 0 && blue == 0){
			handler.addObject(new Security(x*Block.TEXTURE_SIZE, y*Block.TEXTURE_SIZE, 48, 96, ObjectId.Security, handler));
		}
	}
	
	public static Texture getTextureInstance(){
		return texture;
	}
	
}
