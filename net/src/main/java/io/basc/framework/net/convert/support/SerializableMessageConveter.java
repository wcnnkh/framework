package io.basc.framework.net.convert.support;

import java.io.IOException;
import java.util.Arrays;

import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.convert.MessageConvertException;
import io.basc.framework.util.io.MimeType;
import io.basc.framework.util.io.serializer.Serializer;
import lombok.NonNull;

public class SerializableMessageConveter extends AbstractBinaryMessageConverter<Object> {
	private final Serializer serializer;

	public SerializableMessageConveter(Serializer serializer) {
		super(Object.class);
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
		this.serializer = serializer;
	}

	@Override
	protected Object parseObject(byte[] body, @NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) throws IOException {
		try {
			return serializer.deserialize(body);
		} catch (ClassNotFoundException e) {
			throw new MessageConvertException(e);
		}
	}

	@Override
	protected byte[] toBinary(@NonNull Data<Object> body, @NonNull Message message, MediaType mediaType)
			throws IOException {
		return serializer.serialize(body.get());
	}

}
