package run.soeasy.framework.net.convert;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.net.MediaType;
import run.soeasy.framework.net.Message;
import run.soeasy.framework.net.OutputMessage;
import run.soeasy.framework.util.io.MimeType;

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
