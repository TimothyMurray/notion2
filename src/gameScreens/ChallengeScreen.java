package gameScreens;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;

import utility.ScreenPosition;
import uiObjects.Button;

public class ChallengeScreen
{
	private Image bgChallengeScreen;
	
	private TextField answer;
	private Button submitButton;
	
	public static String submittedAnswer = "";
	
	private ScreenPosition p = new ScreenPosition();
	
	public ChallengeScreen(GameContainer gc, Font mainFont, Font textFieldFont)
	{
		try
		{
			bgChallengeScreen = new Image("ui/bgChallenged.png");
			bgChallengeScreen = bgChallengeScreen.getScaledCopy(gc.getWidth(), gc.getHeight());
			
			answer = new TextField(gc, textFieldFont, p.screenX(1320, gc.getWidth()), p.screenY(600, gc.getHeight()), p.screenX(580, gc.getWidth()), p.screenY(30, gc.getHeight()));
			answer.setBackgroundColor(Color.white);
			answer.setBorderColor(Color.black);
			answer.setTextColor(Color.black);
			answer.setMaxLength(70);
			
			submitButton = new Button(gc, "buttons/yellowButtonOff.png", p.screenX(1550,gc.getWidth()), p.screenY(640,gc.getHeight()), p.screenX(140,gc.getWidth()),p.screenY(42,gc.getHeight()), "ANSWER!");
		}
		catch (SlickException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**Method to return the value of bgChallengeScreen
	 * @return the bgChallengeScreen
	 */
	public Image getBg()
	{
		return bgChallengeScreen;
	}

	
	/**Method to return the value of bgChallengeScreen
	 * @param bgChallengeScreen the bgChallengeScreen to set
	 */
	public void setBg(Image bgChallengeScreen)
	{
		this.bgChallengeScreen = bgChallengeScreen;
	}

	
	/**Method to return the value of answer
	 * @return the answer
	 */
	public TextField getAnswer()
	{
		return answer;
	}

	
	/**Method to return the value of answer
	 * @param answer the answer to set
	 */
	public void setAnswer(TextField answer)
	{
		this.answer = answer;
	}

	
	/**Method to return the value of submitButton
	 * @return the submitButton
	 */
	public Button getSubmitButton()
	{
		return submitButton;
	}

	
	/**Method to return the value of submitButton
	 * @param submitButton the submitButton to set
	 */
	public void setSubmitButton(Button submitButton)
	{
		this.submitButton = submitButton;
	}
	
	
}
