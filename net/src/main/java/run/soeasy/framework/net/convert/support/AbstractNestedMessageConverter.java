package run.soeasy.framework.net.convert.support;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.net.convert.MessageConverter;
import run.soeasy.framework.net.convert.MessageConverterAware;

@Getter
@Setter
public abstract class AbstractNestedMessageConverter extends AbstractMessageConverter implements MessageConverterAware{
	@NonNull
	private MessageConverter messageConverter = GlobalMessageConverters.getInstance();
}
