package scw.mvc.rpc.support;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import scw.core.Constants;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.net.MimeTypeUtils;
import scw.net.message.InputMessage;
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

	public Object convert(InputMessage inputMessage, Type type, MessageConverterChain chain) throws Throwable {
		if (inputMessage.getMimeType() != null
				&& inputMessage.getMimeType().equalsTypeAndSubtype(MimeTypeUtils.APPLICATION_OCTET_STREAM)) {
			Object object = serializer.deserialize(inputMessage.toByteArray());
			if (object == null) {
				return null;
			}

			if (object instanceof ObjectRpcResponseMessage) {
				Throwable throwable = ((ObjectRpcResponseMessage) object).getThrowable();
				if (throwable != null) {
					throw throwable;
				}

				return ((ObjectRpcResponseMessage) object).getResponse();
			}
			return object;
		}

		if (TypeUtils.isAssignableFrom(type, Collection.class) || TypeUtils.isAssignableFrom(type, Map.class)
				|| !TypeUtils.isInterface(type)) {
			String content = inputMessage
					.toString(StringUtils.isEmpty(charsetName) ? Constants.DEFAULT_CHARSET_NAME : charsetName);
			return StringParse.defaultParse(content, type);
		}

		return chain.doConvert(inputMessage, type);
	}
}
