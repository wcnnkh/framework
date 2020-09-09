package scw.mvc;

import java.io.IOException;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.InstanceUtils;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.http.HttpUtils;
import scw.http.MediaType;
import scw.http.server.HttpServiceHandler;
import scw.http.server.HttpServiceHandlerAccept;
import scw.http.server.ServerHttpAsyncControl;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.http.server.jsonp.JsonpUtils;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.lang.NotSupportedException;
import scw.logger.Level;
import scw.mvc.action.Action;
import scw.mvc.action.ActionInterceptor;
import scw.mvc.action.ActionInterceptorChain;
import scw.mvc.action.ActionLookup;
import scw.mvc.action.ActionParameters;
import scw.mvc.annotation.Jsonp;
import scw.mvc.annotation.ResultFactory;
import scw.mvc.exception.ExceptionHandler;
import scw.mvc.view.View;
import scw.net.FileMimeTypeUitls;
import scw.net.InetUtils;
import scw.net.MimeType;
import scw.net.message.Entity;
import scw.net.message.InputMessage;
import scw.net.message.Text;
import scw.net.message.converter.MessageConverter;
import scw.net.message.converter.MultiMessageConverter;
import scw.result.Result;
import scw.util.MultiIterable;
import scw.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE, value = HttpServiceHandler.class)
public class HttpControllerHandler implements HttpServiceHandler, HttpServiceHandlerAccept {
	protected final LinkedList<ActionLookup> actionLookups = new LinkedList<ActionLookup>();
	protected final LinkedList<ActionInterceptor> actionInterceptor = new LinkedList<ActionInterceptor>();
	private JSONSupport jsonSupport = JSONUtils.getJsonSupport();
	private final MultiMessageConverter messageConverter = new MultiMessageConverter();
	private final ExceptionHandler exceptionHandler;
	private final HttpChannelFactory httpChannelFactory;
	protected final BeanFactory beanFactory;

	public HttpControllerHandler(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		this.beanFactory = beanFactory;
		if (beanFactory.isInstance(HttpChannelFactory.class)) {
			httpChannelFactory = beanFactory.getInstance(HttpChannelFactory.class);
		} else {
			httpChannelFactory = new DefaultHttpChannelFactory(beanFactory);
		}

		this.exceptionHandler = beanFactory.isInstance(ExceptionHandler.class)
				? beanFactory.getInstance(ExceptionHandler.class) : null;

		this.actionInterceptor
				.addAll(InstanceUtils.getConfigurationList(ActionInterceptor.class, beanFactory, propertyFactory));
		this.actionLookups.addAll(InstanceUtils.getConfigurationList(ActionLookup.class, beanFactory, propertyFactory));
		messageConverter
				.addAll(InstanceUtils.getConfigurationList(MessageConverter.class, beanFactory, propertyFactory));
	}

	public MultiMessageConverter getMessageConverter() {
		return messageConverter;
	}

	public JSONSupport getJsonSupport() {
		return jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
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

		ServerHttpRequest requestToUse = request;
		ServerHttpResponse responseToUse = response;

		// jsonp支持
		Jsonp jsonp = AnnotationUtils.getAnnotation(Jsonp.class, action.getSourceClass(), action.getAnnotatedElement());
		if (jsonp != null && jsonp.value()) {
			responseToUse = JsonpUtils.wrapper(requestToUse, responseToUse);
		}

		HttpChannel httpChannel = httpChannelFactory.create(requestToUse, responseToUse);
		HttpChannelDestroy httpChannelDestroy = new HttpChannelDestroy(httpChannel);
		if (!MVCUtils.isLoggerEnable(action)) {
			// 禁用error级别以下的日志
			httpChannelDestroy.setDisableLevel(Level.ERROR);
		}

		try {
			@SuppressWarnings("unchecked")
			MultiIterable<ActionInterceptor> filters = new MultiIterable<ActionInterceptor>(actionInterceptor,
					action.getActionInterceptors());
			ActionParameters parameters = new ActionParameters();
			Object message;
			try {
				message = new ActionInterceptorChain(filters.iterator()).intercept(httpChannel, action, parameters);
			} catch (Throwable e) {
				httpChannelDestroy.setError(e);
				message = doError(httpChannel, action, e);
			}

			httpChannelDestroy.setResponseBody(message);
			doResponse(httpChannel, action, message);
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

	protected Object doError(HttpChannel httpChannel, Action action, Throwable error) throws IOException {
		if (exceptionHandler != null) {
			return exceptionHandler.doHandle(httpChannel, action, error);
		}
		httpChannel.getResponse().sendError(500, "system error");
		return null;
	}

	protected void doResponse(HttpChannel httpChannel, Action action, Object message) throws IOException {
		if (message == null) {
			return;
		}

		ResultFactory resultFactory = AnnotationUtils.getAnnotation(ResultFactory.class, action.getSourceClass(),
				action.getAnnotatedElement());
		if (!(message instanceof Result) && resultFactory != null && resultFactory.enable()) {
			Result result = beanFactory.getInstance(resultFactory.value()).success(message);
			writeTextBody(httpChannel, result, MediaType.APPLICATION_JSON);
			return;
		}

		if (httpChannel.getResponse().getContentType() == null) {
			httpChannel.getResponse().setContentType(MediaType.TEXT_HTML);
		}

		if (message instanceof View) {
			((View) message).render(httpChannel);
			return;
		} else if (message instanceof InputMessage) {
			InetUtils.writeHeader((InputMessage) message, httpChannel.getResponse());
			IOUtils.write(((InputMessage) message).getBody(), httpChannel.getResponse().getBody());
		} else if (message instanceof Text) {
			writeTextBody(httpChannel, ((Text) message).getTextContent(), ((Text) message).getMimeType());
		} else if (message instanceof Resource) {
			Resource resource = (Resource) message;
			MimeType mimeType = FileMimeTypeUitls.getMimeType(resource);
			HttpUtils.writeStaticResource(httpChannel.getRequest(), httpChannel.getResponse(), resource, mimeType);
		} else if (message instanceof Entity) {
			@SuppressWarnings("rawtypes")
			Entity entity = (Entity) message;
			if (getMessageConverter().canWrite(entity.getBody(), entity.getContentType())) {
				InetUtils.writeHeader(entity, httpChannel.getResponse());
				getMessageConverter().write(entity.getBody(), entity.getContentType(), httpChannel.getResponse());
			}
		} else {
			if ((message instanceof String) || (ClassUtils.isPrimitiveOrWrapper(message.getClass()))) {
				writeTextBody(httpChannel, message, MediaType.TEXT_HTML);
			} else {
				writeTextBody(httpChannel, message, MediaType.APPLICATION_JSON);
			}
		}
	}

	protected void writeTextBody(HttpChannel httpChannel, Object body, MimeType contentType) throws IOException {
		if (contentType != null) {
			httpChannel.getResponse().setContentType(contentType);
		}

		String text = getJsonSupport().toJSONString(body);
		httpChannel.getResponse().getWriter().write(text);
	}
}
