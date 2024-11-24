package io.basc.framework.netflix.eureka;

import io.basc.framework.boot.ApplicationEvent;
import io.basc.framework.cloud.event.HeartbeatEvent;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.util.actor.EventsDispatcher;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.reflect.ReflectionUtils;
import io.basc.framework.util.logging.LogManager;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.discovery.AbstractDiscoveryClientOptionalArgs;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;

/**
 * Subclass of {@link DiscoveryClient} that sends a {@link HeartbeatEvent} when
 * {@link CloudEurekaClient#onCacheRefreshed()} is called.
 *
 */
public class CloudEurekaClient extends DiscoveryClient {

	private static final Logger log = LogManager.getLogger(CloudEurekaClient.class);

	private final AtomicLong cacheRefreshedCount = new AtomicLong(0);

	private EventsDispatcher<ApplicationEvent> publisher;

	private Field eurekaTransportField;

	private ApplicationInfoManager applicationInfoManager;

	private AtomicReference<EurekaHttpClient> eurekaHttpClient = new AtomicReference<>();

	public CloudEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config,
			EventsDispatcher<ApplicationEvent> publisher) {
		this(applicationInfoManager, config, null, publisher);
	}

	public CloudEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config,
			AbstractDiscoveryClientOptionalArgs<?> args, EventsDispatcher<ApplicationEvent> publisher) {
		super(applicationInfoManager, config, args);
		this.applicationInfoManager = applicationInfoManager;
		this.publisher = publisher;
		this.eurekaTransportField = ReflectionUtils.getDeclaredField(DiscoveryClient.class, "eurekaTransport");
		ReflectionUtils.makeAccessible(this.eurekaTransportField);
	}

	public ApplicationInfoManager getApplicationInfoManager() {
		return applicationInfoManager;
	}

	public void cancelOverrideStatus(InstanceInfo info) {
		getEurekaHttpClient().deleteStatusOverride(info.getAppName(), info.getId(), info);
	}

	public InstanceInfo getInstanceInfo(String appname, String instanceId) {
		EurekaHttpResponse<InstanceInfo> response = getEurekaHttpClient().getInstance(appname, instanceId);
		HttpStatus httpStatus = HttpStatus.valueOf(response.getStatusCode());
		if (httpStatus.is2xxSuccessful() && response.getEntity() != null) {
			return response.getEntity();
		}
		return null;
	}

	EurekaHttpClient getEurekaHttpClient() {
		if (this.eurekaHttpClient.get() == null) {
			try {
				Object eurekaTransport = this.eurekaTransportField.get(this);
				Field registrationClientField = ReflectionUtils.getDeclaredField(eurekaTransport.getClass(),
						"registrationClient");
				ReflectionUtils.makeAccessible(registrationClientField);
				this.eurekaHttpClient.compareAndSet(null,
						(EurekaHttpClient) registrationClientField.get(eurekaTransport));
			} catch (IllegalAccessException e) {
				log.error(e, "error getting EurekaHttpClient");
			}
		}
		return this.eurekaHttpClient.get();
	}

	public void setStatus(InstanceStatus newStatus, InstanceInfo info) {
		getEurekaHttpClient().statusUpdate(info.getAppName(), info.getId(), newStatus, info);
	}

	@Override
	protected void onCacheRefreshed() {
		super.onCacheRefreshed();

		if (this.cacheRefreshedCount != null) { // might be called during
												// construction and
			// will be null
			long newCount = this.cacheRefreshedCount.incrementAndGet();
			log.trace("onCacheRefreshed called with count: " + newCount);
			this.publisher.publishEvent(new HeartbeatEvent(this, newCount));
		}
	}

}
