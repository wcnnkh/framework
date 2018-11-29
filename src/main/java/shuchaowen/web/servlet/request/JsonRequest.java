package shuchaowen.web.servlet.request;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.XUtils;
import shuchaowen.web.servlet.WebRequest;

public class JsonRequest extends WebRequest{
	private JSONObject json;

	public JsonRequest(BeanFactory beanFactory, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean isDebug) throws IOException {
		super(beanFactory, httpServletRequest, httpServletResponse, isDebug);
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = getReader();
			if (br.markSupported()) {
				br.mark(0);
			}
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br.markSupported()) {
				if (br != null) {
					try {
						br.reset();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			XUtils.close(br);
		}

		String content = sb.toString();
		if(isDebug){
			Logger.debug(this.getClass().getName(), "servletPath=" + getServletPath() + ",method=" + getMethod() + "," + content);
		}
		
		json = JSONObject.parseObject(sb.toString());
	}

	public JSONObject getJson() {
		return json;
	}

	@Override
	public String getString(String key) {
		return json.getString(key);
	}

	@Override
	public Byte getByte(String key) {
		return json.getByte(key);
	}

	@Override
	public byte getByteValue(String key) {
		return json.getByteValue(key);
	}

	@Override
	public Short getShort(String key) {
		return json.getShort(key);
	}

	@Override
	public short getShortValue(String key) {
		return json.getShortValue(key);
	}

	@Override
	public Integer getInteger(String key) {
		return json.getInteger(key);
	}

	@Override
	public int getIntValue(String key) {
		return json.getIntValue(key);
	}

	@Override
	public Long getLong(String key) {
		return json.getLong(key);
	}

	@Override
	public long getLongValue(String key) {
		return json.getLongValue(key);
	}

	@Override
	public Boolean getBoolean(String key) {
		String v = getString(key);
		if (v == null || v.length() == 0) {
			return null;
		}

		return "1".equals(v) || "true".equals(v);
	}

	@Override
	public boolean getBooleanValue(String key) {
		if (!json.containsKey(key)) {
			throw new NullPointerException("require '" + key + "'");
		}

		String v = getString(key);
		if (v == null || v.length() == 0) {
			throw new NullPointerException("require '" + key + "'");
		}

		return "1".equals(v) || "true".equals(v.toLowerCase());
	}

	@Override
	public Float getFloat(String key) {
		return json.getFloat(key);
	}

	@Override
	public float getFloatValue(String key) {
		return json.getFloatValue(key);
	}

	@Override
	public Double getDouble(String key) {
		return json.getDouble(key);
	}

	@Override
	public double getDoubleValue(String key) {
		return json.getDoubleValue(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(Class<T> type, String key) {
		if (JSONObject.class.isAssignableFrom(type)) {
			return (T) json.getJSONObject(key);
		} else if (JSONArray.class.isAssignableFrom(type)) {
			return (T) json.getJSONArray(key);
		} else {
			return json.getObject(key, type);
		}
	}

	@Override
	public char getChar(String name) {
		String v = json.getString(name);
		if(v == null || v.length() == 0){
			throw new NullPointerException("require '" + name + "'");
		}
		
		return v.charAt(0);
	}

	@Override
	public Character getCharacter(String name) {
		String v = json.getString(name);
		if(v == null || v.length() == 0){
			return null;
		}
		
		return v.charAt(0);
	}
}
