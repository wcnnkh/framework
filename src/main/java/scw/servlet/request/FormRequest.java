package scw.servlet.request;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import scw.common.Logger;
import scw.common.utils.StringUtils;
import scw.servlet.Request;
import scw.servlet.action.PathSearchAction;
import scw.servlet.bean.RequestBeanFactory;

public class FormRequest extends Request {
	private static final Charset GET_DEFAULT_CHARSET = Charset.forName("ISO-8859-1");
	private Map<String, String> restUrlValueMap;
	private final boolean cookieValue;

	@SuppressWarnings("unchecked")
	public FormRequest(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean isDebug, boolean cookieValue) throws IOException {
		super(requestBeanFactory, httpServletRequest, httpServletResponse, isDebug);
		this.cookieValue = cookieValue;
		Object map = getAttribute(PathSearchAction.RESTURL_PATH_PARAMETER);
		if (map != null) {
			this.restUrlValueMap = (Map<String, String>) map;
		}

		if (isDebug) {
			StringBuilder sb = new StringBuilder();
			sb.append("servletPath=");
			sb.append(httpServletRequest.getServletPath());
			sb.append(",method=");
			sb.append(httpServletRequest.getMethod());
			sb.append(",");
			sb.append(JSONObject.toJSONString(getParameterMap()));
			Logger.debug(this.getClass().getName(), sb.toString());
		}
	}

	protected String getRequireValue(String key) {
		String v = getValue(key);
		if (v == null && cookieValue) {
			Cookie cookie = getCookie(key, false);
			if (cookie != null) {
				v = cookie.getValue();
			}
		}

		if (isNull(v)) {
			throw new NullPointerException("require '" + key + "'");
		}
		return v;
	}

	protected String getValue(String key) {
		String v;
		if (restUrlValueMap == null) {
			v = getParameter(key);
		} else {
			v = restUrlValueMap.get(key);
			if (v == null) {
				v = getParameter(key);
			}
		}

		if (!isNull(v) && "GET".equals(getMethod())) {
			v = decodeGETParameter(v);
		}
		return v;
	}

	protected String decodeGETParameter(String value) {
		if (StringUtils.containsChinese(value)) {
			return value;
		}
		return new String(value.getBytes(GET_DEFAULT_CHARSET), Charset.forName(getCharacterEncoding()));
	}

	public String getString(String key) {
		return getValue(key);
	}

	public Byte getByte(String key) {
		String v = getValue(key);
		return StringUtils.isNull(v) ? null : Byte.valueOf(v);
	}

	public byte getByteValue(String key) {
		String v = getRequireValue(key);
		return Byte.parseByte(v);
	}

	public Short getShort(String key) {
		String v = getValue(key);
		if (isNull(v)) {
			return null;
		}

		return Short.valueOf(formatNumberText(v));
	}

	public short getShortValue(String key) {
		String v = getRequireValue(key);

		return Short.parseShort(formatNumberText(v));
	}

	public Integer getInteger(String key) {
		String str = getValue(key);
		return isNull(str) ? null : Integer.parseInt(formatNumberText(str));
	}

	public int getIntValue(String key) {
		String v = getRequireValue(key);

		return Integer.parseInt(formatNumberText(v));
	}

	public Long getLong(String key) {
		String v = getValue(key);
		return isNull(v) ? null : Long.valueOf(formatNumberText(v));
	}

	public long getLongValue(String key) {
		String v = getRequireValue(key);

		return Long.parseLong(formatNumberText(v));
	}

	public Boolean getBoolean(String key) {
		String v = getValue(key);
		return "1".equals(v) ? true : Boolean.valueOf(v);
	}

	public boolean getBooleanValue(String key) {
		String v = getRequireValue(key);
		return "1".equals(v) ? true : Boolean.parseBoolean(v);
	}

	public Float getFloat(String key) {
		String v = getValue(key);
		return isNull(v) ? null : Float.valueOf(formatNumberText(v));
	}

	public float getFloatValue(String key) {
		String v = getRequireValue(key);
		return Float.parseFloat(formatNumberText(v));
	}

	public Double getDouble(String key) {
		String v = getValue(key);
		return isNull(v) ? null : Double.valueOf(formatNumberText(v));
	}

	public double getDoubleValue(String key) {
		String v = getRequireValue(key);
		return Double.parseDouble(formatNumberText(v));
	}

	@Override
	public char getChar(String key) {
		String v = getRequireValue(key);
		return v.charAt(0);
	}

	@Override
	public Character getCharacter(String key) {
		String v = getValue(key);
		return isNull(v) ? null : v.charAt(0);
	}

	protected boolean isNull(String value) {
		if (value == null) {
			return true;
		}

		if (value.length() == 0) {
			return true;
		}

		return false;
	}
}
