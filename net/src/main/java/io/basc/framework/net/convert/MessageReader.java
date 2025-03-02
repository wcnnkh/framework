package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.Message;
import io.basc.framework.util.io.MimeType;
import lombok.NonNull;

public interface MessageReader {
	/**
	 * 是否可读
	 * 
	 * @param targetDescriptor
	 * @param message
	 * @param contentType      为空表示任意类型
	 * @return
	 */
	boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message, MimeType contentType);

	/**
	 * 读取
	 * 
	 * @param targetDescriptor
	 * @param message
	 * @param contentType      为空表示任意类型
	 * @return
	 * @throws IOException
	 */
	Object readFrom(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message, MimeType contentType)
			throws IOException;
}
