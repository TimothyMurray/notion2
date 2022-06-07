package gameScreens;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;

import uiObjects.Button;
import utility.ScreenPosition;
import utility.Txt;


public class WaitingPopup
{
	
	private static int HOST = 1;
	
	private Image bgWaitingPopup;
	private Button startButton;
	
	ScreenPosition p = new ScreenPosition();
	
	public WaitingPopup(GameContainer gc)
	{
		
		try
		{
			this.bgWaitingPopup = new Image("ui/bgWaitingPopup.png");
			this.bgWaitingPopup = this.bgWaitingPopup.getScaledCopy(p.screenX(400, gc.getWidth()), p.screenY(300, gc.getHeight()));
		}
		catch (SlickException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			this.startButton = new Button(
			gc,"buttons/yellowButtonOff.png",p.screenX(810,gc.getWidth()), p.screenY(540,gc.getHeight()), p.screenX(160,gc.getWidth()),p.screenY(42,gc.getHeight()), "START!");
	}
	
	public void draw(GameContainer gc, Graphics g, TrueTypeFont f)
	{
		g.setFont(f);
		g.setColor(Color.black);
		
		this.bgWaitingPopup.draw(p.screenX(700f,gc.getWidth()), p.screenY(300f, gc.getHeight()));
		
		if (TitleScreen.netStatus == HOST)
		{
			this.startButton.render(gc, g);		
			g.drawString(startButton.getButtonText(), Txt.centerTextHorizontal(startButton, f), Txt.centerTextVertical(startButton, f));
		}
	}

	/**Method to return the value of bgWaitingPopup
	 * @return the bgWaitingPopup
	 */
	public Image getBgWaitingPopup()
	{
		return bgWaitingPopup;
	}

	
	/**Method to return the value of bgWaitingPopup
	 * @param bgWaitingPopup the bgWaitingPopup to set
	 */
	public void setBgWaitingPopup(Image bgWaitingPopup)
	{
		this.bgWaitingPopup = bgWaitingPopup;
	}

	
	/**Method to return the value of startButton
	 * @return the startButton
	 */
	public Button getButton()
	{
		return startButton;
	}

	
	/**Method to return the value of startButton
	 * @param startButton the startButton to set
	 */
	public void setButton(Button startButton)
	{
		this.startButton = startButton;
	}
	
	
}
