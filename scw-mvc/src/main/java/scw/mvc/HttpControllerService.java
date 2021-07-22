package scw.mvc;

import java.io.IOException;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.context.annotation.Provider;
import scw.core.Ordered;
import scw.core.annotation.AnnotationUtils;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.lang.NotSupportedException;
import scw.logger.Levels;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.action.Action;
import scw.mvc.action.ActionInterceptor;
import scw.mvc.action.ActionInterceptorChain;
import scw.mvc.action.ActionManager;
import scw.mvc.action.ActionParameters;
import scw.mvc.annotation.Jsonp;
import scw.mvc.exception.ExceptionHandler;
import scw.net.message.convert.DefaultMessageConverters;
import scw.net.message.convert.MessageConverters;
import scw.util.MultiIterable;
import scw.util.XUtils;
import scw.web.HttpService;
import scw.web.ServerHttpAsyncControl;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.jsonp.JsonpUtils;
import scw.web.pattern.ServerHttpRequestAccept;

@Provider(order = Ordered.LOWEST_PRECEDENCE, value = HttpService.class)
public class HttpControllerService implements HttpService, ServerHttpRequestAccept {
	private static Logger logger = LoggerFactory.getLogger(HttpControllerService.class);
	
	protected final LinkedList<ActionInterceptor> actionInterceptor = new LinkedList<ActionInterceptor>();
	private JSONSupport jsonSupport;
	private final MessageConverters messageConverters;
	private final ExceptionHandler exceptionHandler;
	private final HttpChannelFactory httpChannelFactory;
	protected final BeanFactory beanFactory;
	private ActionManager actionManager;

	public HttpControllerService(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		if (beanFactory.isInstance(HttpChannelFactory.class)) {
			httpChannelFactory = beanFactory.getInstance(HttpChannelFactory.class);
		} else {
			httpChannelFactory = new DefaultHttpChannelFactory(beanFactory);
		}

		this.actionManager = beanFactory.getInstance(ActionManager.class);
		this.exceptionHandler = beanFactory.isInstance(ExceptionHandler.class)
				? beanFactory.getInstance(ExceptionHandler.class)
				: null;

		for (ActionInterceptor actionInterceptor : beanFactory.getServiceLoader(ActionInterceptor.class)) {
			this.actionInterceptor.add(actionInterceptor);
		}

		messageConverters = new DefaultMessageConverters(beanFactory.getEnvironment().getConversionService(),
				beanFactory);
	}

	public MessageConverters getMessageConverters() {
		return messageConverters;
	}

	public JSONSupport getJsonSupport() {
		return jsonSupport == null ? JSONUtils.getJsonSupport() : jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	public boolean accept(ServerHttpRequest request) {
		return getAction(request) != null;
	}

	private Action getAction(ServerHttpRequest request) {
		Action action = MVCUtils.getAction(request);
		if (action == null) {
			action = actionManager.getAction(request);
			MVCUtils.setAction(request, action);
		}
		return action;
	}

	@Override
	public void service(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		Action action = getAction(request);
		if (action == null) {
			// 不应该到这里的，因为accept里面已经判断过了
			throw new NotSupportedException(request.toString());
		}
		String requestLogId = MVCUtils.getRequestLogId(request);
		if(requestLogId == null) {
			requestLogId = XUtils.getUUID();
			MVCUtils.setRequestLogId(request, requestLogId);
		}
		
		Levels level = MVCUtils.getActionLoggerLevel(action);
		if(logger.isLoggable(level.getValue())) {
			logger.log(level.getValue(), "[{}] request: {}", requestLogId, request);
		}
		
		ServerHttpRequest requestToUse = request;
		ServerHttpResponse responseToUse = response;

		// jsonp支持
		Jsonp jsonp = AnnotationUtils.getAnnotation(Jsonp.class, action.getDeclaringClass(), action);
		if (jsonp != null && jsonp.value()) {
			responseToUse = JsonpUtils.wrapper(requestToUse, responseToUse);
		}

		HttpChannel httpChannel = httpChannelFactory.create(requestToUse, responseToUse);
		HttpChannelDestroy httpChannelDestroy = new HttpChannelDestroy(httpChannel);
		MultiIterable<ActionInterceptor> filters = new MultiIterable<ActionInterceptor>(actionInterceptor,
				action.getActionInterceptors());
		try {
			ActionParameters parameters = new ActionParameters();
			Object message;
			try {
				message = new ActionInterceptorChain(filters.iterator()).intercept(httpChannel, action, parameters);
			} catch (Throwable e) {
				logger.error(e, httpChannel.toString());
				message = doError(httpChannel, action, e, httpChannelDestroy);
			}

			try {
				httpChannel.write(action.getReturnType(), message);
			} finally {
				if(logger.isLoggable(level.getValue())) {
					logger.log(level.getValue(), "Execution {}ms of [{}] response: {}", System.currentTimeMillis() - httpChannel.getCreateTime(), requestLogId, message);
				}
			}
		} finally {
			if (!httpChannel.isCompleted()) {
				if (requestToUse.isSupportAsyncControl()) {
					ServerHttpAsyncControl asyncControl = requestToUse.getAsyncControl(responseToUse);
					if (asyncControl.isStarted()) {
						asyncControl.addListener(httpChannelDestroy);
						return;
					}
				}

				httpChannelDestroy.destroy();
			}
			responseToUse.close();
		}
	}

	protected Object doError(HttpChannel httpChannel, Action action, Throwable error,
			HttpChannelDestroy httpChannelDestroy) throws IOException {
		if (exceptionHandler != null) {
			return exceptionHandler.doHandle(httpChannel, action, error);
		}
		httpChannel.getResponse().sendError(500, "system error");
		return null;
	}
}
