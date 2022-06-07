package client;

import functionality.GameFunction;
import gameObjects.Card;
import gameObjects.Player;
import gameScreens.MainGame;
import gameScreens.ChallengeScreen;
import gameScreens.TitleScreen;

import java.util.ArrayList;

import server.Packet.*;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;


public class NetworkListener extends Listener
{
	private Client listenerClient;
	Connection conn;
	
	public static ArrayList<Player>downloadedPlayerList = new ArrayList<Player>();
	
	public static boolean newPlayerConnected = false;

	public void init(Client client)
	{
		listenerClient = client;	
	}
	
	
	public void connected(Connection c)
	{
		//As soon as we connect, sent a loginRequest packet with the client's screen name
		
		conn = c;
		
		TitleScreen.netStatus = 2;
		
		System.out.println("[CLIENT] You have connected.");
		
		Packet0LoginRequest loginRequest = new Packet0LoginRequest();
		
		loginRequest.player = MPClient.player;
	
		listenerClient.sendTCP(loginRequest);
	}
	
	
	public void disconnected(Connection c)
	{
		System.out.println("[CLIENT] You have disconnected.");
	}
	
	
	
	public void received(Connection c, Object o)
	{
		try
		{
			//==============================================================================
			
			if (o instanceof Packet1LoginAnswer)
			{
				boolean answer = ((Packet1LoginAnswer) o).accepted;
				int numPlayers = 0;
				
				if (answer == true)
				{
					System.out.println("[CLIENT] New played joining...");
					newPlayerConnected = true;
				}
				
				else
				{
					//Close the connection if not allowed
					System.out.println("[CLIENT] Game is full");
					c.close();
				}
		
				if (newPlayerConnected == true)
				{	
					MainGame.status = ((Packet1LoginAnswer)o).gameStatus;
									
					downloadedPlayerList.clear();
					
					Player[] p = new Player[10];					
					//Just make it easier to read
					for (int i = 0; i < 10; i++)
					{
						if ( ((Packet1LoginAnswer) o).playerList[i] != null)
						{
							p[i] = ((Packet1LoginAnswer) o).playerList[i];
							numPlayers++;
						}
					}
					
					GameFunction.numPlayers = numPlayers;
					
					for (int i = 0; i < p.length; i++)
					{
						if (p[i] != null)
						{
							downloadedPlayerList.add(p[i]);
						};
					}
					
					
					if (MainGame.status == MainGame.IN_PROGRESS)
					{
						MainGame.turn = ((Packet1LoginAnswer)o).turn;
						
						GameFunction.table.clear();
						for (int i = 0; i < 250; i++)
						{
							if ( ((Packet1LoginAnswer) o).tableCards[i] != null)
							{
								GameFunction.table.add( ((Packet1LoginAnswer) o).tableCards[i]);
							}
						}
						
						GameFunction.playerHands.clear();		
						for (int i = 0; i < GameFunction.numPlayers; i++)
						{
							GameFunction.playerHands.add(new ArrayList<Card>());
						}
						
						for (int players = 0; players < GameFunction.numPlayers; players++)
						{
							for (int card = 0; card < GameFunction.maxCardsHeld; card++)
							{		
								if ( ((Packet1LoginAnswer) o).clientCards[players][card] != null)
									GameFunction.playerHands.get(players).add(card,  ((Packet1LoginAnswer) o).clientCards[players][card]);													
							}
						}				
					}
				}
			}
			
			//==============================================================================
			
			if (o instanceof OptionsToClient)
			{
	
				boolean[] optToggles = ((OptionsToClient) o).optionToggles;
				int maxP = ((OptionsToClient) o).maxPlayers;
				int timeLim = ((OptionsToClient) o).timeout;
				int vicScore = ((OptionsToClient) o).victoryScore;
				
				ArrayList<Boolean>t = new ArrayList<Boolean>();
				
				//convert array to arraylist
				
				for (int i = 0; i < optToggles.length; i++)
				{
					t.add(optToggles[i]);
				}
				
				MainGame.options.setToggles(t);
				
				MainGame.options.setMaxPlayers(maxP);
				
				//Textfield for time limit
				MainGame.options.setTimeLimit(MainGame.options.getTimeLimit());
				MainGame.options.getTimeLimit().setText(Integer.toString(timeLim));
				MainGame.options.getTimeLimit().setAcceptingInput(false);
				
				//Textfield for victory score
				MainGame.options.setPointsRequired(MainGame.options.getPointsRequired());
				MainGame.options.getPointsRequired().setText(Integer.toString(vicScore));
				MainGame.options.getPointsRequired().setAcceptingInput(false);
		
			}
			
			//====================================================================================
			
			
			if (o instanceof InitialDealToClients)
			{
				System.out.println("[CLIENT] Received first hand from server.");
				
				//[num hands] [cards in each hand]
				Card[][] deal = ((InitialDealToClients) o).hands;
				
				Card[] tableCards = ((InitialDealToClients) o).table;
				
				GameFunction.playerHands.clear();
				//Make a hand for each player, now that we know how many
				for (int i = 0; i < deal.length; i++)
				{
					GameFunction.playerHands.add(new ArrayList<Card>());
				}
				
				for (int players = 0; players < deal.length; players++)
				{
					for (int card = 0; card < 20; card++)
					{		
						GameFunction.playerHands.get(players).add(card, deal[players][card]);													
					}
				}
				
				GameFunction.table.clear();
				GameFunction.table.add(tableCards[0]);	
				
				MainGame.turn = ((InitialDealToClients) o).playerTurn;
				MainGame.optionsOpen = 0;
				MainGame.status = MainGame.IN_PROGRESS;
				
				System.out.println("[CLIENT] Game starting...");
			
			}
			
			//====================================================================================
			
			if (o instanceof TurnDataToClients)
			{
	/*			public Card[] tableCards;
				public Card[][] clientCards;
				public int playerTurn;*/
				
				Card[] tableCards = new Card[250];		
				tableCards = ((TurnDataToClients)o).tableCards;
				
				Card[][] clientCards = ((TurnDataToClients)o).clientCards;
				int playerTurn = ((TurnDataToClients)o).playerTurn;
				
				MainGame.turn = playerTurn;
				
				GameFunction.table.clear();
				
				for (int i = 0; i < 250; i++)
				{
					if (tableCards[i] != null)
					{
						GameFunction.table.add(tableCards[i]);
					}
				}
				
				//Rebuild each player's hand			
				for (int i = 0; i < GameFunction.numPlayers; i++)
				{
					GameFunction.playerHands.get(i).clear();
				}
				
				for (int players = 0; players < GameFunction.numPlayers; players++)
				{
					for (int card = 0; card < GameFunction.maxCardsHeld; card++)
					{		
						if (clientCards[players][card] != null)
							GameFunction.playerHands.get(players).add(card, clientCards[players][card]);													
					}
				}
				
			}
			
			//===========================================================================================
			
			if (o instanceof ServerTurnOver)
			{
	/*			public Card[] tableCards;
				public Card[] serverCards;
				public int playerTurn;*/
				
				GameFunction.table.clear();
				for (int i = 0; i < 250; i++)
				{
					if ( ((ServerTurnOver)o).tableCards[i]  != null)
					{
						GameFunction.table.add( ((ServerTurnOver)o).tableCards[i]);
					}
				}
				
				//Reload the server's hand locally
				GameFunction.playerHands.get(0).clear();
				
				for (int i = 0; i < GameFunction.maxCardsHeld; i++)
				{
					if ( ((ServerTurnOver)o).serverCards[i] != null)
					{
						GameFunction.playerHands.get(0).add( ((ServerTurnOver)o).serverCards[i]);
					}
				}
				
				MainGame.turn = ((ServerTurnOver)o).playerTurn;
			}
			
			//============================================================================================
			
			if (o instanceof HostChallengingClient)
			{
				System.out.println("[CLIENT] A player has been challenged!");
				MainGame.challengedPlayer = ((HostChallengingClient)o).playerChallenged;	
				MainGame.challenger = 0;
			}
			
			//============================================================================================
			
			
			
			if (o instanceof ClientChallengingSomeone)
			{
				System.out.println("[CLIENT] A player has been challenged!");
				
				MainGame.challengedPlayer = ((ClientChallengingSomeone)o).playerChallenged;	
				MainGame.challenger = ((ClientChallengingSomeone)o).playerChallenging;	
			}
			
			
			//=============================================================================================
			
			
			if (o instanceof ChallengeResponseFromServer)
			{
				ChallengeScreen.submittedAnswer = ((ChallengeResponseFromServer)o).response;
			}
			
			//Each vote, so players can see a running tally of the votes
			if (o instanceof CurrentVoteToClients)
			{
				/*public int playerFrom;
				public int vote;*/
				
				int from = ((CurrentVoteToClients)o).playerFrom;
				int vote = ((CurrentVoteToClients)o).vote;
				
				MPClient.votesReceived[from] = vote;
			}
			
			
			//=============================================================================================
			
			
				
			//Last vote received
			if (o instanceof RoundOverToClients)
			{
	/*			public int yesVotes;
				public int noVotes;*/
				
				int yesVotes = ((RoundOverToClients)o).yesVotes;
				int noVotes =  ((RoundOverToClients)o).noVotes;
				
				System.out.println("[CLIENT] Round Over!");
				
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
				
				
			}
			
			
			
			//=============================================================================================
			
	
			if (o instanceof ScoreUpdate)
			{
		
				for (int i = 0; i < GameFunction.numPlayers; i++)
				{
					if (MainGame.players.get(i) != null)
						MainGame.players.get(i).setPoints( ((ScoreUpdate)o).scores[i] );
				}
				
			}
			
			
			
			//=============================================================================================
			
			if (o instanceof NewRound)
			{
	
				MainGame.cardSelected = -1;
				MainGame.addingNewPlayer = false;
				MainGame.turn = -1;
				MainGame.challengedPlayer = -1;
				MainGame.	challenger = -1;
				MainGame.voted = false;
				MainGame.vote = -1;
				MainGame.winner = 0;
				MainGame.challengeScreen.getAnswer().setText("");
				ChallengeScreen.submittedAnswer = "";
				MainGame.status = MainGame.DOWNLOADING_FRESH_HAND;
				MPClient.votesReceived = new int[10];
				downloadedPlayerList = new ArrayList<Player>();
				MainGame.answerSubmitted = false;
				MainGame.gameWinner = -1;
			}
			
			
			//===============================================================================================
			
			if (o instanceof PlayerLeft)
			{
				GameFunction.numPlayers = ((PlayerLeft)o).numberOfPlayers;
				
				Player[] players = new Player[10];
				Card[][] cards = new Card[10][GameFunction.maxCardsHeld];
				
				players = ((PlayerLeft)o).p;
				cards = ((PlayerLeft)o).h;
				
				//Clear and reload player and hand lists
				MainGame.players.clear();
				GameFunction.playerHands.clear();
				
				//rebuild the playerHands based on the new number of players.
				for (int i = 0; i < GameFunction.numPlayers; i++)
				{
					GameFunction.playerHands.add(new ArrayList<Card>());
				}
				
				//Add cards to the playerHands			
				for (int play = 0; play < GameFunction.numPlayers; play++)
				{
					
					for (int card = 0; card < GameFunction.maxCardsHeld; card++)
					{
						if (cards[play][card] != null)
							GameFunction.playerHands.get(play).add(cards[play][card]);
					}
				}
				
				//Rebuild players			
				for (int i = 0; i < GameFunction.numPlayers; i++)
				{
					MainGame.players.add(players[i]);
				}
				
				
				MainGame.turn =  ((PlayerLeft)o).turn;
			}
			
			
			
			//=====================================================================================================
			
			
			
			if (o instanceof GameOver)
			{
				MainGame.gameWinner = ((GameOver)o).winnerID;
				
				MainGame.status = MainGame.WINNER_DETERMINED;
			}
			
			
			
			//=====================================================================================================
			
			
			
			if (o instanceof ClearPoints)
			{
				for (int i = 0; i < MainGame.players.size(); i++)
				{
					MainGame.players.get(i).setPoints(0);
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
	
	


}
