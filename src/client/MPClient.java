package client;

import gameObjects.Player;

import java.io.IOException;

import server.Packet.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

public class MPClient
{
	
	public static Client client;
	public static Player player;
	public static int[] votesReceived = new int[10];
	
	public MPClient(Player p, String ip)
	{
		MPClient.player = p;
		
		client = new Client(65536, 65536);
		register();
		
		NetworkListener nl = new NetworkListener();
		nl.init(client);
		client.addListener(nl);
		client.setKeepAliveTCP(1000);
		
		client.start();
		
		try
		{
			client.connect(4000, ip, 9001, 9001); //If packet not sent , client is disconnected.
		}
		catch (IOException e)
		{
			e.printStackTrace();
			client.stop();
			
			client = null;
		}
	}
	
	private void register()
	{
		//Serialization
		Kryo kryo = client.getKryo();
		
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
