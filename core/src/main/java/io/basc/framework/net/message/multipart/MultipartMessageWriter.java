package io.basc.framework.net.message.multipart;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.OutputMessage;
import io.basc.framework.net.message.convert.AbstractMessageConverter;
import io.basc.framework.net.message.convert.MessageConvertException;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MultipartMessageWriter extends AbstractMessageConverter<Object> {
	private static Logger logger = LoggerFactory.getLogger(MultipartMessageWriter.class);
	private static final String DEFAULT_BOUNDARY = XUtils.getUUID();
	private static final String BOUNDARY_NAME = "boundary";
	private static final String LINE = "\r\n";
	private static final String BOUNDARY_APPEND = "--";

	public MultipartMessageWriter() {
		supportMimeTypes.add(MimeTypeUtils.MULTIPART_FORM_DATA);
	}

	@Override
	public boolean support(Class<?> clazz) {
		return true;
	}

	@Override
	public boolean canRead(TypeDescriptor type) {
		return false;
	}

	@Override
	protected Object readInternal(TypeDescriptor type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void writeInternal(TypeDescriptor type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		String boundary;
		MimeType mimeType = contentType;
		if (contentType != null) {
			boundary = contentType.getParameter(BOUNDARY_NAME);
			if (StringUtils.isEmpty(boundary)) {
				MimeType outputMessageContentType = outputMessage.getContentType();
				if (outputMessageContentType != null) {
					boundary = outputMessageContentType.getParameter(BOUNDARY_NAME);
				}
			}

			if (StringUtils.isEmpty(boundary)) {
				boundary = DEFAULT_BOUNDARY;
				Map<String, String> map = new LinkedHashMap<String, String>(mimeType.getParameters());
				map.put(BOUNDARY_NAME, boundary);
				mimeType = new MimeType(mimeType, map);
				outputMessage.setContentType(mimeType);
			}
		} else {
			boundary = DEFAULT_BOUNDARY;
			Map<String, String> map = new LinkedHashMap<String, String>(mimeType.getParameters());
			map.put(BOUNDARY_NAME, boundary);
			outputMessage.setContentType(new MimeType(MimeTypeUtils.MULTIPART_FORM_DATA, map));
		}

		if (body instanceof MultipartMessage) {
			writeItem(boundary, (MultipartMessage) body, outputMessage);
		} else if (body.getClass().isArray()) {
			int len = Array.getLength(body);
			for (int i = 0; i < len; i++) {
				Object item = Array.get(body, i);
				if (item == null) {
					continue;
				}

				writeItem(boundary, item, outputMessage);
			}
		} else if (Iterable.class.isAssignableFrom(body.getClass())) {
			for (Object item : (Iterable) body) {
				if (item == null) {
					continue;
				}

				writeItem(boundary, item, outputMessage);
			}
		} else {
			writeItem(boundary, body, outputMessage);
		}

		OutputStreamWriter osw = new OutputStreamWriter(outputMessage.getOutputStream(), getCharset(outputMessage));
		osw.write(LINE);
		osw.write(BOUNDARY_APPEND);
		osw.write(boundary);
		osw.write(BOUNDARY_APPEND);
		osw.write(LINE);
		osw.flush();
	}

	protected void writeItem(String boundary, String fieldName, Object value, OutputMessage outputMessage)
			throws IOException {
		MultipartMessage multipartMessage;
		if (value instanceof File) {
			File file = (File) value;
			if (!file.exists()) {
				logger.error("non existent file [{}]", file.getPath());
				return;
			}

			multipartMessage = new ResourceMultipartMessage(fieldName, (File) value);
		} else if (value instanceof Resource) {
			Resource resource = (Resource) value;
			if (!resource.exists()) {
				logger.error("non existent resource [{}]", resource.getDescription());
				return;
			}
			multipartMessage = new ResourceMultipartMessage(fieldName, resource);
		} else {
			Charset charset = getCharset(outputMessage);
			String content = getJsonSupport().toJsonString(value);
			multipartMessage = new FromMultipartMessage(fieldName, content.getBytes(charset));
		}
		writeItem(boundary, multipartMessage, outputMessage);
	}

	protected void writeItem(String boundary, Object multipartItem, OutputMessage outputMessage) throws IOException {
		if (multipartItem instanceof MultipartMessage) {
			writeItem(boundary, (MultipartMessage) multipartItem, outputMessage);
		} else if (multipartItem instanceof File) {
			writeItem(boundary, "file", multipartItem, outputMessage);
		} else if (multipartItem instanceof Map) {
			for (Entry<?, ?> entry : ((Map<?, ?>) multipartItem).entrySet()) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				if (key == null || value == null) {
					continue;
				}

				if (value.getClass().isArray()) {
					int len = Array.getLength(value);
					for (int i = 0; i < len; i++) {
						Object item = Array.get(value, i);
						if (item == null) {
							continue;
						}

						writeItem(boundary, item, outputMessage);
					}
				} else if (value instanceof Iterable) {
					for (Object item : (Iterable<?>) value) {
						if (item == null) {
							continue;
						}

						writeItem(boundary, item, outputMessage);
					}
				} else {
					writeItem(boundary, key.toString(), value, outputMessage);
				}
			}
		} else {
			throw new NotSupportedException(multipartItem.toString());
		}
	}

	protected void writeItem(String boundary, MultipartMessage multipartMessage, OutputMessage outputMessage)
			throws IOException {
		OutputStream os = outputMessage.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os, getCharset(outputMessage));
		osw.write(LINE);
		osw.write(BOUNDARY_APPEND);
		osw.write(boundary);
		osw.write(LINE);
		for (Entry<String, List<String>> entry : multipartMessage.getHeaders().entrySet()) {
			osw.write(entry.getKey() + ": " + StringUtils.collectionToDelimitedString(entry.getValue(), ", "));
			osw.write(LINE);
		}
		osw.write(LINE);
		osw.flush();
		InputStream inputStream = multipartMessage.getInputStream();
		IOUtils.write(inputStream, os);
		os.flush();
	}
}