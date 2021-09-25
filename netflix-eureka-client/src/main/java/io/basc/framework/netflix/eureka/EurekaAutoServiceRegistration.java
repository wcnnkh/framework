/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.basc.framework.netflix.eureka;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.basc.framework.context.Destroy;
import io.basc.framework.context.Init;

/**
 * @author Dave Syer
 * @author Spencer Gibb
 * @author Jon Schneider
 * @author Jakub Narloch
 * @author Raiyan Raiyan
 */
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
