package gameScreens;

import java.util.ArrayList;
import java.awt.Font;

import main.Game;
import server.MPServer;
import server.Packet.*;
import uiObjects.Button;
import uiObjects.FontMaster;
import utility.ScreenPosition;
import utility.Txt;
import functionality.GameFunction;
import gameObjects.Card;
import gameObjects.Player;
import uiObjects.AreaHighlight;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import client.MPClient;
import client.NetworkListener;


public class MainGame extends BasicGameState
{
	//Constants=====================================
	private final int HOST = 1;
	private final int CLIENT = 2;
	
	public static final int SETTING_UP = 0;
	public static final int WAITING_TO_START = 1;
	public static final int STARTING = 2;
	public static final int IN_PROGRESS = 3;
	
	public static final int ROUND_WIN = 4;
	public static final int ROUND_LOSE = 5;
	public static final int WAITING_FOR_NEXT_ROUND = 6;
	public static final int DOWNLOADING_FRESH_HAND = 7;
	public static final int WINNER_DETERMINED = 8;
	
	public static int winner = 0;
	public static final int CHALLENGED = 1;
	public static final int CHALLENGER = 2;
	
	//Player objects================================
	Player player;
	//==============================================
	
	//Flow control==================================
	boolean firstRun = true;
	public static int status = SETTING_UP;
	public static int optionsOpen;
	public static int cardSelected = -1;
	public static boolean addingNewPlayer = false;
	public static int turn = -1;
	
	public boolean playPressedOutOfTurn = false;
	
	public static int challengedPlayer = -1;
	public static int challenger = -1;
	
	public static boolean voted = false;
	
	public static int roundNumber = 1;
	
	public static boolean answerSubmitted = false;
	
	public static int gameWinner = -1;
	//==============================================
	
	//Networking message control====================
	boolean sendNewOptionsToClients = false;
	//==============================================
	
	
	//Lists============================================================================
	private ArrayList<Button> buttons = new ArrayList<Button>(); //buttons
	public static ArrayList<Player> players = new ArrayList<Player>(); // player name, server/client status and current score
	private int playerID;
	
	private ArrayList<AreaHighlight> clickAreas = new ArrayList<AreaHighlight>(); // Regions that can be clicked to highlight/select cards
	
	
	//=================================================================================

	//VOTING=========================================
	Button yesButton;
	Button noButton;
	public static int vote = -1;
	public static final int YES = 1;
	public static final int NO = 2;
	//===============================================
	
	private Button nextRoundButton;
	
	
	//LARGE OBJECTS==================================
	WaitingPopup popup;
	public static Options options;
	public static ChallengeScreen challengeScreen;
	//===============================================
	
	
	
	//FONTS==========================================
	private TrueTypeFont heading1;
	
	private TrueTypeFont buttonText1;
	
	private TrueTypeFont playerNameTurn;
	private TrueTypeFont playerNameNotTurn;
	
	private TrueTypeFont points1;
	private TrueTypeFont numCards1;
	
	private TrueTypeFont myCards1;
	
	private TrueTypeFont options1;
	
	private TrueTypeFont  challengeFont;
	private TrueTypeFont challengeFont2;
	private TrueTypeFont challengeSmall;
	
	//===============================================
	
	
	//COLORS=========================================
	Color LIGHTORANGE;
	Color LIGHTBLUE;
	//===============================================
	
	
	//BUTTON CONSTANTS===============================
	private final int LEAVE_GAME_BUTTON = 0;
	private final int AWAY_BUTTON = 1;
	private final int OPTIONS_BUTTON = 2;
	private final int PLAY_BUTTON = 3;
	//===============================================
	
	
	//Functionality / Utility========================
	private ScreenPosition pos = new ScreenPosition();
	//===============================================
	
	
	
	
	//Images=========================================
	Image bg;
	Image bgOptions;
	//===============================================
	
	
	//Input adapters=================================
	private  Input input;
	//===============================================
	
	Rectangle highlight;
	
	public MainGame(int id)
	{
		
	}
	
	private void initFonts(GameContainer gc)
	{
		FontMaster fm = new FontMaster();
		
		//These are used to keep the font sizes consistent at every resolution
		float sizeFloat;
		int fontSize; 
	
		float fontSizeMultiplier = gc.getWidth()/1920f;
		
		sizeFloat = 36f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;	
		heading1 = fm.createFont("Arial", Font.PLAIN, fontSize);
		
		sizeFloat = 20f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		buttonText1 = fm.createFont("Arial", Font.BOLD, fontSize);
		
		sizeFloat = 18f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		playerNameTurn = fm.createFont("Arial", Font.BOLD, fontSize);
		
		sizeFloat = 18f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		playerNameNotTurn = fm.createFont("Arial", Font.PLAIN, fontSize);
		
		sizeFloat = 18f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		points1 = fm.createFont("Arial", Font.PLAIN, fontSize);
		
		sizeFloat = 16f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		numCards1 = fm.createFont("Arial", Font.PLAIN, fontSize);
		
		sizeFloat = 18f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		myCards1 = fm.createFont("Arial", Font.PLAIN, fontSize);
		
		sizeFloat = 18f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		options1 = fm.createFont("Arial", Font.BOLD, fontSize);
		
		sizeFloat = 20f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		challengeFont = fm.createFont("Arial", Font.BOLD, fontSize);
		
		sizeFloat = 18f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		challengeFont2 = fm.createFont("Arial", Font.BOLD, fontSize);
		
		sizeFloat = 16f * fontSizeMultiplier;
		fontSize = (int)sizeFloat;
		challengeSmall = fm.createFont("Arial", Font.BOLD, fontSize);
		
		
		LIGHTORANGE = new Color(238, 222, 160);
		LIGHTBLUE = new Color(135, 233, 243);
		
	}
	
