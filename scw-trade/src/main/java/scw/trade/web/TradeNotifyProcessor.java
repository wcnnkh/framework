package scw.trade.web;

import java.util.ArrayList;

import scw.beans.BeanFactory;
import scw.http.server.ServerHttpRequest;
import scw.lang.Nullable;
import scw.trade.TradeException;
import scw.trade.status.TradeStatusDispatcher;

public class TradeNotifyProcessor extends ArrayList<TradeNotifyAdapter> implements TradeNotifyAdapter{
	private static final long serialVersionUID = 1L;

	public TradeNotifyProcessor(){
		super();
	}
	
	public TradeNotifyProcessor(BeanFactory beanFactory){
		addAll(beanFactory.getServiceLoader(TradeNotifyAdapter.class).toList());
	}
	
	@Nullable
	public TradeNotifyAdapter getAdapter(String method, String status){
		for(TradeNotifyAdapter adapter : this){
			if(adapter.isAccept(method)){
				return adapter;
			}
		}
		return null;
	}
	
	@Override
	public boolean isAccept(String tradeMethod) {
		for(TradeNotifyAdapter adapter : this){
			if(adapter.isAccept(tradeMethod)){
				return true;
			}
		}
		return false;
	}

	@Override
	public Object notify(String tradeMethod, String tradeStatus,
			ServerHttpRequest request, TradeStatusDispatcher dispatcher)
			throws TradeException {
		for(TradeNotifyAdapter adapter : this){
			if(adapter.isAccept(tradeMethod)){
				return adapter.notify(tradeMethod, tradeStatus, request, dispatcher);
			}
		}
		throw new TradeException("not supported notify tradeMethod [" + tradeMethod + "] tradeStatus [" + tradeStatus + "] request [" + request + "]");
	}

}
