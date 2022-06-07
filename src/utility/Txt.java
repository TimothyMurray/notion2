package utility;

import org.newdawn.slick.TrueTypeFont;

import uiObjects.Button;


public class Txt
{
	public Txt()
	{
		
	}
	
	public static float centerTextHorizontal(Button b, TrueTypeFont font)
	{
		return b.getX()+(b.getWidth()/2)-(font.getWidth(b.getButtonText())/2);
	}
	
	public static float centerTextVertical(Button b, TrueTypeFont font)
	{
		return b.getY()+(b.getHeight()/2) - (font.getHeight(b.getButtonText())/2);
	}
}
