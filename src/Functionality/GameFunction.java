package functionality;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import server.MPServer;
import server.Packet.InitialDealToClients;
import gameObjects.Card;
import gameScreens.MainGame;
import gameScreens.Options;

//This class contains the functionality of the game's core mechanics.

public class GameFunction
{
	public static int numPlayers = 0;
	public static int maxCardsHeld = 25;
	
	public static ArrayList<Card> deck = new ArrayList<Card>();
	public static ArrayList<Card> table = new ArrayList<Card>();
	public static ArrayList<Card> discard = new ArrayList<Card>();
	
	public static ArrayList<ArrayList<Card>> playerHands = new ArrayList<>();

	
	//Load the card text files
	public static void createCards(Options o)
	{
		
		String filename = "cards/desc"; // useful when we get a more powerful system in place
		 
		 ArrayList<String>filenames = new ArrayList<String>();
		 
		 filenames.add("cards/descMain.txt");
		 filenames.add("cards/descMature.txt");
		 filenames.add("cards/descGaming.txt");
		 filenames.add("cards/descGeek.txt");
		 
		 for (int i = 0; i < 4; i++)
		 {
			 if (o.getToggles().get(i) == true)
			 {
				filename = filenames.get(i);
				try
				{
					File f = new File(filename);
					Scanner in = new Scanner(f);
					int id = 0;
					while (in.hasNext())
					{
						//Get the String text from the file and store it
						String line = in.nextLine();
		
						int type = Card.DESCRIPTION;
						
						//Create the card and add it to the deck
						Card c = new Card(id, type, line);
						deck.add(c);
						
						id++;
					}
					
					in.close();
				}
								
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
			 }
	
		 }
	}
	
	public static void placeFirstCardOnTable()
	{
		//Generate a random index to draw from the deck
		Random r = new Random();
		int randomIndex = r.nextInt(deck.size());
		
		//Add that card to the table and remove it from the deck
		Card cardDealt = deck.get(randomIndex);
		table.add(cardDealt);
		deck.remove(cardDealt);
	}
	
	private static void initHands()
	{
		for (int players = 0; players < numPlayers; players++)
		{
			playerHands.add(new ArrayList<Card>()); // add a hand of cards for each player to the master arraylist of player hands
		}
	}
	
	private static void buildInitialDealPacket()
	{
		InitialDealToClients theDeal = new InitialDealToClients();
		
		theDeal.hands = new Card[numPlayers][20];
		
		//player ID loop		
		for (int p = 0; p < numPlayers; p++)
		{
			//Card ID loop
			for (int c = 0; c < 20; c++)
			{
				//Fill this array with the arrayList we already have since we can't send an arraylist over the network directly.
				theDeal.hands[p][c] = playerHands.get(p).get(c);
			}
		}
		
		theDeal.table = new Card[250];
		
		for (int c = 0; c < table.size(); c++)
		{
			theDeal.table[c] = table.get(c);
		}
		
		//Start the game with the first client
		theDeal.playerTurn = 1; 
		MainGame.turn = 1;
		
		MPServer.server.sendToAllTCP(theDeal);
	}
	
	//Deal all cards at the beginning of the first round
	//"Dealing" actually involves moving random cards from the DECK to all players equally.
	public static void initialDeal()
	{	
		initHands();

		//Now we have all of our hands initialized. The deck has all of the cards at this point.
		
		//Loop through each player "handSize" number of times to give them that many cards
		for (int handSize = 0; handSize < 20; handSize++)
		{
			Random r = new Random();
			
			//Loop through each player
			for (int playerID = 0; playerID < numPlayers; playerID++)
			{
				int randomIndex =  r.nextInt(deck.size());
				Card cardDealt = deck.get(randomIndex); // Determine the card object that will be moved from the deck to the player's hand
				playerHands.get(playerID).add(cardDealt);
				deck.remove(randomIndex); //Remove that card from the deck.
			}
		}
		
		
		placeFirstCardOnTable();
		
		buildInitialDealPacket();
	}
	
	
	public static void subsequentDeal()
	{	

		//Now we have all of our hands initialized. The deck has all of the cards at this point.
		
		//Loop through each player "handSize" number of times to give them that many cards
		for (int handSize = 0; handSize < 20; handSize++)
		{
			Random r = new Random();
			
			//Loop through each player
			for (int playerID = 0; playerID < numPlayers; playerID++)
			{
				//Give everyone 20 cards again.
				if (playerHands.get(playerID).size() < 20)
				{
					int randomIndex =  r.nextInt(deck.size());
					Card cardDealt = deck.get(randomIndex); // Determine the card object that will be moved from the deck to the player's hand
					playerHands.get(playerID).add(cardDealt);
					deck.remove(randomIndex); //Remove that card from the deck.
				}
			}
		}
		
		
		placeFirstCardOnTable();
		
		buildInitialDealPacket();
	}
}
