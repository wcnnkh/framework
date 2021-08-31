package io.basc.framework.web.apache.multipart;

import io.basc.framework.net.MimeType;
import io.basc.framework.net.message.Headers;
import io.basc.framework.net.message.multipart.MultipartMessage;
import io.basc.framework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.fileupload.FileItem;

public class ApacheMultipartMessage implements MultipartMessage, Serializable {
	private static final long serialVersionUID = 1L;
	private final FileItem fileItem;

	public ApacheMultipartMessage(FileItem fileItem) {
		this.fileItem = fileItem;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return fileItem.getInputStream();
	}

	@Override
	public byte[] getBytes() throws IOException {
		return fileItem.get();
	}

	private ApacheFileItemHeaders headers;

	@Override
	public Headers getHeaders() {
		if (headers == null) {
			headers = new ApacheFileItemHeaders(fileItem.getHeaders());
		}
		return headers;
	}

	@Override
	public MimeType getContentType() {
		String contentType = fileItem.getContentType();
		if (StringUtils.isEmpty(contentType)) {
			return null;
		}

		return MimeType.valueOf(contentType);
	}

	@Override
	public long getContentLength() {
		return fileItem.getSize();
	}

	@Override
	public String getName() {
		return fileItem.getFieldName();
	}

	@Override
	public String getOriginalFilename() {
		return fileItem.getName();
	}

	@Override
	public long getSize() {
		return fileItem.getSize();
	}

	@Override
	public void close() {
		fileItem.delete();
	}
}