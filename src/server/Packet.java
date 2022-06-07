package server;

import gameObjects.Card;
import gameObjects.Player;

public class Packet
{
	
	public static class Packet0LoginRequest
	{
		public Player player;	
	}
	
	public static class Packet1LoginAnswer
	{
		//All the stuff that a new player will need when they immediately join a game

		public boolean accepted = false; //May they join the game?
		public Player[] playerList;
		public int gameStatus;
		
		public Card[] tableCards;
		public Card[][] clientCards;
		public int turn;
	}
	
	public static class OptionsToClient
	{
		public boolean[] optionToggles;
		public int maxPlayers;
		public int timeout;
		public int victoryScore;
	}
	
	public static class InitialDealToClients
	{
		public Card[][] hands; // [player #] [card #]
		public Card[] table; // [Card #]
		public int playerTurn;
	}
	
	public static class TurnOverPacketToServer
	{
		public Card[] tableCards;
		public Card[] clientCards;
		public int playerTurn;
	}
	
	public static class TurnDataToClients
	{
		public Card[] tableCards;
		public Card[][] clientCards;
		public int playerTurn;
	}
	
	public static class ServerTurnOver
	{
		public Card[] tableCards;
		public Card[] serverCards;
		public int playerTurn;
	}
	
	public static class AnotherClientHasJoined
	{
		public Player[] playerList;
	}
	
	public static class HostChallengingClient
	{
		public int playerChallenged;
	}
	
	public static class ClientChallengingSomeone
	{
		public int playerChallenged;
		public int playerChallenging;
	}
	
	public static class ChallengeResponseToServer
	{
		public String answer;
	}
	
	public static class ChallengeResponseFromServer
	{
		public String response;
	}
	
	public static class VoteSubmissionToServer
	{
		public int playerFrom;
		public int vote;
	}
	
	public static class RoundOverToClients
	{
		public int yesVotes;
		public int noVotes;
	}
	
	public static class CurrentVoteToClients
	{
		public int playerFrom;
		public int vote;
	}
	
	public static class ScoreUpdate
	{
		public int[] scores;
	}
	
	public static class NewRound
	{
		public boolean nextRound = true;
	}
	
	public static class PlayerLeft
	{
		public int numberOfPlayers;
		public Player p[];
		public Card h[][];
		public int turn;
	}
	
	public static class GameOver
	{
		public int winnerID;
	}
	
	public static class ClearPoints
	{
		boolean clear = true;
	}
}
