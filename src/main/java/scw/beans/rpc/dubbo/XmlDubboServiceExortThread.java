package scw.beans.rpc.dubbo;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.dubbo.config.ServiceConfig;

import scw.beans.BeanFactory;
import scw.core.PropertiesFactory;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;

public class XmlDubboServiceExortThread extends Thread {
	private static Logger logger = LoggerFactory.getLogger(XmlDubboServiceExortThread.class);
	private final PropertiesFactory propertiesFactory;
	private final BeanFactory beanFactory;
	private final NodeList nodeList;
	private boolean tag = false;
	private int size = 0;
	private Thread thread;

	public XmlDubboServiceExortThread(PropertiesFactory propertiesFactory, BeanFactory beanFactory, NodeList nodeList) {
		this.propertiesFactory = propertiesFactory;
		this.beanFactory = beanFactory;
		this.nodeList = nodeList;
	}

	@Override
	public void run() {
		try {
			export();
		} finally {
			tag = true;
		}

		if (size > 0) {
			logger.trace("-------dubbo服务注册完成-------");
		}
	}

	private void check() {
		if (thread == null) {
			thread = new Thread(new Runnable() {

				public void run() {
					try {
						while (!tag && !Thread.currentThread().isInterrupted()) {
							Thread.sleep(3000L);
							if (tag) {
								break;
							}
							logger.trace("-------正在检查dubbo服务是否注册完成-------");
						}
					} catch (InterruptedException e) {
					}
				}
			});
		} else {
			thread.interrupt();
		}
		thread.start();
	}

	private void export() {
		if (nodeList != null) {
			for (int x = 0; x < nodeList.getLength(); x++) {
				Node node = nodeList.item(x);
				if (node == null) {
					continue;
				}

				if (DubboUtils.isServiceNode(node)) {
					if (size == 0) {
						logger.trace("-------开始注册dubbo服务-------");
						check();
					}
					size++;
					List<ServiceConfig<?>> serviceConfigs = XmlDubboUtils.getServiceConfigList(propertiesFactory,
							beanFactory, node);
					for (ServiceConfig<?> serviceConfig : serviceConfigs) {
						size++;
						serviceConfig.export();
					}
				}
			}
		}
	}
}
