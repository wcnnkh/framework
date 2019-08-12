package scw.net.support;

import scw.core.Constants;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringParse;

/**
 * 基础解析方式
 * 
 * @author shuchaowen
 *
 */
public final class BaseDecoderFilter extends AbstractTextDecoderFilter {
	private String charsetName;

	public BaseDecoderFilter() {
		this(Constants.DEFAULT_CHARSET_NAME);
	}

	public BaseDecoderFilter(String charsetName) {
		this.charsetName = charsetName;
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	@Override
	protected boolean isVerifyType(Class<?> type) {
		return ClassUtils.isStringType(type) || ClassUtils.isPrimitiveOrWrapper(type);
	}

	@Override
	protected Object textDecoder(String contentType, String text, Class<?> type) {
		return StringParse.defaultParse(text, type);
	}

}
