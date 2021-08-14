package scw.lucene.test;

import org.apache.lucene.index.Term;
import org.junit.Test;

import scw.lucene.DefaultLuceneTemplete;

public class LuceneTest {
	
	@Test
	public void saveTest() {
		DefaultLuceneTemplete templete = new DefaultLuceneTemplete("test");
		templete.saveOrUpdate(new Term("name", "1"), new TestBean());
	}
}
