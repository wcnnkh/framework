package io.basc.framework.lucene.test;

import java.util.concurrent.ExecutionException;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;

import io.basc.framework.lucene.DefaultLuceneTemplate;
import io.basc.framework.lucene.SearchParameters;
import io.basc.framework.util.XUtils;

public class LuceneTest {

	@Test
	public void saveTest() {
		DefaultLuceneTemplate templete = new DefaultLuceneTemplate("test");
		TestBean testBean = new TestBean();
		testBean.setName("1");
		testBean.setValue(XUtils.getUUID());
		if (templete.query(testBean).getElements().isEmpty()) {
			templete.updateById(testBean);
		} else {
			templete.insert(testBean);
		}
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		DefaultLuceneTemplate templete = new DefaultLuceneTemplate("test");
		TestBean bean1 = new TestBean();
		bean1.setName("a");
		bean1.setValue("sss");

		TestBean bean2 = new TestBean();
		bean2.setName("a");
		bean2.setValue("adsfdsfsf");

		Term term = new Term("name", "a");

		for (int i = 0; i < 100; i++) {
			boolean b = templete.saveOrUpdate(bean2);
			System.out.println(b);
		}

		for (int i = 0; i < 100; i++) {
			new Thread(() -> {
				TestBean testBean = templete.search(SearchParameters.top(new TermQuery(term), 1), TestBean.class)
						.getElements().first();
				System.out.println(testBean);
			}).start();
			;
		}
		Thread.sleep(Integer.MAX_VALUE);
	}
}
