package io.basc.framework.net.multipart;

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

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.service.ConversionServiceAware;
import io.basc.framework.core.convert.support.DefaultConversionService;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.net.Response;
import io.basc.framework.net.convert.support.AbstractMessageConverter;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.io.IOUtils;
import io.basc.framework.util.io.MimeType;
import io.basc.framework.util.io.Resource;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.sequences.StringSequence;
import io.basc.framework.util.sequences.uuid.UUIDSequences;
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
	private StringSequence boundarySequence = UUIDSequences.global();

	public MultipartMessageConverter() {
		getMediaTypeRegistry().add(MediaType.MULTIPART_FORM_DATA);
	}

	protected boolean canReadType(TargetDescriptor targetDescriptor) {
		if (targetDescriptor.getRequiredTypeDescriptor().isArray()
				|| targetDescriptor.getRequiredTypeDescriptor().isCollection()) {
			return targetDescriptor.getRequiredTypeDescriptor().getElementTypeDescriptor()
					.getType() == MultipartMessage.class;
		}

		return targetDescriptor.getRequiredTypeDescriptor().getType() == MultipartMessage.class;
	}

	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message request) {
		return multipartMessageResolver != null && canReadType(targetDescriptor)
				&& super.isReadable(targetDescriptor, request);
	}

	@Override
	protected Object doRead(@NonNull TargetDescriptor targetDescriptor, MimeType contentType,
			@NonNull InputMessage request, @NonNull Response response) throws IOException {
		List<MultipartMessage> fileItems;
		if (multipartMessageResolver.isMultipart(request)) {
			fileItems = multipartMessageResolver.resolve(request);
		} else {
			MultipartMessage multipartMessage = new InputMessageToMultipartMessage(boundarySequence.next(), null,
					request);
			fileItems = Arrays.asList(multipartMessage);
		}
		return convert(fileItems, targetDescriptor.getRequiredTypeDescriptor());
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
			Collection<MultipartMessage> collections = CollectionUtils.createCollection(typeDescriptor.getType(),
					typeDescriptor.getElementTypeDescriptor().getType(), messages.size());
			collections.addAll(messages);
			return collections;
		} else {
			return Elements.of(messages).first();
		}
	}

	@Override
	protected void doWrite(Source source, MediaType contentType, Request request, OutputMessage outputMessage)
			throws IOException {
		String boundary;
		MediaType mimeType = contentType;
		if (contentType != null) {
			boundary = contentType.getParameter(BOUNDARY_NAME);
			if (StringUtils.isEmpty(boundary)) {
				MimeType outputMessageContentType = outputMessage.getContentType();
				if (outputMessageContentType != null) {
					boundary = outputMessageContentType.getParameter(BOUNDARY_NAME);
				}
			}

			if (StringUtils.isEmpty(boundary)) {
				boundary = boundarySequence.next();
				Map<String, String> map = new LinkedHashMap<String, String>(mimeType.getParameters());
				map.put(BOUNDARY_NAME, boundary);
				mimeType = new MediaType(mimeType, map);
				outputMessage.setContentType(mimeType);
			}
		} else {
			boundary = boundarySequence.next();
			Map<String, String> map = new LinkedHashMap<String, String>(mimeType.getParameters());
			map.put(BOUNDARY_NAME, boundary);
			mimeType = new MediaType(MediaType.MULTIPART_FORM_DATA, map);
			outputMessage.setContentType(mimeType);
		}

		write(boundary, source, outputMessage, contentType);
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

	protected void writeItem(String boundary, String fieldName, Source source, OutputMessage target,
			MimeType targetContentType) throws IOException {
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
		} else if (source.isMultiple()) {
			Elements<? extends Source> elements = source.getAsElements();
			for (Source item : elements) {
				writeItem(boundary, fieldName, item, target, targetContentType);
			}
		} else {
			Charset charset = targetContentType.getCharset();
			String content = (String) conversionService.convert(source, TypeDescriptor.valueOf(String.class));
			MultipartMessage multipartMessage = new FromMultipartMessage(fieldName, content.getBytes(charset));
			writeMultipartMessage(boundary, multipartMessage, target, targetContentType);
		}
	}

	@SuppressWarnings({ "unchecked" })
	private void write(String boundary, Source source, OutputMessage target, MimeType targetContentType)
			throws IOException {
		if (source instanceof MultipartMessage) {
			writeMultipartMessage(boundary, (MultipartMessage) source, target, targetContentType);
		} else if (conversionService.canConvert(source.getTypeDescriptor(), MultipartMessage.TYPE_DESCRIPTOR)) {
			MultipartMessage message = (MultipartMessage) conversionService.convert(source,
					MultipartMessage.TYPE_DESCRIPTOR);
			writeMultipartMessage(boundary, message, target, targetContentType);
		} else if (source instanceof File) {
			writeItem(boundary, "file", source, target, targetContentType);
		} else if (source instanceof Resource) {
			writeItem(boundary, "resource", source, target, targetContentType);
		} else if (source instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) source;
			TypeDescriptor keyTypeDescriptor = source.getTypeDescriptor().getMapKeyTypeDescriptor();
			TypeDescriptor valueTypeDescriptor = source.getTypeDescriptor().getMapValueTypeDescriptor();
			for (Entry<?, ?> entry : map.entrySet()) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				if (key == null || value == null) {
					continue;
				}

				String fieldName = toString(Source.of(key, keyTypeDescriptor));
				writeItem(boundary, fieldName, Source.of(value, valueTypeDescriptor), target, targetContentType);
			}
		} else if (source.isMultiple()) {
			for (Source item : source.getAsElements()) {
				write(boundary, item, target, targetContentType);
			}
		} else {
			TypeDescriptor mapTypeDescriptor = TypeDescriptor.map(LinkedHashMap.class, String.class, Object.class);
			if (conversionService.canConvert(source.getTypeDescriptor(), mapTypeDescriptor)) {
				Map<String, Object> map = (Map<String, Object>) conversionService.convert(source, mapTypeDescriptor);
				for (Entry<String, Object> entry : map.entrySet()) {
					writeItem(boundary, entry.getKey(), Source.of(entry.getValue()), target, targetContentType);
				}
			} else {
				String body = toString(source);
				writeItem(boundary, "body", Source.of(body), target, targetContentType);
			}
		}
	}

	private String toString(Source source) {
		return conversionService.canConvert(source.getTypeDescriptor(), TypeDescriptor.valueOf(String.class))
				? (String) conversionService.convert(source, TypeDescriptor.valueOf(String.class))
				: ObjectUtils.toString(source);
	}
}
