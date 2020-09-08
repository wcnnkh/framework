package scw.alibaba.pay;

/**
 * 支付宝交易
 * 
 * @author shuchaowen
 *
 */
public enum TradeStatus {
	TRADE_CLOSED, TRADE_FINISHED, TRADE_SUCCESS, WAIT_BUYER_PAY;

	TradeStatus() {
	}

	public static TradeStatus forName(String name) {
		TradeStatus tradeStatus = TradeStatus.valueOf(name);
		if (tradeStatus == null) {
			tradeStatus = TradeStatus.valueOf(name.toLowerCase());
		}
		return tradeStatus;
	}
}
