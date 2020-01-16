package scw.lang;

import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.instance.InstanceUtils;
import scw.core.utils.XUtils;

public final class StringCodecUtils {
	private static final ConcurrentHashMap<String, StringCodec> charsetStringCodecMap = new ConcurrentHashMap<String, StringCodec>();

	private StringCodecUtils() {
	};

	private static StringCodec createStringCodec(String charsetName) {
		return (StringCodec) (XUtils.isSupportJdk6()
				? InstanceUtils.getInstance("scw.core.string.Jdk6StringCodec", charsetName)
				: InstanceUtils.getInstance("scw.core.string.Jdk5StringCodec", charsetName));
	}

	public static StringCodec getStringCodec(String charsetName) {
		StringCodec stringCodec = charsetStringCodecMap.get(charsetName);
		if (stringCodec == null) {
			stringCodec = createStringCodec(charsetName);
			StringCodec old = charsetStringCodecMap.putIfAbsent(charsetName, stringCodec);
			if (old != null) {
				stringCodec = old;
			}
		}
		return stringCodec;
	}

	public static StringCodec getStringCodec(Charset charset) {
		return getStringCodec(charset.name());
	}
}
