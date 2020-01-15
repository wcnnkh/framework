package scw.mvc.rpc.support;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import scw.core.Constants;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.net.message.converter.MessageConverter;
import scw.net.message.converter.MessageConverterChain;
import scw.serializer.Serializer;

public final class ObjectRpcMessageConvert implements MessageConverter {
	private final Serializer serializer;
	private final String charsetName;

	public ObjectRpcMessageConvert(Serializer serializer, String charsetName) {
		this.serializer = serializer;
		this.charsetName = charsetName;
	}

	public Object read(Type type, InputMessage inputMessage, MessageConverterChain chain) throws IOException {
		if (inputMessage.getContentType() != null
				&& inputMessage.getContentType().equalsTypeAndSubtype(MimeTypeUtils.APPLICATION_OCTET_STREAM)) {
			return serializer.deserialize(inputMessage.toByteArray());
		}

		if (TypeUtils.isAssignableFrom(type, Collection.class) || TypeUtils.isAssignableFrom(type, Map.class)
				|| !TypeUtils.isInterface(type)) {
			String content = inputMessage
					.convertToString(StringUtils.isEmpty(charsetName) ? Constants.DEFAULT_CHARSET_NAME : charsetName);
			return StringParse.defaultParse(content, type);
		}

		return chain.read(type, inputMessage);
	}

	public void write(Object body, MimeType contentType, OutputMessage outputMessage, MessageConverterChain chain)
			throws IOException {
		// TODO
	}
}
