package org.bladecoder.venus.actions.scene0;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;
import com.bladecoder.engine.actions.Action;
import com.bladecoder.engine.actions.ActionCallback;
import com.bladecoder.engine.actions.Param;
import com.bladecoder.engine.actions.Param.Type;
import com.bladecoder.engine.anim.AnimationDesc;
import com.bladecoder.engine.assets.EngineAssetManager;
import com.bladecoder.engine.model.ImageRenderer;
import com.bladecoder.engine.model.SpriteActor;
import com.bladecoder.engine.model.Text;
import com.bladecoder.engine.model.TextManager;
import com.bladecoder.engine.model.World;
import com.bladecoder.engine.model.BaseActor.ActorLayer;
import com.bladecoder.engine.util.EngineLogger;

public class Scene0InitAction implements Action, ActionCallback {
	
	public static final String INFO = "Init action for the intro scene";
	public static final Param[] PARAMS = {
		new Param("text", "The text to show", Type.STRING)
		};		

	private static final float TITLE_TIME = 0.2f;
	private static final float BLACK_SCREEN_TIME = 6f;
	
	private static final int INIT_STATE = 0;
	private static final int TITLE_STATE = 1;
	private static final int LEAVE_STATE = 2;

	String text;
	String titleFilename;
	
	int state = INIT_STATE;

	@Override
	public void setParams(HashMap<String, String> params) {
		if(params.get("text") != null && params.get("title_filename") != null ) {
			text = params.get("text");
			titleFilename = params.get("title_filename");
		} else {
			EngineLogger.debug("Scene0InitAction: some parameter missing");
		}
	}

	@Override
	public boolean run(ActionCallback cb) {
		EngineLogger.debug("SCENE0 INIT ");
		
		World.getInstance().showInventory(false);
		
		World.getInstance().getTextManager().addSubtitle(text, TextManager.POS_CENTER, TextManager.POS_CENTER, false, Text.Type.PLAIN, Color.WHITE, this);
		
		return false;
	}

	@Override
	public void resume() {
		switch(state) {
		case INIT_STATE:
			
			World.getInstance().getCurrentScene().getActor("forward", false)
			.setInteraction(false);			
			
			SpriteActor a = new SpriteActor();
			ImageRenderer r = new ImageRenderer();
			AnimationDesc fa = new AnimationDesc();
			fa.source = titleFilename;
			fa.id = titleFilename;
			r.addAnimation(fa);
			a.setLayer(ActorLayer.FOREGROUND);
			a.setRenderer(r);
			a.setVisible(true);
			a.setInteraction(false);
			a.setBbox(new Polygon());
			a.setBboxFromRenderer(true);
			a.startAnimation(fa.id, null);
			a.setPosition(1920/2 * EngineAssetManager.getInstance().getScale(), 0);
			World.getInstance().getCurrentScene().addActor(a);
							
			World.getInstance().addTimer(TITLE_TIME, this);
			state = TITLE_STATE;
			break;
		case TITLE_STATE:
			
			World.getInstance().addTimer(BLACK_SCREEN_TIME, this);
			state = LEAVE_STATE;
			
			break;
		case LEAVE_STATE:
			World.getInstance().getCurrentScene().runVerb("leave");
			break;
		}		
	}

	@Override
	public String getInfo() {
		return INFO;
	}

	@Override
	public Param[] getParams() {
		return PARAMS;
	}
}
