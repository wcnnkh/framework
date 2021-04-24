package scw.net.message.multipart;

import scw.core.utils.StringUtils;
import scw.http.HttpHeaders;
import scw.http.HttpInputMessage;

public abstract class AbstractFileItem implements HttpInputMessage, FileItem {
	private final HttpHeaders httpHeaders = new HttpHeaders();
	private final String fieldName;

	public AbstractFileItem(String fieldName) {
		this.fieldName = fieldName;
	}

	public HttpHeaders getHeaders() {
		return httpHeaders;
	}

	/**
	 * 获取文件名
	 * 
	 * @return
	 */
	public String getName() {
		return null;
	}

	public boolean isFormField() {
		return StringUtils.isEmpty(getName());
	}

	/**
	 * 字段名
	 * 
	 * @return
	 */
	public String getFieldName() {
		return fieldName;
	}
}
