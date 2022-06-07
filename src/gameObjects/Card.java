package gameObjects;


public class Card
{
	public static final int DESCRIPTION = 0;
	public static final int ACTION = 1;
	public static final int SURPRISE = 2;

	
	private int cardID; //The unique ID number of the card
	private int cardType; // 0 = Description card. 1 = Action card. 2 = Surprise card.
	private String cardText; //The string of text that appears on the card
	
	
	public Card(int id, int type, String text)
	{
		cardID = id;
		cardType = type;
		cardText = text;
	}

	public Card()
	{
		
	}

	
	/**Method to return the value of cardID
	 * @return the cardID
	 */
	public int getCardID()
	{
		return cardID;
	}


	
	/**Method to return the value of cardID
	 * @param cardID the cardID to set
	 */
	public void setCardID(int cardID)
	{
		this.cardID = cardID;
	}

	
	/**Method to return the value of cardType
	 * @return the cardType
	 */
	public int getCardType()
	{
		return cardType;
	}


	
	/**Method to return the value of cardType
	 * @param cardType the cardType to set
	 */
	public void setCardType(int cardType)
	{
		this.cardType = cardType;
	}


	
	/**Method to return the value of cardText
	 * @return the cardText
	 */
	public String getCardText()
	{
		return cardText;
	}


	
	/**Method to return the value of cardText
	 * @param cardText the cardText to set
	 */
	public void setCardText(String cardText)
	{
		this.cardText = cardText;
	}
	
	
}
