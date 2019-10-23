package scw.data.redis.script;

import scw.core.StringFormat;
import scw.core.utils.StringUtils;

public class RedisScriptFormat extends StringFormat {
	private final int beginIndex;

	public RedisScriptFormat(String prefix, String suffix, int beginIndex) {
		super(prefix, suffix);
		this.beginIndex = beginIndex;
	}

	public String getProperty(String key) {
		if (StringUtils.isNumeric(key)) {
			return (beginIndex + StringUtils.parseInt(key)) + "";
		}
		return null;
	}
}
