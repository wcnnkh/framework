package scw.trade.create;

import scw.trade.TradeAdapter;
import scw.trade.TradeException;

public interface TradeCreateAdapter extends TradeAdapter{
	TradeCreateResponse create(TradeCreate request) throws TradeException;
}