	private void initBackground(GameContainer gc)
	{
		try
		{
			//Load the background image(s) and resize based on the game's resolution
			bg = new Image("ui/bgFullGame.png");
			bg = bg.getScaledCopy(gc.getWidth(), gc.getHeight());
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}
	}
	

	
	private void createChallengeButtons(GameContainer gc)
	{
		for (int i = 0; i < GameFunction.numPlayers; i++)
		{
			Button b = new Button(gc,"buttons/yellowButtonOff.png",pos.screenX(40,gc.getWidth()), pos.screenY(48 + (92*i),gc.getHeight()), pos.screenX(140,gc.getWidth()),pos.screenY(42,gc.getHeight()), "CHALLENGE!");
			buttons.add(b);
		}
	}
	
	private void createFunctionButtons(GameContainer gc)
	{
		//"Leave Game" button
		Button b = new Button(gc,"buttons/yellowButtonOff.png",pos.screenX(20,gc.getWidth()), pos.screenY(940,gc.getHeight()), pos.screenX(140,gc.getWidth()),pos.screenY(42,gc.getHeight()), "LEAVE GAME");
		buttons.add(b);
		
		//"Away" button
		b = new Button(gc,"buttons/yellowButtonOff.png",pos.screenX(210,gc.getWidth()), pos.screenY(940,gc.getHeight()), pos.screenX(140,gc.getWidth()),pos.screenY(42,gc.getHeight()), "AWAY");
		buttons.add(b);
		
		//"Options" button
		b = new Button(gc,"buttons/yellowButtonOff.png",pos.screenX(20,gc.getWidth()), pos.screenY(1020,gc.getHeight()), pos.screenX(140,gc.getWidth()),pos.screenY(42,gc.getHeight()), "OPTIONS");
		buttons.add(b);
		
		//"Play" button
		b = new Button(gc,"buttons/yellowButtonOff.png",pos.screenX(1550,gc.getWidth()), pos.screenY(640,gc.getHeight()), pos.screenX(140,gc.getWidth()),pos.screenY(42,gc.getHeight()), "PLAY");
		buttons.add(b);
	}
	
	private void createVoteButtons(GameContainer gc)
	{
		yesButton = new Button(gc, "buttons/yellowButtonOff.png", pos.screenX(1380,gc.getWidth()), pos.screenY(340,gc.getHeight()), pos.screenX(180,gc.getWidth()), pos.screenY(60,gc.getHeight()), "YES!");	
		noButton = new Button(gc, "buttons/yellowButtonOff.png", pos.screenX(1680,gc.getWidth()), pos.screenY(340,gc.getHeight()), pos.screenX(180,gc.getWidth()), pos.screenY(60,gc.getHeight()), "NO WAY!");
	}
	
	private void initButtons(GameContainer gc)
	{

		if (firstRun == true)
			createFunctionButtons(gc);	
		
		createChallengeButtons(gc);
		
		createVoteButtons(gc);
	}


	
	private void renderButtons(GameContainer gc, Graphics g)
	{
		
		renderButton(buttons.get(LEAVE_GAME_BUTTON), gc, g);
		renderButton(buttons.get(AWAY_BUTTON), gc, g);
		renderButton(buttons.get(OPTIONS_BUTTON),gc, g);
		renderButton(buttons.get(PLAY_BUTTON),gc, g);
		
		determinePlayerID();
		
		if (addingNewPlayer == true)
		{
			Button b = new Button(gc,"buttons/yellowButtonOff.png",pos.screenX(40,gc.getWidth()), pos.screenY(48 + (92*(GameFunction.numPlayers-1)),gc.getHeight()), pos.screenX(140,gc.getWidth()),pos.screenY(42,gc.getHeight()), "CHALLENGE!");
			buttons.add(b);
			
			addingNewPlayer = false;
		}
		//CHALLENGE buttons
		for (int i = 4; i < GameFunction.numPlayers+4; i++)
		{
			if ( (i-4 != playerID) )
				renderButton(buttons.get(i), gc, g);
		}

	
		
	}
	
	private void renderButton(Button b, GameContainer gc, Graphics g)
	{
		g.setFont(buttonText1);
		g.setColor(Color.black);
		
		b.render(gc, g);		
		g.drawString(b.getButtonText(),Txt.centerTextHorizontal(b, buttonText1), Txt.centerTextVertical(b, buttonText1)) ;
	}

	
	private void drawPlayerNames(GameContainer gc, Graphics g)
	{

		for (int i = 0; i < GameFunction.numPlayers; i++)
		{
			if (i == turn)
			{
				g.setFont(playerNameTurn);
				g.setColor(Color.green);
			}
			
			else 
			{
				g.setFont(playerNameNotTurn);
				g.setColor(Color.white);
			}
			
			g.drawString(players.get(i).getName(),  pos.screenX(40,gc.getWidth()),  pos.screenY(20 + (92*i),gc.getHeight()));
		}
	}
	
	private void drawPlayerPoints(GameContainer gc, Graphics g)
	{
		g.setFont(points1);
		g.setColor(Color.yellow);
		
		for (int i = 0; i < GameFunction.numPlayers; i++)
		{
			String pointsText = "";
			
			if (players.get(i).getPoints() != 1 && players.get(i).getPoints() != -1)
				pointsText = " Points";		
			
			else
				pointsText = " Point";
			
			
			g.drawString( (players.get(i).getPoints() + pointsText), pos.screenX(280, gc.getWidth()), pos.screenY(20 + (92*i), gc.getHeight()));
		}
	}

	
	private void drawPlayerCardQuantities(GameContainer gc, Graphics g)
	{
		g.setFont(numCards1);
		g.setColor(Color.white);
		
		for (int i = 0; i < GameFunction.numPlayers; i++)
		{
			String numCardsText;
			
			numCardsText = Integer.toString(GameFunction.playerHands.get(i).size());
			numCardsText = numCardsText + " Cards";
			
			g.drawString( numCardsText, pos.screenX(280, gc.getWidth()), pos.screenY(50 + (92*i), gc.getHeight()));
		}
	}
	
	
	private void drawHeadings(GameContainer gc, Graphics g)
	{
		g.setFont(heading1);
		g.setColor(Color.green);
		
		g.drawString("Cards In Play", pos.screenX(720, gc.getWidth()), pos.screenY(12, gc.getHeight()));		
		g.drawString("My Cards", pos.screenX(1540, gc.getWidth()), pos.screenY(12, gc.getHeight()));
	}
	
