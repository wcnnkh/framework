package scw.mvc.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.json.JSONParseSupport;
import scw.mvc.AbstractParameterChannel;
import scw.mvc.MVCUtils;
import scw.mvc.ParameterDefinition;
import scw.mvc.ParameterFilter;
import scw.mvc.View;
import scw.mvc.http.annotation.Json;
import scw.net.ContentType;
import scw.net.http.Cookie;
import scw.servlet.parameter.Body;

public abstract class AbstractHttpChannel<R extends HttpRequest, P extends HttpResponse>
		extends AbstractParameterChannel implements HttpChannel<R, P> {
	private static final String GET_DEFAULT_CHARSET_ANME = "ISO-8859-1";
	protected static final String JSONP_CALLBACK = "callback";
	protected static final String JSONP_RESP_PREFIX = "(";
	protected static final String JSONP_RESP_SUFFIX = ");";
	protected final boolean cookieValue;
	private final R request;
	private final P response;

	public AbstractHttpChannel(BeanFactory beanFactory, boolean logEnabled, Collection<ParameterFilter> parameterFilters, JSONParseSupport jsonParseSupport, boolean cookieValue, R request,
			P response) {
		super(beanFactory, logEnabled, parameterFilters, jsonParseSupport);
		this.cookieValue = cookieValue;
		this.request = request;
		this.response = response;
	}

	@Override
	public Object getParameter(ParameterDefinition parameterDefinition) {
		if(HttpRequest.class.isAssignableFrom(parameterDefinition.getType())){
			return getRequest();
		}else if(HttpResponse.class.isAssignableFrom(parameterDefinition.getType())){
			return getResponse();
		}
		
		String name = parameterDefinition.getName();
		Json json = parameterDefinition.getAnnotation(Json.class);
		if (json != null) {
			name = json.value();
			if (StringUtils.isEmpty(name)) {
				if ((request instanceof HttpRequest)
						&& "GET".equals(((HttpRequest) request).getMethod())) {
					return getObject(parameterDefinition.getType());
				} else {
					return jsonParseSupport.parseObject(
							getBean(Body.class).getBody(),
							parameterDefinition.getGenericType());
				}
			} else {
				return jsonParseSupport.parseObject(getString(name),
						parameterDefinition.getGenericType());
			}
		}
		
		return super.getParameter(parameterDefinition);
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

	public <E> E[] getArray(String name, Class<E> type) {
		String[] values = getRequest().getParameterValues(name);
		return StringParse.DEFAULT.getArray(values, type);
	}

	private String getJsonpCallback() {
		String callbackTag = getString(JSONP_CALLBACK);
		return StringUtils.isEmpty(callbackTag) ? null : callbackTag;
	}

	public void write(Object obj) throws Throwable {
		if (obj == null) {
			return;
		}

		String callbackTag = getJsonpCallback();
		if (callbackTag != null) {
			getResponse().getWriter().write(JSONP_CALLBACK);
			getResponse().getWriter().write(JSONP_RESP_PREFIX);
		}

		if (obj instanceof View) {
			((View) obj).reader(this);
		} else {
			String content;
			if (obj instanceof Text) {
				content = ((Text) obj).getTextContent();
				if (JSONP_CALLBACK == null) {
					getResponse().setContentType(((Text) obj).getTextContentType());
				}
			} else if ((obj instanceof String) || (ClassUtils.isPrimitiveOrWrapper(obj.getClass()))) {
				content = obj.toString();
			} else {
				content = jsonParseSupport.toJSONString(obj);
			}

			if (JSONP_CALLBACK == null) {
				if (StringUtils.isEmpty(getResponse().getContentType())) {
					getResponse().setContentType(ContentType.TEXT_HTML);
				}
			}

			getResponse().getWriter().write(content);
			if (isLogEnabled()) {
				log(content);
			}
		}

		if (callbackTag != null) {
			getResponse().setContentType(ContentType.TEXT_JAVASCRIPT);
			getResponse().getWriter().write(JSONP_RESP_SUFFIX);
		}
	}

	public InputStream getInputStream() throws IOException {
		return getRequest().getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return getResponse().getOutputStream();
	}

	public String getController() {
		return getRequest().getRequestPath();
	}

	public R getRequest() {
		return request;
	}

	public P getResponse() {
		return response;
	}
}
