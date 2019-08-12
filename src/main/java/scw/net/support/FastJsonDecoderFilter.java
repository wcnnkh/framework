package scw.net.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

import scw.core.Constants;

public class FastJsonDecoderFilter extends AbstractTextDecoderFilter {
	private String charsetName;

	public FastJsonDecoderFilter() {
		this(Constants.DEFAULT_CHARSET_NAME);
	}

	public FastJsonDecoderFilter(String charsetName) {
		this.charsetName = charsetName;
	}

	@Override
	public String getCharsetName() {
		return charsetName;
	}

	@Override
	protected boolean isVerifyType(Class<?> type) {
		return true;
	}

	@Override
	protected Object textDecoder(String contentType, String text, Class<?> type) {
		return JSON.parseObject(text, type, Feature.SupportNonPublicField);
	}
}
