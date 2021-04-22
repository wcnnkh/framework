package scw.trade.status;

import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;

/**
 * 交易状态事件分发
 * 
 * @author shuchaowen
 *
 */
public interface TradeStatusDispatcher extends
		NamedEventDispatcher<String, TradeResultsEvent> {
	@Override
	EventRegistration registerListener(String status,
			EventListener<TradeResultsEvent> eventListener);

	@Override
	void publishEvent(String status, TradeResultsEvent event);
}
