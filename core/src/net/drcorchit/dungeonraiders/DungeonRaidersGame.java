package net.drcorchit.dungeonraiders;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.drcorchit.dungeonraiders.actors.Player;
import net.drcorchit.dungeonraiders.actors.Room;
import net.drcorchit.dungeonraiders.assets.*;
import net.drcorchit.dungeonraiders.assets.animation.AnimationState;
import net.drcorchit.dungeonraiders.assets.animation.Animations;
import net.drcorchit.dungeonraiders.drawing.Draw;
import net.drcorchit.dungeonraiders.input.KeyboardInfo;
import net.drcorchit.dungeonraiders.input.MouseInfo;
import net.drcorchit.dungeonraiders.stages.DungeonStage;
import net.drcorchit.dungeonraiders.utils.Coordinate;
import net.drcorchit.dungeonraiders.utils.Direction;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.TreeMap;

public class DungeonRaidersGame extends ApplicationAdapter {
	private static DungeonRaidersGame instance;

	public static DungeonRaidersGame getInstance() {
		return instance;
	}

	private PolygonSpriteBatch batch;
	private DungeonStage stage;
	private Draw draw;
	public final MouseInfo mouse;
	public final KeyboardInfo keyboard;
	public final TreeMap<String, Object> debugInfoMap;

	public static Draw getDraw() {
		return instance.draw;
	}

	private Skeleton skeleton;
	private Skin skin;

	public DungeonRaidersGame() {
		super();
		mouse = new MouseInfo();
		keyboard = new KeyboardInfo();
		instance = this;
		debugInfoMap = new TreeMap<>();
	}

	@Override
	public void create() {
		batch = new PolygonSpriteBatch();
		batch.enableBlending();
		draw = new Draw(batch);
		LocalAssets.getInstance().load();

		stage = new DungeonStage(2);
		stage.backgroundSprite = Sprites.getSprite(Textures.BRICK);
		stage.wallSprites[1] = Sprites.getSprite(Textures.DUNGEON);
		stage.wallSprites[0] = Sprites.getSprite(Textures.DUNGEON);

		Player player = new Player(stage,
				Skeletons.human_female,
				Skins.lifeguard,
				new Vector(500, 500),
				new Vector(0, 110));
		player.setColliderToRectangle(0, 95, 40, 190);
		player.setCameraOffset(new Vector(0, 300));
		player.setViewBounds(0, 95, 400, 400);
		stage.addActor(player);

		Room room = new Room(stage, new Coordinate(0, 0));

		//a row of blocks
		Room.Layer layer = room.getLayer(0);
		for (int i = 0; i < Room.SIZE; i++) {
			layer.placeSquare(i, 0);
		}

		//some steps
		layer = room.getLayer(1);
		for (int i = 0; i < Room.SIZE; i++) {
			for (int j = 0; j < Room.SIZE; j++) {
				if (i > j) {
					layer.placeSquare(i, j);
				}
			}
		}

		stage.addRoom(room);

		mouse.setH(Gdx.graphics.getHeight());

		skeleton = Skeletons.human_female;
		skin = Skins.purple;
		AnimationState state = new AnimationState(Animations.jog);

	}

	public void act() {
		mouse.update();
		keyboard.update();
		stage.act();
	}

	public void draw() {
		stage.draw();
		batch.begin();
		StringBuilder builder = new StringBuilder();
		debugInfoMap.forEach((key, val) -> builder.append(key).append(": ").append(val).append("\n"));
		draw.drawText(40, 1040, Fonts.getDefaultFont(), builder.toString(), -1, Direction.SOUTHEAST, Color.GREEN);
		debugInfoMap.clear();

		Texture temp = Textures.BLG;
		TextureRegion tempR = new TextureRegion(temp);
		temp.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		//batch.draw(temp, 0, 0, 500, 500, 0, 0, 500, 500, false, false);
		//batch.draw(tempR, 0, 0, 0, 0, 256, 128, 1, 2, 0);
		//batch.draw();
		batch.end();
	}

	@Override
	public void render() {
		act();
		draw();


	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
