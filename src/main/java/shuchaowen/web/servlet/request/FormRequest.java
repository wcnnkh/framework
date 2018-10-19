package shuchaowen.web.servlet.request;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.http.server.search.PathSearchAction;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.Logger;
import shuchaowen.web.servlet.WebRequest;

public class FormRequest extends WebRequest {
	private Map<String, String> restUrlValueMap;

	@SuppressWarnings("unchecked")
	public FormRequest(BeanFactory beanFactory, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean isDebug) throws IOException {
		super(beanFactory, httpServletRequest, httpServletResponse, isDebug);
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
			Logger.debug("REQUEST", sb.toString());
		}
	}

	protected String getValue(String key) {
		if (restUrlValueMap == null) {
			return getParameter(key);
		} else {
			String v = restUrlValueMap.get(key);
			if (v == null) {
				v = getParameter(key);
			}
			return v;
		}
	}

	public String getString(String key) {
		return getValue(key);
	}

	public Byte getByte(String key) {
		String v = getValue(key);
		return v == null ? null : Byte.valueOf(v);
	}

	public byte getByteValue(String key) {
		String v = getValue(key);
		if (v == null) {
			throw new NullPointerException("require '" + key + "'");
		}

		return Byte.parseByte(v);
	}

	public Short getShort(String key) {
		String v = getValue(key);
		if (v == null) {
			return null;
		}

		return Short.valueOf(formatNumberText(v));
	}

	public short getShortValue(String key) {
		String v = getValue(key);
		if (v == null) {
			throw new NullPointerException("require '" + key + "'");
		}

		return Short.parseShort(formatNumberText(v));
	}

	public Integer getInteger(String key) {
		String str = getValue(key);
		return str == null ? null : Integer.parseInt(formatNumberText(str));
	}

	public int getIntValue(String key) {
		String v = getValue(key);
		if (v == null) {
			throw new NullPointerException("require '" + key + "'");
		}

		return Integer.parseInt(formatNumberText(v));
	}

	public Long getLong(String key) {
		String v = getValue(key);
		return v == null ? null : Long.valueOf(formatNumberText(v));
	}

	public long getLongValue(String key) {
		String v = getValue(key);
		if (v == null) {
			throw new NullPointerException("require '" + key + "'");
		}

		return Long.parseLong(formatNumberText(v));
	}

	public Boolean getBoolean(String key) {
		String v = getValue(key);
		if (v == null || v.length() == 0) {
			return null;
		}

		return "1".equals(v) ? true : Boolean.valueOf(v);
	}

	public boolean getBooleanValue(String key) {
		String v = getValue(key);
		if (v == null || v.length() == 0) {
			throw new NullPointerException("require '" + key + "'");
		}

		return "1".equals(v) ? true : Boolean.parseBoolean(v);
	}

	public Float getFloat(String key) {
		String v = getValue(key);
		return v == null ? null : Float.valueOf(formatNumberText(v));
	}

	public float getFloatValue(String key) {
		String v = getValue(key);
		if (v == null) {
			throw new NullPointerException("require '" + key + "'");
		}

		return Float.parseFloat(formatNumberText(v));
	}

	public Double getDouble(String key) {
		String v = getValue(key);
		return v == null ? null : Double.valueOf(formatNumberText(v));
	}

	public double getDoubleValue(String key) {
		String v = getValue(key);
		if (v == null) {
			throw new NullPointerException("require '" + key + "'");
		}

		return Double.parseDouble(formatNumberText(v));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(Class<T> type, String key) throws Throwable{
		Object obj = type.newInstance();
		if (obj == null) {
			return null;
		}
		wrapperObject(obj, null, type);
		return (T) obj;
	}
	
	private void wrapperObject(Object superObj, String fieldName, Class<?> type) throws Throwable{
		ClassInfo classInfo = ClassUtils.getClassInfo(type);
		String prefixKey = fieldName == null? null: (fieldName + ".");
		for (Entry<String, FieldInfo> entry : classInfo.getFieldMap().entrySet()) {
			String key = prefixKey == null? entry.getKey(): (prefixKey + entry.getKey());
			String v = getValue(key);
			if (v != null) {
				if(String.class.isAssignableFrom(entry.getValue().getType()) || ClassUtils.isBasicType(entry.getValue().getType())){
					entry.getValue().set(superObj, getParameter(entry.getValue().getType(), key));
				}else{
					Object o = entry.getValue().getType().newInstance();
					wrapperObject(o, key, entry.getValue().getType());
					entry.getValue().set(superObj, o);
				}
			}
		}
	}

	/**
	 * 可以解决1,234这种问题
	 * 
	 * @param text
	 * @return
	 */
	private String formatNumberText(final String text) {
		if (text == null) {
			return text;
		}

		if (text.indexOf(",") != -1) {
			return text.replaceAll(",", "");
		} else {
			return text;
		}
	}

	@Override
	public char getChar(String name) {
		String v = getValue(name);
		if (v == null) {
			throw new NullPointerException("require '" + name + "'");
		}

		return v.charAt(0);
	}

	@Override
	public Character getCharacter(String name) {
		String v = getValue(name);
		return v == null ? null : v.charAt(0);
	}
}
