package scw.mvc.output;

import java.io.IOException;

import scw.core.utils.StringUtils;
import scw.http.jsonp.JsonpUtils;
import scw.io.IOUtils;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.mvc.HttpChannel;
import scw.mvc.view.View;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.NetworkUtils;
import scw.net.message.Entity;
import scw.net.message.InputMessage;
import scw.net.message.Text;
import scw.net.message.converter.MultiMessageConverter;
import scw.net.message.converter.support.AllMessageConverter;

public abstract class AbstractControllerOutput<T> implements ControllerOutput {
	private JSONSupport jsonSupport = JSONUtils.getJsonSupport();
	private final MultiMessageConverter messageConverter = new MultiMessageConverter();

	public AbstractControllerOutput() {
		messageConverter.add(new AllMessageConverter());
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

		return body instanceof View || body instanceof InputMessage || body instanceof Text || body instanceof Entity
				|| canWriteInternal(httpChannel, body);
	}

	protected abstract boolean canWriteInternal(HttpChannel httpChannel, Object body);

	protected void writeBodyBefore(HttpChannel httpChannel, T body) throws IOException {
		String callback = JsonpUtils.getCallback(httpChannel.getRequest());
		if (!StringUtils.isEmpty(callback)) {
			JsonpUtils.writePrefix(httpChannel.getResponse(), callback);
		}
	}

	protected abstract void writeBody(HttpChannel httpChannel, T body) throws IOException;

	protected void writeBodyAfter(HttpChannel httpChannel, T body) throws IOException {
		String callback = JsonpUtils.getCallback(httpChannel.getRequest());
		if (!StringUtils.isEmpty(callback)) {
			JsonpUtils.writeSuffix(httpChannel.getResponse(), callback);
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
			NetworkUtils.writeHeader((InputMessage) body, httpChannel.getResponse());
			writeBodyBefore(httpChannel, b);
			IOUtils.write(((InputMessage) body).getBody(), httpChannel.getResponse().getBody());
		} else if (body instanceof Text) {
			MimeType mimeType = ((Text) body).getMimeType();
			if (mimeType != null) {
				httpChannel.getResponse().setContentType(mimeType);
			}
			writeBodyBefore(httpChannel, b);
			httpChannel.getResponse().getWriter().write(((Text) body).getTextContent());
		} else if (body instanceof Entity) {
			NetworkUtils.writeHeader((Entity<?>) body, httpChannel.getResponse());
			writeBodyBefore(httpChannel, b);
			getMessageConverter().write(((Entity<?>) body).getBody(), ((Entity<?>) body).getContentType(),
					httpChannel.getResponse());
		} else {
			writeBodyBefore(httpChannel, b);
			writeBody(httpChannel, b);
		}
		writeBodyAfter(httpChannel, b);
	}
}
