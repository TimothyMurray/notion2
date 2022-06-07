package server;

import java.util.ArrayList;
import java.util.Random;

import functionality.GameFunction;
import gameObjects.Card;
import gameObjects.Player;
import gameScreens.MainGame;
import gameScreens.ChallengeScreen;
import server.Packet.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class HostListener extends Listener
{

	public void connected(Connection c)
	{
		
		System.out.println("[SERVER] " + c.getRemoteAddressTCP() + " has connected.");
		c.setKeepAliveTCP(1000);
	}
	
	
	
	public void disconnected(Connection c)
	{
		System.out.println("[SERVER] Player " + c.getID() + " has disconnected.");
		
		GameFunction.numPlayers--;
		

		Player players[] = new Player[10];
		Card hands[][] = new Card[10][GameFunction.maxCardsHeld];
	
		
		
		//REMOVE PLAYER HAND--------------------------------------------------------
		
		for (int i = 0; i < GameFunction.playerHands.size(); i++)
		{
			if (MainGame.players.get(i) != null)
			{
				if (MainGame.players.get(i).getIpAddress() == c.getID() )
				{
					GameFunction.playerHands.remove(i);
				}		
			}
		}
		
		//---------------------------------------------------------------------------
	
		
		
		//REMOVE PLAYER--------------------------------------------------------------
		
		for (int i = 0; i < MainGame.players.size(); i++)
		{
			//If a player's IP matches the Disconnected IP, remove it from the game
			if (MainGame.players.get(i).getIpAddress() == (c.getID() ) )
			{
				MainGame.players.remove(i);
			}
		}
		
		for (int i = 0; i < MainGame.players.size(); i++)
		{
			players[i] = MainGame.players.get(i);
		}
		
		//--------------------------------------------------------------------------
		
		
		//Build the arrays from the arraylists--------------------------------------
		
		//PLAYER HANDS
		
		if (MainGame.status == MainGame.IN_PROGRESS)
		{
			for (int pp = 0; pp < GameFunction.numPlayers; pp++)
			{		
				for (int hh = 0; hh < GameFunction.playerHands.get(pp).size(); hh++)
				{
					if (GameFunction.playerHands.get(pp).get(hh) != null)
						hands[pp][hh] = GameFunction.playerHands.get(pp).get(hh);
				}
			}
		}
		
		
		//PLAYER
		for (int i = 0; i < GameFunction.numPlayers; i++)
		{
			if (MainGame.players.get(i) != null)
			players[i] = MainGame.players.get(i);
		}
		
		
		PlayerLeft pl = new PlayerLeft();
		pl.numberOfPlayers = GameFunction.numPlayers;
		pl.h = hands;
		pl.p = players;
		
		//Make sure the turn # is still valid
		if (MainGame.turn >= GameFunction.numPlayers-1)
		{
			MainGame.turn--;
		}
		
		if (MainGame.turn < 0)
		{
			MainGame.turn = 0;
		}
		
		pl.turn = MainGame.turn;
	
		MPServer.server.sendToAllTCP(pl);
	}
	
	
	
	
	public void buildLoginAnswerObject(Connection c, Packet1LoginAnswer loginAnswer)
	{
		loginAnswer.accepted = true;
		Player[] list = new Player[10];
		
		for (int i = 0; i < MainGame.players.size(); i++)
		{
			list[i] = (MainGame.players.get(i));
		}
		
		loginAnswer.playerList = list;
	}
	
	private void addNewPlayerServerSide(Connection c, Object o, Packet1LoginAnswer l)
	{
		//If the game hasn't been started yet...
		if  ( (MainGame.status == MainGame.SETTING_UP) || (MainGame.status == MainGame.WAITING_TO_START) )
		{
			//Increment the number of players in the game
			GameFunction.numPlayers++;
			MainGame.addingNewPlayer = true;
			
			//Add the client's screen name to the array list of screen names at the table
			Player player = ((Packet0LoginRequest) o).player;
			player.setIpAddress(c.getID());
			
			MainGame.players.add(player);
			
			l.gameStatus = MainGame.SETTING_UP;
		}
		
		
		//If a player joins in-game
		
		else if (MainGame.status >= MainGame.STARTING)
		{
			GameFunction.numPlayers++;
			MainGame.addingNewPlayer = true;
			
			GameFunction.playerHands.add(new ArrayList<Card>());
			
			dealNewPlayerAHand();
			
			//Add the client to the playerList
			Player p = ((Packet0LoginRequest) o).player;
			p.setIpAddress(c.getID());			
			MainGame.players.add(p);
	
			l.gameStatus = MainGame.IN_PROGRESS;
			
			l.tableCards = new Card[250];
			for (int  i = 0; i < GameFunction.table.size(); i++)
			{
				l.tableCards[i] = GameFunction.table.get(i);
			}
			
			l.clientCards = new Card[10][GameFunction.maxCardsHeld];
			
			l.clientCards = new Card[GameFunction.numPlayers][GameFunction.maxCardsHeld];
		
			
			for (int player = 0; player < GameFunction.numPlayers; player++)
			{
				
				for (int cardNum = 0; cardNum < GameFunction.playerHands.get(player).size(); cardNum++)
				{
					if (GameFunction.playerHands.get(player).get(cardNum) != null)
					{
						l.clientCards[player][cardNum] = GameFunction.playerHands.get(player).get(cardNum);
					}
				}
			}
			
			l.turn = MainGame.turn;
		}
	}
	
	private void dealNewPlayerAHand()
	{
		for (int i = 0; i < 20; i++)
		{
			Random r = new Random();
			
			int randomIndex =  r.nextInt(GameFunction.deck.size());
			Card cardDealt = GameFunction.deck.get(randomIndex); // Determine the card object that will be moved from the deck to the player's hand			
			
			GameFunction.playerHands.get( GameFunction.playerHands.size() - 1).add(cardDealt);
			GameFunction.deck.remove(randomIndex);
		}
	}
	
	
	private void buildOptionsToClientsPacket()
	{
		OptionsToClient opt = new OptionsToClient();

		opt.optionToggles = new boolean[20];
		
		for (int i = 0; i <  MainGame.options.getToggles().size(); i++)
		{
			opt.optionToggles[i] = MainGame.options.getToggles().get(i);
		}
				
		opt.maxPlayers = MainGame.options.getMaxPlayers();
		opt.timeout = Integer.parseInt(MainGame.options.getTimeLimit().getText());
		opt.victoryScore = Integer.parseInt(MainGame.options.getPointsRequired().getText());
		
		MPServer.server.sendToAllTCP(opt);
	}
	
	
	public void received(Connection c, Object o)
	{
		try
		{
		
			MPServer.c = c;
			
			//==============================================================================================================
			
			
	
			//If the host receives a JOIN request... Respond TRUE if the player may join or FALSE to deny the connection.
			if (o instanceof Packet0LoginRequest)
			{	
				Packet1LoginAnswer loginAnswer = new Packet1LoginAnswer();
				
				//If this is too many players...
				if (GameFunction.numPlayers >= MainGame.options.getMaxPlayers())
				{
					c.close(); // For now just kill the connection
				}
				
				
				//If we're good to add this player to the game...
				else
				{
				
					buildOptionsToClientsPacket();
				
					//If adding this new player won't put us over the player limit....
					if (GameFunction.numPlayers < MainGame.options.getMaxPlayers())
					{	
						addNewPlayerServerSide(c, o, loginAnswer);					
						buildLoginAnswerObject(c, loginAnswer);
					}					
					
				}
				
				//Reply to the client
				MPServer.server.sendToAllTCP(loginAnswer);
			}
			
			
			
			//==============================================================================================================
			
			
			
			if (o instanceof TurnOverPacketToServer)
			{
				/*
				public Card[] tableCards;
				public Card[] clientCards;
				public int playerTurn;*/
				
				int playerTurn = ((TurnOverPacketToServer)o).playerTurn;
				
				Card[] table = ((TurnOverPacketToServer) o).tableCards;
				
				//Build a fresh arraylist from the client's data
				GameFunction.table.clear();
				for (int i = 0; i < table.length; i++)
				{
					if (table[i] != null)
					GameFunction.table.add(table[i]);
				}
				
				Card[] clientCards = ((TurnOverPacketToServer) o).clientCards;
				
				GameFunction.playerHands.get(playerTurn).clear();
				for (int i = 0; i < GameFunction.maxCardsHeld; i++)
				{
					if (clientCards[i] != null)
					{
						GameFunction.playerHands.get(playerTurn).add(clientCards[i]);
					}
				}
				
				//If there's another player with a higher ID than the last player...
				
				if (playerTurn < GameFunction.numPlayers-1)
				{
					MainGame.turn = playerTurn + 1;
				}
				
				else
				{
					MainGame.turn = 0;
				}
				
				if (MainGame.turn < 0)
				{
					MainGame.turn = 0;
				}
				
				
				//Build turn data for clients
				
				TurnDataToClients turnData = new TurnDataToClients();
				turnData.tableCards = new Card[250];
				
				for (int  i = 0; i < GameFunction.table.size(); i++)
				{
					turnData.tableCards[i] = GameFunction.table.get(i);
				}
				
				turnData.clientCards = new Card[10][GameFunction.maxCardsHeld];
				
				
				for (int player = 0; player < GameFunction.numPlayers; player++)
				{
					
					for (int cardNum = 0; cardNum < GameFunction.playerHands.get(player).size(); cardNum++)
					{
						if (GameFunction.playerHands.get(player).get(cardNum) != null)
						{
							turnData.clientCards[player][cardNum] = GameFunction.playerHands.get(player).get(cardNum);
						}
					}
				}
				
				turnData.playerTurn = MainGame.turn;
				
				MPServer.server.sendToAllTCP(turnData);
			}
			
			
			//=========================================================================================================
			
			if (o instanceof ChallengeResponseToServer)
			{
				ChallengeScreen.submittedAnswer = ((ChallengeResponseToServer)o).answer;
				
				//Now submit this to all players
				ChallengeResponseFromServer response = new ChallengeResponseFromServer();
				
				response.response = ChallengeScreen.submittedAnswer;
				
				//Make a default answer if the player left the response blank
				if (response.response.equalsIgnoreCase(""))
				{
					response.response = "I have no idea =(";
				}
				
				MPServer.server.sendToAllTCP(response);
			}
			
			
			//==========================================================================================================
	
			
			if (o instanceof VoteSubmissionToServer)
			{
				
				/*		
				public int playerFrom;
				public int vote;*/
				
				int playerFrom = ((VoteSubmissionToServer)o).playerFrom;
				int playerVote = ((VoteSubmissionToServer)o).vote;
				
				MPServer.votesReceived[playerFrom] = playerVote;
				
				MPServer.numVotesReceived++;
				
				//If we've received all of the votes...
				if (MPServer.numVotesReceived == GameFunction.numPlayers-1)
				{
					calculateWinner();
				}
				
				CurrentVoteToClients currentVote = new CurrentVoteToClients();
				
				currentVote.playerFrom = playerFrom;
				currentVote.vote = playerVote;
				
				MPServer.server.sendToAllTCP(currentVote);
				
			}
			
			
			//=================================================================================================
			
			
			
			if (o instanceof ClientChallengingSomeone)
			{
				MainGame.challengedPlayer = ((ClientChallengingSomeone)o).playerChallenged;
				MainGame.challenger = ((ClientChallengingSomeone)o).playerChallenging;
				
				
				//Tell everyone that a challenge has happened.
				
				ClientChallengingSomeone ch = new ClientChallengingSomeone();
				
				ch.playerChallenged = MainGame.challengedPlayer;
				ch.playerChallenging = MainGame.challenger;
				
				MPServer.server.sendToAllTCP(ch);
				
				
			}
		}
		
		catch(NullPointerException npe)
		{
			
		}
		
		catch(ArrayIndexOutOfBoundsException oobe)
		{
			
		}
	}
	
	
	private void calculateWinner()
	{
		int yesVotes = 0;
		int noVotes = 0;
		
		for (int i = 0; i < 10; i++)
		{
			if (MPServer.votesReceived[i] == MainGame.YES)
			{
				yesVotes++;
			}
			
			if (MPServer.votesReceived[i] == MainGame.NO)
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
