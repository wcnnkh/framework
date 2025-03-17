package run.soeasy.framework.net.convert.support;

import java.io.IOException;
import java.util.Arrays;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Data;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.net.MediaType;
import run.soeasy.framework.net.Message;
import run.soeasy.framework.util.io.MimeType;

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
