package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.io.MimeType;
import lombok.NonNull;

public interface MessageWriter {
	/**
	 * 是否可写
	 * 
	 * @param sourceDescriptor
	 * @param message
	 * @param contentType      为空表示任意类型
	 * @return
	 */
	boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message, MimeType contentType);

	/**
	 * 写入
	 * 
	 * @param source
	 * @param message
	 * @param contentType 为空表示任意类型
	 * @throws IOException
	 */
	void writeTo(@NonNull Source source, @NonNull OutputMessage message, MediaType contentType) throws IOException;
}
