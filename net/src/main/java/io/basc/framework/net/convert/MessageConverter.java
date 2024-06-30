package io.basc.framework.net.convert;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.net.MimeTypes;

public interface MessageConverter extends MessageReader, MessageWriter {
	/**
	 * 返回受支持的类型
	 * 
	 * @return
	 */
	MimeTypes getSupportedMediaTypes();

	/**
	 * Return the list of media types supported by this converter for the given
	 * class. The list may differ from {@link #getSupportedMediaTypes()} if the
	 * converter does not support the given Class or if it supports it only for a
	 * subset of media types.
	 * 
	 * @param typeDescriptor
	 * @return
	 */
	default MimeTypes getSupportedMediaTypes(TypeDescriptor typeDescriptor) {
		return (isReadable(typeDescriptor, null) || isWriteable(typeDescriptor, null)) ? getSupportedMediaTypes()
				: MimeTypes.EMPTY;
	}
}
