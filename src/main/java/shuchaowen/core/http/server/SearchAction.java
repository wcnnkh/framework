package shuchaowen.core.http.server;

import java.util.Collection;

public interface SearchAction {
	public Action getAction(Request request) throws Throwable;
	
	void init(Collection<Class<?>> classList) throws Throwable;
}
