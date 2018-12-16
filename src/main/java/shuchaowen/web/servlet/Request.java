package shuchaowen.web.servlet;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import shuchaowen.connection.http.enums.Header;
import shuchaowen.web.servlet.bean.RequestBeanFactory;
import shuchaowen.web.servlet.context.DefaultRequestBeanContext;
import shuchaowen.web.servlet.context.RequestBeanContext;
import shuchaowen.web.servlet.context.WrapperRequestBeanContext;

public abstract class Request extends HttpServletRequestWrapper {
	private boolean isDebug;
	private long createTime;
	private Response response;
	private RequestBeanContext requestBeanContext;
	private RequestBeanContext wrapperBeanContext;

	public Request(RequestBeanFactory requestBeanFactory,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean isDebug)
			throws IOException {
		super(httpServletRequest);
		this.createTime = System.currentTimeMillis();
		this.isDebug = isDebug;
		this.response = new Response(this, httpServletResponse);
		this.requestBeanContext = new DefaultRequestBeanContext(this,
				requestBeanFactory);
		this.wrapperBeanContext = new WrapperRequestBeanContext(this);
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

	private Object get(Class<?> type, String name) throws Exception {
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
		} else {
			return getObject(type, name);
		}
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

	public boolean isDebug() {
		return isDebug;
	}

	public boolean isAJAX() {
		return "XMLHttpRequest".equals(getHeader(Header.X_Requested_With
				.getValue()));
	}

	public String getIP() {
		String ip = getHeader("x-forwarded-for");
		return ip == null ? getRequest().getRemoteAddr() : ip;
	}

	/**
	 * 可以解决1,234这种问题
	 * 
	 * @param text
	 * @return
	 */
	protected String formatNumberText(final String text) {
		if (text == null) {
			return text;
		}

		if (text.indexOf(",") != -1) {
			return text.replaceAll(",", "");
		} else {
			return text;
		}
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
		if (name == null) {
			return null;
		}

		Cookie[] cookies = getCookies();
		if (cookies == null || cookies.length == 0) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (cookie == null) {
				continue;
			}

			if (ignoreCase) {
				if (name.equalsIgnoreCase(cookie.getName())) {
					return cookie;
				}
			} else {
				if (name.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}
}
