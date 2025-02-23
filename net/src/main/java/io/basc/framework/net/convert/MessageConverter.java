package io.basc.framework.net.convert;

import io.basc.framework.core.convert.transform.stereotype.AccessDescriptor;
import io.basc.framework.net.MediaTypes;

public interface MessageConverter extends MessageReader, MessageWriter {
	/**
	 * 返回受支持的类型
	 * 
	 * @return
	 */
	MediaTypes getSupportedMediaTypes();

	/**
	 * Return the list of media types supported by this converter for the given
	 * class. The list may differ from {@link #getSupportedMediaTypes()} if the
	 * converter does not support the given Class or if it supports it only for a
	 * subset of media types.
	 * 
	 * @param requiredDescriptor
	 * @return
	 */
	default MediaTypes getSupportedMediaTypes(AccessDescriptor requiredDescriptor) {
		return (isReadable(requiredDescriptor, null) || isWriteable(requiredDescriptor, null))
				? getSupportedMediaTypes()
				: MediaTypes.EMPTY;
	}
}
