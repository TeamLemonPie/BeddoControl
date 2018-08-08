package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocontrol.model.card.Card;
import javafx.scene.image.Image;
import logger.Logger;

public class ImageHandler
{
	public static Image getImageForCard(Card card)
	{
		Image image = null;
		try
		{
			String base = "/de/lemonpie/beddocontrol/cards/";
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
