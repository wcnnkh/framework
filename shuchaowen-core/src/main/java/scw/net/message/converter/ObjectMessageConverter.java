package scw.net.message.converter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;

import scw.lang.NestedRuntimeException;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public class ObjectMessageConverter extends AbstractMessageConverter {
	private SupportMimeTypes<MimeType> supports = new SupportMimeTypes<MimeType>();

	public ObjectMessageConverter() {
		supports.add(MimeTypeUtils.APPLICATION_OCTET_STREAM);
	}

	@Override
	protected boolean canRead(Type type) {
		return true;
	}

	@Override
	protected boolean canRead(MimeType contentType) {
		return supports.canRead(contentType);
	}

	@Override
	protected boolean canWrite(Object body) {
		return true;
	}

	@Override
	protected boolean canWrite(MimeType contentType) {
		return supports.canWrite(contentType);
	}

	@Override
	protected Object readInternal(Type type, InputMessage inputMessage) throws IOException {
		ObjectInputStream objectInputStream = new ObjectInputStream(inputMessage.getBody());
		try {
			return objectInputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new NestedRuntimeException(e);
		} finally {
			objectInputStream.close();
		}
	}

	@Override
	protected void writeInternal(Object body, MimeType contentType, OutputMessage outputMessage) throws IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputMessage.getBody());
		try {
			objectOutputStream.writeObject(body);
		} finally {
			objectOutputStream.close();
		}
	}

}
