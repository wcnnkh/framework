package io.basc.framework.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import io.basc.framework.io.OutputStreamSource;
import io.basc.framework.io.WriterSource;
import io.basc.framework.util.StringUtils;

public interface OutputMessage extends Message, OutputStreamSource, WriterSource {
	void setContentType(MimeType contentType);

	void setContentLength(long contentLength);

	default void setCharacterEncoding(String charsetName) {
		MimeType mimeType = getContentType();
		if (mimeType == null) {
			return;
		}

		setContentType(new MimeType(mimeType, charsetName));
	}

	@Override
	default Writer getWriter() throws IOException {
		OutputStream outputStream = getOutputStream();
		String charsetName = getCharacterEncoding();
		return StringUtils.isEmpty(charsetName) ? new OutputStreamWriter(outputStream)
				: new OutputStreamWriter(outputStream, charsetName);
	}
}
