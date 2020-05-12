package scw.mvc.http;

import java.io.UnsupportedEncodingException;
import java.util.List;

import scw.beans.BeanFactory;
import scw.compatible.CompatibleUtils;
import scw.core.Constants;
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
	private final ServerHttpRequest request;
	private final ServerHttpResponse response;

	public AbstractHttpChannel(BeanFactory beanFactory,
			JSONSupport jsonParseSupport, ServerHttpRequest request,
			ServerHttpResponse response) {
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
			return new String(CompatibleUtils.getStringOperations().getBytes(value, Constants.ISO_8859_1),
					request.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return value;
		}
	}

	public String getStringValue(String name) {
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
	
	@Override
	public String[] getStringArray(String key) {
		String[] array = getRequest().getParameterValues(key);
		MultiValueMap<String, String> restfulParameterMap = MVCUtils
				.getRestfulParameterMap(this);
		if (restfulParameterMap != null) {
			List<String> values = restfulParameterMap.get(key);
			if(values != null && values.size() != 0){
				String[] newArray = new String[array == null? values.size():(array.length + values.size())];
				values.toArray(newArray);
				System.arraycopy(array, 0, newArray, values.size(), array.length);
				return newArray;
			}
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	public ServerHttpRequest getRequest() {
		return request;
	}

	@SuppressWarnings("unchecked")
	public ServerHttpResponse getResponse() {
		return response;
	}

	@Override
	public String toString() {
		StringBuilder appendable = new StringBuilder();
		appendable.append("path=").append(getRequest().getController());
		appendable.append(",method=").append(getRequest().getMethod());
		appendable.append(",").append(
				getJsonSupport().toJSONString(getRequest().getParameterMap()));
		return appendable.toString();
	}
}
