package gameScreens;

import gameObjects.Player;

import java.awt.Font;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import main.Game;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;

import client.MPClient;

import com.esotericsoftware.kryonet.Server;

import server.MPServer;
import uiObjects.Button;
import uiObjects.FontMaster;
import utility.ScreenPosition;
import utility.Txt;

public class TitleScreen extends BasicGameState
{
	public static int netStatus = 0;
	public final int HOST = 1;
	public final int CLIENT = 2;
	
	private boolean joinPressed = false;
	
	public String screenName;
	
	private int playerNameEntered = 0;
	
	private ArrayList<Button> buttons = new ArrayList<Button>();
	
	private Image bgTitleScreen;
	
	private TrueTypeFont titleText1;
	private TrueTypeFont buttonText1;
	private TrueTypeFont ipText;
	private TrueTypeFont labelText1;
	
	private int HOST_BUTTON = 0;
	private int JOIN_BUTTON = 1;
	
	private TextField hostIP;
	private TextField playerName;
	
	ScreenPosition p = new ScreenPosition();
	
	//Create a server
	Server server = new Server();

	public TitleScreen(int id)
	{
		
	}
	
	
	private void initBackground(GameContainer gc)
	{
		try
		{
			//Load the background image(s) and resize based on the game's resolution
			bgTitleScreen = new Image("ui/bgTitleScreen.png");
			bgTitleScreen = bgTitleScreen.getScaledCopy(gc.getWidth(), gc.getHeight());
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}
	}
	
	private void initFonts (GameContainer gc)
	{
		FontMaster fm = new FontMaster();
		
		//These are used to keep the font sizes consistent at every resolution
		float sizeFloat;
		int fontSize; 
	
		float fontSizeMultiplier = gc.getWidth()/1920f;
		
		sizeFloat = 48f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;	
		titleText1 = fm.createFont("Arial", Font.PLAIN, fontSize);
		
		sizeFloat = 20f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		buttonText1 = fm.createFont("Arial", Font.BOLD, fontSize);
		
		sizeFloat = 18f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		ipText = fm.createFont("Arial", Font.PLAIN, fontSize);
		
		sizeFloat = 22f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		labelText1 = fm.createFont("Arial", Font.BOLD, fontSize);
	}
	
	private void initButtons(GameContainer gc)
	{
		//"Host Game" button
		Button b = new Button(gc,"buttons/yellowButtonOff.png",p.screenX(830,gc.getWidth()), p.screenY(510,gc.getHeight()), p.screenX(240,gc.getWidth()),p.screenY(90,gc.getHeight()), "HOST GAME");
		buttons.add(b);
		
		//"Join Game" button
		b = new Button(gc,"buttons/yellowButtonOff.png",p.screenX(830,gc.getWidth()), p.screenY(900,gc.getHeight()), p.screenX(240,gc.getWidth()),p.screenY(90,gc.getHeight()), "JOIN GAME");
		buttons.add(b);
	}
	
	private void drawTitle(GameContainer gc, Graphics g)
	{
		g.setFont(titleText1);
		g.setColor(Color.yellow);
		
		g.drawString("Game Title",p.screenX(824, gc.getWidth()), p.screenY(50, gc.getHeight()));
	}
	
	private void drawButtons(GameContainer gc, Graphics g)
	{
		g.setFont(buttonText1);
		g.setColor(Color.black);
		for (int i = 0; i < buttons.size();  i++)
		{
			renderButton(buttons.get(i), gc, g);
		}
	}
	
	private void renderButton(Button b, GameContainer gc, Graphics g)
	{
		
		b.render(gc, g);		
		g.drawString(b.getButtonText(),Txt.centerTextHorizontal(b, buttonText1), Txt.centerTextVertical(b, buttonText1)) ;
	}

