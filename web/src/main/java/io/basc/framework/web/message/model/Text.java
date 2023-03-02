package io.basc.framework.web.message.model;

import io.basc.framework.net.MimeType;

/**
 * 对于文本的定义，这里只是一个定义，不做具体实现
 * 
 * @author wcnnkh
 *
 */
public interface Text {
	String toTextContent();

	MimeType getMimeType();
}
