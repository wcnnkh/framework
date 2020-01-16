package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public final class DefaultMessageConverterChain extends AbstractMessageConverterChain {
	private Iterator<? extends MessageConverter> iterator;

	public DefaultMessageConverterChain(Collection<? extends MessageConverter> messageConverters,
			MessageConverterChain chain) {
		super(chain);
		if (!CollectionUtils.isEmpty(messageConverters)) {
			this.iterator = messageConverters.iterator();
		}
	}

	@Override
	protected MessageConverter getNextReadMessageConverter(Type type, InputMessage inputMessage) throws IOException {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}

	@Override
	protected MessageConverter getNextWriteMessageConveter(Object body, MimeType contentType,
			OutputMessage outputMessage) throws IOException {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;
	}

}