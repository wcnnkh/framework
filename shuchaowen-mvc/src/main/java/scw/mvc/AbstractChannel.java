package scw.mvc;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.core.Destroy;
import scw.core.annotation.DefaultValue;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.NumberUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.core.utils.XUtils;
import scw.json.JSONSupport;
import scw.lang.ParameterException;
import scw.mvc.annotation.BigDecimalMultiply;
import scw.mvc.annotation.DateFormat;
import scw.mvc.annotation.RequestBean;
import scw.mvc.annotation.RequestBody;
import scw.mvc.beans.ChannelBeanFactory;
import scw.mvc.beans.DefaultChannelBeanFactory;
import scw.mvc.parameter.RequestBodyParse;
import scw.util.value.AbstractValueFactory;
import scw.util.value.DefaultValueDefinition;
import scw.util.value.StringValue;
import scw.util.value.Value;
import scw.util.value.ValueUtils;

public abstract class AbstractChannel extends
		AbstractValueFactory<String> implements Channel, Destroy {
	private final long createTime;
	private final JSONSupport jsonSupport;
	private final ChannelBeanFactory channelBeanFactory;

	public AbstractChannel(BeanFactory beanFactory, JSONSupport jsonSupport) {
		this.createTime = System.currentTimeMillis();
		this.jsonSupport = jsonSupport;
		this.channelBeanFactory = new DefaultChannelBeanFactory(beanFactory, this);
	}

	private Map<String, Object> attributeMap;

	public Object getAttribute(String name) {
		return attributeMap == null ? null : attributeMap.get(name);
	}

	protected Map<String, Object> createAttributeMap() {
		return new LinkedHashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> getAttributeNames() {
		return (Enumeration<String>) (attributeMap == null ? Collections
				.emptyEnumeration() : Collections.enumeration(attributeMap
				.keySet()));
	}

	public void setAttribute(String name, Object o) {
		if (attributeMap == null) {
			attributeMap = createAttributeMap();
		}

		attributeMap.put(name, o);
	}

	public void removeAttribute(String name) {
		if (attributeMap == null) {
			return;
		}

		attributeMap.remove(name);
	}

	public final JSONSupport getJsonSupport() {
		return jsonSupport;
	}

	public void destroy() {
		attributeMap = null;
		XUtils.destroy(channelBeanFactory);
		try {
			getResponse().flush();
		} catch (IOException e) {
			getLogger().error(e, "flush error:{}", toString());
		}
	}

	public final <T> T getBean(Class<T> type) {
		return channelBeanFactory.getBean(type);
	}

	public final <T> T getBean(String name) {
		return channelBeanFactory.getBean(name);
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
		String value = getString(key);
		if (value == null) {
			return null;
		}
		return parseValue(value);
	}

	public abstract String getString(String key);

	public abstract String[] getStringArray(String key);

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
	public final <T> T getObject(String key, Class<? extends T> type) {
		return super.getObject(key, type);
	}

	@Override
	protected Object getObjectSupport(String key, Class<?> type) {
		if(type.isArray()){
			return getArray(key, type.getComponentType());
		}
		
		// 不可以被实例化且不存在无参的构造方法
		if (!ReflectionUtils.isInstance(type, true)) {
			return getBean(type);
		}
		
		return getObjectIsNotBean(key, type);
	}

	protected Object getObjectIsNotBean(String name, Class<?> type) {
		try {
			return ParameterUtils.createObjectByParameter(this, type, name);
		} catch (Exception e) {
			throw new ParameterException("name=" + name + ", type=" + type, e);
		}
	}
	
	public final Object getObject(String name, Type type) {
		return super.getObject(name, type);
	}
	
	// 一般情况下建议重写止方法，因为默认的实现不支持泛型
	@Override
	protected Object getObjectSupport(String key, Type type) {
		return getObjectSupport(key, TypeUtils.toClass(type));
	}

	public Object getParameter(ParameterConfig parameterConfig) {
		if (Request.class.isAssignableFrom(parameterConfig.getType())) {
			return getRequest();
		} else if (Response.class.isAssignableFrom(parameterConfig.getType())) {
			return getResponse();
		} else if (Channel.class.isAssignableFrom(parameterConfig.getType())) {
			return this;
		}

		RequestBody requestBody = parameterConfig
				.getAnnotation(RequestBody.class);
		if (requestBody != null) {
			RequestBodyParse requestBodyParse = getBean(requestBody.value());
			try {
				return requestBodyParse.requestBodyParse(this, jsonSupport,
						parameterConfig);
			} catch (Exception e) {
				throw ParameterException.createError(parameterConfig.getName(),
						e);
			}
		}

		RequestBean requestBean = parameterConfig
				.getAnnotation(RequestBean.class);
		if (requestBean != null) {
			return StringUtils.isEmpty(requestBean.value()) ? getBean(parameterConfig
					.getType().getName()) : getBean(requestBean.value());
		}

		String name = ParameterUtils.getParameterName(parameterConfig);
		BigDecimalMultiply bigDecimalMultiply = parameterConfig
				.getAnnotation(BigDecimalMultiply.class);
		if (bigDecimalMultiply != null) {
			return bigDecimalMultiply(name, parameterConfig, bigDecimalMultiply);
		}

		DateFormat dateFormat = parameterConfig.getAnnotation(DateFormat.class);
		if (dateFormat != null) {
			return dateFormat(dateFormat, parameterConfig, name);
		}

		DefaultValue defaultValue = parameterConfig
				.getAnnotation(DefaultValue.class);
		if (defaultValue != null) {
			Object value = getObject(
					name,
					parameterConfig.getType().isPrimitive() ? ClassUtils
							.resolvePrimitiveIfNecessary(parameterConfig
									.getType()) : parameterConfig
							.getGenericType());
			if (value == null) {
				return ValueUtils.parse(defaultValue.value(),
						parameterConfig.getGenericType());
			}
			return value;
		}

		return getObject(name, parameterConfig.getGenericType());
	}

	protected Object dateFormat(DateFormat dateFormat,
			ParameterConfig parameterConfig, String name) {
		String value = getString(name);
		if (TypeUtils.isString(parameterConfig.getType())) {
			return StringUtils.isEmpty(value) ? value : new SimpleDateFormat(
					dateFormat.value()).format(StringUtils.parseLong(value));
		}

		long time = 0;
		if (StringUtils.isNotEmpty(value)) {
			SimpleDateFormat format = new SimpleDateFormat(dateFormat.value());
			try {
				time = format.parse(value).getTime();
			} catch (ParseException e) {
				getLogger().error("{} format error value:{}",
						dateFormat.value(), value);
			}
		}

		if (Date.class.isAssignableFrom(parameterConfig.getType())) {
			return new Date(time);
		} else if (TypeUtils.isLong(parameterConfig.getType())) {
			return time;
		} else if (TypeUtils.isInt(parameterConfig.getType())) {
			return time / 1000;
		} else if (Calendar.class == parameterConfig.getType()) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(time);
			return calendar;
		}
		throw new ParameterException("not support type ["
				+ parameterConfig.getType() + "]");
	}

	protected Object bigDecimalMultiply(String name,
			ParameterConfig parameterConfig,
			BigDecimalMultiply bigDecimalMultiply) {
		String value = getString(name);
		if (StringUtils.isEmpty(value)) {
			return castBigDecimal(null, parameterConfig.getType());
		}

		BigDecimal a = new BigDecimal(value);
		BigDecimal b = new BigDecimal(bigDecimalMultiply.value());
		return castBigDecimal(a.multiply(b), parameterConfig.getType());
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
}