	private void initClickableCardAreas(GameContainer gc)
	{
		for (int i = 0; i < GameFunction.maxCardsHeld; i++)
		{
			AreaHighlight area = new AreaHighlight(pos.screenX(1328, gc.getWidth()),  pos.screenY(70 + (22*i),gc.getHeight()), pos.screenX(570, gc.getWidth()), pos.screenY(22, gc.getHeight()));
			clickAreas.add(area);
		}
		
	}
	
	private void initOptions(GameContainer gc)
	{
		options = new Options(gc);

	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame game) throws SlickException
	{		
		input = gc.getInput();
		gc.setShowFPS(false);

		initClickableCardAreas(gc);		
		initFonts(gc);		
		initBackground(gc);
		
		popup = new WaitingPopup(gc);
	}

	private void determinePlayerID()
	{
		if (TitleScreen.netStatus == CLIENT)
		{
			String playerName = MPClient.player.getName();
	
			for (int i = 0; i < players.size(); i++)
			{
				if ( players.get(i).getName().equalsIgnoreCase(playerName) )
				{
					playerID = i;
				}
			}
		}
		
		else
		{
			playerID = 0;
		}
	}

	private void drawPlayerCards(GameContainer gc, Graphics g)
	{
		g.setFont(myCards1);
		g.setColor(LIGHTORANGE);	
		
		determinePlayerID();
		
		
		//Draw as many lines of text as the player has cards in their hand.
		for (int i = 0; i < GameFunction.playerHands.get(playerID).size(); i++)
		{
			g.drawString(GameFunction.playerHands.get(playerID).get(i).getCardText(), pos.screenX(1330, gc.getWidth()), pos.screenY(70 + (22*i), gc.getHeight()));		
		}
	}
	
	
	private void drawHighlights(GameContainer gc, Graphics g)
	{
		g.setColor(Color.green);
		
		highlight = new Rectangle(clickAreas.get(cardSelected).getX(),clickAreas.get(cardSelected).getY(), clickAreas.get(cardSelected).getWidth(),clickAreas.get(cardSelected).getHeight());
		g.draw(highlight);
	}
	
	private void drawCardsOnTable(GameContainer gc, Graphics g)
	{
		g.setFont(myCards1);
		g.setColor(LIGHTBLUE);	
		
		determinePlayerID();
		
		//Draw as many lines of text as the player has cards in their hand.
		for (int i = 0; i < GameFunction.table.size(); i++)
		{
			g.drawString(GameFunction.table.get(i).getCardText(), pos.screenX(390, gc.getWidth()), pos.screenY(70 + (22*i), gc.getHeight()));			
		}
	}
	
	private void drawOptionsButtons(GameContainer gc, Graphics g)
	{
		renderButton(options.getOptionsCancel(), gc, g);
		renderButton(options.getOptionsDone(), gc, g);
	}
	
	private void drawOptions(GameContainer gc, Graphics g)
	{
		g.setColor(Color.white);
		g.setFont(options1);
		
		//Background
		options.getBgOptions().draw(pos.screenX(446, gc.getWidth()), pos.screenY(80, gc.getHeight()));
		
		//Text
		g.drawString("Standard",pos.screenX(642, gc.getWidth()), pos.screenY(290, gc.getHeight()));
		g.drawString("Mature",pos.screenX(662, gc.getWidth()), pos.screenY(350, gc.getHeight()));
		g.drawString("Gaming",pos.screenX(654, gc.getWidth()), pos.screenY(410, gc.getHeight()));
		g.drawString("Geek",pos.screenX(668, gc.getWidth()), pos.screenY(470, gc.getHeight()));
		
		//Deck checkboxes
		for (int i = 0; i < 4; i++) //TODO change the 4 to "number of decks".
		{
			options.getCheckBoxes().get(i).draw(pos.screenX(730, gc.getWidth()), pos.screenY(286 + (60 * i), gc.getHeight()));
			
			if (options.getToggles().get(i) == true)
			{
				options.getCheckmarks().get(i).draw(pos.screenX(730, gc.getWidth()), pos.screenY(286 + (60 * i), gc.getHeight()));
			}
		}
		
		//Number of players slider
		options.getCheckBoxes().get(4).draw(pos.screenX(808+ (45*(options.getMaxPlayers()-2)), gc.getWidth()), pos.screenY(577, gc.getHeight()));
		
		
		//Negative Bonus checkbox
		options.getCheckBoxes().get(5).draw(pos.screenX(1120, gc.getWidth()), pos.screenY(728, gc.getHeight()));
		
		if (options.getToggles().get(4) == true)
		{
			options.getCheckmarks().get(4).draw(pos.screenX(1120, gc.getWidth()), pos.screenY(728, gc.getHeight()));		
		}
		
		
		//Textboxes
		options.getPointsRequired().render(gc, g);
		options.getTimeLimit().render(gc, g);
		options.getPassword().render(gc, g);
		
		//CANCEL and DONE buttons
		drawOptionsButtons(gc, g);
		
	}
	
