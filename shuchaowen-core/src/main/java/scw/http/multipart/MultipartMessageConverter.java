package scw.http.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.ResolvableType;
import scw.core.instance.InstanceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.core.utils.XUtils;
import scw.http.DefaultHttpInputMessage;
import scw.http.HttpInputMessage;
import scw.io.IOUtils;
import scw.lang.NotSupportedException;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.net.message.converter.AbstractMessageConverter;
import scw.net.message.converter.MessageConvertException;

public class MultipartMessageConverter extends AbstractMessageConverter<Object> {
	private static final String DEFAULT_BOUNDARY = XUtils.getUUID();
	private static final String BOUNDARY_NAME = "boundary";
	private static final String LINE = "\r\n";
	private static final String BOUNDARY_APPEND = "--";
	private static final FileItemParser FILE_ITEM_PARSER = InstanceUtils.loadService(FileItemParser.class, "scw.http.multipart.ApacheFileItemParser");
	
	private FileItemParser fileItemParser = FILE_ITEM_PARSER;
	public MultipartMessageConverter() {
		supportMimeTypes.add(MimeTypeUtils.MULTIPART_FORM_DATA);
	}

	public FileItemParser getFileItemParser() {
		return fileItemParser;
	}

	public void setFileItemParser(FileItemParser fileItemParser) {
		this.fileItemParser = fileItemParser;
	}

	@Override
	public boolean support(Class<?> clazz) {
		return true;
	}
	
	@Override
	public boolean canRead(Type type) {
		if(getFileItemParser() == null){
			return false;
		}
		
		ResolvableType resolvableType = ResolvableType.forType(type);
		if(Iterable.class.isAssignableFrom(resolvableType.getRawClass())){
			return resolvableType.getGeneric(0).getRawClass() == FileItem.class	;
		}else if(resolvableType.isArray()){
			return resolvableType.getComponentType().getRawClass() == FileItem.class;
		}
		return false;
	}
	
	@Override
	protected Object readInternal(Type type, InputMessage inputMessage) throws IOException, MessageConvertException {
		List<FileItem> fileItems = getFileItemParser().parse(inputMessage instanceof HttpInputMessage? (HttpInputMessage)inputMessage:new DefaultHttpInputMessage(inputMessage));
		if(TypeUtils.toClass(type).isArray()){
			return fileItems.toArray(new FileItem[0]);
		}
		return fileItems;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void writeInternal(Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		String boundary;
		MimeType mimeType = contentType;
		if (contentType != null) {
			boundary = contentType.getParameter(BOUNDARY_NAME);
			if (StringUtils.isEmpty(boundary)) {
				MimeType type = outputMessage.getContentType();
				if (type != null) {
					boundary = type.getParameter(BOUNDARY_NAME);
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

		if (body instanceof FileItem) {
			writeItem(boundary, (FileItem) body, outputMessage);
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

		OutputStreamWriter osw = new OutputStreamWriter(outputMessage.getBody(), getCharset(outputMessage));
		osw.write(LINE);
		osw.write(BOUNDARY_APPEND);
		osw.write(boundary);
		osw.write(BOUNDARY_APPEND);
		osw.write(LINE);
		osw.flush();
	}

	protected void writeItem(String boundary, String fieldName, Object value, OutputMessage outputMessage)
			throws IOException {
		FileItem item;
		if (value instanceof File) {
			item = new DefaultFileItem(fieldName, (File) value);
		} else {
			item = new FormFileItem(fieldName, value, getCharset(outputMessage), getJsonSupport());
		}
		writeItem(boundary, item, outputMessage);
	}

	protected void writeItem(String boundary, Object multipartItem, OutputMessage outputMessage) throws IOException {
		if (multipartItem instanceof FileItem) {
			writeItem(boundary, (FileItem) multipartItem, outputMessage);
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
		}else{
			throw new NotSupportedException(multipartItem.toString());
		}
	}

	protected void writeItem(String boundary, FileItem fileItem, OutputMessage outputMessage) throws IOException {
		OutputStream os = outputMessage.getBody();
		OutputStreamWriter osw = new OutputStreamWriter(os, getCharset(outputMessage));
		osw.write(LINE);
		osw.write(BOUNDARY_APPEND);
		osw.write(boundary);
		osw.write(LINE);
		for (Entry<String, List<String>> entry : fileItem.getHeaders().entrySet()) {
			osw.write(entry.getKey() + ": " + StringUtils.collectionToDelimitedString(entry.getValue(), ", "));
			osw.write(LINE);
		}
		osw.write(LINE);
		osw.flush();
		InputStream inputStream = fileItem.getBody();
		IOUtils.write(inputStream, os);
		os.flush();
	}
}
