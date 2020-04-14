package scw.mvc.http;

import java.io.UnsupportedEncodingException;

import scw.beans.BeanFactory;
import scw.core.parameter.ParameterDescriptor;
import scw.core.utils.StringUtils;
import scw.json.JSONSupport;
import scw.mvc.AbstractChannel;
import scw.mvc.MVCUtils;
import scw.mvc.http.session.HttpChannelAuthorization;
import scw.mvc.http.session.HttpChannelUserSessionFactory;
import scw.net.http.HttpMethod;
import scw.security.session.Authorization;
import scw.security.session.Session;
import scw.util.MultiValueMap;
import scw.util.ip.IP;
import scw.util.ip.SimpleIP;

public abstract class AbstractHttpChannel extends AbstractChannel implements
		HttpChannel {
	private static final String GET_DEFAULT_CHARSET_ANME = "ISO-8859-1";
	private final HttpRequest request;
	private final HttpResponse response;

	public AbstractHttpChannel(BeanFactory beanFactory,
			JSONSupport jsonParseSupport, HttpRequest request,
			HttpResponse response) {
		super(beanFactory, jsonParseSupport);
		this.request = request;
		this.response = response;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getParameter(ParameterDescriptor parameterConfig) {
		if (Session.class == parameterConfig.getType()) {
			return getRequest().getHttpSession();
		} else if (Authorization.class == parameterConfig.getType()) {
			HttpChannelUserSessionFactory httpChannelUserSessionFactory = getBean(HttpChannelUserSessionFactory.class);
			return new HttpChannelAuthorization(this,
					httpChannelUserSessionFactory);
		} else if (IP.class == parameterConfig.getType()) {
			return new SimpleIP(request.getIP());
		}
		return super.getParameter(parameterConfig);
	}

	protected String decodeGETParameter(String value) {
		if (StringUtils.containsChinese(value)) {
			return value;
		}

		try {
			return new String(value.getBytes(GET_DEFAULT_CHARSET_ANME),
					request.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return value;
		}
	}

	public String getString(String name) {
		String v = request.getParameter(name);
		if (v == null) {
			MultiValueMap<String, String> restfulParameterMap = MVCUtils
					.getRestfulParameterMap(this);
			if (restfulParameterMap != null) {
				v = restfulParameterMap.getFirst(name);
			}
		}

		if (v != null && HttpMethod.GET == request.getMethod()) {
			v = decodeGETParameter(v);
		}
		return v;
	}

	@SuppressWarnings("unchecked")
	public HttpRequest getRequest() {
		return request;
	}

	@SuppressWarnings("unchecked")
	public HttpResponse getResponse() {
		return response;
	}

	@Override
	public String toString() {
		StringBuilder appendable = new StringBuilder();
		appendable.append("path=").append(getRequest().getControllerPath());
		appendable.append(",method=").append(getRequest().getMethod());
		appendable.append(",").append(
				getJsonSupport().toJSONString(getRequest().getParameterMap()));
		return appendable.toString();
	}
}
