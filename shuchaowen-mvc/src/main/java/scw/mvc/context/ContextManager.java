package scw.mvc.context;

import scw.context.Context;
import scw.context.Propagation;
import scw.context.ThreadLocalDefaultContextManager;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.ActionFilterChain;
import scw.mvc.handler.HandlerChain;

public class ContextManager extends ThreadLocalDefaultContextManager {
	private static Logger logger = LoggerUtils.getLogger(ContextManager.class);
	private static ContextManager instance = new ContextManager();

	private ContextManager() {
		super();
	};

	public static ContextManager getInstance() {
		return instance;
	}

	public static void bindAction(Context context, Action action) {
		if(context.getResource(Action.class) == null){
			context.bindResource(Action.class, action);
		}else{
			logger.warn("context bind action is exist: {}", action.toString());
		}
	}

	public static Action getAction(Context context) {
		return (Action) context.getResource(Action.class);
	}

	public static void bindChannel(Context context, Channel channel) {
		if(context.getResource(Channel.class) == null){
			context.bindResource(Channel.class, channel);
		}else{
			logger.warn("context bind channel is exist: {}", channel.toString());
		}
	}

	public static Channel getChannel(Context context) {
		return (Channel) context.getResource(Channel.class);
	}

	public static Action getCurrentAction() {
		Context context = getInstance().getContext();
		return context == null ? null : getAction(context);
	}

	public static Channel getCurrentChannel() {
		Context context = getInstance().getContext();
		return context == null ? null : getChannel(context);
	}

	public static Object doFilter(Channel channel, Action action,
			ActionFilterChain chain) throws Throwable {
		return getInstance().execute(Propagation.REQUIRES_NEW,
				new FilterContextExecute(channel, action, chain));
	}
	
	public static Object doHandler(Channel channel, HandlerChain chain) throws Throwable{
		return getInstance().execute(Propagation.REQUIRES_NEW, new HandlerContextExecute(channel, chain));
	}
}
