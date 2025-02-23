package io.basc.framework.net.server.convert;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultServerMessageConverter extends ConfigurableServerMessageConverter {

	public DefaultServerMessageConverter() {
		setLast(global());
	}

}
