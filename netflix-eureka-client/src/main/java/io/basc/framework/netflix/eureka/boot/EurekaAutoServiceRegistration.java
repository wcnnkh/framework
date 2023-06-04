package io.basc.framework.netflix.eureka.boot;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.basc.framework.beans.factory.Destroy;
import io.basc.framework.beans.factory.Init;
import io.basc.framework.netflix.eureka.EurekaRegistration;
import io.basc.framework.netflix.eureka.EurekaServiceRegistry;

public class EurekaAutoServiceRegistration implements Init, Destroy{

	private AtomicBoolean running = new AtomicBoolean(false);

	private AtomicInteger port = new AtomicInteger(0);

	private EurekaServiceRegistry serviceRegistry;

	private EurekaRegistration registration;

	public EurekaAutoServiceRegistration(EurekaServiceRegistry serviceRegistry,
			EurekaRegistration registration) {
		this.serviceRegistry = serviceRegistry;
		this.registration = registration;
	}

	@Override
	public void init() {
		// only set the port if the nonSecurePort or securePort is 0 and this.port != 0
		if (this.port.get() != 0) {
			if (this.registration.getNonSecurePort() == 0) {
				this.registration.setNonSecurePort(this.port.get());
			}

			if (this.registration.getSecurePort() == 0 && this.registration.isSecure()) {
				this.registration.setSecurePort(this.port.get());
			}
		}

		// only initialize if nonSecurePort is greater than 0 and it isn't already running
		// because of containerPortInitializer below
		if (!this.running.get() && this.registration.getNonSecurePort() > 0) {

			this.serviceRegistry.register(this.registration);

			//this.context.publishEvent(new EurekaInstanceRegisteredEvent<>(this, this.registration.getInstanceConfig()));
			this.running.set(true);
		}
	}

	@Override
	public void destroy() {
		this.serviceRegistry.deregister(this.registration);
		this.running.set(false);
	}
}
