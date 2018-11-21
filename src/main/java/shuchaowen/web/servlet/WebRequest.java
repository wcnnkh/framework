package shuchaowen.web.servlet;

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.http.enums.Header;
import shuchaowen.core.http.server.Request;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.FieldInfo;

public abstract class WebRequest extends HttpServletRequestWrapper implements Request {
	private final boolean isDebug;
	private final long createTime;
	private final HttpServletResponse httpServletResponse;
	private final BeanFactory beanFactory;

	public WebRequest(BeanFactory beanFactory, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean isDebug) throws IOException {
		super(httpServletRequest);
		this.createTime = System.currentTimeMillis();
		this.beanFactory = beanFactory;
		this.isDebug = isDebug;
		this.httpServletResponse = httpServletResponse;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	@SuppressWarnings("unchecked")
	public <T> T getRequestWrapper(Class<? extends RequestWrapper> requestWrapper) throws Exception {
		RequestWrapper wrapper = (RequestWrapper) getAttribute(requestWrapper.getName());
		if (wrapper == null) {
			Bean bean = beanFactory.getBean(requestWrapper.getName());
			wrapper = bean.newInstance(new Class<?>[] { WebRequest.class }, this);
			if (requestWrapper != null) {
				bean.autowrite(wrapper);
				bean.init(wrapper);
				setAttribute(requestWrapper.getName(), wrapper);
			}
		}
		return (T) wrapper;
	}
	
	@SuppressWarnings("unchecked")
	private Object get(Class<?> type, String name) throws Exception{
		if (String.class.isAssignableFrom(type)) {
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
			return httpServletResponse;
		} else if (RequestWrapper.class.isAssignableFrom(type)) {
			return getRequestWrapper((Class<RequestWrapper>) type);
		} else {
			return getObject(type, name);
		}
	}
	
	public long getCreateTime() {
		return createTime;
	}

	@SuppressWarnings("unchecked")
	public final <T> T getParameter(Class<T> type, String name) throws Exception {
		T v = (T) getAttribute(name);
		if(v == null){
			v = (T) get(type, name);
			if(v != null){
				setAttribute(name, v);
			}
		}
		return v;
	}

	public String getPath() {
		return getServletPath();
	}

	public boolean isDebug() {
		return isDebug;
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
		return wrapperObject(type, name + ".");
	}
	
	public <T> T wrapperObject(Class<T> type, String prefix) throws Exception{
		Bean bean = beanFactory.getBean(type.getName());
		T t = bean.newInstance();
		if(t != null){
			bean.autowrite(t);
			ClassInfo classInfo = ClassUtils.getClassInfo(type);
			for (Entry<String, FieldInfo> entry : classInfo.getFieldMap().entrySet()) {
				FieldInfo fieldInfo = entry.getValue();
				String key = prefix == null? fieldInfo.getName(): prefix + fieldInfo.getName();
				if(String.class.isAssignableFrom(fieldInfo.getType()) || ClassUtils.isBasicType(fieldInfo.getType())){
					//如果是基本数据类型
					Object v = getParameter(fieldInfo.getType(), key);
					if(v != null){
						fieldInfo.set(t, v);
					}
				}else{
					Object v = wrapperObject(type, key + ".");
					fieldInfo.set(t, v);
				}
			}
			bean.init(bean);
		}
		return t;
	}

	public boolean isAJAX() {
		return "XMLHttpRequest".equals(getHeader(Header.X_Requested_With.getValue()));
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
	public String formatNumberText(final String text) {
		if (text == null) {
			return text;
		}

		if (text.indexOf(",") != -1) {
			return text.replaceAll(",", "");
		} else {
			return text;
		}
	}
}
