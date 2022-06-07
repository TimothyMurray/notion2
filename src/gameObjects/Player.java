package gameObjects;

public class Player
{
	private String name;
	private int netStatus;
	
	private int points;
	
	private int ipAddress;
	
	public Player(String name, int netStatus, int points)
	{
		setName(name);
		setNetStatus(netStatus);
		setPoints(points);
	}
	
	public Player()
	{
		
	}

	
	/**Method to return the value of name
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	
	/**Method to return the value of name
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	
	/**Method to return the value of netStatus
	 * @return the netStatus
	 */
	public int getNetStatus()
	{
		return netStatus;
	}


	
	/**Method to return the value of netStatus
	 * @param netStatus the netStatus to set
	 */
	public void setNetStatus(int netStatus)
	{
		this.netStatus = netStatus;
	}


	
	/**Method to return the value of points
	 * @return the points
	 */
	public int getPoints()
	{
		return points;
	}


	
	/**Method to return the value of points
	 * @param points the points to set
	 */
	public void setPoints(int points)
	{
		this.points = points;
	}

	
	/**Method to return the value of ipAddress
	 * @return the ipAddress
	 */
	public int getIpAddress()
	{
		return ipAddress;
	}

	
	/**Method to return the value of ipAddress
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(int ipAddress)
	{
		this.ipAddress = ipAddress;
	}
	
	
	
	
}

