package scw.mvc;

import scw.core.context.Context;
import scw.core.context.ContextManager;
import scw.core.context.DefaultThreadLocalContextManager;

public interface ControllerService {
	public static final ContextManager<? extends Context> CONTEXT_MANAGER = new DefaultThreadLocalContextManager();
	
	void service(Channel channel);
}
