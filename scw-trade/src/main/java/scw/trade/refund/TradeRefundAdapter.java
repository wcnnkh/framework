package scw.trade.refund;

import scw.trade.TradeAdapter;
import scw.trade.TradeException;

public interface TradeRefundAdapter extends TradeAdapter{
	boolean refund(TradeRefund request) throws TradeException;
}
