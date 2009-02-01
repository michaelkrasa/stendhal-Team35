package games.stendhal.client.gui.tradingcenter;

import games.stendhal.client.PerceptionToObject;
import marauroa.common.game.RPObject;

public class TradingCenterController {

	PerceptionToObject pto = new PerceptionToObject();
	TradePanel tradePanel = new TradePanel();
	
	
	public void onAdded(RPObject object) {
		OrderPanelController orderPanelController = new OrderPanelController(object); 
		pto.register(object, orderPanelController);
		tradePanel.add(orderPanelController);
	}

	public boolean contains(RPObject object) {
		return tradePanel.contains(object);
	}

}
