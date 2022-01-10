package io.basc.framework.test;

import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.XUtils;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

public class CollectionFactoryTest {
	@Test
	public void list() {
		TestList bean = new TestList();
		bean.add(XUtils.getUUID());
		bean.setV(XUtils.getUUID());

		TestList clone = CollectionFactory.clone(bean);
		Assert.assertTrue(clone.getV().equals(bean.getV()) && clone.size() == 1);
	}

	@Test
	public void map() {
		TestMap bean = new TestMap();
		bean.put(XUtils.getUUID(), XUtils.getUUID());
		bean.setV(XUtils.getUUID());

		TestMap clone = CollectionFactory.clone(bean);
		Assert.assertTrue(clone.getV().equals(bean.getV()) && clone.size() == 1);
	}

	private static class TestList extends LinkedList<Object> {
		private static final long serialVersionUID = 1L;
		private String v;

		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}

		@Override
		public String toString() {
			return MapperUtils.toString(this);
		}
	}

	private static class TestMap extends HashMap<Object, Object> {
		private static final long serialVersionUID = 1L;
		private String v;

		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}

		@Override
		public String toString() {
			return MapperUtils.toString(this);
		}
	}
}