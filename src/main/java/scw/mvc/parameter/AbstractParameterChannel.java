package scw.mvc.parameter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import scw.beans.BeanFactory;
import scw.core.annotation.DefaultValue;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.NumberUtils;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.core.utils.XUtils;
import scw.json.JsonSupport;
import scw.lang.ParameterException;
import scw.mvc.AbstractChannel;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.Request;
import scw.mvc.Response;
import scw.mvc.annotation.BigDecimalMultiply;
import scw.mvc.annotation.DateFormat;
import scw.mvc.annotation.RequestBean;
import scw.mvc.annotation.RequestBody;

public abstract class AbstractParameterChannel extends AbstractChannel implements ParameterChannel {
	protected final JsonSupport jsonParseSupport;

	public AbstractParameterChannel(BeanFactory beanFactory, JsonSupport jsonParseSupport) {
		super(beanFactory);
		this.jsonParseSupport = jsonParseSupport;
	}

	public Object getParameter(ParameterConfig parameterConfig) {
		if (Request.class.isAssignableFrom(parameterConfig.getType())) {
			return getRequest();
		} else if (Response.class.isAssignableFrom(parameterConfig.getType())) {
			return getResponse();
		} else if (Channel.class.isAssignableFrom(parameterConfig.getType())) {
			return this;
		}

		RequestBody requestBody = parameterConfig.getAnnotation(RequestBody.class);
		if (requestBody != null) {
			RequestBodyParse requestBodyParse = getBean(requestBody.value());
			try {
				return requestBodyParse.requestBodyParse(this, jsonParseSupport, parameterConfig);
			} catch (Exception e) {
				throw ParameterException.createError(parameterConfig.getName());
			}
		}

		RequestBean requestBean = parameterConfig.getAnnotation(RequestBean.class);
		if (requestBean != null) {
			return StringUtils.isEmpty(requestBean.value()) ? getBean(parameterConfig.getType().getName())
					: getBean(requestBean.value());
		}

		String name = ParameterUtils.getParameterName(parameterConfig);
		BigDecimalMultiply bigDecimalMultiply = parameterConfig.getAnnotation(BigDecimalMultiply.class);
		if (bigDecimalMultiply != null) {
			return bigDecimalMultiply(name, parameterConfig, bigDecimalMultiply);
		}

		DateFormat dateFormat = parameterConfig.getAnnotation(DateFormat.class);
		if (dateFormat != null) {
			return dateFormat(dateFormat, parameterConfig, name);
		}

		Object value = XUtils.getValue(this, name, parameterConfig.getGenericType());
		if(value == null){
			DefaultValue defaultValue = parameterConfig.getAnnotation(DefaultValue.class);
			if(defaultValue != null){
				return StringParse.defaultParse(defaultValue.value(), parameterConfig.getGenericType());
			}
		}
		return value;
	}
	
