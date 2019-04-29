package scw.utils.ali.dayu;

import java.lang.reflect.InvocationTargetException;

import com.alibaba.fastjson.JSONObject;

import scw.core.utils.XTime;
import scw.core.utils.XUtils;
import scw.data.redis.Redis;

public final class RedisXmlPhoneVerificationCode extends AbstractXmlPhoneVerificationCode {
	private final Redis redis;
	private final String tempPrefix;

	public RedisXmlPhoneVerificationCode(String xmlPath, Redis redis)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(xmlPath);
		this.redis = redis;
		this.tempPrefix = XUtils.getUUID();
	}

	@Override
	public JSONObject getCacheData(int configIndex, String phone) {
		String dataKey = tempPrefix + "&" + configIndex + "&" + phone;
		String cache = redis.getStringOperations().get(dataKey);
		if (cache == null) {
			return null;
		}
		return JSONObject.parseObject(cache);
	}

	@Override
	public void setCacheData(int configIndex, String phone, JSONObject json) {
		if (json == null) {
			return;
		}

		String dataKey = tempPrefix + "&" + configIndex + "&" + phone;
		redis.getStringOperations().setex(dataKey, (int) XTime.ONE_DAY / 1000, json.toJSONString());
	}
}
