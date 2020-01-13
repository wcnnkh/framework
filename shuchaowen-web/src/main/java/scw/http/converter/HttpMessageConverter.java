package scw.http.converter;

import java.io.IOException;
import java.util.List;

import scw.http.HttpInputMessage;
import scw.http.HttpOutputMessage;
import scw.http.MediaType;
public interface HttpMessageConverter<T> {

	boolean canRead(Class<?> clazz, MediaType mediaType);

	boolean canWrite(Class<?> clazz, MediaType mediaType);

	List<MediaType> getSupportedMediaTypes();

	T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException;
	
	void write(T t, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException;

}
