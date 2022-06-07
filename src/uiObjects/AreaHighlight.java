package uiObjects;

import org.newdawn.slick.Input;


public class AreaHighlight 
{

	private int x;
	private int y;
	private int width;
	private int height;

	public AreaHighlight(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	
	/**Method to return the value of x
	 * @return the x
	 */
	public int getX()
	{
		return x;
	}

	
	/**Method to return the value of x
	 * @param x the x to set
	 */
	public void setX(int x)
	{
		this.x = x;
	}

	
	/**Method to return the value of y
	 * @return the y
	 */
	public int getY()
	{
		return y;
	}

	
	/**Method to return the value of y
	 * @param y the y to set
	 */
	public void setY(int y)
	{
		this.y = y;
	}

	
	/**Method to return the value of width
	 * @return the width
	 */
	public int getWidth()
	{
		return width;
	}

	
	/**Method to return the value of width
	 * @param width the width to set
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}

	
	/**Method to return the value of height
	 * @return the height
	 */
	public int getHeight()
	{
		return height;
	}

	
	/**Method to return the value of height
	 * @param height the height to set
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	public boolean isMouseOver(Input i)
	{
		if ( (i.getMouseX() >= this.x) && ( i.getMouseX()  <= (this.x + this.width) ) &&
				(i.getMouseY() >= this.y) && ( i.getMouseY()  <= (this.y + this.height) ) )
			return true;
		
		else
			return false;
		
	}

}
