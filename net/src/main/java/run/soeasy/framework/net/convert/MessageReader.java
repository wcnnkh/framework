package run.soeasy.framework.net.convert;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.net.Message;
import run.soeasy.framework.util.io.MimeType;

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
