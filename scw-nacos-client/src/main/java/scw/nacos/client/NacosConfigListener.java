package scw.nacos.client;

import java.util.concurrent.Executor;

import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventType;

import com.alibaba.nacos.api.config.listener.Listener;

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
		eventListener.onEvent(new ChangeEvent<String>(EventType.UPDATE,
				configInfo));
	}

}
