package scw.utils.ali.dayu;

import java.lang.reflect.InvocationTargetException;

import com.alibaba.fastjson.JSONObject;

import scw.core.utils.XTime;
import scw.core.utils.XUtils;
import scw.data.memcached.Memcached;

public final class MemcachedXmlPhoneVerificationCode extends AbstractXmlPhoneVerificationCode {
	private final Memcached memcached;
	private final String tempPrefix;

	public MemcachedXmlPhoneVerificationCode(String xmlPath, Memcached memcached)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(xmlPath);
		this.memcached = memcached;
		this.tempPrefix = XUtils.getUUID();
	}

	public JSONObject getCacheData(int configIndex, String phone) {
		String dataKey = tempPrefix + "&" + configIndex + "&" + phone;
		String cache = (String) memcached.get(dataKey);
		if (cache == null) {
			return null;
		}
		return JSONObject.parseObject(cache);
	}

	public void setCacheData(int configIndex, String phone, JSONObject json) {
		if (json == null) {
			return;
		}

		String dataKey = tempPrefix + "&" + configIndex + "&" + phone;
		memcached.set(dataKey, (int) XTime.ONE_DAY / 1000, json.toJSONString());
	}
}
