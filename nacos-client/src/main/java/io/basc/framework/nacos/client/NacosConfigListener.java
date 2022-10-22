package io.basc.framework.nacos.client;

import java.util.concurrent.Executor;

import com.alibaba.nacos.api.config.listener.Listener;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventTypes;

public class NacosConfigListener implements Listener {
	private final EventListener<ChangeEvent<String>> eventListener;

	public NacosConfigListener(EventListener<ChangeEvent<String>> eventListener) {
		this.eventListener = eventListener;
	}

	public Executor getExecutor() {
		return new Executor() {

			public void execute(Runnable command) {
				command.run();
			}
		};
	}

	public void receiveConfigInfo(String configInfo) {
		eventListener.onEvent(new ChangeEvent<String>(EventTypes.UPDATE, configInfo));
	}

}
