package io.basc.framework.lucene.test;

import java.util.concurrent.ExecutionException;

import io.basc.framework.lucene.DefaultLuceneTemplate;
import io.basc.framework.lucene.LuceneTemplate;
import io.basc.framework.lucene.LuceneWriteException;

public class QueryTest {
	public static void main(String[] args) throws LuceneWriteException, InterruptedException, ExecutionException {
		LuceneTemplate luceneTemplate = new DefaultLuceneTemplate("test");

		TestBean bean1 = new TestBean();
		bean1.setName("a");
		bean1.setValue("sss");

		TestBean bean2 = new TestBean();
		bean2.setName("a");
		bean2.setValue("adsfdsfsf");

		luceneTemplate.saveOrUpdate(bean2);
		luceneTemplate.saveOrUpdate(bean1);

		TestBean bean = luceneTemplate.getById(bean1);
		System.out.println(bean);
	}
}
