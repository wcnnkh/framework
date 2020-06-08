package scw.mvc;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.core.utils.XUtils;
import scw.http.server.HttpServiceHandler;
import scw.http.server.ServerHttpAsyncControl;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.http.server.cors.CorsUtils;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.action.Action;
import scw.mvc.action.ActionFilter;
import scw.mvc.action.ActionLookup;
import scw.mvc.action.ActionWrapper;
import scw.mvc.action.DefaultNotfoundActionService;
import scw.mvc.action.NotFoundActionService;
import scw.mvc.exception.ExceptionHandler;
import scw.mvc.output.DefaultHttpControllerOutput;
import scw.mvc.output.HttpControllerOutput;
import scw.net.InetUtils;
import scw.net.message.converter.MessageConverter;
import scw.result.Result;
import scw.value.property.PropertyFactory;

public class HttpControllerHandler implements HttpServiceHandler {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	protected final LinkedList<ActionLookup> actionLookups = new LinkedList<ActionLookup>();
	protected final LinkedList<ActionFilter> actionFilters = new LinkedList<ActionFilter>();
	private JSONSupport jsonSupport = JSONUtils.getJsonSupport();
	protected final LinkedList<HttpControllerOutput> httpControllerOutputs = new LinkedList<HttpControllerOutput>();

	private final ExceptionHandler exceptionHandler;
	private final NotFoundActionService notFoundActionService;
	protected final BeanFactory beanFactory;

	public HttpControllerHandler(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		this.beanFactory = beanFactory;
		this.exceptionHandler = beanFactory.isInstance(ExceptionHandler.class)
				? beanFactory.getInstance(ExceptionHandler.class) : null;

		this.notFoundActionService = beanFactory.isInstance(NotFoundActionService.class)
				? beanFactory.getInstance(NotFoundActionService.class) : new DefaultNotfoundActionService();

		this.actionFilters.addAll(InstanceUtils.getConfigurationList(ActionFilter.class, beanFactory, propertyFactory));

		this.actionLookups.addAll(InstanceUtils.getConfigurationList(ActionLookup.class, beanFactory, propertyFactory));

		httpControllerOutputs
				.addAll(InstanceUtils.getConfigurationList(HttpControllerOutput.class, beanFactory, propertyFactory));
		List<MessageConverter> messageConverters = InstanceUtils.getConfigurationList(MessageConverter.class,
				beanFactory, propertyFactory);
		DefaultHttpControllerOutput output = new DefaultHttpControllerOutput();
		output.getMessageConverter().addAll(messageConverters);
		httpControllerOutputs.add(output);
	}

	public final JSONSupport getJsonSupport() {
		return jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	public boolean accept(ServerHttpRequest request) {
		return true;
	}

	public void doHandle(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		HttpChannel httpChannel = createHttpChannel(request, response);
		try {
			Action action = null;
			for (ActionLookup actionLookup : actionLookups) {
				action = actionLookup.lookup(httpChannel);
				if (action != null) {
					break;
				}
			}

			Object message;
			if (action == null) {
				if (CorsUtils.isPreFlightRequest(request)) {
					return;
				}

				try {
					message = notFoundActionService.notfound(httpChannel);
				} catch (Throwable e) {
					message = doError(httpChannel, action, e);
				}
			} else {
				try {
					message = new FitlerAction(action).doAction(httpChannel);
				} catch (Throwable e) {
					message = doError(httpChannel, action, e);
				}
			}

			if (message == null) {
				return;
			}

			if (message != null && httpChannel.getLogger().isErrorEnabled() && message instanceof Result
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
					XUtils.destroy(httpChannel);
				} catch (Exception e) {
					logger.error(e, "destroy channel error: {}", httpChannel.toString());
				}
			}
		}
	}

	protected HttpChannel createHttpChannel(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		if (InetUtils.isJsonMessage(request)) {
			return new JsonHttpChannel<ServerHttpRequest, ServerHttpResponse>(beanFactory, jsonSupport, request,
					response);
		} else {
			return new FormHttpChannel<ServerHttpRequest, ServerHttpResponse>(beanFactory, jsonSupport, request,
					response);
		}
	}

	protected Object doError(HttpChannel httpChannel, Action action, Throwable error) throws IOException {
		httpChannel.getLogger().error(error, httpChannel.toString());
		if (exceptionHandler != null) {
			return exceptionHandler.doHandle(httpChannel, action, error);
		}
		httpChannel.getResponse().sendError(500, "system error");
		return null;
	}

	private final class FitlerAction extends ActionWrapper {
		private Iterator<ActionFilter> iterator = actionFilters.iterator();

		public FitlerAction(Action action) {
			super(action);
		}

		@Override
		public Object doAction(HttpChannel httpChannel) throws Throwable {
			if (iterator.hasNext()) {
				return iterator.next().doFilter(this, httpChannel);
			}

			return super.doAction(httpChannel);
		}
	}
}
