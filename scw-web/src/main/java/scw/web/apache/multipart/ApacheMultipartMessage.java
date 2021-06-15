package scw.web.apache.multipart;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.fileupload.FileItem;

import scw.core.utils.StringUtils;
import scw.net.MimeType;
import scw.net.message.Headers;
import scw.net.message.multipart.MultipartMessage;

public class ApacheMultipartMessage implements MultipartMessage, Serializable, Closeable {
	private static final long serialVersionUID = 1L;
	private final FileItem fileItem;

	public ApacheMultipartMessage(FileItem fileItem) {
		this.fileItem = fileItem;
	}

	@Override
	public InputStream getBody() throws IOException {
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
