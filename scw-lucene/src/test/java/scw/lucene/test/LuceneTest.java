package scw.lucene.test;

import org.junit.Test;

import scw.lucene.DefaultLuceneTemplete;

public class LuceneTest {
	
	@Test
	public void saveTest() {
		DefaultLuceneTemplete templete = new DefaultLuceneTemplete("test");
		templete.save(new TestBean());
	}
}
