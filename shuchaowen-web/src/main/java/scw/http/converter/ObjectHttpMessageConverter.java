package scw.http.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import scw.http.HttpInputMessage;
import scw.http.HttpOutputMessage;
import scw.http.MediaType;
import scw.io.IOUtils;

public class ObjectHttpMessageConverter implements HttpMessageConverter<Object> {

	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		if (MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) {
			return true;
		}

		return false;
	}

	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		if (MediaType.APPLICATION_OCTET_STREAM.equals(mediaType)) {
			return true;
		}

		return false;
	}

	public List<MediaType> getSupportedMediaTypes() {
		return Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM);
	}

	public Object read(Class<? extends Object> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		InputStream is = null;
		ObjectInputStream ois = null;
		try {
			is = inputMessage.getBody();
			ois = new ObjectInputStream(is);
			return ois.readObject();
		} catch (IOException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			throw new HttpMessageNotReadableException(clazz.getName(), e);
		} finally {
			IOUtils.close(ois, is);
		}
	}

	public void write(Object t, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		OutputStream os = null;
		ObjectOutputStream oos = null;
		try {
			os = outputMessage.getBody();
			oos = new ObjectOutputStream(os);
			oos.writeObject(t);
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.close(oos, os);
		}
	}

}
