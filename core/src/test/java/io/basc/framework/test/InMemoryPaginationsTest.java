package io.basc.framework.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.basc.framework.util.XUtils;
import io.basc.framework.util.page.InMemoryPaginations;

public class InMemoryPaginationsTest {
	@Test
	public void test() {
		int count = 100;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			list.add(XUtils.getUUID());
		}

		for (int i = 1; i < count; i++) {
			InMemoryPaginations<String> paginations = new InMemoryPaginations<String>(list, 0, i);
			Assert.assertTrue(paginations.jumpToPage(paginations.getPages() + 1).getList().size() == 0);
			Assert.assertTrue(!paginations.jumpToPage(paginations.getPages()).hasNext());
			Assert.assertArrayEquals(list.toArray(new String[0]), paginations.all().stream().toArray(String[]::new));
		}
	}
}
