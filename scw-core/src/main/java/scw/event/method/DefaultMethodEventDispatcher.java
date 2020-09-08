package scw.event.method;

import scw.core.instance.annotation.Configuration;
import scw.event.support.DefaultNamedEventDispatcher;

@Configuration(value = MethodEventDispatcher.class)
public class DefaultMethodEventDispatcher extends DefaultNamedEventDispatcher<MethodEvent>
		implements MethodEventDispatcher {

	public DefaultMethodEventDispatcher() {
		super(true);
	}

}
