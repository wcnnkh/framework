package scw.mvc.output;

import java.nio.charset.Charset;

import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.mvc.Channel;
import scw.mvc.View;
import scw.net.MimeType;
import scw.net.NetworkUtils;
import scw.net.Text;
import scw.net.message.InputMessage;

public abstract class AbstractOutput<C extends Channel, T> implements Output {
	private JSONSupport jsonSupport = JSONUtils.DEFAULT_JSON_SUPPORT;

	public JSONSupport getJsonSupport() {
		return jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	public boolean canWrite(Channel channel, Object body) {
		if (body == null) {
			return false;
		}

		if (body instanceof InputMessage) {
			return true;
		}

		if (body instanceof View) {
			return true;
		}

		if (body instanceof Text) {
			return true;
		}

		return canWriteInternal(channel, body);
	}

	protected abstract boolean canWriteInternal(Channel channel, Object body);

	protected void writeView(Channel channel, View view) throws Throwable {
		view.render(channel);
	}

	@SuppressWarnings("unchecked")
	public void write(Channel channel, Object body) throws Throwable {
		if (body instanceof View) {
			writeView(channel, (View) body);
			return;
		}

		C wrapperChannel = (C) channel;
		T b = (T) body;
		if (body instanceof InputMessage) {
			NetworkUtils
					.writeHeader((InputMessage) body, channel.getResponse());
			appendHeader(wrapperChannel, b);
			writeBodyBefore(wrapperChannel, b);
			if (channel.isLogEnabled()) {
				channel.log(body);
			}

			NetworkUtils.write((InputMessage) body, channel.getResponse());
			writeBodyAfter(wrapperChannel, b);
			return;
		}

		if (body instanceof Text) {
			MimeType mimeType = ((Text) body).getMimeType();
			if (mimeType != null) {
				channel.getResponse().setContentType(mimeType);
			}

			appendHeader(wrapperChannel, b);
			String text = ((Text) body).getTextContent();
			if (channel.isLogEnabled()) {
				channel.log(text);
			}

			if (text != null) {
				channel.getResponse().getWriter().write(text);
			}
			return;
		}

		appendHeader(wrapperChannel, b);
		writeBodyBefore(wrapperChannel, b);
		writeBody(wrapperChannel, b);
		writeBodyAfter(wrapperChannel, b);
	}

	protected void appendHeader(C channel, T body) {
		if (channel.getResponse().getContentLength() < 0) {
			Long contentLength = getContentLength(channel, body);
			if (contentLength != null && contentLength >= 0) {
				channel.getResponse().setContentLength(contentLength);
			}
		}

		MimeType contentType = channel.getResponse().getContentType();
		if (contentType == null) {
			contentType = getContentType(channel, body);
		}

		Charset charset = contentType.getCharset();
		if (charset == null) {
			String charsetName = getCharsetName(channel, body);
			if (charsetName != null) {
				contentType = createContentType(contentType, charsetName);
			}
		}
		channel.getResponse().setContentType(contentType);
	}

	protected MimeType createContentType(MimeType mimeType, String charsetName) {
		return new MimeType(mimeType, charsetName);
	}

	protected String getCharsetName(C channel, T body) {
		String charsetName = null;
		MimeType contentType = channel.getResponse().getContentType();
		if (contentType != null) {
			charsetName = contentType.getCharsetName();
		}

		if (charsetName == null) {
			charsetName = channel.getResponse().getCharacterEncoding();
		}

		if (charsetName == null) {
			contentType = channel.getRequest().getContentType();
			if (contentType != null) {
				charsetName = contentType.getCharsetName();
			}
		}

		if (charsetName == null) {
			charsetName = channel.getRequest().getCharacterEncoding();
		}
		return charsetName;
	}

	protected MimeType getContentType(C channel, T body) {
		return null;
	}

	protected Long getContentLength(C channel, T body) {
		return null;
	}

	protected void writeBodyBefore(C channel, T body) throws Throwable {
	}

	protected abstract void writeBody(C channel, T body) throws Throwable;

	protected void writeBodyAfter(C channel, T body) throws Throwable {
	}
}
