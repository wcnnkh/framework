package scw.http.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.fileupload.FileItemHeaders;

public class ApacheFileItem extends FileItem {
	private final org.apache.commons.fileupload.FileItem fileItem;

	public ApacheFileItem(org.apache.commons.fileupload.FileItem fileItem) {
		super(fileItem.getFieldName());
		this.fileItem = fileItem;
		FileItemHeaders fileItemHeaders = fileItem.getHeaders();
		Iterator<String> iterator = fileItemHeaders.getHeaderNames();
		while (iterator.hasNext()) {
			String name = iterator.next();
			Iterator<String> valueIterator = fileItemHeaders
					.getHeaders(name);
			while (valueIterator.hasNext()) {
				getHeaders().add(name, valueIterator.next());
			}
		}
		getHeaders().readyOnly();
	}

	public org.apache.commons.fileupload.FileItem getFileItem() {
		return fileItem;
	}

	@Override
	public long getContentLength() {
		return fileItem.getSize();
	}

	@Override
	public String getTextBody() {
		return fileItem.getString();
	}
	
	@Override
	public byte[] getBytes() {
		return fileItem.get();
	}

	public InputStream getBody() throws IOException {
		return fileItem.getInputStream();
	}
	
	@Override
	public boolean isFormField() {
		return fileItem.isFormField();
	}

	public void close() throws IOException {
		fileItem.delete();
	}
	
	@Override
	public String toString() {
		return fileItem.toString();
	}
}
