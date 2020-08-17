package scw.mvc;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.Destroy;
import scw.compatible.CompatibleUtils;
import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.core.parameter.AbstractParameterFactory;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterDescriptors;
import scw.core.parameter.ParameterUtils;
import scw.core.parameter.RenameParameterDescriptor;
import scw.core.parameter.annotation.ParameterName;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.NumberUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.http.HttpMethod;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.json.JSONSupport;
import scw.lang.ParameterException;
import scw.logger.Level;
import scw.mapper.MapperUtils;
import scw.mapper.Mapping;
import scw.mapper.support.AbstractParameterMapping;
import scw.mvc.annotation.Attribute;
import scw.mvc.annotation.BigDecimalMultiply;
import scw.mvc.annotation.DateFormat;
import scw.mvc.annotation.IP;
import scw.mvc.annotation.RequestBean;
import scw.mvc.annotation.RequestBody;
import scw.mvc.parameter.RequestBodyParse;
import scw.net.RestfulParameterMapAware;
import scw.security.session.Session;
import scw.util.MultiValueMap;
import scw.value.AbstractStringValue;
import scw.value.EmptyValue;
import scw.value.StringValue;
import scw.value.Value;
import scw.value.property.PropertyFactory.DynamicValue;

public abstract class AbstractHttpChannel<R extends ServerHttpRequest, P extends ServerHttpResponse>
		extends AbstractParameterFactory implements HttpChannel, Destroy {
	private static final DynamicValue<Long> WARN_TIMEOUT = GlobalPropertyFactory.getInstance()
			.getDynamicValue("mvc.warn-execute-time", Long.class, 100L);
	private final long createTime;
	private final BeanFactory beanFactory;
	private final JSONSupport jsonSupport;
	private boolean completed = false;
	private final R request;
	private final P response;
	private volatile Map<String, Object> beanMap;

	public AbstractHttpChannel(BeanFactory beanFactory, JSONSupport jsonSupport, R request, P response) {
		this.createTime = System.currentTimeMillis();
		this.beanFactory = beanFactory;
		this.jsonSupport = jsonSupport;
		this.request = request;
		this.response = response;
	}

	public final JSONSupport getJsonSupport() {
		return jsonSupport;
	}

	public boolean isCompleted() {
		return completed;
	}

	protected void destroyBeans() {
		if (beanMap == null) {
			return;
		}

		List<String> idList = new ArrayList<String>(beanMap.keySet());
		ListIterator<String> iterator = idList.listIterator(idList.size());
		while (iterator.hasPrevious()) {
			String name = iterator.previous();
			BeanDefinition beanDefinition = beanFactory.getDefinition(name);
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

	private Object getBeanInstance(String id) {
		return beanMap == null ? null : beanMap.get(id);
	}

	private Map<String, Object> createBeanMap() {
		return new LinkedHashMap<String, Object>(4);
	}

	@SuppressWarnings("unchecked")
	public final <T> T getBean(String name) {
		BeanDefinition beanDefinition = beanFactory.getDefinition(name);
		if (beanDefinition == null) {
			return null;
		}

		Object bean = getBeanInstance(beanDefinition.getId());
		if (bean != null) {
			return (T) bean;
		}

		for (ParameterDescriptors parameterDescriptors : beanDefinition) {
			if (isAccept(parameterDescriptors)) {
				if (beanDefinition.isSingleton()) {
					synchronized (this) {
						bean = getBeanInstance(beanDefinition.getId());
						if (bean == null) {
							try {
								bean = beanDefinition.create(parameterDescriptors.getTypes(),
										getParameters(parameterDescriptors));
								if (beanMap == null) {
									beanMap = createBeanMap();
								}
								beanMap.put(beanDefinition.getId(), bean);
								beanDefinition.dependence(bean);
								beanDefinition.init(bean);
							} catch (Exception e) {
								throw new BeansException(beanDefinition.getId(), e);
							}
						}
					}
				} else {
					try {
						bean = beanDefinition.create(parameterDescriptors.getTypes(),
								getParameters(parameterDescriptors));
						beanDefinition.dependence(bean);
						beanDefinition.init(bean);
					} catch (Exception e) {
						throw new BeansException(beanDefinition.getId(), e);
					}
				}
				break;
			}
		}
		return (T) bean;
	}

	public void destroy() throws Exception {
		if (isCompleted()) {
			return;
		}

		completed = true;
		if (getLogger().isTraceEnabled()) {
			getLogger().trace("destroy channel: {}", toString());
		}

		destroyBeans();
		long useTime = System.currentTimeMillis() - createTime;
		Level level = useTime > getExecuteWarnTime() ? Level.WARN : Level.TRACE;
		if (getLogger().isLogEnable(level)) {
			getLogger().log(level, "execute：{}, use time:{}ms", toString(), useTime);
		}
	}

	protected long getExecuteWarnTime() {
		return WARN_TIMEOUT.getValue();
	}

	public boolean isLogEnabled() {
		return getLogger().isDebugEnabled();
	}

	public void log(Object format, Object... args) {
		if (getLogger().isDebugEnabled()) {
			getLogger().debug(format, args);
		}
	}

	public final long getCreateTime() {
		return createTime;
	}

	public final Value getValue(String name) {
		return getValue(name, EmptyValue.INSTANCE);
	}

	public final Value getValue(String name, Value defaultValue) {
		return new RequestValue(name, defaultValue == null ? EmptyValue.INSTANCE : defaultValue);
	}

	protected Value parseValue(String value) {
		return new StringValue(value);
	}

	@SuppressWarnings("unchecked")
	protected final <E> E[] parseArray(MultiValueMap<String, String> parameterMap, String name,
			Class<? extends E> type) {
		List<String> values = request.getParameterMap().get(name);
		if (CollectionUtils.isEmpty(values)) {
			return (E[]) Array.newInstance(type, 0);
		}

		Object array = Array.newInstance(type, values.size());
		for (int i = 0, len = values.size(); i < len; i++) {
			Value value = parseValue(values.get(i));
			Array.set(array, i, value.getAsObject(type));
		}

		return (E[]) array;
	}

	public <E> E[] getArray(String name, Class<? extends E> type) {
		E[] array = parseArray(request.getParameterMap(), name, type);
		E[] restfulArray = parseArray(request.getRestfulParameterMap(), name, type);
		if (restfulArray.length != 0) {
			return ArrayUtils.merge(array, restfulArray);
		}
		return (E[]) array;
	}

	private final Object getObjectSupport(String key, Class<?> type) {
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
		Mapping mapping = new AbstractParameterMapping(true, name) {

			@Override
			protected Object getValue(ParameterDescriptor parameterDescriptor) {
				return getParameter(parameterDescriptor);
			}
		};
		try {
			return MapperUtils.getMapper().mapping(type, null, mapping);
		} catch (Exception e) {
			throw new ParameterException("name=" + name + ", type=" + type, e);
		}
	}

	// 一般情况下建议重写止方法，因为默认的实现不支持泛型
	protected Object getObjectSupport(String key, Type type) {
		return getObjectSupport(key, TypeUtils.toClass(type));
	}

	@Override
	protected boolean isAccept(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) {
		return getParameter(parameterDescriptor) != null;
	}

	@Override
	protected Object getParameter(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) throws Exception {
		return getParameter(parameterDescriptor);
	}

	protected Object getParameterInternal(ParameterDescriptor parameterDescriptor) {
		Value defaultValue = ParameterUtils.getDefaultValue(parameterDescriptor);
		BigDecimalMultiply bigDecimalMultiply = parameterDescriptor.getAnnotatedElement()
				.getAnnotation(BigDecimalMultiply.class);
		if (bigDecimalMultiply != null) {
			return bigDecimalMultiply(parameterDescriptor, bigDecimalMultiply, defaultValue);
		}

		DateFormat dateFormat = parameterDescriptor.getAnnotatedElement().getAnnotation(DateFormat.class);
		if (dateFormat != null) {
			return dateFormat(dateFormat, parameterDescriptor, defaultValue);
		}

		Value value = getValue(parameterDescriptor.getName(), defaultValue);
		return value.getAsObject(parameterDescriptor.getGenericType());
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

		ParameterName parameterName = parameterDescriptor.getAnnotatedElement().getAnnotation(ParameterName.class);
		return getParameterInternal(
				(parameterName == null || StringUtils.isEmpty(parameterName.value())) ? parameterDescriptor
						: new RenameParameterDescriptor(parameterDescriptor, parameterDescriptor.getName()));
	}

	private Object dateFormat(DateFormat dateFormat, ParameterDescriptor parameterDescriptor, Value defaultValue) {
		String value = getValue(parameterDescriptor.getName(), defaultValue).getAsString();
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

	private Object bigDecimalMultiply(ParameterDescriptor parameterDescriptor, BigDecimalMultiply bigDecimalMultiply,
			Value defaultValue) {
		String value = getValue(parameterDescriptor.getName(), defaultValue).getAsString();
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

	protected final String decodeGETParameter(String value) {
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
		String v = request.getParameterMap().getFirst(name);
		if (v == null && request instanceof RestfulParameterMapAware) {
			v = request.getRestfulParameterMap().getFirst(name);
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
		appendable.append("path=").append(getRequest().getPath());
		appendable.append(",method=").append(getRequest().getMethod());
		appendable.append(",").append(getJsonSupport().toJSONString(getRequest().getParameterMap()));
		return appendable.toString();
	}

	private final class RequestValue extends AbstractStringValue {
		private final String name;

		public RequestValue(String name, Value defaultValue) {
			super(defaultValue);
			this.name = name;
		}

		public String getAsString() {
			return AbstractHttpChannel.this.getStringValue(name);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected <T> T getAsObjectNotSupport(Class<? extends T> type) {
			return (T) AbstractHttpChannel.this.getObjectSupport(name, type);
		}

		@Override
		protected Object getAsObjectNotSupport(Type type) {
			return AbstractHttpChannel.this.getObjectSupport(name, type);
		}
	}
}
