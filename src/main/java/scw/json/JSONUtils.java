package scw.json;

import scw.core.exception.NotSupportException;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.reflect.ReflectUtils;

@SuppressWarnings("unchecked")
public final class JSONUtils {
	private static Logger logger = LoggerFactory.getLogger(JSONUtils.class);

	private JSONUtils() {
	};

	static {
		String[] supportClassNames = { "scw.json.support.FastJSONParseSupport" };

		Class<JSONParseSupport> jsonSupportClass = null;
		for (String name : supportClassNames) {
			try {
				jsonSupportClass = (Class<JSONParseSupport>) Class.forName(name);
				break;
			} catch (Throwable e) {
			}
		}

		if (jsonSupportClass == null) {
			throw new NotSupportException("not found default json parse support");
		}

		logger.info("default json parse：{}", jsonSupportClass.getName());
		DEFAULT_PARSE_SUPPORT = ReflectUtils.newInstance(jsonSupportClass);
	}

	/**
	 * 默认的json序列化工具
	 */
	public static final JSONParseSupport DEFAULT_PARSE_SUPPORT;

	public static String toJSONString(Object obj) {
		return DEFAULT_PARSE_SUPPORT.toJSONString(obj);
	}

	public static JSONObject parseObject(String json) {
		return DEFAULT_PARSE_SUPPORT.parseObject(json);
	}

	public static JSONArray parseArray(String json) {
		return DEFAULT_PARSE_SUPPORT.parseArray(json);
	}
}
