/*
 * Copyright 2012-2020 the original author or authors.
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

package io.basc.framework.cloud;

/**
 * Contract to register and deregister instances with a Service Registry.
 * 
 * @author shuchaowen
 *
 */
public interface ServiceRegistry<T extends ServiceInstance> {

	/**
	 * Registers the instance. A registration typically has information
	 * about an instance, such as its hostname and port.
	 * 
	 */
	void register(T instance) throws ServiceRegistryException;

	/**
	 * Deregisters the instance.
	 * 
	 */
	void deregister(T instance) throws ServiceRegistryException;

}
