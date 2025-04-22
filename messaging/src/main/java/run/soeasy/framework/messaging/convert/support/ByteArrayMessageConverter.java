package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;
import java.util.Arrays;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.Data;
import run.soeasy.framework.core.convert.value.Writeable;
import run.soeasy.framework.core.io.MimeType;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;

public class ByteArrayMessageConverter extends AbstractBinaryMessageConverter<byte[]> {

	public ByteArrayMessageConverter() {
		super(byte[].class);
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
	}

	@Override
	protected byte[] parseObject(byte[] body, @NonNull Writeable targetDescriptor, @NonNull Message message,
			MimeType contentType) throws IOException {
		return body;
	}

	@Override
	protected byte[] toBinary(@NonNull Data<byte[]> body, @NonNull Message message, MediaType mediaType)
			throws IOException {
		return body.orElse(null);
	}
}
