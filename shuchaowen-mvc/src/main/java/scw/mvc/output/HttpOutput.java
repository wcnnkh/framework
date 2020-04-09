package scw.mvc.output;

import java.io.PrintWriter;

import scw.core.utils.StringUtils;
import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.util.value.property.PropertyFactory;

public abstract class HttpOutput<T> extends AbstractOutput<HttpChannel, T> {
	private static final String JSONP_RESP_PREFIX = "(";
	private static final String JSONP_RESP_SUFFIX = ");";
	private static final String DEFAULT_JSONP_CALLBACK = "callback";

	public static String getJsonp(PropertyFactory propertyFactory) {
		if (propertyFactory.getValue("mvc.http.jsonp.enable", boolean.class,
				true)) {
			return propertyFactory.getValue("mvc.http.jsonp", String.class,
					DEFAULT_JSONP_CALLBACK);
		}
		return null;
	}

	private String jsonp = DEFAULT_JSONP_CALLBACK;

	public String getJsonp() {
		return jsonp;
	}

	public void setJsonp(String jsonp) {
		this.jsonp = jsonp;
	}

	@Override
	protected boolean canWriteInternal(Channel channel, Object body) {
		if (channel instanceof HttpChannel) {
			return canWriteInternal((HttpChannel) channel, body);
		}
		return false;
	}

	protected abstract boolean canWriteInternal(HttpChannel httpChannel,
			Object body);

	protected String getJsonpCallback(HttpChannel httpChannel) {
		// 非GET请求不支持jsonp
		if (scw.net.http.Method.GET != httpChannel.getRequest().getMethod()) {
			return null;
		}

		String jsonp = getJsonp();
		if (StringUtils.isEmpty(jsonp)) {
			return null;
		}
		return httpChannel.getString(jsonp);
	}

	protected MimeType getJsonpContentType(HttpChannel channel, T body) {
		return MimeTypeUtils.TEXT_JAVASCRIPT;
	}

	@Override
	protected void writeBodyBefore(HttpChannel channel, T body)
			throws Throwable {
		String jsonp = getJsonpCallback(channel);
		if (!StringUtils.isEmpty(jsonp)) {
			MimeType contentType = getJsonpContentType(channel, body);
			if (contentType != null) {
				channel.getResponse().setContentType(contentType);
			}
			PrintWriter writer = channel.getResponse().getWriter();
			writer.write(jsonp);
			writer.write(JSONP_RESP_PREFIX);
		}
	}

	@Override
	protected void writeBodyAfter(HttpChannel channel, T body) throws Throwable {
		String jsonp = getJsonpCallback(channel);
		if (!StringUtils.isEmpty(jsonp)) {
			channel.getResponse().getWriter().write(JSONP_RESP_SUFFIX);
		}
	}
}
