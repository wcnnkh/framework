package io.basc.framework.dubbo;

import io.basc.framework.beans.factory.Init;
import io.basc.framework.boot.Application;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.util.Assert;

@Provider
public class DubboApplication extends DefaultDubboServiceRegistry implements Init {
	private Application application;

	public DubboApplication(Application application) {
		Assert.requiredArgument(application != null, "application");
		this.application = application;
	}

	@Override
	public void init() {
		loadXml(application);
	}
}
