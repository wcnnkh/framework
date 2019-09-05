package scw.net.support;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.URLConnection;

import scw.core.Constants;
import scw.core.utils.XUtils;
import scw.json.JSONParseSupport;
import scw.json.JSONUtils;

public class DefaultDecoderFilter extends AbstractTextDecoderFilter {
	private JSONParseSupport jsonParseSupport;

	public DefaultDecoderFilter() {
		this(Constants.DEFAULT_CHARSET_NAME, JSONUtils.DEFAULT_JSON_SUPPORT);
	}

	public DefaultDecoderFilter(String charsetName, JSONParseSupport jsonParseSupport) {
		super(charsetName);
		this.jsonParseSupport = jsonParseSupport;
	}

	@Override
	protected boolean isVerifyType(Type type, URLConnection urlConnection) {
		return true;
	}

	@Override
	protected Object textDecoder(String contentType, String text, Type type) {
		return XUtils.getValue(this, text, type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] getArray(String text, Class<E> type) {
		return (E[]) jsonParseSupport.parseObject(text, Array.newInstance(type, 0).getClass());
	}

	@Override
	public Object getObject(String text, Class<?> type) {
		return super.getObject(text, type);
	}

	@Override
	public Object getObject(String text, Type type) {
		return jsonParseSupport.parseObject(text, type);
	}
}
