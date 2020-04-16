package scw.mvc.service;

import scw.context.Context;
import scw.context.ContextExecute;
import scw.mvc.Channel;

class FilterContextExecute implements ContextExecute<Object>{
	private Channel channel;
	private FilterChain chain;
	
	public FilterContextExecute(Channel channel, FilterChain chain){
		this.channel = channel;
		this.chain = chain;
	}
	
	public Object execute(Context context) throws Throwable {
		ContextManager.bindChannel(context, channel);
		return chain.doFilter(channel);
	}

}
