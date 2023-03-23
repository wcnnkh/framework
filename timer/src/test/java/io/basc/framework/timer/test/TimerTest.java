package io.basc.framework.timer.test;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.LoggerFactory;

public class TimerTest {

	@Test
	public void test() throws InterruptedException, ExecutionException {
		LoggerFactory.getSource().getLevelManager().getSourceMap().put("io.basc.framework.context",
				Levels.DEBUG.getValue());
		Application application = MainApplication.run(TimerTest.class).get();
		application.destroy();
	}
}
