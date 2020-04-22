package scw.mvc.action.notfound.adapter;

import scw.mvc.Channel;

public abstract class AbstractNotFoundAdapter<T extends Channel> implements
		NotFoundAdapter {

	@SuppressWarnings("unchecked")
	public Object notfound(Channel channel) throws Throwable {
		return notfoundInternal((T) channel);
	}

	protected abstract T notfoundInternal(T channel) throws Throwable;
}
