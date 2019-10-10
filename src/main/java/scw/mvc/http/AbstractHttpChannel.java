package scw.mvc.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.core.reflect.ParameterConfig;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.json.JSONParseSupport;
import scw.json.JSONUtils;
import scw.mvc.AbstractParameterChannel;
import scw.mvc.MVCUtils;
import scw.mvc.ParameterFilter;
import scw.mvc.http.parameter.Body;
import scw.net.http.Cookie;
import scw.security.session.Authorization;
import scw.security.session.Session;
import scw.security.session.http.HttpChannelAuthorization;
import scw.security.session.http.HttpChannelUserSessionFactory;

public abstract class AbstractHttpChannel extends AbstractParameterChannel implements HttpChannel {
	private static final String GET_DEFAULT_CHARSET_ANME = "ISO-8859-1";

	protected static final String JSONP_CALLBACK = "callback";
	protected static final String JSONP_RESP_PREFIX = "(";
	protected static final String JSONP_RESP_SUFFIX = ");";
	protected final boolean cookieValue;
	private final HttpRequest request;
	private final HttpResponse response;
	private final String jsonp;
	private final HttpParameterRequest httpParameterRequest;

	public <R extends HttpRequest, P extends HttpResponse> AbstractHttpChannel(BeanFactory beanFactory,
			boolean logEnabled, Collection<ParameterFilter> parameterFilters, JSONParseSupport jsonParseSupport,
			boolean cookieValue, R request, P response, String jsonp) {
		super(beanFactory, logEnabled, parameterFilters, jsonParseSupport);
		this.cookieValue = cookieValue;
		this.request = request;
		this.response = response;
		this.jsonp = jsonp;
		this.httpParameterRequest = new HttpParameterRequest(request, this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getParameter(ParameterConfig parameterConfig) {
		if (HttpRequest.class.isAssignableFrom(parameterConfig.getType())) {
			return getRequest();
		} else if (HttpResponse.class.isAssignableFrom(parameterConfig.getType())) {
			return getResponse();
		} else if (Session.class == parameterConfig.getType()) {
			return getRequest().getHttpSession();
		} else if (HttpParameterRequest.class == parameterConfig.getType()) {
			return getHttpParameterRequest();
		} else if (Authorization.class == parameterConfig.getType()) {
			HttpChannelUserSessionFactory httpChannelUserSessionFactory = (HttpChannelUserSessionFactory) beanFactory
					.getInstance(HttpChannelUserSessionFactory.class);
			return new HttpChannelAuthorization(this, httpChannelUserSessionFactory);
		}
		return super.getParameter(parameterConfig);
	}

	public Object getAttribute(String name) {
		return getRequest().getAttribute(name);
	};

	public void setAttribute(String name, Object o) {
		getRequest().setAttribute(name, o);
	}

	public Enumeration<String> getAttributeNames() {
		return getRequest().getAttributeNames();
	}

	public void removeAttribute(String name) {
		getRequest().removeAttribute(name);
	}

	public String decodeGETParameter(String value) {
		if (StringUtils.containsChinese(value)) {
			return value;
		}

		try {
			return new String(value.getBytes(GET_DEFAULT_CHARSET_ANME), getRequest().getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return value;
		}
	}

	public String getString(String name) {
		String v = getRequest().getParameter(name);
		if (v == null) {
			Map<String, String> restParameterMap = MVCUtils.getRestPathParameterMap(this);
			if (restParameterMap != null) {
				v = restParameterMap.get(name);
			}
		}

		if (v == null) {
			if (cookieValue) {
				Cookie cookie = getRequest().getCookie(name, false);
				if (cookie != null) {
					v = cookie.getValue();
				}
			}
		} else {
			if ("GET".equals(getRequest().getMethod())) {
				v = decodeGETParameter(v);
			}
		}
		return v;
	}

	@SuppressWarnings("unchecked")
	public <T extends HttpRequest> T getRequest() {
		return (T) request;
	}

	@SuppressWarnings("unchecked")
	public <T extends HttpResponse> T getResponse() {
		return (T) response;
	}

	public <E> E[] getArray(String name, Class<E> type) {
		String[] values = getRequest().getParameterValues(name);
		return StringParse.DEFAULT.getArray(values, type);
	}

	protected String getJsonpCallback() {
		String callbackTag = getString(jsonp);
		return StringUtils.isEmpty(callbackTag) ? null : callbackTag;
	}

	public void write(Object obj) throws Throwable {
		MVCUtils.httpWrite(this, jsonp, jsonParseSupport, obj);
	}

	public InputStream getInputStream() throws IOException {
		return getRequest().getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return getResponse().getOutputStream();
	}

	public final Object getObject(Type type) {
		if (type instanceof Class) {
			return getObject((Class<?>) type);
		}

		Body body = getBean(Body.class);
		return jsonParseSupport.parseObject(body.getBody(), type);
	}

	public HttpParameterRequest getHttpParameterRequest() {
		return httpParameterRequest;
	}

	@Override
	public String toString() {
		StringBuilder appendable = new StringBuilder();
		appendable.append("path=").append(getRequest().getRequestPath());
		appendable.append(",method=").append(getRequest().getMethod());
		appendable.append(",").append(JSONUtils.toJSONString(getRequest().getParameterMap()));
		return appendable.toString();
	}
}
