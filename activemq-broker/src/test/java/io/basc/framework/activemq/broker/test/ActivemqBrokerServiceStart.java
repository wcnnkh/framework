package io.basc.framework.activemq.broker.test;

import org.apache.activemq.broker.BrokerService;

import io.basc.framework.activemq.broker.EnableActivemqBroker;
import io.basc.framework.boot.annotation.BootApplication;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.context.annotation.Bean;

@EnableActivemqBroker
@BootApplication
public class ActivemqBrokerServiceStart {
	public static void main(String[] args) {
		MainApplication.run(ActivemqBrokerServiceStart.class, args);
	}

	@Bean
	public BrokerService getBrokerService() throws Exception {
		BrokerService brokerService = new BrokerService();
		brokerService.addConnector("tcp://127.0.0.1:61616");
		return brokerService;
	}
}
