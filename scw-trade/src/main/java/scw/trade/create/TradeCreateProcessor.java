package scw.trade.create;

import java.util.ArrayList;

import scw.beans.BeanFactory;
import scw.lang.Nullable;
import scw.trade.TradeException;

/**
 * 订单创建处理器
 * @author shuchaowen
 *
 */
public class TradeCreateProcessor extends ArrayList<TradeCreateAdapter> implements TradeCreateAdapter{
	private static final long serialVersionUID = 1L;

	public TradeCreateProcessor(){
		super();
	}
	
	public TradeCreateProcessor(BeanFactory beanFactory){
		addAll(beanFactory.getServiceLoader(TradeCreateAdapter.class).toList());
	}
	
	@Nullable
	public TradeCreateAdapter getAdapter(String tradeMethod){
		for(TradeCreateAdapter adapter : this){
			if(adapter.isAccept(tradeMethod)){
				return adapter;
			}
		}
		return null;
	}
	
	@Override
	public boolean isAccept(String tradeMethod) {
		for(TradeCreateAdapter adapter : this){
			if(adapter.isAccept(tradeMethod)){
				return true;
			}
		}
		return false;
	}

	@Override
	public TradeCreateResponse create(TradeCreate tradeCreate)
			throws TradeException {
		for(TradeCreateAdapter adapter : this){
			if(adapter.isAccept(tradeCreate.getTradeMethod())){
				return adapter.create(tradeCreate);
			}
		}
		throw new TradeException("not supported create [" + tradeCreate + "]");
	}

}
