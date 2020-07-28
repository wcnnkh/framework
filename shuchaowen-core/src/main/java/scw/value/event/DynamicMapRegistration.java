package scw.value.event;

import scw.event.EventRegistration;

public interface DynamicMapRegistration extends EventRegistration {
	boolean isRegister();

	void register();
}