	private void renderChallengeScreen(GameContainer gc, Graphics g)
	{
		challengeScreen.getBg().draw();
		
		if (answerSubmitted == true)
		{
			g.setColor(Color.white);
			
			g.setFont(challengeFont);
			g.setColor(Color.orange);
			
			g.drawString(players.get(challengedPlayer).getName() + "'s idea:", pos.screenX(1310, gc.getWidth()), pos.screenY(480, gc.getHeight()));
			
			g.setFont(challengeSmall);
			g.drawString(ChallengeScreen.submittedAnswer, pos.screenX(1310, gc.getWidth()), pos.screenY(520, gc.getHeight()));
			
			g.setFont(challengeFont);
			
			g.drawString("Current votes:", pos.screenX(1340, gc.getWidth()), pos.screenY(600, gc.getHeight()));
			
			g.drawString("YES", pos.screenX(1440, gc.getWidth()), pos.screenY(640, gc.getHeight()));
			
			g.drawString("NO", pos.screenX(1700, gc.getWidth()), pos.screenY(640, gc.getHeight()));
			
			calculateVotes(gc, g);
		}
		
		challengeScreen.getAnswer().setBackgroundColor(Color.lightGray);
		challengeScreen.getAnswer().setTextColor(Color.black);		
		g.setColor(Color.white);
		
		if (challengeScreen.getAnswer().hasFocus())
		{
			challengeScreen.getAnswer().setBackgroundColor(Color.white);
		}
		
		
		
		if ( answerSubmitted == false)
		{

			
			challengeScreen.getAnswer().render(gc, g);
			renderButton(challengeScreen.getSubmitButton(), gc, g);
			
			g.setFont(challengeFont);
			g.setColor(Color.orange);
			
			g.drawString("Tell everyone what you're thinking of!", pos.screenX(1360, gc.getWidth()), pos.screenY(480, gc.getHeight()));
			g.drawString("If they think it works, you win!", pos.screenX(1360, gc.getWidth()), pos.screenY(520, gc.getHeight()));
			g.drawString("If not, you lose!", pos.screenX(1360, gc.getWidth()), pos.screenY(560, gc.getHeight()));
			
		}
		
		
		g.setFont(challengeFont);
		g.setColor(Color.orange);
		
		g.drawString("You have been challenged by:", pos.screenX(1360, gc.getWidth()), pos.screenY(20, gc.getHeight()));
		g.drawString(players.get(challenger).getName(), pos.screenX(1360, gc.getWidth()), pos.screenY(50, gc.getHeight()));
		

	}
	
	
	private void calculateVotes(GameContainer gc, Graphics g)
	{
		int yesVotes = 0;
		int noVotes = 0;
		
		if (TitleScreen.netStatus == CLIENT)
		{
			for (int i = 0; i < 10; i++)
			{
				if  ( MPClient.votesReceived[i] == YES)
					yesVotes++;
				
				if (MPClient.votesReceived[i] == NO)
					noVotes++;
			}

		}
		
		if (TitleScreen.netStatus == HOST)
		{
			for (int i = 0; i < 10; i++)
			{
				if  ( MPServer.votesReceived[i] == YES)
					yesVotes++;
				
				if (MPServer.votesReceived[i] == NO)
					noVotes++;
			}

		}
		
		
			g.drawString(Integer.toString(yesVotes), pos.screenX(1440, gc.getWidth()), pos.screenY(660, gc.getHeight()));
			g.drawString(Integer.toString(noVotes), pos.screenX(1700, gc.getWidth()), pos.screenY(660, gc.getHeight()));
	}
	
	
	private void renderAnotherPlayerChallenged(GameContainer gc, Graphics g)
	{
		challengeScreen.getBg().draw();	
		g.setColor(Color.white);
		
		g.setFont(challengeFont);
		g.setColor(Color.orange);
		
		g.drawString(players.get(challengedPlayer).getName() + " has been challenged by:", pos.screenX(1340, gc.getWidth()), pos.screenY(20, gc.getHeight()));
		g.drawString(players.get(challenger).getName(), pos.screenX(1340, gc.getWidth()), pos.screenY(50, gc.getHeight()));
		
		g.drawString(players.get(challengedPlayer).getName() + "'s idea:", pos.screenX(1310, gc.getWidth()), pos.screenY(480, gc.getHeight()));
		
		g.setFont(challengeSmall);
		g.drawString(ChallengeScreen.submittedAnswer, pos.screenX(1310, gc.getWidth()), pos.screenY(520, gc.getHeight()));
		
		g.setFont(challengeFont);
		
		g.drawString("Current votes:", pos.screenX(1340, gc.getWidth()), pos.screenY(600, gc.getHeight()));
		
		g.drawString("YES", pos.screenX(1440, gc.getWidth()), pos.screenY(640, gc.getHeight()));
		
		g.drawString("NO", pos.screenX(1700, gc.getWidth()), pos.screenY(640, gc.getHeight()));
		
		calculateVotes(gc, g);
		
		
		//Draw the vote buttons once wwe've received a response:
		if ( (! ChallengeScreen.submittedAnswer.equals("")) && (voted == false) )
		{	
			g.drawString("Everyone vote! Good answer or not?", pos.screenX(1410, gc.getWidth()), pos.screenY(260, gc.getHeight()) );
			renderButton(yesButton, gc, g);	
			renderButton(noButton, gc, g);
			
			vote = 0;
		}
	}
	
	
	private void handleChallenges(GameContainer gc, StateBasedGame game, Graphics g)
	{
		determinePlayerID();
		
		
		//If I'm the one being challenged...=============================================================================

		
		if ( (challengedPlayer == playerID))
		{		
			renderChallengeScreen(gc, g);		
		}
				
		
		//===============================================================================================================
		
		//If someone else is being challenged============================================================================
	
		
		else if ( (challengedPlayer != playerID) )
		{
			renderAnotherPlayerChallenged(gc, g);
		}
		
		//===============================================================================================================
		
		
		
	}
	
	private void  buildScoreUpdatePacket()
	{
		ScoreUpdate u = new ScoreUpdate();
		u.scores = new int[GameFunction.numPlayers];
		
		for (int i = 0; i < GameFunction.numPlayers; i++)
		{
			u.scores[i] = players.get(i).getPoints();
		}
		
		MPServer.server.sendToAllTCP(u);
		
	}
	
	private void hostUpdatesScore ()
	{
		if (status == ROUND_WIN)
		{
			players.get(challengedPlayer).setPoints( players.get(challengedPlayer).getPoints() +2 );
			players.get(challenger).setPoints( players.get(challenger).getPoints() -1 );
		}
		
		if (status == ROUND_LOSE)
		{
			players.get(challenger).setPoints( players.get(challenger).getPoints() +2 );
			players.get(challengedPlayer).setPoints( players.get(challengedPlayer).getPoints() -1 );
		}
	
		buildScoreUpdatePacket();
		
	}
	
