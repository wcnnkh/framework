package scw.mvc;

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
import scw.mvc.annotation.Attribute;
import scw.mvc.annotation.BigDecimalMultiply;
import scw.mvc.annotation.DateFormat;
import scw.mvc.annotation.RequestBean;
import scw.mvc.annotation.RequestBody;
import scw.mvc.beans.ChannelBeanFactory;
import scw.mvc.beans.DefaultChannelBeanFactory;
import scw.mvc.parameter.RequestBodyParse;
import scw.util.value.DefaultValueDefinition;
import scw.util.value.SimpleValueFactory;
import scw.util.value.StringValue;
import scw.util.value.Value;

public abstract class AbstractChannel extends SimpleValueFactory implements Channel, Destroy {
	private final long createTime;
	private final JSONSupport jsonSupport;
	private final ChannelBeanFactory channelBeanFactory;
	private boolean completed = false;

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
		return (Enumeration<String>) (attributeMap == null ? Collections.emptyEnumeration()
				: Collections.enumeration(attributeMap.keySet()));
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

		attributeMap = null;
		XUtils.destroy(channelBeanFactory);
		getResponse().flush();
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
		String value = getStringValue(key);
		if (value == null) {
			return null;
		}
		return parseValue(value);
	}

	protected abstract String getStringValue(String key);

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
	protected Object getObjectSupport(String key, Class<?> type) {
		if (type.isArray()) {
			return getArray(key, type.getComponentType());
		}

		// 不可以被实例化且不存在无参的构造方法
		if (!ReflectionUtils.isInstance(type, true)) {
			return getBean(type);
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
		if (Request.class.isAssignableFrom(parameterDescriptor.getType())) {
			return getRequest();
		} else if (Response.class.isAssignableFrom(parameterDescriptor.getType())) {
			return getResponse();
		} else if (Channel.class.isAssignableFrom(parameterDescriptor.getType())) {
			return this;
		}

		Attribute attribute = parameterDescriptor.getAnnotatedElement().getAnnotation(Attribute.class);
		if (attribute != null) {
			return getAttribute(attribute.value());
		}

		RequestBody requestBody = parameterDescriptor.getAnnotatedElement().getAnnotation(RequestBody.class);
		if (requestBody != null) {
			RequestBodyParse requestBodyParse = getBean(requestBody.value());
			try {
				return requestBodyParse.requestBodyParse(this, getJsonSupport(), parameterDescriptor);
			} catch (Exception e) {
				throw ParameterException.createError(parameterDescriptor.getName(), e);
			}
		}

		RequestBean requestBean = parameterDescriptor.getAnnotatedElement().getAnnotation(RequestBean.class);
		if (requestBean != null) {
			return StringUtils.isEmpty(requestBean.value()) ? getBean(parameterDescriptor.getType().getName())
					: getBean(requestBean.value());
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
}