	public Object dateFormat(DateFormat dateFormat, ParameterConfig parameterConfig, String name){
		String value = getString(name);
		if (TypeUtils.isString(parameterConfig.getType())) {
			return StringUtils.isEmpty(value) ? value
					: new SimpleDateFormat(dateFormat.value()).format(StringUtils.parseLong(value));
		}

		long time = 0;
		if (StringUtils.isEmpty(value)) {
			SimpleDateFormat format = new SimpleDateFormat(dateFormat.value());
			try {
				time = format.parse(value).getTime();
			} catch (ParseException e) {
				throw new ParameterException(value);
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
		throw new ParameterException("not support type [" + parameterConfig.getType() + "]");
	}

	public Object bigDecimalMultiply(String name, ParameterConfig parameterConfig,
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

	protected void parameterError(Exception e, String key, String v) {
		getLogger().error("参数解析错误key={},value={}", key, v);
	}

	public Byte getByte(String key) {
		String v = getString(key);
		if (StringUtils.isEmpty(v)) {
			return null;
		}

		try {
			return StringUtils.parseByte(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return null;
	}

	public byte getByteValue(String key) {
		String v = getString(key);
		try {
			return StringUtils.parseByte(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return 0;
	}

	public Short getShort(String key) {
		String v = getString(key);
		if (StringUtils.isEmpty(v)) {
			return null;
		}

		try {
			return StringUtils.parseShort(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return null;
	}

	public short getShortValue(String key) {
		String v = getString(key);
		try {
			return StringUtils.parseShort(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return 0;
	}

	public Integer getInteger(String key) {
		String v = getString(key);
		if (StringUtils.isEmpty(v)) {
			return null;
		}

		try {
			return StringUtils.parseInt(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return null;
	}

	public int getIntValue(String key) {
		String v = getString(key);
		try {
			return StringUtils.parseInt(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return 0;
	}

	public Long getLong(String key) {
		String v = getString(key);
		if (StringUtils.isEmpty(v)) {
			return null;
		}

		try {
			return StringUtils.parseLong(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return null;
	}

	public long getLongValue(String key) {
		String v = getString(key);
		try {
			return StringUtils.parseLong(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return 0;
	}

	public Boolean getBoolean(String key) {
		String v = getString(key);
		if (StringUtils.isEmpty(v)) {
			return null;
		}

		try {
			return StringUtils.parseBoolean(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return null;
	}

	public boolean getBooleanValue(String key) {
		String v = getString(key);
		try {
			return StringUtils.parseBoolean(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return false;
	}

	public Float getFloat(String key) {
		String v = getString(key);
		if (StringUtils.isEmpty(v)) {
			return null;
		}

		try {
			return StringUtils.parseFloat(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return null;
	}

	public float getFloatValue(String key) {
		String v = getString(key);
		try {
			return StringUtils.parseFloat(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return 0;
	}

	public Double getDouble(String key) {
		String v = getString(key);
		if (StringUtils.isEmpty(v)) {
			return null;
		}

		try {
			return StringUtils.parseDouble(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return null;
	}

	public double getDoubleValue(String key) {
		String v = getString(key);
		try {
			return StringUtils.parseDouble(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return 0;
	}

	public char getChar(String key) {
		String v = getString(key);
		try {
			return StringUtils.parseChar(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return 0;
	}

	public Character getCharacter(String key) {
		String v = getString(key);
		if (StringUtils.isEmpty(v)) {
			return null;
		}

		try {
			return StringUtils.parseChar(v);
		} catch (Exception e) {
			parameterError(e, key, v);
		}
		return null;
	}

	public BigInteger getBigInteger(String name) {
		return StringUtils.parseBigInteger(getString(name));
	}

	public BigDecimal getBigDecimal(String name) {
		return StringUtils.parseBigDecimal(getString(name));
	}

	public Class<?> getClass(String data) {
		return StringUtils.parseClass(getString(data));
	}

	/**
	 * 此方法不处理爱ValueFactory管理的其他类型
	 */
	public final Object getObject(String name, Class<?> type) {
		// 不可以被实例化且不存在无参的构造方法
		if (!ReflectionUtils.isInstance(type, true)) {
			return getBean(type);
		}

		return getObjectIsNotBean(name, type);
	}

	protected Object getObjectIsNotBean(String name, Class<?> type) {
		return MVCUtils.getParameterWrapper(this, type, name);
	}

	public Object getObject(String name, Type type) {
		if (TypeUtils.isClass(type)) {
			return getObject(name, (Class<?>) type);
		}

		return getObject(name, TypeUtils.toClass(type));
	}

	@SuppressWarnings("rawtypes")
	public Enum<?> getEnum(String name, Class<? extends Enum> enumType) {
		return StringUtils.parseEnum(getString(name), enumType);
	}

	public <T> T getObject(Class<T> type) {
		return MVCUtils.getParameterWrapper(this, type, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(Type type) {
		if (TypeUtils.isClass(type)) {
			return getObject((Class<T>) type);
		}

		return (T) getObject(TypeUtils.toClass(type));
	}
}
