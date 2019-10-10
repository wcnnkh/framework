package scw.rpc.support;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.io.serializer.Serializer;
import scw.net.ContentType;
import scw.net.Message;
import scw.net.MessageConverter;
import scw.net.MessageConverterChain;

public final class ObjectRpcMessageConvert implements MessageConverter {
	private final Serializer serializer;
	private final String charsetName;

	public ObjectRpcMessageConvert(Serializer serializer, String charsetName) {
		this.serializer = serializer;
		this.charsetName = charsetName;
	}

	public Object convert(Message message, Type type, MessageConverterChain chain) throws Throwable {
		if (StringUtils.startsWith(message.getContentType(), ContentType.APPLICATION_OCTET_STREAM, true)) {
			Object object = serializer.deserialize(message.toByteArray());
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
			String content = message.toString(
					StringUtils.isEmpty(message.getContentEncoding()) ? charsetName : message.getContentEncoding());
			return StringParse.defaultParse(content, type);
		}

		return chain.doConvert(message, type);
	}
}
