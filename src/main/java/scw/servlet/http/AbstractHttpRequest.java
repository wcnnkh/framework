package scw.servlet.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import scw.core.Destroy;
import scw.core.utils.StringUtils;
import scw.servlet.ServletUtils;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.context.DefaultRequestBeanContext;
import scw.servlet.context.RequestBeanContext;

public abstract class AbstractHttpRequest extends HttpServletRequestWrapper implements HttpRequest, Destroy {
	private static final String GET_DEFAULT_CHARSET_ANME = "ISO-8859-1";
	private long createTime;
	private RequestBeanContext requestBeanContext;
	private boolean cookieValue;
	private boolean debug;
	private boolean require;

	public AbstractHttpRequest(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			boolean cookieValue, boolean debug, boolean require) throws IOException {
		super(httpServletRequest);
		this.createTime = System.currentTimeMillis();
		this.requestBeanContext = new DefaultRequestBeanContext(this, requestBeanFactory);
		this.cookieValue = cookieValue;
		this.debug = debug;
		this.require = require;
	}

	public long getCreateTime() {
		return createTime;
	}

	@SuppressWarnings("unchecked")
	public final <T> T getParameter(Class<T> type, String name) {
		T v = (T) getAttribute(name);
		if (v == null) {
			v = (T) ServletUtils.getParameter(this, type, name);
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

	@Override
	public String getParameter(String name) {
		String v = super.getParameter(name);
		if (v == null) {
			Map<String, String> restParameterMap = ServletUtils.getRestPathParameterMap(this);
			if (restParameterMap != null) {
				v = restParameterMap.get(name);
			}
		}

		if (v == null) {
			if (cookieValue) {
				Cookie cookie = getCookie(name, false);
				if (cookie != null) {
					v = cookie.getValue();
				}
			}
		} else {
			if ("GET".equals(getMethod())) {
				v = decodeGETParameter(v);
			}
		}
		return v;
	}

	public String getRequireParameter(String name) {
		String v = getParameter(name);
		if (require && isNull(v)) {
			throw new NullPointerException("require '" + name + "'");
		}
		return v;
	}

	protected boolean isNull(String value) {
		return StringUtils.isEmpty(value);
	}

	public String decodeGETParameter(String value) {
		if (StringUtils.containsChinese(value)) {
			return value;
		}

		try {
			return new String(value.getBytes(GET_DEFAULT_CHARSET_ANME), getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return value;
		}
	}

	public String getString(String key) {
		return getParameter(key);
	}

	public Byte getByte(String key) {
		String v = getParameter(key);
		return StringUtils.isNull(v) ? null : Byte.valueOf(v);
	}

	public byte getByteValue(String key) {
		return StringUtils.parseByte(getRequireParameter(key));
	}

	public Short getShort(String key) {
		String v = getParameter(key);
		if (isNull(v)) {
			return null;
		}

		return Short.valueOf(StringUtils.formatNumberText(v));
	}

	public short getShortValue(String key) {
		return StringUtils.parseShort(getRequireParameter(key));
	}

	public Integer getInteger(String key) {
		String v = getParameter(key);
		return isNull(v) ? null : StringUtils.parseInt(v);
	}

	public int getIntValue(String key) {
		return StringUtils.parseInt(getRequireParameter(key));
	}

	public Long getLong(String key) {
		String v = getParameter(key);
		return isNull(v) ? null : StringUtils.parseLong(v);
	}

	public long getLongValue(String key) {
		return StringUtils.parseLong(getRequireParameter(key));
	}

	public Boolean getBoolean(String key) {
		String v = getParameter(key);
		if (StringUtils.isEmpty(v)) {
			return null;
		}

		return StringUtils.parseBoolean(v);
	}

	public boolean getBooleanValue(String key) {
		return StringUtils.parseBoolean(getRequireParameter(key));
	}

	public Float getFloat(String key) {
		String v = getParameter(key);
		return isNull(v) ? null : StringUtils.parseFloat(v);
	}

	public float getFloatValue(String key) {
		return StringUtils.parseFloat(getRequireParameter(key));
	}

	public Double getDouble(String key) {
		String v = getParameter(key);
		return isNull(v) ? null : StringUtils.parseDouble(v);
	}

	public double getDoubleValue(String key) {
		return StringUtils.parseDouble(getRequireParameter(key));
	}

	public char getChar(String key) {
		return StringUtils.parseChar(getRequireParameter(key));
	}

	public Character getCharacter(String key) {
		String v = getParameter(key);
		return isNull(v) ? null : StringUtils.parseChar(v);
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

	public boolean isDebugEnabled() {
		return debug;
	}

	public void debug(String format, Object... args) {
		if (isDebugEnabled()) {
			getLogger().debug(format, args);
		}
	}
}
