package scw.mvc;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.instance.InstanceUtils;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.http.server.HttpServiceHandler;
import scw.http.server.HttpServiceHandlerAccept;
import scw.http.server.ServerHttpAsyncControl;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.json.JSONUtils;
import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.action.Action;
import scw.mvc.action.ActionInterceptor;
import scw.mvc.action.ActionInterceptorChain;
import scw.mvc.action.ActionLookup;
import scw.mvc.action.ActionParameters;
import scw.mvc.exception.ExceptionHandler;
import scw.mvc.output.DefaultHttpControllerOutput;
import scw.mvc.output.HttpControllerOutput;
import scw.net.message.converter.MessageConverter;
import scw.result.Result;
import scw.util.MultiIterable;
import scw.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE, value = HttpServiceHandler.class)
public class HttpControllerHandler implements HttpServiceHandler, HttpServiceHandlerAccept {
	private static Logger logger = LoggerUtils.getLogger(HttpControllerHandler.class);
	protected final LinkedList<ActionLookup> actionLookups = new LinkedList<ActionLookup>();
	protected final LinkedList<ActionInterceptor> actionInterceptor = new LinkedList<ActionInterceptor>();
	protected final LinkedList<HttpControllerOutput> httpControllerOutputs = new LinkedList<HttpControllerOutput>();
	private final ExceptionHandler exceptionHandler;
	private final HttpChannelFactory httpChannelFactory;

	public HttpControllerHandler(HttpChannelFactory httpChannelFactory, ExceptionHandler exceptionHandler) {
		this.httpChannelFactory = httpChannelFactory;
		this.exceptionHandler = exceptionHandler;
	}

	public HttpControllerHandler(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		if (beanFactory.isInstance(HttpChannelFactory.class)) {
			httpChannelFactory = beanFactory.getInstance(HttpChannelFactory.class);
		} else {
			if (ClassUtils.isPresent("scw.mvc.servlet.DefaultServletHttpChannelFactory")) {
				httpChannelFactory = InstanceUtils.INSTANCE_FACTORY
						.getInstance("scw.mvc.servlet.DefaultServletHttpChannelFactory", beanFactory);
			} else {
				httpChannelFactory = new DefaultHttpChannelFactory(beanFactory);
			}
		}

		this.exceptionHandler = beanFactory.isInstance(ExceptionHandler.class)
				? beanFactory.getInstance(ExceptionHandler.class) : null;

		this.actionInterceptor
				.addAll(InstanceUtils.getConfigurationList(ActionInterceptor.class, beanFactory, propertyFactory));

		this.actionLookups.addAll(InstanceUtils.getConfigurationList(ActionLookup.class, beanFactory, propertyFactory));

		httpControllerOutputs
				.addAll(InstanceUtils.getConfigurationList(HttpControllerOutput.class, beanFactory, propertyFactory));
		List<MessageConverter> messageConverters = InstanceUtils.getConfigurationList(MessageConverter.class,
				beanFactory, propertyFactory);
		DefaultHttpControllerOutput output = new DefaultHttpControllerOutput();
		output.getMessageConverter().addAll(messageConverters);
		httpControllerOutputs.add(output);
	}

	public boolean accept(ServerHttpRequest request) {
		return getAction(request) != null;
	}

	private Action getAction(ServerHttpRequest request) {
		Object value = request.getAttribute(Action.class.getName());
		if (value != null && value instanceof Action) {
			return (Action) value;
		}

		for (ActionLookup actionLookup : actionLookups) {
			Action action = actionLookup.lookup(request);
			if (action != null) {
				if (value != null) {
					request.setAttribute(Action.class.getName(), action);
				}
				return action;
			}
		}
		return null;
	}

	public void doHandle(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		Action action = getAction(request);
		if (action == null) {
			// 不应该到这里的，因为accept里面已经判断过了
			throw new NotSupportedException(request.toString());
		}

		HttpChannel httpChannel = httpChannelFactory.create(request, response);
		try {
			@SuppressWarnings("unchecked")
			MultiIterable<ActionInterceptor> filters = new MultiIterable<ActionInterceptor>(actionInterceptor,
					action.getActionInterceptors());
			ActionParameters parameters = new ActionParameters();
			Object message;
			try {
				message = new ActionInterceptorChain(filters.iterator()).intercept(httpChannel, action, parameters);
			} catch (Throwable e) {
				message = doError(httpChannel, action, e);
			}

			if (message == null) {
				return;
			}

			if (message != null && logger.isErrorEnabled() && message instanceof Result
					&& ((Result) message).isError()) {
				logger.error("fail:{}, result={}", httpChannel.toString(), JSONUtils.toJSONString(message));
			}

			for (HttpControllerOutput httpControllerOutput : httpControllerOutputs) {
				if (httpControllerOutput.canWrite(httpChannel, message)) {
					if (logger.isTraceEnabled()) {
						logger.trace("{} output adapter is body:{}, channel:{}", httpControllerOutput, message,
								httpChannel.toString());
					}
					httpControllerOutput.write(httpChannel, message);
					return;
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("not support output body:{}, channel:{}", message, httpChannel.toString());
			}
		} finally {
			if (!httpChannel.isCompleted()) {
				if (request.isSupportAsyncControl()) {
					ServerHttpAsyncControl asyncControl = request.getAsyncControl(response);
					if (asyncControl.isStarted()) {
						asyncControl.addListener(new HttpChannelAsyncListener(httpChannel));
						return;
					}
				}

				try {
					BeanUtils.destroy(httpChannel);
				} catch (Exception e) {
					logger.error(e, "destroy channel error: {}", httpChannel.toString());
				}
			}
		}
	}

	protected Object doError(HttpChannel httpChannel, Action action, Throwable error) throws IOException {
		logger.error(error, httpChannel.toString());
		if (exceptionHandler != null) {
			return exceptionHandler.doHandle(httpChannel, action, error);
		}
		httpChannel.getResponse().sendError(500, "system error");
		return null;
	}
}
