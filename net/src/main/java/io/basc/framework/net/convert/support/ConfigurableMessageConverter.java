package io.basc.framework.net.convert.support;

import io.basc.framework.net.convert.MessageConverter;
import io.basc.framework.net.convert.MessageConverters;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConfigurableMessageConverter extends MessageConverters<MessageConverter> {

	public ConfigurableMessageConverter() {
		setServiceClass(MessageConverter.class);
	}

}
