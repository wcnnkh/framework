package scw.mvc.output;

import scw.io.IOUtils;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.mvc.Channel;
import scw.mvc.View;
import scw.net.MimeType;
import scw.net.NetworkUtils;
import scw.net.Text;
import scw.net.message.Entity;
import scw.net.message.InputMessage;
import scw.net.message.converter.MultiMessageConverter;
import scw.net.message.converter.support.AllMessageConverter;

public abstract class AbstractOutput<C extends Channel, T> implements Output {
	private JSONSupport jsonSupport = JSONUtils.DEFAULT_JSON_SUPPORT;
	private final MultiMessageConverter messageConverter = new MultiMessageConverter();
	
	public AbstractOutput(){
		messageConverter.add(new AllMessageConverter());
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

	protected abstract void writeBodyBefore(C channel, T body) throws Throwable;

	protected abstract void writeBody(C channel, T body) throws Throwable;

	protected abstract void writeBodyAfter(C channel, T body) throws Throwable;

	@SuppressWarnings("unchecked")
	public void write(Channel channel, Object body) throws Throwable {
		if (body == null) {
			return;
		}

		if (body instanceof View) {
			((View) body).render(channel);
			return;
		}

		C wrapperChannel = (C) channel;
		T b = (T) body;
		if (body instanceof InputMessage) {
			NetworkUtils
					.writeHeader((InputMessage) body, channel.getResponse());
			writeBodyBefore(wrapperChannel, b);
			IOUtils.write(((InputMessage) body).getBody(), channel
					.getResponse().getBody());
		} else if (body instanceof Text) {
			MimeType mimeType = ((Text) body).getMimeType();
			if (mimeType != null) {
				channel.getResponse().setContentType(mimeType);
			}
			writeBodyBefore(wrapperChannel, b);
			channel.getResponse().getWriter()
					.write(((Text) body).getTextContent());
		} else if (body instanceof Entity) {
			NetworkUtils.writeHeader((Entity<?>) body, channel.getResponse());
			writeBodyBefore(wrapperChannel, b);
			getMessageConverter().write(((Entity<?>) body).getBody(),
					((Entity<?>) body).getContentType(), channel.getResponse());
		} else {
			writeBodyBefore(wrapperChannel, b);
			writeBody(wrapperChannel, b);
		}
		writeBodyAfter(wrapperChannel, b);
	}
}
