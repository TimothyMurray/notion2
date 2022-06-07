package main;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class Launcher
{

	//Start running. Don't do anything else here.
	public static void main(String[] args) throws SlickException
	{
		AppGameContainer app = new AppGameContainer(new Game("Perpetual Notion"));
		
		app.setDisplayMode(app.getScreenWidth()-192, app.getScreenHeight()-108, false);  //W, H, fullscreen?
		
		app.setMaximumLogicUpdateInterval(17);
		app.setMinimumLogicUpdateInterval(16);
		app.setTargetFrameRate(60);
		
		app.setAlwaysRender(true); //VERY useful in a realtime server situation! lol =P Prevents game code from pausing when alt-tabbed, minimized, etc.
		
		app.start();
		
	}
	
}