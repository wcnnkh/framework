package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;
import java.util.Arrays;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.convert.MessageConvertException;
import run.soeasy.framework.serializer.Serializer;

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
	protected byte[] toBinary(@NonNull TypedData<Object> body, @NonNull Message message, MediaType mediaType)
			throws IOException {
		return serializer.serialize(body.get());
	}

}
