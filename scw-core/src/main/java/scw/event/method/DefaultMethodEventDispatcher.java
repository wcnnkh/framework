package scw.event.method;

import scw.core.instance.annotation.Configuration;
import scw.event.support.DefaultEventDispatcher;

@Configuration(value=MethodEventDispatcher.class)
public class DefaultMethodEventDispatcher extends DefaultEventDispatcher<MethodEvent> implements MethodEventDispatcher {

	public DefaultMethodEventDispatcher() {
		super(true);
	}

}
