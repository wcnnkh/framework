package scw.servlet.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import scw.common.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.servlet.Request;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.parameter.Body;

public class JsonRequest extends Request {
	private static Logger logger = LoggerFactory.getLogger(JsonRequest.class);
	
	private JSONObject json;

	public JsonRequest(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean isDebug) throws IOException {
		super(requestBeanFactory, httpServletRequest, httpServletResponse, isDebug);
		Body body = getBean(Body.class);
		if (isDebug) {
			logger.debug("servletPath=" + getServletPath() + ",method=" + getMethod() + "," + body.getBody());
		}
		json = JSONObject.parseObject(body.getBody());
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
		if (StringUtils.isEmpty(v)) {
			return null;
		}

		return StringUtils.parseBoolean(v);
	}

	@Override
	public boolean getBooleanValue(String key) {
		String v = getString(key);
		if (StringUtils.isEmpty(v)) {
			throw new NullPointerException("require '" + key + "'");
		}

		return StringUtils.parseBoolean(v);
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
		if (v == null || v.length() == 0) {
			throw new NullPointerException("require '" + name + "'");
		}

		return v.charAt(0);
	}

	@Override
	public Character getCharacter(String name) {
		String v = json.getString(name);
		if (v == null || v.length() == 0) {
			return null;
		}

		return v.charAt(0);
	}
}
