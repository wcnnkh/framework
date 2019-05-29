package scw.utils.ali.pay;

/**
 * 支付宝交易
 * @author shuchaowen
 *
 */
public enum TradeStatus {
	TRADE_CLOSED,
	TRADE_FINISHED, 
	TRADE_SUCCESS, 
	WAIT_BUYER_PAY;

	TradeStatus() {
	}
}
