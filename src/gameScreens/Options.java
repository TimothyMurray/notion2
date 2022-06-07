package gameScreens;

import java.awt.Font;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.TextField;

import uiObjects.AreaHighlight;
import uiObjects.Button;
import uiObjects.FontMaster;
import utility.ScreenPosition;


public class Options
{	
	
	private final int CLIENT = 2;
	
	//OPTIONS MENU OBJECTS===========================
	
	private Image bgOptions;
	private Image checkBox;
	private Image checkmark;

	private ArrayList<Image>checkBoxes = new ArrayList<Image>(); //The boxes for turning decks on and off,as well as the max. player option
	private ArrayList<Image>checkmarks = new ArrayList<Image>();
	
	private ArrayList<Boolean>toggles = new ArrayList<Boolean>();
	
	private ArrayList<AreaHighlight> areas = new ArrayList<AreaHighlight>();
	
	private int maxPlayers;
	
	private boolean negativeBonus;
	
	private TextField pointsRequired;
	private TextField timeLimit;
	private TextField password;
	
	private Button optionsCancel;
	private Button optionsDone;
	
	private TrueTypeFont options1;
	
	private ScreenPosition pos = new ScreenPosition();
	
	
	//===============================================
	
	public Options(GameContainer gc)
	{
		maxPlayers = 10;
		
		try
		{
			//Load the background image(s) and resize based on the game's resolution
			bgOptions = new Image("ui/bgOptions.png");
			bgOptions = bgOptions.getScaledCopy(pos.screenX(800,gc.getWidth()), pos.screenY(900,gc.getHeight()));
			
			//Checkboxes and checkmarks
			int numDecks = 4; //This will get calculated based on files in the cards folder later. TODO

			
			//Create the graphics and mouse listeners for the deck checkboxes
			for (int i = 0; i < numDecks; i++) // because we need 1 more for the Max Players box.
			{
				//Create the checkbox graphics and store them
				checkBox = new Image("buttons/checkbox.png");
				checkBox = checkBox.getScaledCopy(pos.screenX(32,gc.getWidth()), pos.screenY(32,gc.getHeight()));
				checkBoxes.add(checkBox);
				
				//Mouse over areas
				if (i < 4)
				{
					AreaHighlight area = new AreaHighlight(pos.screenX(730, gc.getWidth()),pos.screenY(286 + (60 * i), gc.getHeight()),
							pos.screenX(32,gc.getWidth()), pos.screenY(32,gc.getHeight()));	
					areas.add(area);
				
				}
				
				//Set default checkmark states
				toggles.add(true);
				toggles.add(false);
				toggles.add(false);
				toggles.add(false);
				
				
				//Create the checkmark graphics and store them
				checkmark = new Image("buttons/checkmark.png");
				checkmark = checkmark.getScaledCopy(pos.screenX(32,gc.getWidth()), pos.screenY(32,gc.getHeight()));
				checkmarks.add(checkmark);
			}
			
			//Create a checkbox graphic for the Max Players
			checkBox = new Image("buttons/checkbox.png");
			checkBox = checkBox.getScaledCopy(pos.screenX(32,gc.getWidth()), pos.screenY(32,gc.getHeight()));
			checkBoxes.add(checkBox);
			
			//Create the mouseOVerAreas for Max Players		
			for (int i = 0; i < 9; i++)
			{
				AreaHighlight area = new AreaHighlight(pos.screenX(808 + (45*i), gc.getWidth()),pos.screenY(577, gc.getHeight()),
						pos.screenX(32,gc.getWidth()), pos.screenY(32,gc.getHeight()));	
				areas.add(area);
			}
			
			
			//Create a checkbox graphic for the Negative Bonus
			checkBox = new Image("buttons/checkbox.png");
			checkBox = checkBox.getScaledCopy(pos.screenX(32,gc.getWidth()), pos.screenY(32,gc.getHeight()));
			checkBoxes.add(checkBox);
			
			//Create the checkmark graphic
			checkmark = new Image("buttons/checkmark.png");
			checkmark = checkmark.getScaledCopy(pos.screenX(32,gc.getWidth()), pos.screenY(32,gc.getHeight()));
			checkmarks.add(checkmark);
			
			//Mouse over area for negative bonus
			AreaHighlight area = new AreaHighlight(pos.screenX(1120, gc.getWidth()),pos.screenY(728, gc.getHeight()),
					pos.screenX(32,gc.getWidth()), pos.screenY(32,gc.getHeight()));	
			areas.add(area);
			
			//Add the toggle for it
			toggles.add(true);
			
			FontMaster fm = new FontMaster();
			
			//These are used to keep the font sizes consistent at every resolution
			float sizeFloat;
			int fontSize; 
		
			float fontSizeMultiplier = gc.getWidth()/1920f;
			
			sizeFloat = 18f * fontSizeMultiplier;
			fontSize = (int)sizeFloat;
			options1 = fm.createFont("Arial", Font.BOLD, fontSize);
			
			//Points textfield
			pointsRequired = new TextField(gc, options1, pos.screenX(760, gc.getWidth()), pos.screenY(660, gc.getHeight()), pos.screenX(60, gc.getWidth()), pos.screenY(22, gc.getHeight()));
			pointsRequired.setBackgroundColor(Color.white);
			pointsRequired.setTextColor(Color.black);
			pointsRequired.setText("10");
			
			timeLimit = new TextField(gc, options1, pos.screenX(786, gc.getWidth()), pos.screenY(800, gc.getHeight()), pos.screenX(46, gc.getWidth()), pos.screenY(22, gc.getHeight()));
			timeLimit.setBackgroundColor(Color.white);
			timeLimit.setTextColor(Color.black);
			timeLimit.setText("60");
			
			password = new TextField(gc, options1, pos.screenX(860, gc.getWidth()), pos.screenY(930, gc.getHeight()), pos.screenX(350, gc.getWidth()), pos.screenY(22, gc.getHeight()));
			password.setBackgroundColor(Color.white);
			password.setTextColor(Color.black);
			
			optionsCancel = new Button(gc,"buttons/yellowButtonOff.png",pos.screenX(456,gc.getWidth()), pos.screenY(100,gc.getHeight()), pos.screenX(140,gc.getWidth()),pos.screenY(42,gc.getHeight()), "CANCEL");
			optionsDone = new Button(gc,"buttons/yellowButtonOff.png",pos.screenX(1100,gc.getWidth()), pos.screenY(100,gc.getHeight()), pos.screenX(140,gc.getWidth()),pos.screenY(42,gc.getHeight()), "DONE");

			//Disable edit ability for clients:
			if (TitleScreen.netStatus == CLIENT)
			{
				pointsRequired.setAcceptingInput(false);
				timeLimit.setAcceptingInput(false);
			}
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}
	}

	
	/**Method to return the value of bgOptions
	 * @return the bgOptions
	 */
	public Image getBgOptions()
	{
		return bgOptions;
	}

	
	/**Method to return the value of bgOptions
	 * @param bgOptions the bgOptions to set
	 */
	public void setBgOptions(Image bgOptions)
	{
		this.bgOptions = bgOptions;
	}

	
	/**Method to return the value of checkBox
	 * @return the checkBox
	 */
	public Image getCheckBox()
	{
		return checkBox;
	}

	
	/**Method to return the value of checkBox
	 * @param checkBox the checkBox to set
	 */
	public void setCheckBox(Image checkBox)
	{
		this.checkBox = checkBox;
	}

	
	/**Method to return the value of checkmark
	 * @return the checkmark
	 */
	public Image getCheckmark()
	{
		return checkmark;
	}

	
	/**Method to return the value of checkmark
	 * @param checkmark the checkmark to set
	 */
	public void setCheckmark(Image checkmark)
	{
		this.checkmark = checkmark;
	}

	
	/**Method to return the value of checkBoxes
	 * @return the checkBoxes
	 */
	public ArrayList<Image> getCheckBoxes()
	{
		return checkBoxes;
	}

	
	/**Method to return the value of checkBoxes
	 * @param checkBoxes the checkBoxes to set
	 */
	public void setCheckBoxes(ArrayList<Image> checkBoxes)
	{
		this.checkBoxes = checkBoxes;
	}

	
	/**Method to return the value of checkmarks
	 * @return the checkmarks
	 */
	public ArrayList<Image> getCheckmarks()
	{
		return checkmarks;
	}

	
	/**Method to return the value of checkmarks
	 * @param checkmarks the checkmarks to set
	 */
	public void setCheckmarks(ArrayList<Image> checkmarks)
	{
		this.checkmarks = checkmarks;
	}

	
	/**Method to return the value of pointsRequired
	 * @return the pointsRequired
	 */
	public TextField getPointsRequired()
	{
		return pointsRequired;
	}

	
	/**Method to return the value of pointsRequired
	 * @param pointsRequired the pointsRequired to set
	 */
	public void setPointsRequired(TextField pointsRequired)
	{
		this.pointsRequired = pointsRequired;
	}

	
	/**Method to return the value of timeLimit
	 * @return the timeLimit
	 */
	public TextField getTimeLimit()
	{
		return timeLimit;
	}

	
	/**Method to return the value of timeLimit
	 * @param timeLimit the timeLimit to set
	 */
	public void setTimeLimit(TextField timeLimit)
	{
		this.timeLimit = timeLimit;
	}

	
	/**Method to return the value of password
	 * @return the password
	 */
	public TextField getPassword()
	{
		return password;
	}

	
	/**Method to return the value of password
	 * @param password the password to set
	 */
	public void setPassword(TextField password)
	{
		this.password = password;
	}

	
	/**Method to return the value of optionsCancel
	 * @return the optionsCancel
	 */
	public Button getOptionsCancel()
	{
		return optionsCancel;
	}

	
	/**Method to return the value of optionsCancel
	 * @param optionsCancel the optionsCancel to set
	 */
	public void setOptionsCancel(Button optionsCancel)
	{
		this.optionsCancel = optionsCancel;
	}

	
	/**Method to return the value of optionsDone
	 * @return the optionsDone
	 */
	public Button getOptionsDone()
	{
		return optionsDone;
	}

	
	/**Method to return the value of optionsDone
	 * @param optionsDone the optionsDone to set
	 */
	public void setOptionsDone(Button optionsDone)
	{
		this.optionsDone = optionsDone;
	}

	
	/**Method to return the value of options1
	 * @return the options1
	 */
	public TrueTypeFont getOptions1()
	{
		return options1;
	}

	
	/**Method to return the value of options1
	 * @param options1 the options1 to set
	 */
	public void setOptions1(TrueTypeFont options1)
	{
		this.options1 = options1;
	}


	
	/**Method to return the value of areas
	 * @return the areas
	 */
	public ArrayList<AreaHighlight> getAreas()
	{
		return areas;
	}


	
	/**Method to return the value of areas
	 * @param areas the areas to set
	 */
	public void setAreas(ArrayList<AreaHighlight> areas)
	{
		this.areas = areas;
	}


	
	/**Method to return the value of toggles
	 * @return the toggles
	 */
	public ArrayList<Boolean> getToggles()
	{
		return toggles;
	}


	
	/**Method to return the value of toggles
	 * @param toggles the toggles to set
	 */
	public void setToggles(ArrayList<Boolean> toggles)
	{
		this.toggles = toggles;
	}


	
	/**Method to return the value of maxPlayers
	 * @return the maxPlayers
	 */
	public int getMaxPlayers()
	{
		return maxPlayers;
	}


	
	/**Method to return the value of maxPlayers
	 * @param maxPlayers the maxPlayers to set
	 */
	public void setMaxPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}


	
	/**Method to return the value of negativeBonus
	 * @return the negativeBonus
	 */
	public boolean isNegativeBonus()
	{
		return negativeBonus;
	}


	
	/**Method to return the value of negativeBonus
	 * @param negativeBonus the negativeBonus to set
	 */
	public void setNegativeBonus(boolean negativeBonus)
	{
		this.negativeBonus = negativeBonus;
	}
	
	
	
}
