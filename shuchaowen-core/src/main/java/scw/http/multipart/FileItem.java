package scw.http.multipart;

import java.io.Closeable;

import scw.http.AbstractHttpInputMessage;
import scw.http.HttpHeaders;
import scw.http.HttpInputMessage;

public abstract class FileItem extends AbstractHttpInputMessage implements HttpInputMessage, Closeable {
	private final HttpHeaders httpHeaders = new HttpHeaders();
	private final String fieldName;

	public FileItem(String fieldName) {
		this.fieldName = fieldName;
	}

	public HttpHeaders getHeaders() {
		return httpHeaders;
	}

	/**
	 * 获取文件名
	 * @return
	 */
	public String getName() {
		return null;
	}
	
	public boolean isFormField(){
		return getName() == null;
	}

	/**
	 * 字段名
	 * @return
	 */
	public String getFieldName() {
		return fieldName;
	}
}
