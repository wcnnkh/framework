package io.basc.framework.nacos.client;

import java.util.concurrent.Executor;

import com.alibaba.nacos.api.config.listener.Listener;

import io.basc.framework.event.EventListener;
import io.basc.framework.observe.ObservableEvent;
import io.basc.framework.observe.ChangeType;

public class NacosConfigListener implements Listener {
	private final EventListener<ObservableEvent<String>> eventListener;

	public NacosConfigListener(EventListener<ObservableEvent<String>> eventListener) {
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
		eventListener.onEvent(new ObservableEvent<String>(ChangeType.UPDATE, configInfo));
	}

}
