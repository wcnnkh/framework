package io.basc.framework.druid.test;

import java.util.concurrent.ExecutionException;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.util.TimeUtils;

public class DruidTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Application application = MainApplication.run(DruidTest.class, args).get();
		System.out.println(TimeUtils.format(application.getCreateTime(), TimeUtils.TIME_MILLIS_PATTERN));
	}
}
