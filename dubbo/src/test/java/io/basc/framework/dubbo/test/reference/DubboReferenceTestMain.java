package io.basc.framework.dubbo.test.reference;

import java.util.concurrent.ExecutionException;

import io.basc.framework.beans.factory.annotation.ImportResource;
import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

@ImportResource("reference.xml")
public class DubboReferenceTestMain {
	private static Logger logger = LogManager.getLogger(DubboReferenceTestMain.class);

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Application application = MainApplication.run(DubboReferenceTestMain.class, args).get();

		HelloService helloService = application.getInstance(HelloService.class);
		logger.info(Thread.currentThread().getName());
		while (true) {
			Thread.sleep(1000L);
			logger.info(helloService.hello(XUtils.getUUID()));
		}
	}
}
