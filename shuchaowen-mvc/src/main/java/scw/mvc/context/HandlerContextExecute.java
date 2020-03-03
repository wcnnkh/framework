package scw.mvc.context;

import scw.context.Context;
import scw.context.ContextExecute;
import scw.mvc.Channel;
import scw.mvc.handler.HandlerChain;

class HandlerContextExecute implements ContextExecute<Void>{
	private Channel channel;
	private HandlerChain chain;
	
	public HandlerContextExecute(Channel channel, HandlerChain chain){
		this.channel = channel;
		this.chain = chain;
	}
	
	public Void execute(Context context) throws Throwable {
		ContextManager.bindChannel(context, channel);
		chain.doHandler(channel);
		return null;
	}

}
