package io.basc.framework.net.message;

import io.basc.framework.io.OutputStreamSourceWrapper;
import io.basc.framework.net.MimeType;

public class OutputMessageWrapper<O extends OutputMessage> extends OutputStreamSourceWrapper<O>
		implements OutputMessage {

	public OutputMessageWrapper(O wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Headers getHeaders() {
		return wrappedTarget.getHeaders();
	}

	@Override
	public MimeType getContentType() {
		return wrappedTarget.getContentType();
	}

	@Override
	public long getContentLength() {
		return wrappedTarget.getContentLength();
	}

	@Override
	public void setContentType(MimeType contentType) {
		wrappedTarget.setContentType(contentType);
	}

	@Override
	public void setContentLength(long contentLength) {
		wrappedTarget.setContentLength(contentLength);
	}

	@Override
	public void setCharacterEncoding(String charsetName) {
		wrappedTarget.setCharacterEncoding(charsetName);
	}
	
	@Override
	public String getCharacterEncoding() {
		return wrappedTarget.getCharacterEncoding();
	}
}
