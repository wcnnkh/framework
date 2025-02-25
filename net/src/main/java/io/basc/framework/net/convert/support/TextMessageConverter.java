package io.basc.framework.net.convert.support;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.config.ConversionServiceAware;
import io.basc.framework.core.convert.support.DefaultConversionService;
import io.basc.framework.net.MediaType;
import io.basc.framework.util.io.MimeType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class TextMessageConverter extends AbstractTextMessageConverter<Object> implements ConversionServiceAware {
	public static final MediaType TEXT_ALL = new MediaType("text", "*");
	@NonNull
	private ConversionService conversionService = DefaultConversionService.getInstance();

	public TextMessageConverter() {
		super(Object.class);
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.TEXT_PLAIN, TEXT_ALL));
	}

	@Override
	public final boolean isReadable(@NonNull TargetDescriptor targetDescriptor, MimeType contentType) {
		return getConversionService().canConvert(TypeDescriptor.valueOf(String.class),
				targetDescriptor.getRequiredTypeDescriptor()) && super.isReadable(targetDescriptor, contentType);
	}

	@Override
	public final boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, MimeType contentType) {
		return getConversionService().canConvert(sourceDescriptor.getTypeDescriptor(),
				TypeDescriptor.valueOf(String.class)) && super.isWriteable(sourceDescriptor, contentType);
	}

	@Override
	protected Object parseObject(String body, TargetDescriptor targetDescriptor) throws IOException{
		return getConversionService().convert(body, TypeDescriptor.forObject(body),
				targetDescriptor.getRequiredTypeDescriptor());
	}

	@Override
	protected String toString(Data<Object> body, MediaType contentType, Charset charset) throws IOException {
		return (String) getConversionService().convert(body.any(), TypeDescriptor.valueOf(String.class));
	}
}
