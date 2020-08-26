package scw.mvc.output;

import java.io.IOException;

import scw.core.utils.StringUtils;
import scw.http.jsonp.JsonpUtils;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.mvc.HttpChannel;
import scw.mvc.view.View;
import scw.net.FileMimeTypeUitls;
import scw.net.InetUtils;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.Entity;
import scw.net.message.InputMessage;
import scw.net.message.Text;
import scw.net.message.converter.MultiMessageConverter;

public abstract class AbstractHttpControllerOutput<T> implements HttpControllerOutput {
	private JSONSupport jsonSupport = JSONUtils.getJsonSupport();
	private final MultiMessageConverter messageConverter = new MultiMessageConverter();

	public AbstractHttpControllerOutput() {
		messageConverter.add(InetUtils.getMessageConverter());
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

		return body instanceof View || body instanceof InputMessage || body instanceof Text || body instanceof Resource
				|| body instanceof Entity || canWriteInternal(httpChannel, body);
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
			InetUtils.writeHeader((InputMessage) body, httpChannel.getResponse());
			writeBodyBefore(httpChannel, b);
			IOUtils.write(((InputMessage) body).getBody(), httpChannel.getResponse().getBody());
		} else if (body instanceof Text) {
			MimeType mimeType = ((Text) body).getMimeType();
			if (mimeType != null) {
				httpChannel.getResponse().setContentType(mimeType);
			}
			writeBodyBefore(httpChannel, b);
			httpChannel.getResponse().getWriter().write(((Text) body).getTextContent());
		} else if (body instanceof Resource) {
			Resource resource = (Resource) body;
			MimeType mimeType = FileMimeTypeUitls.getMimeType(resource);
			if (mimeType != null) {
				httpChannel.getResponse().setContentType(mimeType);
			}
			writeBodyBefore(httpChannel, b);
			IOUtils.write(resource.getInputStream(), httpChannel.getResponse().getBody());
		} else if (body instanceof Entity) {
			@SuppressWarnings("rawtypes")
			Entity entity = (Entity) body;
			if (getMessageConverter().canWrite(entity.getBody(), entity.getContentType())) {
				InetUtils.writeHeader(entity, httpChannel.getResponse());
				writeBodyBefore(httpChannel, b);
				getMessageConverter().write(entity.getBody(), entity.getContentType(), httpChannel.getResponse());
			}
		} else {
			writeBodyBefore(httpChannel, b);
			writeBody(httpChannel, b);
		}
		writeBodyAfter(httpChannel, b);
	}
}
