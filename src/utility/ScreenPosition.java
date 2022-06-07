package utility;


public class ScreenPosition
{
	public ScreenPosition()
	{
		
	}
	
	public float screenX(float xCoordIn1080p, int screenWidth)
	{
		float outputX = xCoordIn1080p / 1920f; //convert it to a decimal of 1080p
		
		//Now we know what percentage we want this position to be.
		//Now we need to multiply by our actual screen resolution to get our result
		
		outputX = outputX * screenWidth;
		
		return outputX;
	}
	
	
	public float screenY(float yCoordIn1080p, int screenHeight)
	{
		float outputY = yCoordIn1080p / 1080f; //convert it to a decimal of 1080p
		
		//Now we know what percentage we want this position to be.
		//Now we need to multiply by our actual screen resolution to get our result
		
		outputY = outputY * screenHeight;
		
		return outputY;
	}
	
	public int screenX(int xCoordIn1080p, int screenWidth)
	{
		float outputX = xCoordIn1080p / 1920f; //convert it to a decimal of 1080p
		
		//Now we know what percentage we want this position to be.
		//Now we need to multiply by our actual screen resolution to get our result
		
		outputX = (outputX * screenWidth);
		
		return (int) outputX;
	}
	
	
	public int screenY(int yCoordIn1080p, int screenHeight)
	{
		float outputY = yCoordIn1080p / 1080f; //convert it to a decimal of 1080p
		
		//Now we know what percentage we want this position to be.
		//Now we need to multiply by our actual screen resolution to get our result
		
		outputY = (outputY * screenHeight);
		
		return (int) outputY;
	}
}
