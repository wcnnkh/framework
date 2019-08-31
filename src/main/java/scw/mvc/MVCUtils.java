package scw.mvc;

import java.util.Collection;

import scw.core.context.Context;
import scw.core.context.ContextManager;
import scw.core.context.support.ThreadLocalContextManager;

public final class MVCUtils {
	private static final ContextManager MVC_CONTEXT_MANAGER = new ThreadLocalContextManager(
			true);

	public static Channel getContextChannel() {
		Context context = MVC_CONTEXT_MANAGER.getCurrentContext();
		return (Channel) (context == null ? null : context
				.getResource(Channel.class));
	}

	public static Context getContext() {
		return MVC_CONTEXT_MANAGER.getCurrentContext();
	}

	public static void service(Collection<Filter> filters, Channel channel)
			throws Throwable {
		FilterChain filterChain = new SimpleFilterChain(filters);
		Context context = MVC_CONTEXT_MANAGER.createContext();
		context.bindResource(Channel.class, channel);
		try {
			filterChain.doFilter(channel);
		} finally {
			MVC_CONTEXT_MANAGER.release(context);
		}
	}
}
