package run.soeasy.framework.messaging.convert.support;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.messaging.convert.MessageConverter;
import run.soeasy.framework.messaging.convert.MessageConverterAware;

@Getter
@Setter
public abstract class AbstractNestedMessageConverter extends AbstractMessageConverter implements MessageConverterAware{
	@NonNull
	private MessageConverter messageConverter = GlobalMessageConverters.getInstance();
}
