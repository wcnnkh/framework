package scw.lucene.test;

import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;

import scw.lucene.DefaultLuceneTemplete;
import scw.lucene.LuceneTemplate;
import scw.lucene.ScoreDocMapper;
import scw.lucene.SearchParameters;

public class QueryTest {
	public static void main(String[] args) {
		LuceneTemplate luceneTemplate = new DefaultLuceneTemplete("test");
		
		TestBean bean1 = new TestBean();
		bean1.setName("a");
		bean1.setValue("sss");
		
		TestBean bean2 = new TestBean();
		bean2.setName("a");
		bean2.setValue("adsfdsfsf");
		
		Term term = new Term("name", "a");
		luceneTemplate.saveOrUpdate(term, bean2);
		luceneTemplate.saveOrUpdate(term, bean1);
		
		luceneTemplate.search(new SearchParameters(new TermQuery(term), 10), new ScoreDocMapper<TestBean>() {

			@Override
			public TestBean map(IndexSearcher indexSearcher, ScoreDoc scoreDoc)
					throws IOException {
				org.apache.lucene.document.Document document = indexSearcher.doc(scoreDoc.doc);
				TestBean testBean = new TestBean();
				testBean.setName(document.get("name"));
				testBean.setValue(document.get("value"));
				return testBean;
			}

		}).stream().forEach((b) -> {
			System.out.println(b);
		});
	}
}
