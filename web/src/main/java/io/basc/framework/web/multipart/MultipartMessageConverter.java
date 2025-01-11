package io.basc.framework.web.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.config.ConversionServiceAware;
import io.basc.framework.core.convert.support.DefaultConversionService;
import io.basc.framework.core.convert.transform.stractegy.CollectionFactory;
import io.basc.framework.generator.string.StringGenerator;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.convert.AbstractMessageConverter;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.io.IOUtils;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class MultipartMessageConverter extends AbstractMessageConverter implements ConversionServiceAware {
	private static final String BOUNDARY_APPEND = "--";
	private static final String BOUNDARY_NAME = "boundary";
	private static final String LINE = "\r\n";
	private static Logger logger = LogManager.getLogger(Logger.class);
	@NonNull
	private ConversionService conversionService = DefaultConversionService.getInstance();
	@NonNull
	private MultipartMessageResolver multipartMessageResolver = GlobalMultipartMessageResolver.getInstance();
	@NonNull
	private StringGenerator boundaryGenerator = () -> XUtils.getUUID();

	public MultipartMessageConverter() {
		getMimeTypes().add(MimeTypeUtils.MULTIPART_FORM_DATA);
	}

	protected boolean canReadType(TypeDescriptor type) {
		if (type.isArray() || type.isCollection()) {
			return type.getElementTypeDescriptor().getType() == MultipartMessage.class;
		}

		return type.getType() == MultipartMessage.class;
	}

	@Override
	public boolean isReadable(TypeDescriptor typeDescriptor, MimeType contentType) {
		return multipartMessageResolver != null && canReadType(typeDescriptor)
				&& super.isReadable(typeDescriptor, contentType);
	}

	@Override
	protected Object doRead(TypeDescriptor typeDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException {
		List<MultipartMessage> fileItems;
		if (multipartMessageResolver.isMultipart(inputMessage)) {
			fileItems = multipartMessageResolver.resolve(inputMessage);
		} else {
			MultipartMessage multipartMessage = new InputMessageToMultipartMessage(boundaryGenerator.next(), null,
					inputMessage);
			fileItems = Arrays.asList(multipartMessage);
		}
		return convert(fileItems, typeDescriptor);
	}

	protected Object convert(Collection<MultipartMessage> messages, TypeDescriptor typeDescriptor) {
		if (CollectionUtils.isEmpty(messages)) {
			return null;
		}

		if (typeDescriptor.getType() == messages.getClass()) {
			return messages;
		}

		if (typeDescriptor.isArray()) {
			return messages.toArray(new MultipartMessage[0]);
		} else if (typeDescriptor.isCollection()) {
			Collection<MultipartMessage> collections = CollectionFactory.createCollection(typeDescriptor.getType(),
					typeDescriptor.getElementTypeDescriptor().getType(), messages.size());
			collections.addAll(messages);
			return collections;
		} else {
			return Elements.of(messages).first();
		}
	}

	@Override
	protected void doWrite(Value source, MimeType contentType, OutputMessage outputMessage) throws IOException {
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
				boundary = boundaryGenerator.next();
				Map<String, String> map = new LinkedHashMap<String, String>(mimeType.getParameters());
				map.put(BOUNDARY_NAME, boundary);
				mimeType = new MimeType(mimeType, map);
				outputMessage.setContentType(mimeType);
			}
		} else {
			boundary = boundaryGenerator.next();
			Map<String, String> map = new LinkedHashMap<String, String>(mimeType.getParameters());
			map.put(BOUNDARY_NAME, boundary);
			mimeType = new MimeType(MimeTypeUtils.MULTIPART_FORM_DATA, map);
			outputMessage.setContentType(mimeType);
		}

		write(boundary, source.getValue(), source.getTypeDescriptor(), outputMessage, contentType);
		OutputStreamWriter osw = new OutputStreamWriter(outputMessage.getOutputStream(), contentType.getCharset());
		osw.write(LINE);
		osw.write(BOUNDARY_APPEND);
		osw.write(boundary);
		osw.write(BOUNDARY_APPEND);
		osw.write(LINE);
		osw.flush();
	}

	private void writeMultipartMessage(String boundary, MultipartMessage source, OutputMessage target,
			MimeType targetContentType) throws IOException {
		OutputStream outputStream = target.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(outputStream, targetContentType.getCharset());
		osw.write(LINE);
		osw.write(BOUNDARY_APPEND);
		osw.write(boundary);
		osw.write(LINE);
		for (Entry<String, List<String>> entry : source.getHeaders().entrySet()) {
			osw.write(entry.getKey() + ": " + StringUtils.collectionToDelimitedString(entry.getValue(), ", "));
			osw.write(LINE);
		}
		osw.write(LINE);
		osw.flush();
		InputStream inputStream = source.getInputStream();
		IOUtils.write(inputStream, outputStream);
		outputStream.flush();
	}

	protected void writeItem(String boundary, String fieldName, Object source, TypeDescriptor sourceTypeDescriptor,
			OutputMessage target, MimeType targetContentType) throws IOException {
		if (source instanceof File) {
			File file = (File) source;
			if (!file.exists()) {
				logger.error("non existent file [{}]", file.getPath());
				return;
			}

			MultipartMessage multipartMessage = new ResourceMultipartMessage(fieldName, file);
			writeMultipartMessage(boundary, multipartMessage, target, targetContentType);
		} else if (source instanceof Resource) {
			Resource resource = (Resource) source;
			if (!resource.exists()) {
				logger.error("non existent resource [{}]", resource.getDescription());
				return;
			}
			MultipartMessage multipartMessage = new ResourceMultipartMessage(fieldName, resource);
			writeMultipartMessage(boundary, multipartMessage, target, targetContentType);
		} else if (Value.isElements(sourceTypeDescriptor)) {
			Elements<Value> elements = Value.asElements(source, sourceTypeDescriptor);
			for (Value item : elements) {
				writeItem(boundary, fieldName, item.getValue(), item.getTypeDescriptor(), target, targetContentType);
			}
		} else {
			Charset charset = targetContentType.getCharset();
			String content = conversionService.convert(source, sourceTypeDescriptor, String.class);
			MultipartMessage multipartMessage = new FromMultipartMessage(fieldName, content.getBytes(charset));
			writeMultipartMessage(boundary, multipartMessage, target, targetContentType);
		}
	}

	@SuppressWarnings({ "unchecked" })
	private void write(String boundary, Object source, TypeDescriptor sourceTypeDescriptor, OutputMessage target,
			MimeType targetContentType) throws IOException {
		if (source instanceof MultipartMessage) {
			writeMultipartMessage(boundary, (MultipartMessage) source, target, targetContentType);
		} else if (conversionService.canConvert(sourceTypeDescriptor, MultipartMessage.class)) {
			MultipartMessage message = conversionService.convert(source, sourceTypeDescriptor, MultipartMessage.class);
			writeMultipartMessage(boundary, message, target, targetContentType);
		} else if (source instanceof File) {
			writeItem(boundary, "file", source, sourceTypeDescriptor, target, targetContentType);
		} else if (source instanceof Resource) {
			writeItem(boundary, "resource", source, sourceTypeDescriptor, target, targetContentType);
		} else if (source instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) source;
			TypeDescriptor keyTypeDescriptor = sourceTypeDescriptor.getMapKeyTypeDescriptor();
			TypeDescriptor valueTypeDescriptor = sourceTypeDescriptor.getMapValueTypeDescriptor();
			for (Entry<?, ?> entry : map.entrySet()) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				if (key == null || value == null) {
					continue;
				}

				String fieldName = toString(key, keyTypeDescriptor);
				writeItem(boundary, fieldName, value, valueTypeDescriptor, target, targetContentType);
			}
		} else if (Value.isElements(sourceTypeDescriptor)) {
			Elements<Value> elements = Value.asElements(source, sourceTypeDescriptor);
			for (Value item : elements) {
				write(boundary, item.getValue(), item.getTypeDescriptor(), target, targetContentType);
			}
		} else {
			TypeDescriptor mapTypeDescriptor = TypeDescriptor.map(LinkedHashMap.class, String.class, Object.class);
			if (conversionService.canConvert(sourceTypeDescriptor, mapTypeDescriptor)) {
				Map<String, Object> map = (Map<String, Object>) conversionService.convert(source, sourceTypeDescriptor,
						mapTypeDescriptor);
				for (Entry<String, Object> entry : map.entrySet()) {
					writeItem(boundary, entry.getKey(), entry.getValue(), TypeDescriptor.valueOf(Object.class), target,
							targetContentType);
				}
			} else {
				String body = toString(source, sourceTypeDescriptor);
				writeItem(boundary, "body", body, TypeDescriptor.valueOf(String.class), target, targetContentType);
			}
		}
	}

	private String toString(Object source, TypeDescriptor sourceTypeDescriptor) {
		return conversionService.canConvert(sourceTypeDescriptor, String.class)
				? conversionService.convert(sourceTypeDescriptor, String.class)
				: ObjectUtils.toString(source);
	}
}
