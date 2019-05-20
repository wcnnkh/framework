package scw.servlet.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import scw.core.Destroy;
import scw.core.utils.StringParseUtils;
import scw.core.utils.StringUtils;
import scw.servlet.ServletUtils;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.context.DefaultRequestBeanContext;
import scw.servlet.context.RequestBeanContext;
import scw.servlet.http.filter.RestService;

public class DefaultHttpRequest extends HttpServletRequestWrapper implements HttpRequest, Destroy {
	private static final Charset GET_DEFAULT_CHARSET = Charset.forName("ISO-8859-1");
	private long createTime;
	private RequestBeanContext requestBeanContext;
	private boolean cookieValue;

	public DefaultHttpRequest(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			boolean cookieValue) throws IOException {
		super(httpServletRequest);
		this.createTime = System.currentTimeMillis();
		this.requestBeanContext = new DefaultRequestBeanContext(this, requestBeanFactory);
		this.cookieValue = cookieValue;
	}

	public long getCreateTime() {
		return createTime;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected final Object get(Class<?> type, String name) {
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
		} else if (type.isEnum()) {
			String v = getString(name);
			return StringUtils.isEmpty(v) ? null : Enum.valueOf((Class<? extends Enum>) type, v);
		} else {
			return requestBeanContext.getBean(type, name);
		}
	}

	@SuppressWarnings("unchecked")
	public final <T> T getParameter(Class<T> type, String name) {
		T v = (T) getAttribute(name);
		if (v == null) {
			v = (T) get(type, name);
		}
		return v;
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

	@SuppressWarnings("unchecked")
	@Override
	public String getParameter(String name) {
		String v = super.getParameter(name);
		if (v == null) {
			Map<String, String> restParameterMap = (Map<String, String>) getAttribute(
					RestService.RESTURL_PATH_PARAMETER);
			if (restParameterMap != null) {
				v = restParameterMap.get(name);
			}
		}

		if (isNull(v)) {
			return null;
		}

		if ("GET".equals(getMethod())) {
			v = decodeGETParameter(v);
		}

		if (v == null && cookieValue) {
			Cookie cookie = getCookie(name, false);
			if (cookie != null) {
				v = cookie.getValue();
			}
		}
		return v;
	}

	public String getRequireParameter(String key) {
		String v = getParameter(key);
		if (isNull(v)) {
			throw new NullPointerException("require '" + key + "'");
		}
		return v;
	}

	public String decodeGETParameter(String value) {
		if (StringUtils.containsChinese(value)) {
			return value;
		}
		return new String(value.getBytes(GET_DEFAULT_CHARSET), Charset.forName(getCharacterEncoding()));
	}

	public String getString(String key) {
		return getParameter(key);
	}

	public Byte getByte(String key) {
		String v = getParameter(key);
		return StringUtils.isNull(v) ? null : Byte.valueOf(v);
	}

	public byte getByteValue(String key) {
		String v = getRequireParameter(key);
		return Byte.parseByte(v);
	}

	public Short getShort(String key) {
		String v = getParameter(key);
		if (isNull(v)) {
			return null;
		}

		return Short.valueOf(StringParseUtils.formatNumberText(v));
	}

	public short getShortValue(String key) {
		String v = getRequireParameter(key);

		return Short.parseShort(StringParseUtils.formatNumberText(v));
	}

	public Integer getInteger(String key) {
		String str = getParameter(key);
		return isNull(str) ? null : Integer.parseInt(StringParseUtils.formatNumberText(str));
	}

	public int getIntValue(String key) {
		String v = getRequireParameter(key);

		return Integer.parseInt(StringParseUtils.formatNumberText(v));
	}

	public Long getLong(String key) {
		String v = getParameter(key);
		return isNull(v) ? null : Long.valueOf(StringParseUtils.formatNumberText(v));
	}

	public long getLongValue(String key) {
		String v = getRequireParameter(key);

		return Long.parseLong(StringParseUtils.formatNumberText(v));
	}

	public Boolean getBoolean(String key) {
		String v = getParameter(key);
		if (StringUtils.isEmpty(v)) {
			return null;
		}

		return StringParseUtils.parseBoolean(v);
	}

	public boolean getBooleanValue(String key) {
		String v = getRequireParameter(key);
		return StringParseUtils.parseBoolean(v);
	}

	public Float getFloat(String key) {
		String v = getParameter(key);
		return isNull(v) ? null : Float.valueOf(StringParseUtils.formatNumberText(v));
	}

	public float getFloatValue(String key) {
		String v = getRequireParameter(key);
		return Float.parseFloat(StringParseUtils.formatNumberText(v));
	}

	public Double getDouble(String key) {
		String v = getParameter(key);
		return isNull(v) ? null : Double.valueOf(StringParseUtils.formatNumberText(v));
	}

	public double getDoubleValue(String key) {
		String v = getRequireParameter(key);
		return Double.parseDouble(StringParseUtils.formatNumberText(v));
	}

	public char getChar(String key) {
		String v = getRequireParameter(key);
		return v.charAt(0);
	}

	public Character getCharacter(String key) {
		String v = getParameter(key);
		return isNull(v) ? null : v.charAt(0);
	}

	protected boolean isNull(String value) {
		return StringUtils.isEmpty(value);
	}

	public void destroy() {
		requestBeanContext.destroy();
	}

	public <T> T getBean(Class<T> type, String name) {
		return requestBeanContext.getBean(type, name);
	}

	public final <T> T getBean(Class<T> type) {
		return requestBeanContext.getBean(type);
	}
}
