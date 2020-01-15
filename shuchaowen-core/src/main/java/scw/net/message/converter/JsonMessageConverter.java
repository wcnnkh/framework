package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import scw.json.JSONUtils;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public class JsonMessageConverter extends AbstractTextMessageConverter {
	private SupportMimeTypes<MimeType> supportMimeTypes = new SupportMimeTypes<MimeType>();

	public JsonMessageConverter() {
		supportMimeTypes.add(MimeTypeUtils.APPLICATION_JSON, MimeTypeUtils.TEXT_JSON,
				new MimeType("application", "*+json"));
	}

	@Override
	protected boolean canRead(Type type) {
		return true;
	}

	@Override
	protected boolean canRead(MimeType contentType) {
		return supportMimeTypes.canRead(contentType);
	}

	@Override
	protected boolean canWrite(Object body) {
		return true;
	}

	@Override
	protected boolean canWrite(MimeType contentType) {
		return supportMimeTypes.canWrite(contentType);
	}

	@Override
	protected Object readInternal(Type type, InputMessage inputMessage) throws IOException {
		String text = read(inputMessage);
		return JSONUtils.parseObject(text, type);
	}

	@Override
	protected void writeInternal(Object body, MimeType contentType, OutputMessage outputMessage) throws IOException {
		String text = JSONUtils.toJSONString(body);
		write(text, contentType, outputMessage);
	}

}
