package io.basc.framework.observe.registry;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe_old.poll.Pollable;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.Registry;

public class PollableRegistry<T extends Poller> implements Registry<T, Registration> {
	
	@Override
	public Elements<Registration> getRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Registration register(T element) throws RegistrationException {
		// TODO Auto-generated method stub
		return null;
	}

}
