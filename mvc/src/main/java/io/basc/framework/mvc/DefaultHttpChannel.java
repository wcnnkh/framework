package io.basc.framework.mvc;

import java.io.IOException;

import io.basc.framework.context.Context;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.parameter.DefaultParameterDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mvc.security.UserSessionManager;
import io.basc.framework.mvc.view.View;
import io.basc.framework.security.login.UserToken;
import io.basc.framework.security.session.UserSession;
import io.basc.framework.util.Decorator;
import io.basc.framework.util.XUtils;
import io.basc.framework.value.Value;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

public class DefaultHttpChannel extends RequestBeanFactory implements HttpChannel, Decorator {
	private static Logger defaultLogger = LoggerFactory.getLogger(DefaultHttpChannel.class);
	private final long createTime;
	private boolean completed = false;
	private final ServerHttpResponse response;
	private final UserSessionManager userSessionManager;
	private Logger logger;

	public DefaultHttpChannel(Context context, ServerHttpRequest request, ServerHttpResponse response,
			WebMessageConverter messageConverter, UserSessionManager userSessionManager) {
		super(request, messageConverter, context);
		this.createTime = System.currentTimeMillis();
		this.response = response;
		this.userSessionManager = userSessionManager;
	}

	public Logger getLogger() {
		return logger == null ? defaultLogger : logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void write(TypeDescriptor type, Object body) throws WebMessagelConverterException, IOException {
		if (body == null) {
			return;
		}

		if (body instanceof View) {
			((io.basc.framework.mvc.view.View) body).render(this);
			return;
		}

		getMessageConverters().write(getRequest(), response, type, body);
	}

	public boolean isCompleted() {
		return completed;
	}

	public void destroy() {
		if (isCompleted()) {
			return;
		}

		completed = true;
		if (getLogger().isTraceEnabled()) {
			getLogger().trace("destroy channel: {}", toString());
		}

		super.destroy();
	}

	public final long getCreateTime() {
		return createTime;
	}

	public final Value getValue(String name) {
		ParameterDescriptor parameterDescriptor = new DefaultParameterDescriptor(name, Value.class);
		try {
			return (Value) getMessageConverters().read(getRequest(), parameterDescriptor);
		} catch (IOException e) {
			throw new WebMessagelConverterException(name, e);
		}
	}

	public <T> T getDelegate(Class<T> targetType) {
		T target = XUtils.getDelegate(getRequest(), targetType);
		if (target != null) {
			return target;
		}

		target = XUtils.getDelegate(getResponse(), targetType);
		if (target != null) {
			return target;
		}
		return null;
	}

	protected Object getExtend(ParameterDescriptor parameterDescriptor) {
		Object target = XUtils.getDelegate(this, parameterDescriptor.getType());
		if (target != null) {
			return target;
		}

		if (parameterDescriptor.getType().isInstance(this)) {
			return this;
		}

		if (UserToken.class == parameterDescriptor.getType()) {
			ResolvableType resolvableType = ResolvableType.forType(parameterDescriptor.getGenericType());
			return getUserToken(resolvableType.getGeneric(0).getRawClass());
		}

		if (UserSession.class == parameterDescriptor.getType()) {
			ResolvableType resolvableType = ResolvableType.forType(parameterDescriptor.getGenericType());
			return getUserSession(resolvableType.getGeneric(0).getRawClass());
		}
		return null;
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return getExtend(parameterDescriptor) != null || super.isAccept(parameterDescriptor);
	}

	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		Object target = getExtend(parameterDescriptor);
		if (target != null) {
			return target;
		}
		return super.getParameter(parameterDescriptor);
	}

	public ServerHttpResponse getResponse() {
		return response;
	}

	@Override
	public String toString() {
		return getRequest().toString();
	}

	@Override
	public <T> UserToken<T> getUserToken(Class<T> type) {
		return userSessionManager.read(this, type);
	}

	@Override
	public <T> UserSession<T> getUserSession(Class<T> type) {
		return userSessionManager.getUserSession(this, type);
	}

	@Override
	public <T> UserSession<T> createUserSession(T uid) {
		return userSessionManager.createUserSession(this, uid);
	}
}
