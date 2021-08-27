package io.basc.framework.tcc.test;

import static org.junit.Assert.assertTrue;
import io.basc.framework.beans.annotation.Service;
import io.basc.framework.beans.support.DefaultBeanFactory;
import io.basc.framework.consistency.CompensatePolicy;
import io.basc.framework.consistency.policy.FileCompensatePolicy;
import io.basc.framework.core.utils.ArrayUtils;
import io.basc.framework.io.FileUtils;
import io.basc.framework.tcc.annotation.Tcc;
import io.basc.framework.tcc.annotation.TccStage;
import io.basc.framework.tcc.annotation.TryResult;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.transaction.TransactionUtils;
import io.basc.framework.util.XUtils;

import java.io.File;

import org.junit.Test;

public class TccTest {
	private static final DefaultBeanFactory beanFactory = new DefaultBeanFactory();
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
		Transaction transaction = TransactionUtils.getManager().getTransaction(TransactionDefinition.DEFAULT);
		try {
			TestService testService = beanFactory.getInstance(TestService.class);
			String value = testService.tryMethod("success");
			System.out.println("success:" + value);
			TransactionUtils.getManager().commit(transaction);
		} catch (Throwable e) {
			TransactionUtils.getManager().rollback(transaction);
			e.printStackTrace();
		}
		assertTrue("执行完后目录应该为空", ArrayUtils.isEmpty(file.list()));
	}

	@Test
	public void fail() throws Throwable {
		Transaction transaction = TransactionUtils.getManager().getTransaction(TransactionDefinition.DEFAULT);
		try {
			TestService testService = beanFactory.getInstance(TestService.class);
			String value = testService.tryMethod("fail");
			System.out.println("fail: " + value);
		} finally {
			// 直接回滚，测试失败情况
			TransactionUtils.getManager().rollback(transaction);
		}
		assertTrue("执行完后目录应该为空", ArrayUtils.isEmpty(file.list()));
	}

	public static interface TestService {
		@Tcc(confirm = "confirm", cancel = "cancel")
		String tryMethod(String group);

		@TccStage
		void confirm(@TryResult String tryResult, String group);

		@TccStage
		void cancel(@TryResult String tryResult, String group);
	}

	@Service
	public static class TestServiceImpl implements TestService {

		@Tcc(confirm = "confirm", cancel = "cancel")
		@Override
		public String tryMethod(String group) {
			return XUtils.getUUID();
		}

		@TccStage
		@Override
		public void confirm(@TryResult String tryResult, String group) {
			System.out.println(group + " confirm:" + tryResult);
		}

		@TccStage
		@Override
		public void cancel(@TryResult String tryResult, String group) {
			System.out.println(group + " cancel:" + tryResult);
		}

	}
}
