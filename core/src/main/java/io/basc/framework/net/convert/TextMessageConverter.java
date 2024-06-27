package io.basc.framework.net.convert;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.ConversionServiceAware;
import io.basc.framework.convert.support.GlobalConversionService;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextMessageConverter extends StringMessageConverter<Object> implements ConversionServiceAware {
	public static final MimeType TEXT_ALL = new MimeType("text", "*");
	private ConversionService conversionService;

	public TextMessageConverter() {
		getMimeTypes().add(MimeTypeUtils.TEXT_PLAIN, TEXT_ALL);
	}

	public ConversionService getConversionService() {
		return conversionService == null ? GlobalConversionService.getInstance() : conversionService;
	}

	@Override
	public boolean isReadable(TypeDescriptor typeDescriptor, MimeType contentType) {
		return getConversionService().canConvert(String.class, typeDescriptor)
				&& super.isReadable(typeDescriptor, contentType);
	}

	@Override
	public boolean isWriteable(TypeDescriptor typeDescriptor, MimeType contentType) {
		return getConversionService().canConvert(typeDescriptor, String.class)
				&& super.isWriteable(typeDescriptor, contentType);
	}

	@Override
	protected Object parseObject(String body, TypeDescriptor targetTypeDescriptor) {
		return getConversionService().convert(body, targetTypeDescriptor);
	}

	@Override
	protected String toString(TypeDescriptor typeDescriptor, Object body, MimeType contentType) {
		return getConversionService().convert(body, typeDescriptor, String.class);
	}
}
