package io.basc.framework.net.convert.support;

import io.basc.framework.net.convert.MessageConverter;
import io.basc.framework.net.convert.MessageConverterAware;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractNestedMessageConverter extends AbstractMessageConverter implements MessageConverterAware{
	@NonNull
	private MessageConverter messageConverter = GlobalMessageConverters.getInstance();
}
