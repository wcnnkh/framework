package scw.net.support;

import java.lang.reflect.Type;

import scw.core.Constants;
import scw.json.JSONUtils;

public class JsonDecoderFilter extends AbstractTextDecoderFilter {

	public JsonDecoderFilter() {
		this(Constants.DEFAULT_CHARSET_NAME);
	}

	public JsonDecoderFilter(String charsetName) {
		super(charsetName);
	}

	@Override
	protected boolean isVerifyType(Type type) {
		return true;
	}

	@Override
	protected Object textDecoder(String contentType, String text, Type type) {
		return JSONUtils.parseObject(text, type);
	}
}
