package io.basc.framework.mvc;

import java.io.IOException;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotationUtils;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.mvc.action.ActionInterceptor;
import io.basc.framework.mvc.action.ActionInterceptorChain;
import io.basc.framework.mvc.action.ActionManager;
import io.basc.framework.mvc.action.ActionParameters;
import io.basc.framework.mvc.annotation.Jsonp;
import io.basc.framework.mvc.exception.ExceptionHandler;
import io.basc.framework.util.MultiIterable;
import io.basc.framework.web.HttpService;
import io.basc.framework.web.ServerHttpAsyncControl;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebUtils;
import io.basc.framework.web.jsonp.JsonpUtils;
import io.basc.framework.web.message.model.ModelAndView;
import io.basc.framework.web.message.model.ModelAndViewRegistry;
import io.basc.framework.web.pattern.HttpPattern;
import io.basc.framework.web.pattern.ServerHttpRequestAccept;

@Provider(order = Ordered.LOWEST_PRECEDENCE, value = HttpService.class)
public class HttpControllerService implements HttpService, ServerHttpRequestAccept, Configurable {
	private final ConfigurableServices<HttpChannelInterceptor> httpChannelInterceptors = new ConfigurableServices<HttpChannelInterceptor>(
			HttpChannelInterceptor.class);
	private final ConfigurableServices<ActionInterceptor> actionInterceptors = new ConfigurableServices<>(
			ActionInterceptor.class);
	private final ExceptionHandler exceptionHandler;
	private final HttpChannelFactory httpChannelFactory;
	protected final BeanFactory beanFactory;
	private ActionManager actionManager;
	private ModelAndViewRegistry modelAndViewRegistry;

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

		if (beanFactory.isInstance(ModelAndViewRegistry.class)) {
			this.modelAndViewRegistry = beanFactory.getInstance(ModelAndViewRegistry.class);
		}
		configure(beanFactory);
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		httpChannelInterceptors.configure(serviceLoaderFactory);
		actionInterceptors.configure(serviceLoaderFactory);
	}

	public ConfigurableServices<ActionInterceptor> getActionInterceptors() {
		return actionInterceptors;
	}

	public ConfigurableServices<HttpChannelInterceptor> getHttpChannelInterceptors() {
		return httpChannelInterceptors;
	}

	public ModelAndViewRegistry getModelAndViewRegistry() {
		return modelAndViewRegistry;
	}

	public void setModelAndViewRegistry(ModelAndViewRegistry modelAndViewRegistry) {
		this.modelAndViewRegistry = modelAndViewRegistry;
	}

	protected void service(HttpChannel httpChannel, TypeDescriptor returnType, HttpChannelService service)
			throws IOException {
		HttpChannelDestroy httpChannelDestroy = new HttpChannelDestroy(httpChannel);
		Object message = null;
		try {
			HttpChannelInterceptorChain httpChannelInterceptorChain = new HttpChannelInterceptorChain(
					getHttpChannelInterceptors().iterator(), service);
			message = httpChannelInterceptorChain.service(httpChannel);
			if (message != null) {
				TypeDescriptor returnTypeToUse = returnType;
				if (returnTypeToUse == null || returnTypeToUse.getType() == null) {
					returnTypeToUse = TypeDescriptor.forObject(message);
				} else if (returnTypeToUse.getType() == Object.class || returnTypeToUse.getType() == Void.class) {
					returnTypeToUse = returnTypeToUse.narrow(message);
				}
				httpChannel.write(returnTypeToUse, message);
			}
		} finally {
			if (httpChannel.getLogger().isDebugEnabled()) {
				httpChannel.getLogger().debug("Execution {}ms of [{}] response: {}",
						System.currentTimeMillis() - httpChannel.getCreateTime(),
						WebUtils.getMessageId(httpChannel.getRequest(), httpChannel.getResponse()), message);
			}

			if (!httpChannel.isCompleted()) {
				if (httpChannel.getRequest().isSupportAsyncControl()) {
					ServerHttpAsyncControl asyncControl = httpChannel.getRequest()
							.getAsyncControl(httpChannel.getResponse());
					if (asyncControl.isStarted()) {
						asyncControl.addListener(httpChannelDestroy);
						return;
					}
				}

				httpChannelDestroy.destroy();
			}
			httpChannel.getResponse().close();
		}
	}

	private HttpChannel createHttpChannel(ServerHttpRequest request, ServerHttpResponse response,
			@Nullable Action action) throws IOException {
		ServerHttpRequest requestToUse = request;
		ServerHttpResponse responseToUse = response;

		if (action != null) {
			// jsonp支持
			Jsonp jsonp = AnnotationUtils.getAnnotation(Jsonp.class, action.getSourceClass(), action);
			if (jsonp != null && jsonp.value()) {
				responseToUse = JsonpUtils.wrapper(requestToUse, responseToUse);
			}
		}

		HttpChannel httpChannel = httpChannelFactory.create(requestToUse, responseToUse);
		if (action != null) {
			httpChannel.setLogger(LoggerFactory.getLogger(action.getSourceClass().getName()));
		}

		if (httpChannel.getLogger().isDebugEnabled()) {
			httpChannel.getLogger().debug("[{}] request: {}", WebUtils.getMessageId(requestToUse, responseToUse),
					request);
		}
		return httpChannel;
	}

	private void doAction(HttpChannel channel, Action action) throws IOException {
		service(channel, action.getReturnType(), (httpChannel) -> {
			MultiIterable<ActionInterceptor> filters = new MultiIterable<ActionInterceptor>(actionInterceptors,
					action.getActionInterceptors());
			ActionParameters parameters = new ActionParameters();
			Object message;
			try {
				message = new ActionInterceptorChain(filters.iterator()).intercept(httpChannel, action, parameters);
			} catch (Throwable e) {
				httpChannel.getLogger().error(e, httpChannel.toString());
				message = doError(httpChannel, action, e);
			}

			/**
			 * @see ModelAndView
			 * @see MOdelAndViewMessageConverter
			 */
			if (action.getReturnType().getType() == Void.class && message == null) {
				Object[] args = parameters.getParameters(httpChannel, action);
				if (args != null) {
					for (Object arg : args) {
						if (arg instanceof ModelAndView) {
							message = arg;
							break;
						}
					}
				}
			}
			return message;
		});
	}

	public boolean accept(ServerHttpRequest request) {
		if (getAction(request) != null) {
			return true;
		}

		String view = getModelAndViewRegistry().process(request);
		if (view != null) {
			MVCUtils.setModelAndView(request, view);
			return true;
		}
		return false;
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
		HttpChannel httpChannel = createHttpChannel(request, response, action);
		if (action != null) {
			HttpPattern httpPattern = WebUtils.getHttpPattern(request);
			if (httpPattern != null && httpPattern.hasProduces()) {
				response.getHeaders().put(HttpHeaders.CONTENT_TYPE, httpPattern.getProduces().getRawMimeTypes());
			}
			doAction(httpChannel, action);
			return;
		}

		String path = MVCUtils.getModelAndView(request);
		if (path != null) {
			service(httpChannel, ModelAndView.TYPE_DESCRIPTOR, (channel) -> new ModelAndView(path));
			return;
		}

		// 不应该到这里的，因为accept里面已经判断过了
		throw new NotSupportedException(request.toString());
	}

	protected Object doError(HttpChannel httpChannel, Action action, Throwable error) throws IOException {
		if (exceptionHandler != null) {
			return exceptionHandler.doHandle(httpChannel, action, error);
		}
		httpChannel.getResponse().sendError(500, "system error");
		return null;
	}
}
