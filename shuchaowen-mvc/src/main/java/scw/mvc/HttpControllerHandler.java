package scw.mvc;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.http.server.HttpServiceHandler;
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
import scw.mvc.action.ActionService;
import scw.mvc.context.RequestContextManager;
import scw.mvc.exception.ExceptionHandler;
import scw.mvc.output.ControllerOutput;
import scw.mvc.output.DefaultControllerOutput;
import scw.net.NetworkUtils;
import scw.net.message.converter.MessageConverter;
import scw.value.property.PropertyFactory;

public class HttpControllerHandler implements ActionService, HttpServiceHandler {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	protected final LinkedList<ActionLookup> actionLookups = new LinkedList<ActionLookup>();
	protected final LinkedList<ActionFilter> actionFilters = new LinkedList<ActionFilter>();
	private JSONSupport jsonSupport = JSONUtils.getJsonSupport();
	protected final LinkedList<ControllerOutput> controllerOutputs = new LinkedList<ControllerOutput>();

	private final ExceptionHandler exceptionHandler;
	private final NotFoundService notFoundService;
	protected final BeanFactory beanFactory;

	public HttpControllerHandler(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		this.beanFactory = beanFactory;
		this.exceptionHandler = beanFactory.isInstance(ExceptionHandler.class) ? null
				: beanFactory.getInstance(ExceptionHandler.class);

		this.notFoundService = beanFactory.isInstance(NotFoundService.class) ? new DefaultNotfoundService()
				: beanFactory.getInstance(NotFoundService.class);

		this.actionFilters.addAll(InstanceUtils.getConfigurationList(ActionFilter.class, beanFactory, propertyFactory));

		this.actionLookups.addAll(InstanceUtils.getConfigurationList(ActionLookup.class, beanFactory, propertyFactory));

		controllerOutputs
				.addAll(InstanceUtils.getConfigurationList(ControllerOutput.class, beanFactory, propertyFactory));
		List<MessageConverter> messageConverters = InstanceUtils.getConfigurationList(MessageConverter.class,
				beanFactory, propertyFactory);
		DefaultControllerOutput output = new DefaultControllerOutput();
		output.getMessageConverter().addAll(messageConverters);
		controllerOutputs.add(output);
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
				message = notFoundService.notfound(httpChannel);
			} catch (Throwable e) {
				message = doError(httpChannel, action, e);
			}
		} else {
			try {
				message = doAction(httpChannel, action);
			} catch (Throwable e) {
				message = doError(httpChannel, action, e);
			}
		}

		if (message == null) {
			return;
		}

		for (ControllerOutput controllerOutput : controllerOutputs) {
			if (controllerOutput.canWrite(httpChannel, message)) {
				if (logger.isTraceEnabled()) {
					logger.trace("{} output adapter is body:{}, channel:{}", controllerOutput, message,
							httpChannel.toString());
				}
				controllerOutput.write(httpChannel, message);
				return;
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("not support output body:{}, channel:{}", message, httpChannel.toString());
		}
	}

	protected HttpChannel createHttpChannel(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		if (NetworkUtils.isJsonMessage(request)) {
			return new JsonHttpChannel<ServerHttpRequest, ServerHttpResponse>(beanFactory, jsonSupport, request,
					response);
		} else {
			return new FormHttpChannel<ServerHttpRequest, ServerHttpResponse>(beanFactory, jsonSupport, request,
					response);
		}
	}

	public Object doAction(HttpChannel httpChannel, Action action) throws Throwable {
		return RequestContextManager.doAction(httpChannel, action, new FitlerActionService(actionFilters));
	}

	protected Object doError(HttpChannel httpChannel, Action action, Throwable error) throws IOException {
		if (exceptionHandler != null) {
			return exceptionHandler.doHandle(httpChannel, action, error);
		}

		httpChannel.getLogger().error(error, httpChannel.toString());
		httpChannel.getResponse().sendError(500, "system error");
		return null;
	}

	private final class FitlerActionService implements ActionService {
		private Iterator<ActionFilter> iterator;

		public FitlerActionService(Collection<ActionFilter> filters) {
			if (filters == null) {
				this.iterator = Collections.emptyIterator();
			} else {
				this.iterator = filters.iterator();
			}
		}

		public Object doAction(HttpChannel httpChannel, Action action) throws Throwable {
			if (iterator.hasNext()) {
				return RequestContextManager.doFilter(httpChannel, action, iterator.next(), FitlerActionService.this);
			} else {
				return action.doAction(httpChannel);
			}
		}
	}
}
