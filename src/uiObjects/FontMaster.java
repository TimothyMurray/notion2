package uiObjects;

import java.awt.Font;

import org.newdawn.slick.TrueTypeFont;


public class FontMaster
{
	private Font awtFont;
	
	public FontMaster()
	{


	}
	
	public TrueTypeFont createFont(String fontName, int fontStyle, int fontSize)
	{
		awtFont = new Font(fontName, fontStyle, fontSize);		
		TrueTypeFont resultingFont = new TrueTypeFont(awtFont,false);
		
		return resultingFont;
	}
}
