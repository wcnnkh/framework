package io.basc.framework.nacos.client;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;

import io.basc.framework.util.registry.Registration;

public class NacosConfigEventRegistration implements Registration{
	private final Listener listener;
	private final ConfigService configService;
	private final String dataId;
	private final String group;
	
	public NacosConfigEventRegistration(ConfigService configService, String dataId, String group, Listener listener){
		this.configService = configService;
		this.listener = listener;
		this.dataId = dataId;
		this.group = group;
	}
	
	public void unregister() {
		configService.removeListener(dataId, group, listener);
	}

}