	private void drawRoundOver(GameContainer gc, Graphics g)
	{
		challengeScreen.getBg().draw();	
		
		g.setColor(Color.white);
		
		g.setFont(challengeFont);
		g.setColor(Color.orange);
		
		//Only update the score and send it across the network ONCE!
		if ( (TitleScreen.netStatus == HOST) && ( (status == ROUND_WIN) || (status == ROUND_LOSE) ) )
			hostUpdatesScore();
		
		
		if ((status == ROUND_WIN) || (winner == CHALLENGED) )
		{
			if ((players.get(challengedPlayer) != null) &&  (players.get(challenger) != null) )
			{
				g.drawString(players.get(challengedPlayer).getName() + " WINS THE ROUND!", pos.screenX(1340, gc.getWidth()), pos.screenY(20, gc.getHeight()));
				g.drawString(players.get(challenger).getName() + " WILL BE PENALIZED.", pos.screenX(1340, gc.getWidth()), pos.screenY(60, gc.getHeight()));
				winner = CHALLENGED;
				
				if (status != WINNER_DETERMINED)
					status = WAITING_FOR_NEXT_ROUND;
			}
		}
		
		else if ((status == ROUND_LOSE) || (winner == CHALLENGER) )
		{
			
			if ((players.get(challengedPlayer) != null) &&  (players.get(challenger) != null) )
			{
				g.drawString(players.get(challengedPlayer).getName() + " DIDN'T SURVIVE JUDGEMENT!", pos.screenX(1340, gc.getWidth()), pos.screenY(20, gc.getHeight()));
				g.drawString(players.get(challenger).getName() + " WINS THE ROUND!", pos.screenX(1340, gc.getWidth()), pos.screenY(60, gc.getHeight()));
				winner = CHALLENGER;
						
				if (status != WINNER_DETERMINED)
					status = WAITING_FOR_NEXT_ROUND;

			}
		}	

		
		if ( (status == WAITING_FOR_NEXT_ROUND) || (status == WINNER_DETERMINED) )
		{
			
			String buttonName = "";
			if (status == WAITING_FOR_NEXT_ROUND)
			{
				g.drawString("Next round will begin in a moment.", pos.screenX(1340, gc.getWidth()), pos.screenY(360, gc.getHeight()));
				buttonName = "NEXT ROUND!";
			}
			
			
			
			if (status == WINNER_DETERMINED)
			{
				g.drawString("GAME OVER!", pos.screenX(1340, gc.getWidth()), pos.screenY(320, gc.getHeight()));
				g.drawString(players.get(gameWinner).getName() + " wins the game!", pos.screenX(1340, gc.getWidth()), pos.screenY(360, gc.getHeight()));
				buttonName = "NEW GAME";
			}
			
			
			//make the "next round" button for the host
			if ( (nextRoundButton == null) && (TitleScreen.netStatus == HOST) )
			{
				nextRoundButton = new Button(gc, "buttons/yellowButtonOff.png", pos.screenX(1550,gc.getWidth()), pos.screenY(460,gc.getHeight()), pos.screenX(180,gc.getWidth()), pos.screenY(60,gc.getHeight()), buttonName);
			}
			
			if (TitleScreen.netStatus == HOST)
				renderButton(nextRoundButton, gc, g);
		
		}
		
	}
	
	
	
	private void buildGameOverPacket(int pID)
	{
		GameOver gameOver = new GameOver();
		
		gameOver.winnerID = pID;
		status = WINNER_DETERMINED;
		MPServer.server.sendToAllTCP(gameOver);
	}
	
	private void checkIfGameOver(GameContainer gc, Graphics g)
	{
		if ( (TitleScreen.netStatus == HOST) && (gameWinner == -1) )
		{
			//Loop through all players' points and check it against the victory score		
			for (int i = 0; i < GameFunction.numPlayers; i++)
			{
				if (options.getPointsRequired().getText().equals(""))
					options.getPointsRequired().setText("0");
					
				if (players.get(i).getPoints() >= Integer.parseInt(options.getPointsRequired().getText()) && (Integer.parseInt(options.getPointsRequired().getText()) > 0 ))
				{
					gameWinner = i;
					
					buildGameOverPacket(i);
				}
			}
		}
	}
	
