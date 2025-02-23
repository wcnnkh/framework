package io.basc.framework.net.client.convert;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultClientMessageConverter extends ConfigurableClientMessageConverter {

	public DefaultClientMessageConverter() {
		setLast(global());
	}
}
