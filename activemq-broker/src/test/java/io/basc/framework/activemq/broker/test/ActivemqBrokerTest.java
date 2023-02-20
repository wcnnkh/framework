package io.basc.framework.activemq.broker.test;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.junit.Test;

import io.basc.framework.boot.support.MainApplication;

public class ActivemqBrokerTest {
	@Test
	public void broker() throws Exception {
		BrokerService brokerService = new BrokerService();
		brokerService.setBrokerName("EmbedMq");
		brokerService.setManagementContext(new ManagementContext());
		brokerService.start();
	}

	public static void main(String[] args) {
		MainApplication.run(ActivemqBrokerTest.class, args);
	}

}
