package io.basc.framework.mvc;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.context.Destroy;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.parameter.DefaultParameterDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mvc.security.UserSessionFactoryAdapter;
import io.basc.framework.mvc.security.UserSessionResolver;
import io.basc.framework.mvc.view.View;
import io.basc.framework.security.session.UserSession;
import io.basc.framework.security.session.UserSessionFactory;
import io.basc.framework.util.Decorator;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.value.Value;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.RequestBeanFactory;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.message.WebMessagelConverterException;

import java.io.IOException;

public class DefaultHttpChannel extends RequestBeanFactory implements HttpChannel, Destroy, Decorator {
	private static Logger logger = LoggerFactory.getLogger(DefaultHttpChannel.class);
	private final long createTime;
	private boolean completed = false;
	private final ServerHttpResponse response;
	private final BeanFactory beanFactory;

	public DefaultHttpChannel(BeanFactory beanFactory, ServerHttpRequest request, ServerHttpResponse response,
			WebMessageConverter messageConverter) {
		super(request, messageConverter, beanFactory);
		this.createTime = System.currentTimeMillis();
		this.beanFactory = beanFactory;
		this.response = response;
	}

	public void write(TypeDescriptor type, Object body) throws WebMessagelConverterException, IOException {
		if (body == null) {
			return;
		}

		if (body instanceof View) {
			((io.basc.framework.mvc.view.View) body).render(this);
			return;
		}

		getMessageConverters().write(type, body, getRequest(), response);
	}

	public boolean isCompleted() {
		return completed;
	}

	public void destroy() {
		if (isCompleted()) {
			return;
		}

		completed = true;
		if (logger.isTraceEnabled()) {
			logger.trace("destroy channel: {}", toString());
		}

		super.destroy();
	}

	public final long getCreateTime() {
		return createTime;
	}

	public final Value getValue(String name) {
		ParameterDescriptor parameterDescriptor = new DefaultParameterDescriptor(name, Value.class);
		try {
			return (Value) getMessageConverters().read(parameterDescriptor, getRequest());
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

	public UserSessionResolver getUserSessionResolver() {
		return getService(UserSessionResolver.class);
	}

	@SuppressWarnings("unchecked")
	public <T> T getUid(Class<T> type) {
		T uid = (T) getRequest().getAttribute(UID_ATTRIBUTE);
		if (uid != null) {
			return uid;
		}

		UserSessionResolver userSessionResolver = getUserSessionResolver();
		if (userSessionResolver == null) {
			return null;
		}

		uid = userSessionResolver.getUid(this, type);
		if (uid != null) {
			getRequest().setAttribute(UID_ATTRIBUTE, uid);
		}
		return uid;
	}

	public String getSessionId() {
		String sessionId = (String) getRequest().getAttribute(SESSIONID_ATTRIBUTE);
		if (sessionId != null) {
			return sessionId;
		}

		UserSessionResolver userSessionResolver = getUserSessionResolver();
		if (userSessionResolver == null) {
			return null;
		}

		sessionId = userSessionResolver.getSessionId(this);
		if (sessionId != null) {
			getRequest().setAttribute(SESSIONID_ATTRIBUTE, sessionId);
		}
		return sessionId;
	}

	@SuppressWarnings("unchecked")
	private <T> T getService(Class<T> type) {
		T service = (T) getRequest().getAttribute(type.getName());
		if (service != null) {
			return service;
		}

		if (beanFactory.isInstance(type)) {
			service = beanFactory.getInstance(type);
		}

		if (service != null) {
			getRequest().setAttribute(type.getName(), service);
		}
		return service;
	}

	@SuppressWarnings("unchecked")
	public <T> UserSessionFactory<T> getUserSessionFactory(Class<T> type) {
		UserSessionFactory<T> userSessionFactory = (UserSessionFactory<T>) getRequest()
				.getAttribute(UserSessionFactory.class.getName());
		if (userSessionFactory != null) {
			return userSessionFactory;
		}

		UserSessionFactoryAdapter userSessionFactoryAdapter = getService(UserSessionFactoryAdapter.class);
		if (userSessionFactoryAdapter != null) {
			userSessionFactory = userSessionFactoryAdapter.getUserSessionFactory(type);
			if (userSessionFactory != null) {
				getRequest().setAttribute(UserSessionFactory.class.getName(), userSessionFactory);
			}
		}

		if (userSessionFactory == null) {
			userSessionFactory = getService(UserSessionFactory.class);
		}

		if (userSessionFactory == null) {
			logger.error("Not support user session factory: {}", this.toString());
		}
		return userSessionFactory;
	}

	@SuppressWarnings("unchecked")
	public <T> UserSession<T> getUserSession(Class<T> type) {
		UserSession<T> userSession = (UserSession<T>) getRequest().getAttribute(UserSession.class.getName());
		if (userSession != null) {
			return userSession;
		}

		T uid = getUid(type);
		if (uid == null) {
			return null;
		}

		String sessionId = getSessionId();
		if (StringUtils.isEmpty(sessionId)) {
			return null;
		}

		UserSessionFactory<T> userSessionFactory = getUserSessionFactory(type);
		if (userSessionFactory == null) {
			return null;
		}

		userSession = userSessionFactory.getUserSession(uid, sessionId);
		if (userSession != null) {
			getRequest().setAttribute(UserSession.class.getName(), userSession);
		}
		return userSession;
	}

	public <T> UserSession<T> createUserSession(Class<T> type, T uid, String sessionId) {
		if (uid == null || type == null || StringUtils.isEmpty(sessionId)) {
			throw new IllegalArgumentException();
		}

		UserSessionFactory<T> userSessionFactory = getUserSessionFactory(type);
		if (userSessionFactory == null) {
			return null;
		}

		UserSession<T> userSession = userSessionFactory.getUserSession(uid, sessionId, true);
		if (userSession != null) {
			getRequest().setAttribute(UID_ATTRIBUTE, uid);
			getRequest().setAttribute(SESSIONID_ATTRIBUTE, sessionId);
			getRequest().setAttribute(UserSession.class.getName(), userSession);
		}
		return userSession;
	}
}
