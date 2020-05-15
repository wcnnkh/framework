package scw.net.http.server.mvc.output;

import java.io.IOException;
import java.io.PrintWriter;

import scw.core.utils.StringUtils;
import scw.io.IOUtils;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.NetworkUtils;
import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.view.View;
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
	
	protected String getJsonpCallback(HttpChannel httpChannel) {
		// 非GET请求不支持jsonp
		if (scw.net.http.HttpMethod.GET != httpChannel.getRequest().getMethod()) {
			return null;
		}

		String jsonp = getJsonp();
		if (StringUtils.isEmpty(jsonp)) {
			return null;
		}
		return httpChannel.getString(jsonp);
	}

	protected MimeType getJsonpContentType(HttpChannel httpChannel, T body) {
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

	public boolean canWrite(HttpChannel httpChannel, Object body) {
		if (body == null) {
			return false;
		}

		return body instanceof View || body instanceof InputMessage
				|| body instanceof Text || body instanceof Entity || canWriteInternal(httpChannel, body);
	}

	protected abstract boolean canWriteInternal(HttpChannel httpChannel, Object body);

	protected void writeBodyBefore(HttpChannel httpChannel, T body) throws IOException{
		String jsonp = getJsonpCallback(httpChannel);
		if (!StringUtils.isEmpty(jsonp)) {
			MimeType contentType = getJsonpContentType(httpChannel, body);
			if (contentType != null) {
				httpChannel.getResponse().setContentType(contentType);
			}
			PrintWriter writer = httpChannel.getResponse().getWriter();
			writer.write(jsonp);
			writer.write(JSONP_RESP_PREFIX);
		}
	}

	protected abstract void writeBody(HttpChannel httpChannel, T body) throws IOException;

	protected void writeBodyAfter(HttpChannel httpChannel, T body) throws IOException{
		String jsonp = getJsonpCallback(httpChannel);
		if (!StringUtils.isEmpty(jsonp)) {
			httpChannel.getResponse().getWriter().write(JSONP_RESP_SUFFIX);
		}
	}

	@SuppressWarnings("unchecked")
	public void write(HttpChannel httpChannel, Object body) throws IOException {
		if (body == null) {
			return;
		}

		if (body instanceof View) {
			((View) body).render(httpChannel);
			return;
		}

		T b = (T) body;
		if (body instanceof InputMessage) {
			NetworkUtils
					.writeHeader((InputMessage) body, httpChannel.getResponse());
			writeBodyBefore(httpChannel, b);
			IOUtils.write(((InputMessage) body).getBody(), httpChannel
					.getResponse().getBody());
		} else if (body instanceof Text) {
			MimeType mimeType = ((Text) body).getMimeType();
			if (mimeType != null) {
				httpChannel.getResponse().setContentType(mimeType);
			}
			writeBodyBefore(httpChannel, b);
			httpChannel.getResponse().getWriter()
					.write(((Text) body).getTextContent());
		} else if (body instanceof Entity) {
			NetworkUtils.writeHeader((Entity<?>) body, httpChannel.getResponse());
			writeBodyBefore(httpChannel, b);
			getMessageConverter().write(((Entity<?>) body).getBody(),
					((Entity<?>) body).getContentType(), httpChannel.getResponse());
		} else {
			writeBodyBefore(httpChannel, b);
			writeBody(httpChannel, b);
		}
		writeBodyAfter(httpChannel, b);
	}
}
