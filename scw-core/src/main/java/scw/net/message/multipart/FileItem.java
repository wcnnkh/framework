package scw.net.message.multipart;

import scw.http.HttpInputMessage;

public interface FileItem extends HttpInputMessage, AutoCloseable {

	long getSize();

	/**
	 * 获取文件名
	 * 
	 * @return
	 */
	String getName();

	boolean isFormField();

	/**
	 * 字段名
	 * 
	 * @return
	 */
	String getFieldName();

	void close();
}
