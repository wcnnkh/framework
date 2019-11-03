package scw.mvc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.core.Destroy;
import scw.core.annotation.ParameterName;
import scw.core.parameter.ParameterConfig;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.NumberUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.core.utils.XUtils;
import scw.json.JSONParseSupport;
import scw.mvc.annotation.BigDecimalMultiply;
import scw.mvc.annotation.RequestBean;
import scw.mvc.parameter.ParameterFilter;

public abstract class AbstractChannel implements Channel, Destroy {
	private final long createTime;
	private volatile Map<String, Object> beanMap;
	protected final BeanFactory beanFactory;
	protected final Collection<ParameterFilter> parameterFilters;
	protected final JSONParseSupport jsonParseSupport;

	public AbstractChannel(BeanFactory beanFactory, Collection<ParameterFilter> parameterFilters,
			JSONParseSupport jsonParseSupport) {
		this.createTime = System.currentTimeMillis();
		this.beanFactory = beanFactory;
		this.parameterFilters = parameterFilters;
		this.jsonParseSupport = jsonParseSupport;
	}

	public void destroy() {
		if (beanMap == null) {
			return;
		}

		List<String> idList = new ArrayList<String>(beanMap.keySet());
		ListIterator<String> iterator = idList.listIterator(idList.size());
		while (iterator.hasPrevious()) {
			String name = iterator.previous();
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
			if (beanDefinition == null) {
				continue;
			}

			Object bean = beanMap.get(name);
			if (bean == null) {
				continue;
			}

			try {
				beanDefinition.destroy(bean);
			} catch (Exception e) {
				getLogger().error(e, "销毁bean异常：" + name);
			}
		}
	}

	public final <T> T getBean(Class<T> type) {
		return getBean(type.getName());
	}

	@SuppressWarnings("unchecked")
	public final <T> T getBean(String name) {
		BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
		if (beanDefinition == null) {
			return null;
		}

		if (beanDefinition.isSingleton()) {
			getLogger().warn("请求参数中不应该存在一个单例对象[{}],除非你确定要使用它!", name);
			if (ReflectUtils.isInstance(beanDefinition.getType(), false)) {
				Constructor<?> constructor = MVCUtils.getModelConstructor(beanDefinition.getType());
				if (constructor == null) {
					return null;
				}

				return (T) MVCUtils.getBean(beanFactory, beanDefinition, this, constructor, parameterFilters);
			} else {
				return beanFactory.getInstance(beanDefinition.getId());
			}
		}

		Object bean = beanMap == null ? null : beanMap.get(beanDefinition.getId());
		if (bean == null) {
			if (!ReflectUtils.isInstance(beanDefinition.getType(), false)) {
				synchronized (this) {
					bean = beanMap == null ? null : beanMap.get(beanDefinition.getId());
					if (bean == null) {
						bean = beanFactory.getInstance(beanDefinition.getId());

						if (beanMap == null) {
							beanMap = new LinkedHashMap<String, Object>(8);
						}
						beanMap.put(beanDefinition.getId(), bean);
					}
				}
			} else {
				Constructor<?> constructor = MVCUtils.getModelConstructor(beanDefinition.getType());
				if (constructor == null) {
					return null;
				}

				synchronized (this) {
					bean = beanMap == null ? null : beanMap.get(beanDefinition.getId());
					if (bean == null) {
						bean = MVCUtils.getBean(beanFactory, beanDefinition, this, constructor, parameterFilters);
						if (beanMap == null) {
							beanMap = new LinkedHashMap<String, Object>(8);
						}
						beanMap.put(beanDefinition.getId(), bean);
					}
				}
			}
		}
		return (T) bean;
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

	public Object getParameter(ParameterConfig parameterConfig) {
		if (Channel.class.isAssignableFrom(parameterConfig.getType())) {
			return this;
		}

		String name = getParameterName(parameterConfig);
		if (StringUtils.isEmpty(name)) {
			return getObject(parameterConfig.getGenericType());
		}

		RequestBean requestBean = parameterConfig.getAnnotation(RequestBean.class);
		if (requestBean != null) {
			return StringUtils.isEmpty(requestBean.value()) ? getBean(parameterConfig.getType().getName())
					: getBean(requestBean.value());
		}

		BigDecimalMultiply bigDecimalMultiply = parameterConfig.getAnnotation(BigDecimalMultiply.class);
		if (bigDecimalMultiply != null) {
			return bigDecimalMultiply(name, parameterConfig, bigDecimalMultiply);
		}

		return XUtils.getValue(this, name, parameterConfig.getGenericType());
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

	public String getParameterName(ParameterConfig parameterConfig) {
		String name = parameterConfig.getName();
		ParameterName parameterName = parameterConfig.getAnnotation(ParameterName.class);
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

	public BigInteger getBigInteger(String name) {
		return StringUtils.parseBigInteger(getString(name));
	}

	public BigDecimal getBigDecimal(String name) {
		return StringUtils.parseBigDecimal(getString(name));
	}

	public Class<?> getClass(String data) {
		return StringUtils.parseClass(getString(data));
	}

	public final <T> T getObject(Class<T> type) {
		// 不可以被实例化且不存在无参的构造方法
		if (!ReflectUtils.isInstance(type, true)) {
			return getBean(type);
		}

		return getObjectIsNotBean(type);
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(Type type) {
		if (TypeUtils.isClass(type)) {
			return getObject((Class<T>) type);
		}

		return (T) getObject(TypeUtils.toClass(type));
	}

	protected <T> T getObjectIsNotBean(Class<T> type) {
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
		if (TypeUtils.isClass(type)) {
			return getObject(name, (Class<?>) type);
		}

		return getObject(name, TypeUtils.toClass(type));
	}

	@SuppressWarnings("rawtypes")
	public Enum<?> getEnum(String name, Class<? extends Enum> enumType) {
		return StringUtils.parseEnum(getString(name), enumType);
	}
}
