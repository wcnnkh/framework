package scw.net.http.server.mvc;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import scw.beans.BeanFactory;
import scw.compatible.CompatibleUtils;
import scw.core.Constants;
import scw.core.Destroy;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.NumberUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.core.utils.XUtils;
import scw.json.JSONSupport;
import scw.lang.ParameterException;
import scw.mapper.MapperUtils;
import scw.mapper.support.ParameterFactoryMapping;
import scw.net.http.HttpMethod;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.net.http.server.mvc.annotation.Attribute;
import scw.net.http.server.mvc.annotation.BigDecimalMultiply;
import scw.net.http.server.mvc.annotation.DateFormat;
import scw.net.http.server.mvc.annotation.IP;
import scw.net.http.server.mvc.annotation.RequestBean;
import scw.net.http.server.mvc.annotation.RequestBody;
import scw.net.http.server.mvc.beans.DefaultHttpChannelBeanManager;
import scw.net.http.server.mvc.beans.HttpChannelBeanManager;
import scw.net.http.server.mvc.parameter.RequestBodyParse;
import scw.security.session.Session;
import scw.util.MultiValueMap;
import scw.value.DefaultValueDefinition;
import scw.value.SimpleValueFactory;
import scw.value.StringValue;
import scw.value.Value;

public abstract class AbstractHttpChannel<R extends ServerHttpRequest, P extends ServerHttpResponse>
		extends SimpleValueFactory implements HttpChannel, Destroy {
	private final long createTime;
	private final JSONSupport jsonSupport;
	private final HttpChannelBeanManager httpChannelBeanManager;
	private boolean completed = false;
	private final R request;
	private final P response;

	public AbstractHttpChannel(BeanFactory beanFactory, JSONSupport jsonSupport, R request, P response) {
		this.createTime = System.currentTimeMillis();
		this.jsonSupport = jsonSupport;
		this.request = request;
		this.response = response;
		this.httpChannelBeanManager = new DefaultHttpChannelBeanManager(beanFactory, this);
	}

	public final HttpChannelBeanManager getHttpChannelBeanManager() {
		return httpChannelBeanManager;
	}

	public final JSONSupport getJsonSupport() {
		return jsonSupport;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void destroy() throws Exception {
		if (isCompleted()) {
			return;
		}

		completed = true;
		if (getLogger().isTraceEnabled()) {
			getLogger().trace("destroy channel: {}", toString());
		}

		XUtils.destroy(httpChannelBeanManager);
		getResponse().flush();
	}

	public boolean isLogEnabled() {
		return getLogger().isDebugEnabled();
	}

	public void log(Object format, Object... args) {
		if (getLogger().isDebugEnabled()) {
			getLogger().debug(format, args);
		}
	}

	public long getCreateTime() {
		return createTime;
	}

	protected Value parseValue(String value) {
		return new StringValue(value, getDefaultValue());
	}

	protected Value getDefaultValue() {
		return DefaultValueDefinition.DEFAULT_VALUE_DEFINITION;
	};

	public Value get(String key) {
		String value = getStringValue(key);
		if (value == null) {
			return null;
		}
		return parseValue(value);
	}

	public String[] getStringArray(String key) {
		String[] array = getRequest().getParameterValues(key);
		MultiValueMap<String, String> restfulParameterMap = MVCUtils.getRestfulParameterMap(this);
		if (restfulParameterMap != null) {
			List<String> values = restfulParameterMap.get(key);
			if (values != null && values.size() != 0) {
				String[] newArray = new String[array == null ? values.size() : (array.length + values.size())];
				values.toArray(newArray);
				System.arraycopy(array, 0, newArray, values.size(), array.length);
				return newArray;
			}
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	public <E> E[] getArray(String name, Class<? extends E> type) {
		String[] values = getStringArray(name);
		if (values == null) {
			return (E[]) Array.newInstance(type, 0);
		}

		Object array = Array.newInstance(type, values.length);
		for (int i = 0; i < values.length; i++) {
			Value value = parseValue(values[i]);
			Array.set(array, i, value.getAsObject(type));
		}
		return (E[]) array;
	}

	@Override
	protected Object getObjectSupport(String key, Class<?> type) {
		if (type.isArray()) {
			return getArray(key, type.getComponentType());
		}

		// 不可以被实例化且不存在无参的构造方法
		if (!ReflectionUtils.isInstance(type, true)) {
			return getHttpChannelBeanManager().getBean(type);
		}

		return getObjectIsNotBean(key, type);
	}

	protected Object getObjectIsNotBean(String name, Class<?> type) {
		ParameterFactoryMapping mapper = new ParameterFactoryMapping(this, true, name);
		try {
			return MapperUtils.getMapper().mapping(type, null, mapper);
		} catch (Exception e) {
			throw new ParameterException("name=" + name + ", type=" + type, e);
		}
	}

	// 一般情况下建议重写止方法，因为默认的实现不支持泛型
	@Override
	protected Object getObjectSupport(String key, Type type) {
		return getObjectSupport(key, TypeUtils.toClass(type));
	}

	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		if (ServerHttpRequest.class.isAssignableFrom(parameterDescriptor.getType())) {
			return getRequest();
		} else if (ServerHttpResponse.class.isAssignableFrom(parameterDescriptor.getType())) {
			return getResponse();
		} else if (HttpChannel.class.isAssignableFrom(parameterDescriptor.getType())) {
			return this;
		} else if (Session.class == parameterDescriptor.getType()) {
			return getRequest().getSession();
		}

		if (parameterDescriptor.getAnnotatedElement().getAnnotation(IP.class) != null) {
			return getRequest().getIp();
		}

		Attribute attribute = parameterDescriptor.getAnnotatedElement().getAnnotation(Attribute.class);
		if (attribute != null) {
			return getRequest().getAttribute(attribute.value());
		}

		RequestBody requestBody = parameterDescriptor.getAnnotatedElement().getAnnotation(RequestBody.class);
		if (requestBody != null) {
			RequestBodyParse requestBodyParse = getHttpChannelBeanManager().getBean(requestBody.value());
			try {
				return requestBodyParse.requestBodyParse(this, getJsonSupport(), parameterDescriptor);
			} catch (Exception e) {
				throw ParameterException.createError(parameterDescriptor.getName(), e);
			}
		}

		RequestBean requestBean = parameterDescriptor.getAnnotatedElement().getAnnotation(RequestBean.class);
		if (requestBean != null) {
			return StringUtils.isEmpty(requestBean.value())
					? getHttpChannelBeanManager().getBean(parameterDescriptor.getType().getName())
					: getHttpChannelBeanManager().getBean(requestBean.value());
		}

		BigDecimalMultiply bigDecimalMultiply = parameterDescriptor.getAnnotatedElement()
				.getAnnotation(BigDecimalMultiply.class);
		if (bigDecimalMultiply != null) {
			return bigDecimalMultiply(parameterDescriptor, bigDecimalMultiply);
		}

		DateFormat dateFormat = parameterDescriptor.getAnnotatedElement().getAnnotation(DateFormat.class);
		if (dateFormat != null) {
			return dateFormat(dateFormat, parameterDescriptor);
		}

		Value value = ParameterUtils.getDefaultValue(parameterDescriptor);
		if (value != null) {
			return value.getAsObject(parameterDescriptor.getGenericType());
		}
		return getObject(parameterDescriptor.getName(), parameterDescriptor.getGenericType());
	}

	protected Object dateFormat(DateFormat dateFormat, ParameterDescriptor parameterDescriptor) {
		String value = getString(parameterDescriptor.getName());
		if (TypeUtils.isString(parameterDescriptor.getType())) {
			return StringUtils.isEmpty(value) ? value
					: new SimpleDateFormat(dateFormat.value()).format(StringUtils.parseLong(value));
		}

		long time = 0;
		if (StringUtils.isNotEmpty(value)) {
			SimpleDateFormat format = new SimpleDateFormat(dateFormat.value());
			try {
				time = format.parse(value).getTime();
			} catch (ParseException e) {
				getLogger().error("{} format error value:{}", dateFormat.value(), value);
			}
		}

		if (Date.class.isAssignableFrom(parameterDescriptor.getType())) {
			return new Date(time);
		} else if (TypeUtils.isLong(parameterDescriptor.getType())) {
			return time;
		} else if (TypeUtils.isInt(parameterDescriptor.getType())) {
			return time / 1000;
		} else if (Calendar.class == parameterDescriptor.getType()) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(time);
			return calendar;
		}
		throw new ParameterException("not support type [" + parameterDescriptor.getType() + "]");
	}

	protected Object bigDecimalMultiply(ParameterDescriptor parameterDescriptor,
			BigDecimalMultiply bigDecimalMultiply) {
		String value = getString(parameterDescriptor.getName());
		if (StringUtils.isEmpty(value)) {
			return castBigDecimal(null, parameterDescriptor.getType());
		}

		BigDecimal a = new BigDecimal(value);
		BigDecimal b = new BigDecimal(bigDecimalMultiply.value());
		return castBigDecimal(a.multiply(b), parameterDescriptor.getType());
	}

	private Object castBigDecimal(BigDecimal bigDecimal, Class<?> type) {
		if (type == BigDecimal.class) {
			return bigDecimal;
		}

		if (type == BigInteger.class) {
			return bigDecimal == null ? null : bigDecimal.toBigInteger();
		}

		if (bigDecimal == null) {
			return type.isPrimitive() ? 0 : null;
		}

		return NumberUtils.converPrimitive(bigDecimal, type);
	}

	@Override
	protected Value getDefaultValue(String key) {
		return DefaultValueDefinition.DEFAULT_VALUE_DEFINITION;
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

	protected String getStringValue(String name) {
		String v = request.getParameter(name);
		if (v == null) {
			MultiValueMap<String, String> restfulParameterMap = MVCUtils.getRestfulParameterMap(this);
			if (restfulParameterMap != null) {
				v = restfulParameterMap.getFirst(name);
			}
		}

		if (v != null && HttpMethod.GET == request.getMethod()) {
			v = decodeGETParameter(v);
		}
		return v;
	}

	public R getRequest() {
		return request;
	}

	public P getResponse() {
		return response;
	}

	@Override
	public String toString() {
		StringBuilder appendable = new StringBuilder();
		appendable.append("path=").append(getRequest().getController());
		appendable.append(",method=").append(getRequest().getMethod());
		appendable.append(",").append(getJsonSupport().toJSONString(getRequest().getParameterMap()));
		return appendable.toString();
	}
}