	//===============================================================================================================================
	
	
	
	
	
	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException
	{				
		try
		{
			
			//Draw the background
			bg.draw();
			
/*			g.setColor(Color.white);
			g.setFont(options1);
			
			g.drawString("# Hands: " + GameFunction.playerHands.size(), 800, 400);
			g.drawString("Players size: " + players.size(), 800, 440);
			g.drawString("numPlayers: " + players.size(), 800, 480);*/
	
			if (status != DOWNLOADING_FRESH_HAND)
			{
			
				if (firstRun == true)
				{
					//Wait until we've downloaded the playerlist before doing stuff
					if ( (TitleScreen.netStatus == HOST) || ( (TitleScreen.netStatus == CLIENT) && (NetworkListener.downloadedPlayerList.size() != 0) ) )		
					{
						
						
						if (TitleScreen.netStatus == CLIENT)
						{
							players.clear();
							//Fill the player arraylist from the temp one we just downloaded
							for (int i = 0; i < NetworkListener.downloadedPlayerList.size(); i++)
							{						
								players.add(NetworkListener.downloadedPlayerList.get(i));
							}	
							
							GameFunction.numPlayers = players.size();
							
							
						}
						
						if (challengeScreen == null)
							challengeScreen = new ChallengeScreen(gc, challengeFont, challengeFont2);
						
						initButtons(gc);	
						initOptions(gc);
			
						//Figure out which player # you are before continuing on
									
						//Figure out how to display the options menu to the user, depending on whether they're a host or client
						optionsOpen = TitleScreen.netStatus;
						
						NetworkListener.newPlayerConnected = false;
						firstRun = false;
					}
		
				}
				
				//Check to see if a new player has connected
				
				if ( (TitleScreen.netStatus == CLIENT) && (NetworkListener.newPlayerConnected == true) )
				{
					players.clear();
					//Fill the player arraylist from the temp one we just downloaded
					for (int i = 0; i < NetworkListener.downloadedPlayerList.size(); i++)
					{						
						players.add(NetworkListener.downloadedPlayerList.get(i));
					}	
					
					Button b = new Button(gc,"buttons/yellowButtonOff.png",pos.screenX(40,gc.getWidth()), pos.screenY(48 + (92*(GameFunction.numPlayers-1)),gc.getHeight()), pos.screenX(140,gc.getWidth()),pos.screenY(42,gc.getHeight()), "CHALLENGE!");
					buttons.add(b);
					
				//	GameFunction.playerHands.add(new ArrayList<Card>());
					
					NetworkListener.newPlayerConnected = false;
					
				}
				
				//Only start drawing the game once we have loaded everything! Prevents dem crashes. :P
				if ( (firstRun == false) && (NetworkListener.newPlayerConnected == false) )
				{
					
		
					//Buttons
					g.setFont(buttonText1);
					g.setColor(Color.black);
					renderButtons(gc, g);
				
					//Headings
					drawHeadings(gc, g);
					
					//Player Information		
					drawPlayerNames(gc, g);	
			
					if (status == WAITING_TO_START)
					{
						popup.draw(gc, g, buttonText1);
					}
					
					
					
					if (status == STARTING)
					{
						//If this player is a host...
						if (TitleScreen.netStatus == HOST)
						{
							if (roundNumber == 1)
							{
								//Create the cards
								GameFunction.createCards(options);
								
								//Deal the cards
								GameFunction.initialDeal();
							}
							
							else if (roundNumber > 1)
							{
								GameFunction.subsequentDeal();
							}
							
							status = IN_PROGRESS;
						}
					}
					
					
					
					
					if (status == IN_PROGRESS)
					{
						drawPlayerPoints(gc, g);
						drawPlayerCardQuantities(gc, g);
						
						//The player's cards
						drawPlayerCards(gc, g);
						
						//Highlighted cards
						if (cardSelected != -1)
						{
							drawHighlights(gc, g);
						}
						
						//Cards on the table
						drawCardsOnTable(gc, g);
						
						if (playPressedOutOfTurn == true)
						{
							g.setFont(buttonText1);
							g.setColor(Color.orange);
							
							g.drawString("It's " + players.get(turn).getName() + "'s turn", pos.screenX(1400, gc.getWidth()), pos.screenY(760, gc.getHeight()));
						}
						
						//If someone has been challenged, do stuff
						if ( (challengedPlayer != -1) && (challenger != -1) )
							handleChallenges(gc, game, g);
					}
					
					
					
					//Once  the challenge phase is over...
					if (status > IN_PROGRESS)
					{
						drawPlayerPoints(gc, g);
						drawPlayerCardQuantities(gc, g);
						
						//Cards on the table
						drawCardsOnTable(gc, g);
						
						drawRoundOver(gc, g);
					}
					
					
						
					if  (optionsOpen != 0)
					{
						drawOptions(gc, g);
					}
						

/*					g.setColor(Color.white);
					g.setFont(options1);
					
					g.drawString("# Hands: " + GameFunction.playerHands.size(), 800, 700);
					g.drawString("Players size: " + players.size(), 800, 740);
					g.drawString("numPlayers: " + players.size(), 800, 780);*/
				}
			}
			
			else
			{
				challengeScreen.getAnswer().setText("");
			}
			
			checkIfGameOver(gc, g);
			
		}
		
		catch(NullPointerException npe)
		{
			
		}
		
		catch(ArrayIndexOutOfBoundsException oobe)
		{
			
		}
	}

	//===============================================================================================================================
	
	
	
	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException
	{
	}

	
	
	
	@Override
	public int getID()
	{
		return Game.mainGame;
	}
	
	
	
	
	
//TEXT BOX EVENTS=====================================================================================================
//====================================================================================================================
	
	
	
	
	public void keyReleased(int key, char c)
	{
		//If any of the text boxes have focus, do something.
		if ( options.getTimeLimit().hasFocus() || options.getPointsRequired().hasFocus() )
		{
			sendNewOptionsToClients = true;
		}
	}
	
	
	
	
	
//FUNCTIONALITY FOR BUTTON EVENTS======================================================================================
//=====================================================================================================================
	

	
	private void buildTurnOverPacket()
	{
/*		public Card[] tableCards;
		public Card[] clientCards;
		public int playerTurn;*/
		
		TurnOverPacketToServer t = new TurnOverPacketToServer();
		
		t.tableCards = new Card[250];
		t.clientCards = new Card[GameFunction.maxCardsHeld];
		
		for (int i = 0; i < GameFunction.table.size(); i++)
		{
			t.tableCards[i] = GameFunction.table.get(i);
		}
		
		for (int i = 0; i < GameFunction.playerHands.get(playerID).size(); i++)
		{
			t.clientCards[i] = GameFunction.playerHands.get(playerID).get(i);
		}
		
		t.playerTurn = turn;
		
		MPClient.client.sendTCP(t);
	}
	
	private void  buildServerTurnOverPacket()
	{
/*		public Card[] tableCards;
		public Card[] clientCards;
		public int playerTurn;*/
		
		ServerTurnOver over = new ServerTurnOver();
		
		over.tableCards = new Card[250];
		
		for (int i = 0; i < GameFunction.table.size(); i++)
		{
			over.tableCards[i] = GameFunction.table.get(i);
		}
		
		over.serverCards = new Card[GameFunction.maxCardsHeld];
		
		for (int i = 0; i < GameFunction.playerHands.get(0).size(); i++)
		{
			over.serverCards[i] = GameFunction.playerHands.get(0).get(i);
		}
		
		turn++;
		over.playerTurn = turn;
		
		MPServer.server.sendToAllTCP(over);
	}
	
	
	private void buildHostChallengingClientPacket(int i)
	{
		HostChallengingClient challenge = new HostChallengingClient();
		
		challenge.playerChallenged = i;
		
		challenger = 0;
		challengedPlayer = i;
		
		MPServer.server.sendToAllTCP(challenge);
	}
	
