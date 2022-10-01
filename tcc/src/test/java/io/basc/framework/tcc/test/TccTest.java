package io.basc.framework.tcc.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import io.basc.framework.consistency.CompensatePolicy;
import io.basc.framework.consistency.policy.FileCompensatePolicy;
import io.basc.framework.context.annotation.Service;
import io.basc.framework.context.support.DefaultContext;
import io.basc.framework.io.FileUtils;
import io.basc.framework.tcc.annotation.Tcc;
import io.basc.framework.tcc.annotation.TccStage;
import io.basc.framework.tcc.annotation.TryResult;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.transaction.TransactionUtils;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.XUtils;

public class TccTest {
	private static final DefaultContext beanFactory = new DefaultContext();
	private static final File file = new File(FileUtils.getTempDirectory(), "install_test");
	static {
		file.mkdir();
		FileCompensatePolicy fileCompensatePolicy = new FileCompensatePolicy(file);
		fileCompensatePolicy.setBeanFactory(beanFactory);
		beanFactory.registerSingleton(CompensatePolicy.class.getName(), fileCompensatePolicy);
		try {
			beanFactory.init();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void success() throws Throwable {
		TestService testService = beanFactory.getInstance(TestService.class);
		Transaction transaction = TransactionUtils.getManager().getTransaction(TransactionDefinition.DEFAULT);
		try {
			String value = testService.tryMethod("success");
			System.out.println("success:" + value);
			TransactionUtils.getManager().commit(transaction);
		} catch (Throwable e) {
			TransactionUtils.getManager().rollback(transaction);
			e.printStackTrace();
		}
		assertTrue("执行完后目录应该为空", ArrayUtils.isEmpty(file.list()));
		assertTrue(testService.getStage() == 2);
	}

	@Test
	public void fail() throws Throwable {
		TestService testService = beanFactory.getInstance(TestService.class);
		Transaction transaction = TransactionUtils.getManager().getTransaction(TransactionDefinition.DEFAULT);
		try {
			String value = testService.tryMethod("fail");
			System.out.println("fail: " + value);
		} finally {
			// 直接回滚，测试失败情况
			TransactionUtils.getManager().rollback(transaction);
		}
		assertTrue("执行完后目录应该为空", ArrayUtils.isEmpty(file.list()));
		assertTrue(testService.getStage() == 3);
	}

	public static interface TestService {
		@Tcc(confirm = "confirm", cancel = "cancel")
		String tryMethod(String group);

		@TccStage
		void confirm(@TryResult String tryResult, String group);

		@TccStage
		void cancel(@TryResult String tryResult, String group);
		
		int getStage();
	}

	@Service
	public static class TestServiceImpl implements TestService {
		private int stage = 0;
		
		@Tcc(confirm = "confirm", cancel = "cancel")
		@Override
		public String tryMethod(String group) {
			stage = 1;
			return XUtils.getUUID();
		}

		@TccStage
		@Override
		public void confirm(@TryResult String tryResult, String group) {
			stage = 2;
			System.out.println(group + " confirm:" + tryResult);
		}

		@TccStage
		@Override
		public void cancel(@TryResult String tryResult, String group) {
			stage = 3;
			System.out.println(group + " cancel:" + tryResult);
		}

		@Override
		public int getStage() {
			return stage;
		}

	}
}
