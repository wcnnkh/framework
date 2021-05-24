package scw.mvc;

import java.io.IOException;

import scw.beans.BeanFactory;
import scw.beans.support.ExtendBeanFactory;
import scw.context.Destroy;
import scw.convert.TypeDescriptor;
import scw.core.ResolvableType;
import scw.core.parameter.AbstractParameterFactory;
import scw.core.parameter.DefaultParameterDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterDescriptors;
import scw.core.utils.StringUtils;
import scw.instance.NoArgsInstanceFactory;
import scw.json.JSONSupport;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.security.UserSessionFactoryAdapter;
import scw.mvc.security.UserSessionResolver;
import scw.mvc.view.View;
import scw.security.session.UserSession;
import scw.security.session.UserSessionFactory;
import scw.util.Target;
import scw.util.XUtils;
import scw.value.Value;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;
import scw.web.message.WebMessageConverter;
import scw.web.message.WebMessageConverters;
import scw.web.message.WebMessagelConverterException;

public class DefaultHttpChannel extends AbstractParameterFactory implements HttpChannel, Destroy, Target {
	private static Logger logger = LoggerFactory.getLogger(DefaultHttpChannel.class);
	private final long createTime;
	private final JSONSupport jsonSupport;
	private boolean completed = false;
	private final ServerHttpRequest request;
	private final ServerHttpResponse response;
	private final ExtendBeanFactory extendBeanFactory;
	private final WebMessageConverters messageConverters;

	public DefaultHttpChannel(BeanFactory beanFactory, JSONSupport jsonSupport, ServerHttpRequest request,
			ServerHttpResponse response, WebMessageConverter messageConverter) {
		this.createTime = System.currentTimeMillis();
		this.messageConverters = new WebMessageConverters(messageConverter);
		this.jsonSupport = jsonSupport;
		this.request = request;
		this.response = response;
		this.extendBeanFactory = new ExtendBeanFactory(this, beanFactory);
	}

	public void write(TypeDescriptor type, Object body) throws WebMessagelConverterException, IOException {
		if(body == null) {
			return ;
		}
		
		if(body instanceof View) {
			((scw.mvc.view.View) body).render(this);
			return ;
		}

		getMessageConverters().write(type, body, request, response);
	}

	public final JSONSupport getJsonSupport() {
		return jsonSupport;
	}

	public boolean isCompleted() {
		return completed;
	}

	public NoArgsInstanceFactory getInstanceFactory() {
		return extendBeanFactory;
	}

	public void destroy() throws Exception {
		if (isCompleted()) {
			return;
		}

		completed = true;
		if (logger.isTraceEnabled()) {
			logger.trace("destroy channel: {}", toString());
		}

		extendBeanFactory.destroy();
	}

	public final long getCreateTime() {
		return createTime;
	}

	public final Value getValue(String name) {
		ParameterDescriptor parameterDescriptor = new DefaultParameterDescriptor(name, Value.class);
		try {
			return (Value) getMessageConverters().read(parameterDescriptor, request);
		} catch (IOException e) {
			throw new WebMessagelConverterException(name, e);
		}
	}

	@Override
	protected boolean isAccept(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) {
		return getParameter(parameterDescriptor) != null;
	}

	@Override
	protected Object getParameter(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) throws Exception {
		return getParameter(parameterDescriptor);
	}

	public <T> T getTarget(Class<T> targetType) {
		T target = XUtils.getTarget(getRequest(), targetType);
		if (target != null) {
			return target;
		}

		target = XUtils.getTarget(getResponse(), targetType);
		if (target != null) {
			return target;
		}
		return null;
	}

	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		Object target = XUtils.getTarget(this, parameterDescriptor.getType());
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
		try {
			return getMessageConverters().read(parameterDescriptor, request);
		} catch (IOException e) {
			throw new WebMessagelConverterException("[" + request + "] - " + parameterDescriptor, e);
		}
	}

	public ServerHttpRequest getRequest() {
		return request;
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

		if (extendBeanFactory.isInstance(type)) {
			service = extendBeanFactory.getInstance(type);
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
		}

		if (userSessionFactory == null && extendBeanFactory.isInstance(UserSessionFactory.class)) {
			userSessionFactory = extendBeanFactory.getInstance(UserSessionFactory.class);
		}

		if (userSessionFactory != null) {
			getRequest().setAttribute(UserSessionFactory.class.getName(), userSessionFactory);
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

	@Override
	public WebMessageConverters getMessageConverters() {
		return messageConverters;
	}
}
