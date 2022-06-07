package server;

import java.io.IOException;

import server.Packet.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import functionality.GameFunction;
import gameObjects.Player;
import gameScreens.MainGame;

public class MPServer
{
	
	public static Server server;
	public static Player player;
	public static Connection c;
	
	public static int[] votesReceived = new int[10];
	public static int numVotesReceived = 0;	
	
	public MPServer(Player player)
	{
		server = new Server(65536, 65536);
		registerPackets();
		server.addListener(new HostListener());
		
		server.start();
		
		try
		{
			server.bind(9001, 9001);
			
			MainGame.players.add(player);
			GameFunction.numPlayers++;
			System.out.println("[SERVER] Server started.");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	private void registerPackets()
	{
		//Serialization
		Kryo kryo = server.getKryo();

		kryo.register(gameObjects.Player.class);
		kryo.register(gameObjects.Player[].class);
		kryo.register(boolean.class);
		kryo.register(boolean[].class);
		kryo.register(int.class);
		kryo.register(int[].class);
		kryo.register(gameObjects.Card[].class);
		kryo.register(gameObjects.Card[][].class);
		kryo.register(gameObjects.Card.class);
		kryo.register(String.class);
			
		kryo.register(Packet0LoginRequest.class);
		kryo.register(Packet1LoginAnswer.class);	
		kryo.register(OptionsToClient.class);
		kryo.register(InitialDealToClients.class);
		kryo.register(TurnOverPacketToServer.class);
		kryo.register(TurnDataToClients.class);
		kryo.register(ServerTurnOver.class);
		kryo.register(AnotherClientHasJoined.class);
		kryo.register(HostChallengingClient.class);
		kryo.register(ChallengeResponseToServer.class);
		kryo.register(ChallengeResponseFromServer.class);
		kryo.register(VoteSubmissionToServer.class);
		kryo.register(RoundOverToClients.class);
		kryo.register(CurrentVoteToClients.class);
		kryo.register(ClientChallengingSomeone.class);
		kryo.register(ScoreUpdate.class);
		kryo.register(NewRound.class);
		kryo.register(PlayerLeft.class);
		kryo.register(GameOver.class);
		kryo.register(ClearPoints.class);
	}
}