	private void drawIP(GameContainer gc, Graphics g)
	{
		g.setFont(ipText);
		g.setColor(Color.white);
		
		try
		{
			g.drawString("Your PC's IP: " + InetAddress.getLocalHost().getHostAddress(), p.screenX(760,gc.getWidth()), p.screenY(460, gc.getHeight()));
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}
	
	public void initTextfields(GameContainer gc)
	{
		hostIP = new TextField(gc, ipText, p.screenX(802,gc.getWidth()), p.screenY(850, gc.getHeight()), p.screenX(300,gc.getWidth()), p.screenY(30, gc.getHeight()));
		hostIP.setBackgroundColor(Color.white);
		hostIP.setTextColor(Color.black);
		
		playerName = new TextField(gc, labelText1, p.screenX(802, gc.getWidth()), p.screenY(270,gc.getHeight()), p.screenX(300,gc.getWidth()), p.screenY(30,gc.getHeight()));
		playerName.setBackgroundColor(Color.white);
		playerName.setTextColor(Color.black);
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException
	{
		@SuppressWarnings("unused")
		Input i = gc.getInput();
		
		initBackground(gc);
		initFonts(gc);
		
		initButtons(gc);
		
		initTextfields(gc);
		
	}
	
	public void drawLabels(GameContainer gc, Graphics g)
	{
		g.setFont(labelText1);
		g.setColor(Color.yellow);
		
		g.drawString("Screen Name", p.screenX(878, gc.getWidth()), p.screenY(230, gc.getHeight()));
		g.drawString("Host IP", p.screenX(910, gc.getWidth()), p.screenY(810, gc.getHeight()));
	}
	
	public void startGameIfPossible(GameContainer gc, StateBasedGame game, Graphics g)
	{
		
		//If the player tried to start a game but forgot to enter a name...
		if (playerNameEntered == -1) 
		{
			g.setColor(Color.orange);
			g.drawString("You need to enter a screen name first!", p.screenX(760, gc.getWidth()), p.screenY(350, gc.getHeight()));
		}
		
		if (joinPressed == true)
		{
			g.setColor(Color.orange);
			g.drawString("There is no server at that address!", p.screenX(760, gc.getWidth()), p.screenY(410, gc.getHeight()));
		}
		
		
		//If the player has entered a valid name...
		if (playerNameEntered == 1)
		{
			screenName = playerName.getText();
			
			
			if (netStatus == HOST)
			{
				
				game.enterState(Game.mainGame, null, new FadeInTransition());
			}
			
			if (netStatus == CLIENT)
			{
				game.enterState(Game.mainGame, null, new FadeInTransition());
			}
		}

		

	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException
	{
		//Draw the background
		bgTitleScreen.draw();
		
		//Draw the title
		drawTitle(gc, g);
		
		//Draw buttons
		drawButtons(gc, g);
		
		//Draw your local IP
		drawIP(gc, g);
		
		//Draw the text fields
		
		hostIP.setBackgroundColor(Color.lightGray);
		
		if (hostIP.hasFocus())
			hostIP.setBackgroundColor(Color.white);
		
		playerName.setBackgroundColor(Color.lightGray);
		
		if (playerName.hasFocus())
			playerName.setBackgroundColor(Color.white);
		
		hostIP.render(gc, g);
		playerName.render(gc, g);
		
		//Draw Text field labels
		drawLabels(gc, g);
		
		//If all conditions for starting a game (as server or client) have been met, leave the title screen
		startGameIfPossible(gc, game, g);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException
	{
		
		if (playerName.getText() != "")
		{
			playerNameEntered = 1;
		}		
	}

	@Override
	public int getID()
	{

		return Game.titleScreen;
	}

	public void mouseClicked (int button, int x, int y, int clickCount)
	{
		//Host Button
		if (buttons.get(HOST_BUTTON).getMoa().isMouseOver())
		{
		
			//As long as the player has a name...
			if (playerName.getText() != "")
			{
				netStatus = HOST;
				Player player = new Player(playerName.getText(),netStatus, 0);
						
				new MPServer(player);		
			}
			
			else
			{
				playerNameEntered = -1;
			}

		}
		
		//Join Button
		if (buttons.get(JOIN_BUTTON).getMoa().isMouseOver())
		{
			if (hostIP.getText().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")  &&  (playerName.getText() !="") )
			{				
				Player player = new Player(playerName.getText(),netStatus, 0);
				
				new MPClient(player, hostIP.getText());
				
				joinPressed = true;
			}
			
			else
			{
				hostIP.setText("");
			}

		}

	}
}
