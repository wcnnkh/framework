package io.basc.framework.netflix.eureka;

import static com.netflix.appinfo.InstanceInfo.InstanceStatus.UNKNOWN;
import io.basc.framework.cloud.ServiceRegistry;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;

import java.util.HashMap;

import com.netflix.appinfo.InstanceInfo;


public class EurekaServiceRegistry implements ServiceRegistry<EurekaRegistration> {

	private static final Logger log = LoggerFactory.getLogger(EurekaServiceRegistry.class);

	@Override
	public void register(EurekaRegistration reg) {
		maybeInitializeClient(reg);

		if (log.isInfoEnabled()) {
			log.info("Registering application " + reg.getApplicationInfoManager().getInfo().getAppName()
					+ " with eureka with status " + reg.getInstanceConfig().getInitialStatus());
		}

		reg.getApplicationInfoManager().setInstanceStatus(reg.getInstanceConfig().getInitialStatus());
		reg.getEurekaClient().registerHealthCheck(reg.getHealthCheckHandler());
	}

	private void maybeInitializeClient(EurekaRegistration reg) {
		// force initialization of possibly scoped proxies
		reg.getApplicationInfoManager().getInfo();
		reg.getEurekaClient().getApplications();
	}

	@Override
	public void deregister(EurekaRegistration reg) {
		if (reg.getApplicationInfoManager().getInfo() != null) {

			if (log.isInfoEnabled()) {
				log.info("Unregistering application " + reg.getApplicationInfoManager().getInfo().getAppName()
						+ " with eureka with status DOWN");
			}

			reg.getApplicationInfoManager().setInstanceStatus(InstanceInfo.InstanceStatus.DOWN);

			// shutdown of eureka client should happen with EurekaRegistration.close()
			// auto registration will create a bean which will be properly disposed
			// manual registrations will need to call close()
		}
	}

	public void setStatus(EurekaRegistration registration, String status) {
		InstanceInfo info = registration.getApplicationInfoManager().getInfo();

		// TODO: howto deal with delete properly?
		if ("CANCEL_OVERRIDE".equalsIgnoreCase(status)) {
			registration.getEurekaClient().cancelOverrideStatus(info);
			return;
		}

		// TODO: howto deal with status types across discovery systems?
		InstanceInfo.InstanceStatus newStatus = InstanceInfo.InstanceStatus.toEnum(status);
		registration.getEurekaClient().setStatus(newStatus, info);
	}

	public Object getStatus(EurekaRegistration registration) {
		String appname = registration.getApplicationInfoManager().getInfo().getAppName();
		String instanceId = registration.getApplicationInfoManager().getInfo().getId();
		InstanceInfo info = registration.getEurekaClient().getInstanceInfo(appname, instanceId);

		HashMap<String, Object> status = new HashMap<>();
		if (info != null) {
			status.put("status", info.getStatus().toString());
			status.put("overriddenStatus", info.getOverriddenStatus().toString());
		}
		else {
			status.put("status", UNKNOWN.toString());
		}

		return status;
	}
}
