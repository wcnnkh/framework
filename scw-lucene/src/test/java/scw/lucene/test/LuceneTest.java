package scw.lucene.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;

import scw.lucene.DefaultLuceneTemplate;
import scw.lucene.SearchParameters;

public class LuceneTest {

	@Test
	public void saveTest() {
		DefaultLuceneTemplate templete = new DefaultLuceneTemplate("test");
		templete.saveOrUpdate(new Term("name", "1"), new TestBean());
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
			Future<Long> future = templete.saveOrUpdate(term, bean2);
			System.out.println(future.get());
		}
		
		for(int i=0; i<100; i++) {
			new Thread(() -> {
				TestBean testBean = templete.search(SearchParameters.top(new TermQuery(term), 1), TestBean.class).first();
				System.out.println(testBean);
			}).start();;
		}
		Thread.sleep(Integer.MAX_VALUE);
	}
}
