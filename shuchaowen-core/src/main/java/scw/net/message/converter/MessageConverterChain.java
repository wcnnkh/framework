package scw.net.message.converter;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.message.InputMessage;

public final class MessageConverterChain {
	private static Logger logger = LoggerFactory.getLogger(MessageConverterChain.class);
	
	private Iterator<MessageConverter> iterator;

	public MessageConverterChain(Collection<MessageConverter> messageConverters) {
		if (!CollectionUtils.isEmpty(messageConverters)) {
			this.iterator = messageConverters.iterator();
		}
	}

	public Object doConvert(InputMessage inputMessage, Type type) throws Throwable {
		if (iterator == null) {
			return lastConvert(inputMessage, type);
		}

		if (iterator.hasNext()) {
			return iterator.next().convert(inputMessage, type, this);
		} else {
			return lastConvert(inputMessage, type);
		}
	}

	private Object lastConvert(InputMessage inputMessage, Type type) {
		logger.warn("{}找不到指定的解析方式", type);
		return null;
	}
}
