package scw.trade.web;

import scw.http.server.ServerHttpRequest;
import scw.trade.TradeAdapter;
import scw.trade.TradeException;
import scw.trade.status.TradeStatusDispatcher;

public interface TradeNotifyAdapter extends TradeAdapter{

	Object notify(String tradeMethod, String tradeStatus, ServerHttpRequest request,
			TradeStatusDispatcher dispatcher) throws TradeException;
}
