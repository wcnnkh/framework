package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterAware;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;

@Getter
@Setter
public class TextMessageConverter extends AbstractTextMessageConverter<Object> implements ConverterAware {
	public static final MediaType TEXT_ALL = new MediaType("text", "*");
	@NonNull
	private Converter converter = SystemConversionService.getInstance();

	public TextMessageConverter() {
		super(Object.class);
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.TEXT_PLAIN, TEXT_ALL));
	}

	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) {
		return getConverter().canConvert(TypeDescriptor.valueOf(String.class),
				targetDescriptor.getRequiredTypeDescriptor())
				&& super.isReadable(targetDescriptor, message, contentType);
	}

	@Override
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
			MimeType contentType) {
		return getConverter().canConvert(sourceDescriptor.getReturnTypeDescriptor(),
				TypeDescriptor.valueOf(String.class)) && super.isWriteable(sourceDescriptor, message, contentType);
	}

	@Override
	protected Object parseObject(String body, TargetDescriptor targetDescriptor, Charset charset) throws IOException {
		return getConverter().convert(body, TypeDescriptor.forObject(body),
				targetDescriptor.getRequiredTypeDescriptor());
	}

	@Override
	protected String toString(TypedData<Object> body, MediaType contentType, Charset charset) throws IOException {
		return (String) getConverter().convert(body.value(), TypeDescriptor.valueOf(String.class));
	}
}
