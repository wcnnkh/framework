package io.basc.framework.observe.registry;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.register.RegistrationException;
import io.basc.framework.util.observe.register.Registry;
import io.basc.framework.util.observe_old.poll.Pollable;
import io.basc.framework.util.register.Registration;

public class PollableRegistry<T extends Pollable> implements Registry<T, Registration> {
	
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
