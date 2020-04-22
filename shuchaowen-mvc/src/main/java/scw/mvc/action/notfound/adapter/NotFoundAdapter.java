package scw.mvc.action.notfound.adapter;

import scw.mvc.Channel;

public interface NotFoundAdapter {
	boolean isAdapter(Channel channel);
	
	Object notfound(Channel channel) throws Throwable;
}
