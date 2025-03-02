package io.basc.framework.net.convert.support;

import java.io.IOException;
import java.util.Arrays;

import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.util.io.MimeType;
import lombok.NonNull;

public class ByteArrayMessageConverter extends AbstractBinaryMessageConverter<byte[]> {

	public ByteArrayMessageConverter() {
		super(byte[].class);
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
	}

	@Override
	protected byte[] parseObject(byte[] body, @NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) throws IOException {
		return body;
	}

	@Override
	protected byte[] toBinary(@NonNull Data<byte[]> body, @NonNull Message message, MediaType mediaType)
			throws IOException {
		return body.orElse(null);
	}
}
