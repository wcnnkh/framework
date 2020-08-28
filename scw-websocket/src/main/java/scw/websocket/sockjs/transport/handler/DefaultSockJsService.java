/*
 * Copyright 2002-2018 the original author or authors.
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

package scw.websocket.sockjs.transport.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.websocket.server.support.DefaultHandshakeHandler;
import scw.websocket.sockjs.transport.TransportHandler;
import scw.websocket.sockjs.transport.TransportHandlingSockJsService;

/**
 * A default implementation of {@link scw.websocket.sockjs.SockJsService}
 * with all default {@link TransportHandler} implementations pre-registered.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 4.0
 */
public class DefaultSockJsService extends TransportHandlingSockJsService{

	/**
	 * Create a DefaultSockJsService with default {@link TransportHandler handler} types.
	 * @param scheduler a task scheduler for heart-beat messages and removing
	 * timed-out sessions; the provided TaskScheduler should be declared as a
	 * Spring bean to ensure it is initialized at start up and shut down when the
	 * application stops.
	 */
	public DefaultSockJsService(ScheduledExecutorService scheduledExecutorService) {
		this(scheduledExecutorService, getDefaultTransportHandlers(null));
	}

	/**
	 * Create a DefaultSockJsService with overridden {@link TransportHandler handler} types
	 * replacing the corresponding default handler implementation.
	 * @param scheduler a task scheduler for heart-beat messages and removing timed-out sessions;
	 * the provided TaskScheduler should be declared as a Spring bean to ensure it gets
	 * initialized at start-up and shuts down when the application stops
	 * @param handlerOverrides zero or more overrides to the default transport handler types
	 */
	public DefaultSockJsService(ScheduledExecutorService scheduler, TransportHandler... handlerOverrides) {
		this(scheduler, Arrays.asList(handlerOverrides));
	}

	/**
	 * Create a DefaultSockJsService with overridden {@link TransportHandler handler} types
	 * replacing the corresponding default handler implementation.
	 * @param scheduler a task scheduler for heart-beat messages and removing timed-out sessions;
	 * the provided TaskScheduler should be declared as a Spring bean to ensure it gets
	 * initialized at start-up and shuts down when the application stops
	 * @param handlerOverrides zero or more overrides to the default transport handler types
	 */
	public DefaultSockJsService(ScheduledExecutorService scheduler, Collection<TransportHandler> handlerOverrides) {
		super(scheduler, getDefaultTransportHandlers(handlerOverrides));
	}


	@SuppressWarnings("deprecation")
	private static Set<TransportHandler> getDefaultTransportHandlers(Collection<TransportHandler> overrides) {
		Set<TransportHandler> result = new LinkedHashSet<TransportHandler>(8);
		result.add(new XhrPollingTransportHandler());
		result.add(new XhrReceivingTransportHandler());
		result.add(new XhrStreamingTransportHandler());
		result.add(new JsonpPollingTransportHandler());
		result.add(new JsonpReceivingTransportHandler());
		result.add(new EventSourceTransportHandler());
		result.add(new HtmlFileTransportHandler());
		try {
			result.add(new WebSocketTransportHandler(new DefaultHandshakeHandler()));
		}
		catch (Exception ex) {
			Logger logger = LoggerUtils.getLogger(DefaultSockJsService.class);
			if (logger.isWarnEnabled()) {
				logger.warn("Failed to create a default WebSocketTransportHandler", ex);
			}
		}
		if (overrides != null) {
			result.addAll(overrides);
		}
		return result;
	}
}
