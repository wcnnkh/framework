package run.soeasy.framework.messaging.convert;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.core.io.MimeType;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

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
