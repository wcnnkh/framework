package scw.trade.status;

import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.event.support.DefaultNamedEventDispatcher;

/**
 * 交易状态分发的默认实现
 * 
 * @author shuchaowen
 *
 */
@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class DefaultTradeStatusDispatcher extends
		DefaultNamedEventDispatcher<String, TradeResultsEvent> implements
		TradeStatusDispatcher {

	public DefaultTradeStatusDispatcher() {
		super(true);
	}

}
