package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocommon.model.card.Card;
import de.tobias.logger.Logger;
import javafx.scene.image.Image;

public class ImageHandler
{
	private static Image programIcon;

	public static Image getIcon()
	{
		if(programIcon == null)
		{
			programIcon = new Image("/de/lemonpie/beddocontrol/icon.png");
		}
		return programIcon;
	}

	public static Image getImageForCard(Card card)
	{
		Image image = null;
		try
		{
			String base = "public/cards/";
			if(card == null || card.equals(Card.EMPTY))
			{
				return new Image(base + "back.png");
			}

			image = new Image(base + card.getSymbol() + "-" + card.getValue() + ".png");
		}
		catch(Exception e)
		{
			Logger.error(e);
		}
		return image;
	}
}
