package scw.trade;

import scw.lang.NestedRuntimeException;

/**
 * 交易异常
 * 
 * @author shuchaowen
 *
 */
public class TradeException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public TradeException(String msg) {
		super(msg);
	}

	public TradeException(Throwable cause) {
		super(cause);
	}

	public TradeException(String message, Throwable cause) {
		super(message, cause);
	}
}