	private void buildClientChallengingSomeonePacket(int i)
	{
		ClientChallengingSomeone ch = new ClientChallengingSomeone();
		
		ch.playerChallenged = i;
		ch.playerChallenging = playerID;
		
		MPClient.client.sendTCP(ch);
	}
	
	
	private void buildChallengeResponsePacketToServer()
	{
		ChallengeResponseToServer  response = new ChallengeResponseToServer();
		
		if ((challengeScreen.getAnswer().getText() == null) || (challengeScreen.getAnswer().getText().equals(""))  )
			challengeScreen.getAnswer().setText("I have no idea =(");
		
		response.answer = challengeScreen.getAnswer().getText();
		
		MPClient.client.sendTCP(response);
	}
	
	
	private void buildVoteSubmissionToServer()
	{
		VoteSubmissionToServer submission = new VoteSubmissionToServer();
		
		determinePlayerID();
		submission.playerFrom = playerID;
		
		submission.vote = vote;
		
		MPClient.client.sendTCP(submission);
	}
	
	private void buildChallengeResponsePacketFromServer()
	{
		ChallengeScreen.submittedAnswer = challengeScreen.getAnswer().getText();
		
		if (ChallengeScreen.submittedAnswer.equalsIgnoreCase(""))
		{
			ChallengeScreen.submittedAnswer = "I have no idea =(";
		}
		
		ChallengeResponseFromServer response = new ChallengeResponseFromServer();
		
		response.response = ChallengeScreen.submittedAnswer;

		MPServer.server.sendToAllTCP(response);
	}
	
	
	
	private void resetGameForNextRound() //TODO
	{
		
		if (status == WINNER_DETERMINED)
		{
			
			//Delete all the arraylists so we can rebuild them with the round initialization methods		
			GameFunction.playerHands.clear();
			GameFunction.table.clear();
			GameFunction.deck.clear();
			
			ClearPoints cl = new ClearPoints();
			
			MPServer.server.sendToAllTCP(cl);
		}
		
		
		status = STARTING;
		cardSelected = -1;
		addingNewPlayer = false;
		turn = -1;
		playPressedOutOfTurn = false;
		challengedPlayer = -1;
		challenger = -1;
		voted = false;
		sendNewOptionsToClients = false;
		vote = -1;
		winner = 0;
		ChallengeScreen.submittedAnswer = "";
		answerSubmitted = false;
		
		MPServer.votesReceived = new int[10];
		MPServer.numVotesReceived = 0;	
		
		challengeScreen.getAnswer().setText("");
		
		
		//Put the cards from the table back in the deck.
		while (! GameFunction.table.isEmpty())
		{
			GameFunction.deck.add(  GameFunction.table.get(0)  );
			GameFunction.table.remove(0);
		}
		
		
		GameFunction.table.clear();
		
		NewRound n = new NewRound();
		
		n.nextRound = true;
		
		MPServer.server.sendToAllTCP(n);
	}

//==================================================================================================================================
//==================================================================================================================================
	
