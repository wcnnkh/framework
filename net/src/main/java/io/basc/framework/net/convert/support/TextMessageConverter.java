package io.basc.framework.net.convert.support;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.service.ConversionServiceAware;
import io.basc.framework.core.convert.support.DefaultConversionService;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
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
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message request) {
		return getConversionService().canConvert(TypeDescriptor.valueOf(String.class),
				targetDescriptor.getRequiredTypeDescriptor()) && super.isReadable(targetDescriptor, request);
	}

	@Override
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message response) {
		return getConversionService().canConvert(sourceDescriptor.getTypeDescriptor(),
				TypeDescriptor.valueOf(String.class)) && super.isWriteable(sourceDescriptor, response);
	}

	@Override
	protected Object parseObject(String body, TargetDescriptor targetDescriptor) throws IOException {
		return getConversionService().convert(body, TypeDescriptor.forObject(body),
				targetDescriptor.getRequiredTypeDescriptor());
	}

	@Override
	protected String toString(Data<Object> body, MediaType contentType, Charset charset) throws IOException {
		return (String) getConversionService().convert(body.any(), TypeDescriptor.valueOf(String.class));
	}
}
