package io.basc.framework.messageing.handler;

import io.basc.framework.execution.Executor;
import io.basc.framework.messageing.Message;
import io.basc.framework.messageing.MessageHandler;
import io.basc.framework.messageing.MessageHandlingException;
import io.basc.framework.messageing.MessagingException;
import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public class ExecutorMessageHandler implements MessageHandler {
	private final Executor executor;
	private final HandleMessageConverter handleMessageConverter;

	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		Elements<? extends Object> args = executor.getParameterDescriptors()
				.map((e) -> handleMessageConverter.convert(message, e));
		try {
			executor.execute(args);
		} catch (Throwable e) {
			throw new MessageHandlingException(message, e);
		}
	}
}
