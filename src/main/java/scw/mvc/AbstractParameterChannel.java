package scw.mvc;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.annotation.ParameterName;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.json.JSONParseSupport;

public abstract class AbstractParameterChannel extends AbstractChannel implements ParameterChannel {
	protected final JSONParseSupport jsonParseSupport;

	public AbstractParameterChannel(BeanFactory beanFactory, boolean logEnable,
			Collection<ParameterFilter> parameterFilters, JSONParseSupport jsonParseSupport) {
		super(beanFactory, logEnable, parameterFilters);
		this.jsonParseSupport = jsonParseSupport;
	}

	public Object getParameter(ParameterDefinition parameterDefinition) {
		if (Channel.class.isAssignableFrom(parameterDefinition.getType())) {
			return this;
		}

		String name = getParameterName(parameterDefinition);
		if (StringUtils.isEmpty(name)) {
			return getObject(parameterDefinition.getGenericType());
		}

		return XUtils.getValue(this, name, parameterDefinition.getGenericType());
	}

	public String getParameterName(ParameterDefinition parameterDefinition) {
		String name = parameterDefinition.getName();
		ParameterName parameterName = parameterDefinition.getAnnotation(ParameterName.class);
		if (parameterName != null) {
			name = parameterName.value();
		}
		return name;
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

	public void log(Object format, Object... args) {
		if (isLogEnabled()) {
			getLogger().info(format, args);
		}
	}

	public BigInteger getBigInteger(String name) {
		return StringParse.DEFAULT.getBigInteger(getString(name));
	}

	public BigDecimal getBigDecimal(String name) {
		return StringParse.DEFAULT.getBigDecimal(getString(name));
	}

	public Class<?> getClass(String data) {
		return StringParse.DEFAULT.getClass(getString(data));
	}

	public final Object getObject(Class<?> type) {
		// 不可以被实例化且不存在无参的构造方法
		if (!ReflectUtils.isInstance(type, true)) {
			return getBean(type);
		}

		return getObjectIsNotBean(type);
	}

	protected Object getObjectIsNotBean(Class<?> type) {
		return MVCUtils.getParameterWrapper(this, type, null);
	}

	/**
	 * 此方法不处理爱ValueFactory管理的其他类型
	 */
	public final Object getObject(String name, Class<?> type) {
		// 不可以被实例化且不存在无参的构造方法
		if (!ReflectUtils.isInstance(type, true)) {
			return getBean(type);
		}

		return getObjectIsNotBean(name, type);
	}

	protected Object getObjectIsNotBean(String name, Class<?> type) {
		return MVCUtils.getParameterWrapper(this, type, name);
	}

	public Object getObject(String name, Type type) {
		String content = getString(name);
		if (StringUtils.isEmpty(content)) {
			return null;
		}

		return jsonParseSupport.parseObject(content, type);
	}

	@SuppressWarnings("rawtypes")
	public Enum<?> getEnum(String name, Class<? extends Enum> enumType) {
		return StringParse.DEFAULT.getEnum(getString(name), enumType);
	}

}
