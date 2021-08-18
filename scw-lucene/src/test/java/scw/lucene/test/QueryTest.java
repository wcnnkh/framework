package scw.lucene.test;

import java.util.concurrent.ExecutionException;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;

import scw.lucene.DefaultLuceneTemplate;
import scw.lucene.LuceneTemplate;
import scw.lucene.LuceneWriteException;
import scw.lucene.SearchParameters;

public class QueryTest {
	public static void main(String[] args) throws LuceneWriteException, InterruptedException, ExecutionException {
		LuceneTemplate luceneTemplate = new DefaultLuceneTemplate("test");
		
		TestBean bean1 = new TestBean();
		bean1.setName("a");
		bean1.setValue("sss");
		
		TestBean bean2 = new TestBean();
		bean2.setName("a");
		bean2.setValue("adsfdsfsf");
		
		Term term = new Term("name", "a");
		luceneTemplate.saveOrUpdate(term, bean2).get();
		luceneTemplate.saveOrUpdate(term, bean1).get();
		
		luceneTemplate.search(new SearchParameters(new TermQuery(term), 10), TestBean.class).stream().forEach((b) -> {
			System.out.println(b);
		});
	}
}
