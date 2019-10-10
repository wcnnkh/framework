package scw.net;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public final class MessageConverterChain {
	private static Logger logger = LoggerFactory.getLogger(MessageConverterChain.class);
	
	private Iterator<MessageConverter> iterator;

	public MessageConverterChain(Collection<MessageConverter> messageConverters) {
		if (!CollectionUtils.isEmpty(messageConverters)) {
			this.iterator = messageConverters.iterator();
		}
	}

	public Object doConvert(Message message, Type type) throws Throwable {
		if (iterator == null) {
			return lastConvert(message, type);
		}

		if (iterator.hasNext()) {
			return iterator.next().convert(message, type, this);
		} else {
			return lastConvert(message, type);
		}
	}

	private Object lastConvert(Message message, Type type) {
		logger.warn("{}找不到指定的解析方式", type);
		return null;
	}
}
