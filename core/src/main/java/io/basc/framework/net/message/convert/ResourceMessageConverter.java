package io.basc.framework.net.message.convert;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.MediaType;
import io.basc.framework.io.FileSystemResource;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.FileMimeTypeUitls;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.OutputMessage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ResourceMessageConverter extends AbstractMessageConverter<Resource> {
	private static Logger logger = LoggerFactory.getLogger(ResourceMessageConverter.class);

	public ResourceMessageConverter() {
		supportMimeTypes.add(MediaType.ALL);
	}

	@Override
	public boolean support(Class<?> clazz) {
		return Resource.class.isAssignableFrom(clazz) && File.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean canWrite(MimeType contentType) {
		if (MediaType.MULTIPART_FORM_DATA.equalsTypeAndSubtype(contentType)) {
			return false;
		}
		return super.canWrite(contentType);
	}

	@Override
	public boolean canRead(TypeDescriptor type, MimeType contentType) {
		return false;
	}

	@Override
	protected Resource readInternal(TypeDescriptor type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		throw new NotSupportedException(type.toString());
	}

	private Resource getResource(Object body) {
		if (body == null) {
			return null;
		}

		if (body instanceof Resource) {
			return (Resource) body;
		} else if (body instanceof File) {
			File file = (File) body;
			return new FileSystemResource(file);
		}

		throw new NotSupportedException(body.toString());
	}

	@Override
	public void write(TypeDescriptor type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		Resource resource = getResource(body);
		MimeType mimeType = contentType == null ? FileMimeTypeUitls.getMimeType(resource) : contentType;
		super.write(type, resource, mimeType, outputMessage);
	}

	@Override
	protected void writeInternal(TypeDescriptor type, Resource body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		Resource resource = (Resource) body;
		if (!resource.exists()) {
			logger.error("Not found {}", resource.getDescription());
			return;
		}

		InputStream is = resource.getInputStream();
		try {
			IOUtils.write(is, outputMessage.getOutputStream());
		} finally {
			is.close();
		}
	}
}
