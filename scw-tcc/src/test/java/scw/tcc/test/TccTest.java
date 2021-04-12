package scw.tcc.test;

import java.io.File;

import org.junit.Test;

import scw.beans.annotation.Service;
import scw.beans.support.DefaultBeanFactory;
import scw.consistency.CompensatePolicy;
import scw.consistency.policy.FileCompensatePolicy;
import scw.io.FileUtils;
import scw.tcc.annotation.Tcc;
import scw.tcc.annotation.TccStage;
import scw.tcc.annotation.TryResult;
import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionUtils;
import scw.util.XUtils;

public class TccTest {
	private static final DefaultBeanFactory beanFactory = new DefaultBeanFactory();
	static {
		File file = new File(FileUtils.getTempDirectory(), "install_test");
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
