package scw.mvc.output;

import java.io.PrintWriter;

import scw.core.utils.StringUtils;
import scw.io.IOUtils;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.mvc.Channel;
import scw.mvc.View;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.NetworkUtils;
import scw.net.message.Entity;
import scw.net.message.InputMessage;
import scw.net.message.Text;
import scw.net.message.converter.MultiMessageConverter;
import scw.net.message.converter.support.AllMessageConverter;
import scw.value.property.PropertyFactory;

public abstract class AbstractOutput<T> implements Output {
	private JSONSupport jsonSupport = JSONUtils.getJsonSupport();
	private final MultiMessageConverter messageConverter = new MultiMessageConverter();
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
	
	public AbstractOutput(){
		messageConverter.add(new AllMessageConverter());
	}
	
	protected String getJsonpCallback(Channel channel) {
		// 非GET请求不支持jsonp
		if (scw.net.http.HttpMethod.GET != channel.getRequest().getMethod()) {
			return null;
		}

		String jsonp = getJsonp();
		if (StringUtils.isEmpty(jsonp)) {
			return null;
		}
		return channel.getString(jsonp);
	}

	protected MimeType getJsonpContentType(Channel channel, T body) {
		return MimeTypeUtils.TEXT_JAVASCRIPT;
	}

	public JSONSupport getJsonSupport() {
		return jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	public MultiMessageConverter getMessageConverter() {
		return messageConverter;
	}

	public boolean canWrite(Channel channel, Object body) {
		if (body == null) {
			return false;
		}

		return body instanceof View || body instanceof InputMessage
				|| body instanceof Text || body instanceof Entity || canWriteInternal(channel, body);
	}

	protected abstract boolean canWriteInternal(Channel channel, Object body);

	protected void writeBodyBefore(Channel channel, T body) throws Throwable{
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

	protected abstract void writeBody(Channel channel, T body) throws Throwable;

	protected void writeBodyAfter(Channel channel, T body) throws Throwable{
		String jsonp = getJsonpCallback(channel);
		if (!StringUtils.isEmpty(jsonp)) {
			channel.getResponse().getWriter().write(JSONP_RESP_SUFFIX);
		}
	}

	@SuppressWarnings("unchecked")
	public void write(Channel channel, Object body) throws Throwable {
		if (body == null) {
			return;
		}

		if (body instanceof View) {
			((View) body).render(channel);
			return;
		}

		T b = (T) body;
		if (body instanceof InputMessage) {
			NetworkUtils
					.writeHeader((InputMessage) body, channel.getResponse());
			writeBodyBefore(channel, b);
			IOUtils.write(((InputMessage) body).getBody(), channel
					.getResponse().getBody());
		} else if (body instanceof Text) {
			MimeType mimeType = ((Text) body).getMimeType();
			if (mimeType != null) {
				channel.getResponse().setContentType(mimeType);
			}
			writeBodyBefore(channel, b);
			channel.getResponse().getWriter()
					.write(((Text) body).getTextContent());
		} else if (body instanceof Entity) {
			NetworkUtils.writeHeader((Entity<?>) body, channel.getResponse());
			writeBodyBefore(channel, b);
			getMessageConverter().write(((Entity<?>) body).getBody(),
					((Entity<?>) body).getContentType(), channel.getResponse());
		} else {
			writeBodyBefore(channel, b);
			writeBody(channel, b);
		}
		writeBodyAfter(channel, b);
	}
}
