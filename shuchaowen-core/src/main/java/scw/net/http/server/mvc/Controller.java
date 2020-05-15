package scw.net.http.server.mvc;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import scw.beans.BeanFactory;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.net.NetworkUtils;
import scw.net.http.server.HttpServiceHandler;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.net.http.server.mvc.action.Action;
import scw.net.http.server.mvc.action.ActionFilter;
import scw.net.http.server.mvc.action.ActionLookupManager;
import scw.net.http.server.mvc.action.ActionService;
import scw.net.http.server.mvc.context.ContextManager;
import scw.net.http.server.mvc.exception.ExceptionHandler;
import scw.net.http.server.mvc.output.Output;

public class Controller implements ActionService, HttpServiceHandler {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	protected final Collection<ActionFilter> filters;
	private final ExceptionHandler exceptionHandler;
	private final ActionLookupManager actionLookupManager;
	private final NotFoundService notFoundService;
	private final Output output;
	private final BeanFactory beanFactory;
	private JSONSupport jsonSupport = JSONUtils.getJsonSupport();

	public Controller(BeanFactory beanFactory, ActionLookupManager actionLookupManager, NotFoundService notFoundService,
			Output output, ExceptionHandler exceptionHandler, Collection<ActionFilter> filters) {
		this.exceptionHandler = exceptionHandler;
		this.actionLookupManager = actionLookupManager;
		this.notFoundService = notFoundService;
		this.output = output;
		this.filters = filters;
		this.beanFactory = beanFactory;
	}

	public boolean accept(ServerHttpRequest request) {
		return true;
	}

	public void doHandle(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		HttpChannel httpChannel = createHttpChannel(request, response);
		Action action = actionLookupManager.lookup(httpChannel);
		Object message;
		if (action == null) {
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

		if (output.canWrite(httpChannel, message)) {
			output.write(httpChannel, message);
			return;
		}

		logger.error("{} not can write {}", httpChannel.toString(), message);
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
		return ContextManager.doAction(httpChannel, action, new FitlerActionService(filters));
	}

	protected Object doError(HttpChannel httpChannel, Action action, Throwable error) throws IOException {
		if (exceptionHandler.accept(httpChannel, action, error)) {
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
				return ContextManager.doFilter(httpChannel, action, iterator.next(), FitlerActionService.this);
			} else {
				return action.doAction(httpChannel);
			}
		}
	}
}
