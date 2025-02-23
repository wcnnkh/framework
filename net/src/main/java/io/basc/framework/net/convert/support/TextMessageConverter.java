package io.basc.framework.net.convert.support;

import java.util.Arrays;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.config.ConversionServiceAware;
import io.basc.framework.core.convert.support.DefaultConversionService;
import io.basc.framework.net.MediaType;
import io.basc.framework.util.io.MimeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextMessageConverter extends StringMessageConverter<Object> implements ConversionServiceAware {
	public static final MediaType TEXT_ALL = new MediaType("text", "*");
	private ConversionService conversionService;

	public TextMessageConverter() {
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.TEXT_PLAIN, TEXT_ALL));
	}

	public ConversionService getConversionService() {
		return conversionService == null ? DefaultConversionService.getInstance() : conversionService;
	}

	@Override
	public boolean isReadable(TypeDescriptor typeDescriptor, MimeType contentType) {
		return getConversionService().canConvert(TypeDescriptor.valueOf(String.class), typeDescriptor)
				&& super.isReadable(typeDescriptor, contentType);
	}

	@Override
	public boolean isWriteable(TypeDescriptor typeDescriptor, MimeType contentType) {
		return getConversionService().canConvert(typeDescriptor, TypeDescriptor.valueOf(String.class))
				&& super.isWriteable(typeDescriptor, contentType);
	}

	@Override
	protected Object parseObject(String body, TypeDescriptor targetTypeDescriptor) {
		return getConversionService().convert(body, TypeDescriptor.forObject(body), targetTypeDescriptor);
	}

	@Override
	protected String toString(TypeDescriptor typeDescriptor, Object body, MimeType contentType) {
		return (String) getConversionService().convert(body, typeDescriptor, TypeDescriptor.valueOf(String.class));
	}
}
