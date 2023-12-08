package io.basc.framework.net.message.convert;

import java.io.IOException;
import java.nio.charset.Charset;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.io.IOUtils;
import io.basc.framework.json.JsonSupportAccessor;
import io.basc.framework.lang.Constants;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.message.Headers;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.Message;
import io.basc.framework.net.message.OutputMessage;

public abstract class AbstractMessageConverter<T> extends JsonSupportAccessor implements MessageConverter {
	public static final MimeType TEXT_ALL = new MimeType("text", "*");
	private Charset charset = Constants.UTF_8;
	protected final MimeTypes supportMimeTypes = new MimeTypes();
	private boolean supportBytes = false;

	public boolean isSupportBytes() {
		return supportBytes;
	}

	public void setSupportBytes(boolean supportBytes) {
		this.supportBytes = supportBytes;
	}

	public final MimeTypes getSupportMimeTypes() {
		return supportMimeTypes.readyOnly();
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public boolean canRead(MimeType contentType) {
		if (contentType == null) {
			return true;
		}

		for (MimeType mimeType : getSupportMimeTypes()) {
			if (mimeType.includes(contentType)) {
				return true;
			}
		}
		return false;
	}

	public boolean canWrite(MimeType contentType) {
		if (contentType == null || MimeTypeUtils.ALL.equalsTypeAndSubtype(contentType)) {
			return true;
		}

		for (MimeType mimeType : getSupportMimeTypes()) {
			if (mimeType.isCompatibleWith(contentType)) {
				return true;
			}
		}
		return false;
	}

	public abstract boolean isSupported(Class<?> clazz);

	public boolean canWrite(TypeDescriptor type) {
		return isSupported(type.getResolvableType().getRawClass());
	}

	public boolean canRead(TypeDescriptor type) {
		return isSupported(type.getResolvableType().getRawClass());
	}

	public boolean canRead(TypeDescriptor type, MimeType contentType) {
		if ((type.isArray() && type.getResolvableType().getComponentType().getRawClass() == byte.class)
				&& !isSupportBytes()) {
			return false;
		}

		return canRead(type) && canRead(contentType);
	}

	public boolean canWrite(TypeDescriptor type, Object body, MimeType contentType) {
		if (body == null) {
			return false;
		}

		if (body.getClass() == byte[].class && !isSupportBytes()) {
			return false;
		}

		return canWrite(type) && canWrite(contentType);
	}

	public Object read(TypeDescriptor type, InputMessage inputMessage) throws IOException, MessageConvertException {
		return readInternal(type, inputMessage);
	}

	@SuppressWarnings("unchecked")
	public void write(TypeDescriptor type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		T t = (T) body;
		MimeType contentTypeToUse = contentType;
		if (contentType == null) {
			contentTypeToUse = outputMessage.getContentType();
		} else if (outputMessage.getContentType() == null) {
			if (contentTypeToUse == null || contentTypeToUse.isWildcardType() || contentTypeToUse.isWildcardSubtype()) {
				contentTypeToUse = getDefaultContentType(type, t);
			}

			if (contentTypeToUse != null && !contentTypeToUse.isWildcardType()
					&& !contentTypeToUse.isWildcardSubtype()) {
				if (contentTypeToUse.getCharset() == null) {
					Charset defaultCharset = getCharset();
					if (defaultCharset != null) {
						contentTypeToUse = new MimeType(contentTypeToUse, defaultCharset);
					}
				}
				outputMessage.setContentType(contentTypeToUse);
			}
		}

		if (outputMessage.getContentLength() < 0) {
			Headers headers = outputMessage.getHeaders();
			if (headers != null && headers.containsKey(HttpHeaders.TRANSFER_ENCODING)) {
				Long contentLength = getContentLength(t, outputMessage.getContentType());
				if (contentLength != null && contentLength >= 0) {
					outputMessage.setContentLength(contentLength);
				}
			}
		}
		writeInternal(type, t, contentTypeToUse, outputMessage);
	}

	protected MimeType getDefaultContentType(TypeDescriptor type, T body) throws IOException {
		MimeType mimeType = getSupportMimeTypes().getMimeTypes().first();
		if (mimeType.isWildcardType() || mimeType.isWildcardSubtype()) {
			return null;
		}
		return mimeType;
	}

	protected Long getContentLength(T body, MimeType contentType) throws IOException {
		return null;
	}

	protected Charset getCharset(Message message) {
		MimeType mimeType = message.getContentType();
		if (mimeType == null) {
			return getCharset();
		}

		Charset charset = mimeType.getCharset();
		if (charset == null) {
			return getCharset();
		}
		return charset;
	}

	protected String readTextBody(InputMessage inputMessage) throws IOException {
		return IOUtils.toString(inputMessage.getInputStream(), getCharset(inputMessage).name());
	}

	protected void writeTextBody(String text, MimeType contentType, OutputMessage outputMessage) throws IOException {
		if (text == null) {
			return;
		}

		IOUtils.write(text, outputMessage.getOutputStream(), getCharset(outputMessage).name());
	}

	protected abstract T readInternal(TypeDescriptor type, InputMessage inputMessage)
			throws IOException, MessageConvertException;

	protected abstract void writeInternal(TypeDescriptor type, T body, MimeType contentType,
			OutputMessage outputMessage) throws IOException, MessageConvertException;
}