	public void mouseClicked (int button, int x, int y, int clickCount)
	{
		
		try
		{
		
			
	//CHALLENGE buttons=================================================================================================================
			
			//Only allow these buttons to work  if the game isn't in challenge mode already!
			if ((challenger == -1) && (challengedPlayer == -1) && (status == IN_PROGRESS) )
			{
				determinePlayerID();
				
				for (int i = 4; i < GameFunction.numPlayers+4; i++)
				{
					if (buttons.get(i).getMoa().isMouseOver())
					{
						//As long as the player isn't trying to challenge him/herself, do something.
						if (i - 4 != playerID)
						{
							//If the host is challenging someone...
							if (TitleScreen.netStatus == HOST)
							{
								buildHostChallengingClientPacket(i-4);
							}
							
							
							//If a client is challenging someone...
							if (TitleScreen.netStatus == CLIENT)
							{
								buildClientChallengingSomeonePacket(i-4);
							}
						}
					}
				}
			}
		
			
			
	//ANSWER SUBMIT BUTTON=============================================================================================================
			
			
			//If the challenged player is submitting their answer...
			if (challengeScreen.getSubmitButton().getMoa().isMouseOver())
			{
				if ((challenger != -1) && (challengedPlayer != -1))
				{
					//Challenged player is a client:
					
					if (TitleScreen.netStatus == CLIENT)
					{
						buildChallengeResponsePacketToServer();		
						answerSubmitted = true;
					}
					
					if (TitleScreen.netStatus == HOST)
					{
						buildChallengeResponsePacketFromServer();
						answerSubmitted = true;
					}
				}
			}
	
				
	//NEXT ROUND BUTTON===============================================================================================================
				
				
			if ((challenger != -1) && (challengedPlayer != -1))
			{	
				if ( (status > IN_PROGRESS) && (TitleScreen.netStatus == HOST) )
				{
					
					//Next Round
					if (status == WAITING_FOR_NEXT_ROUND)
					{
						if (nextRoundButton.getMoa().isMouseOver())
						{
							roundNumber++;
							resetGameForNextRound();
						}
					}
					
					
					//Next Game					
					else if (status == WINNER_DETERMINED)				
					{
						if (nextRoundButton.getMoa().isMouseOver())
						{
							roundNumber = 1;
							gameWinner = -1;
									
							//Reset points
									
							for (int i = 0; i < GameFunction.numPlayers; i++)
							{
								players.get(i).setPoints(0);
							}
							
							resetGameForNextRound();
						}
					}
				}

			} // END "must be in challenge mode"
			
			
	
			
	
			
	//VOTING BUTTONS==================================================================================================================
	//================================================================================================================================
			
			
			
			//Voting Buttons during the vote process...	
			if ((challenger != -1) && (challengedPlayer != -1) && (voted == false))
			{
				if ( ( (yesButton.getMoa().isMouseOver()) || (noButton.getMoa().isMouseOver()) ) && (vote != -1) )
				{
					if (yesButton.getMoa().isMouseOver())
						vote = YES;
	
					
					if (noButton.getMoa().isMouseOver())
						vote = NO;
					
					voted = true;
							
					
					//Voter is a server:
					
					if (TitleScreen.netStatus == CLIENT)
					{
						buildVoteSubmissionToServer();
					}
					
					if (TitleScreen.netStatus == HOST)
					{
						MPServer.votesReceived[0] = vote;
						MPServer.numVotesReceived++;
						
						if (MPServer.numVotesReceived == GameFunction.numPlayers-1)
						{
							
							int yesVotes = 0;
							int noVotes = 0;
							
							for (int i = 0; i < 10; i++)
							{
								if (MPServer.votesReceived[i] == YES)
								{
									yesVotes++;
								}
								
								if (MPServer.votesReceived[i] == NO)
								{
									noVotes++;
								}
							}
							
							
							//The challenged played wins
							if (yesVotes >= noVotes)
							{
								MainGame.status = MainGame.ROUND_WIN;
							}
							
							//The challenged played loses
							if (yesVotes < noVotes)
							{
								MainGame.status = MainGame.ROUND_LOSE;
							}
							
							RoundOverToClients result = new RoundOverToClients();
							
							result.noVotes = noVotes;
							result.yesVotes = yesVotes;
							
							MPServer.server.sendToAllTCP(result);
						}
						
						
					}
				}
				
			}
			
			
			
	//=====================================================================================================
	//Play Button
			
			
			
			if ((challenger == -1) && (challengedPlayer == -1))
			{
				if (buttons.get(PLAY_BUTTON).getMoa().isMouseOver())
				{
					determinePlayerID();
					
					//If you're a client and  it's  your turn....
					if (TitleScreen.netStatus == CLIENT)
					{
						
					
						if ( (cardSelected != -1) && playerID == turn)
						{
							//Add the card to the table
							GameFunction.table.add(GameFunction.playerHands.get(playerID).get(cardSelected));
							
							//Remove it from the player's hand.
							GameFunction.playerHands.get(playerID).remove(cardSelected);
							
							cardSelected = -1;	
							
							buildTurnOverPacket();
						}
					}
					
					//If you're a host and it's your turn...
					if (TitleScreen.netStatus == HOST)
					{
					
						if ( (cardSelected != -1) && playerID == turn)
						{
							//Add the card to the table
							GameFunction.table.add(GameFunction.playerHands.get(playerID).get(cardSelected));
							
							//Remove it from the player's hand.
							GameFunction.playerHands.get(playerID).remove(cardSelected);
							
							cardSelected = -1;	
							
							buildServerTurnOverPacket();
						}
					}
					
					if (playerID != turn)
					{
						playPressedOutOfTurn = true;
					}
				}
				
	// OPEN OPTIONS BUTTON==================================================================================
				
				if (buttons.get(OPTIONS_BUTTON).getMoa().isMouseOver())
				{
					optionsOpen = CLIENT;
				}
			}
			
			
			
			
			
	//OPTIONS BUTTONS========================================================================================
			
			
			
			//Options Done Button
			if (options.getOptionsDone().getMoa().isMouseOver())
			{
				optionsOpen = 0;
				
				if (status == SETTING_UP)
					status = WAITING_TO_START;
			}
			
			//START GAME popup button		
			if (TitleScreen.netStatus == HOST)
			{
				if ( ( status == WAITING_TO_START) && (popup.getButton().getMoa().isMouseOver()) && GameFunction.numPlayers > 1)
				{
					status = STARTING;			
				}
			}
			
		}
		
		catch(NullPointerException npe)
		{
			
		}
		
		catch(ArrayIndexOutOfBoundsException oobe)
		{
			
		}
	}

	
	
//MOUSE PRESSED===================================================================================================================
//================================================================================================================================
	
	
	
	
	public void mousePressed (int button, int x, int y)
	{
		if ((challenger == -1) && (challengedPlayer == -1))
		{
			if (status == IN_PROGRESS)
			{
				determinePlayerID();
				
				for (int i = 0; i < GameFunction.playerHands.get(playerID).size(); i++)
				{
					//If the mouse is over the cards area...
					if (clickAreas.get(i).isMouseOver(input))
					{
						cardSelected=i;
					}
				}
			}
		}
		
		
		
//OPTIONS CONTROLS==============================================================
		
		
		
		
		if (optionsOpen != 0)
		{
			if ( TitleScreen.netStatus == HOST)
			{
				//Checkboxes for the decks		
				for (int i = 0; i < 4; i++) // For the 4 decks TODO
				{
					if (options.getAreas().get(i).isMouseOver(input))
					{
						//Toggle the checkmark on click
						if (options.getToggles().get(i) == false)
						{
							options.getToggles().set(i, true);
							sendNewOptionsToClients = true;
						}
						
						else if (options.getToggles().get(i) == true)
						{
							options.getToggles().set(i, false);
							sendNewOptionsToClients = true;
						}
					}
				}
						
				//Checkboxes for the Max Players
				for (int i = 4; i < 13; i++)
				{
					//Set the max players, also used to draw the graphic in the right place
					if (options.getAreas().get(i).isMouseOver(input))
					{
						options.setMaxPlayers(i-2);
						
						sendNewOptionsToClients = true;
					}
					
					
				}
				
				
				//Checkbox for Negative Bonus
				if (options.getAreas().get(13).isMouseOver(input))
				{
					//Toggle the checkmark
					
					if (options.getToggles().get(4) == false)
					{
						options.getToggles().set(4, true);
						sendNewOptionsToClients = true;
					}
					
					else if (options.getToggles().get(4) == true)
					{
						options.getToggles().set(4, false);
						sendNewOptionsToClients = true;
					}
					
					
				}
			
			}
			
			
		}
		
		
		//Send options updates to clients if there was a change...
		if (sendNewOptionsToClients == true)
		{
			OptionsToClient opt = new OptionsToClient();

			opt.optionToggles = new boolean[20];
			
			for (int i = 0; i <  options.getToggles().size(); i++)
			{
				opt.optionToggles[i] = options.getToggles().get(i);
			}
					
			opt.maxPlayers = options.getMaxPlayers();
			opt.timeout = Integer.parseInt(options.getTimeLimit().getText());
			opt.victoryScore = Integer.parseInt(options.getPointsRequired().getText());
			
			MPServer.server.sendToAllTCP(opt);
			
			sendNewOptionsToClients = false;
		}
	}

//================================================================================================================================

}
