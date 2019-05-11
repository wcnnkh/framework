package scw.servlet;

import java.io.IOException;
import java.lang.reflect.Parameter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import scw.core.annotation.Require;
import scw.core.exception.ParameterException;
import scw.core.utils.StringUtils;
import scw.json.JSONParseSupport;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.context.DefaultRequestBeanContext;
import scw.servlet.context.RequestBeanContext;
import scw.servlet.context.WrapperRequestBeanContext;

public abstract class Request extends HttpServletRequestWrapper {
	private boolean isDebug;
	private long createTime;
	private Response response;
	private RequestBeanContext requestBeanContext;
	private RequestBeanContext wrapperBeanContext;
	private JSONParseSupport jsonParseSupport;

	public Request(JSONParseSupport jsonParseSupport,
			RequestBeanFactory requestBeanFactory,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean isDebug)
			throws IOException {
		super(httpServletRequest);
		this.createTime = System.currentTimeMillis();
		this.isDebug = isDebug;
		this.jsonParseSupport = jsonParseSupport;
		this.response = new Response(jsonParseSupport, this,
				httpServletResponse);
		this.requestBeanContext = new DefaultRequestBeanContext(this,
				requestBeanFactory);
		this.wrapperBeanContext = new WrapperRequestBeanContext(this);
	}

	public JSONParseSupport getJsonParseSupport() {
		return jsonParseSupport;
	}

	public abstract String getString(String name);

	public abstract Byte getByte(String name);

	public abstract byte getByteValue(String name);

	public abstract Short getShort(String name);

	public abstract short getShortValue(String name);

	public abstract Integer getInteger(String name);

	public abstract int getIntValue(String name);

	public abstract Long getLong(String name);

	public abstract long getLongValue(String name);

	public abstract Boolean getBoolean(String key);

	public abstract boolean getBooleanValue(String name);

	public abstract Float getFloat(String name);

	public abstract float getFloatValue(String name);

	public abstract Double getDouble(String name);

	public abstract double getDoubleValue(String name);

	public abstract char getChar(String name);

	public abstract Character getCharacter(String name);

	public <T> T getObject(Class<T> type, String name) throws Exception {
		return wrapperBeanContext.getBean(type, name);
	}

	public final Response getResponse() {
		return response;
	}

	public final <T> T getBean(Class<T> type) {
		return requestBeanContext.getBean(type);
	}

	protected void destroy() {
		requestBeanContext.destroy();
	}

	public long getCreateTime() {
		return createTime;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected final Object get(Class<?> type, String name) throws Exception {
		Object bean = requestBeanContext.getBean(type, name);
		if (bean != null) {
			return bean;
		} else if (String.class.isAssignableFrom(type)) {
			return getString(name);
		} else if (int.class.isAssignableFrom(type)) {
			return getIntValue(name);
		} else if (Integer.class.isAssignableFrom(type)) {
			return getInteger(name);
		} else if (long.class.isAssignableFrom(type)) {
			return getLongValue(name);
		} else if (Long.class.isAssignableFrom(type)) {
			return getLong(name);
		} else if (float.class.isAssignableFrom(type)) {
			return getFloatValue(name);
		} else if (Float.class.isAssignableFrom(type)) {
			return getFloat(name);
		} else if (short.class.isAssignableFrom(type)) {
			return getShortValue(name);
		} else if (Short.class.isAssignableFrom(type)) {
			return getShort(name);
		} else if (boolean.class.isAssignableFrom(type)) {
			return getBooleanValue(name);
		} else if (Boolean.class.isAssignableFrom(type)) {
			return getBoolean(name);
		} else if (byte.class.isAssignableFrom(type)) {
			return getByteValue(name);
		} else if (Byte.class.isAssignableFrom(type)) {
			return getByte(name);
		} else if (char.class.isAssignableFrom(type)) {
			return getChar(name);
		} else if (Character.class.isAssignableFrom(type)) {
			return getCharacter(name);
		} else if (ServletRequest.class.isAssignableFrom(type)) {
			return this;
		} else if (ServletResponse.class.isAssignableFrom(type)) {
			return response;
		} else if (type.isEnum()) {
			String v = getString(name);
			return StringUtils.isEmpty(v) ? null : Enum.valueOf(
					(Class<? extends Enum>) type, v);
		} else {
			return getObject(type, name);
		}
	}

	@SuppressWarnings("unchecked")
	public final <T> T getParameter(Class<T> type, String name)
			throws Exception {
		T v = (T) getAttribute(name);
		if (v == null) {
			v = (T) get(type, name);
			if (v != null) {
				setAttribute(name, v);
			}
		}
		return v;
	}

	public Object getParameter(Parameter parameter, String name) {
		Object value;
		try {
			value = getParameter(parameter.getType(), name);
		} catch (Exception e) {
			throw new ParameterException(e, "解析参数错误name=" + name + ",type="
					+ parameter.getType().getName());
		}

		Require require = parameter.getAnnotation(Require.class);
		if (require != null && value == null) {
			throw new NullPointerException("require '" + name + "'");
		}
		return value;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public boolean isAJAX() {
		return ServletUtils.isAjaxRequest(this);
	}

	public String getIP() {
		return ServletUtils.getIP(this);
	}

	/**
	 * 从cookie中获取数据
	 * 
	 * @param name
	 *            cookie中的名字
	 * @param ignoreCase
	 *            查找时是否忽略大小写
	 * @return
	 */
	public Cookie getCookie(String name, boolean ignoreCase) {
		return ServletUtils.getCookie(this, name, ignoreCase);
	}
}
