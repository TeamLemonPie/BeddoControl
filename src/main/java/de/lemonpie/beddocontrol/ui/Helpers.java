package de.lemonpie.beddocontrol.ui;

import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.scene.paint.Color;

public class Helpers
{
	public static FontIcon getFontIcon(FontIconType type, int size, Color color)
	{
	    FontIcon icon = new FontIcon(type);
	    icon.setSize(size);
	    icon.setColor(color);
	    
	    return icon;
	}
}