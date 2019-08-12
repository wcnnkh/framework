package scw.io.support;

import java.io.IOException;
import java.io.InputStream;

import scw.core.Constants;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringParse;
import scw.io.DeserializerFilter;
import scw.io.DeserializerFilterChain;
import scw.io.IOUtils;

/**
 * 基础解析方式
 * 
 * @author shuchaowen
 *
 */
public final class BaseDeserializerFilter implements DeserializerFilter {
	private String charsetName;

	public BaseDeserializerFilter() {
		this(Constants.DEFAULT_CHARSET_NAME);
	}

	public BaseDeserializerFilter(String charsetName) {
		this.charsetName = charsetName;
	}

	public Object deserialize(Class<?> type, InputStream input, DeserializerFilterChain chain) throws IOException {
		if (type == Void.class) {
			return null;
		}

		if (ClassUtils.isStringType(type) || ClassUtils.isPrimitiveOrWrapper(type)) {
			String content = IOUtils.readContent(input, charsetName);
			return StringParse.defaultParse(content, type);
		}
		return chain.doDeserialize(type, input);
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}
}
