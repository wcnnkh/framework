package io.basc.framework.test;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.io.serializer.SerializerException;
import io.basc.framework.util.sequences.uuid.UUIDSequences;
import lombok.ToString;

public class CollectionFactoryTest {
	@Test
	public void list() throws ClassNotFoundException, SerializerException {
		TestList bean = new TestList();
		bean.add(UUIDSequences.getInstance().next());
		bean.setV(UUIDSequences.getInstance().next());

		TestList clone = CollectionUtils.clone(bean);
		Assert.assertTrue(clone.getV().equals(bean.getV()) && clone.size() == 1);
	}

	@Test
	public void map() {
		TestMap bean = new TestMap();
		bean.put(UUIDSequences.getInstance().next(), UUIDSequences.getInstance().next());
		bean.setV(UUIDSequences.getInstance().next());

		TestMap clone = CollectionUtils.clone(bean);
		Assert.assertTrue(clone.getV().equals(bean.getV()) && clone.size() == 1);
	}

	@ToString(callSuper = true)
	private static class TestList extends LinkedList<Object> {
		private static final long serialVersionUID = 1L;
		private String v;

		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}
	}

	@ToString(callSuper = true)
	private static class TestMap extends HashMap<Object, Object> {
		private static final long serialVersionUID = 1L;
		private String v;

		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}
	}
}
