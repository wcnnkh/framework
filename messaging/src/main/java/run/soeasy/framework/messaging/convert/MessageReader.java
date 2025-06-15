package run.soeasy.framework.messaging.convert;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.Message;

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
