package io.basc.framework.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.basc.framework.util.Elements;
import io.basc.framework.util.page.Paginations;
import io.basc.framework.util.sequences.uuid.UUIDSequences;

public class InMemoryPaginationsTest {
	@Test
	public void test() {
		int count = 100;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			list.add(UUIDSequences.getUUID());
		}

		for (int i = 1; i < count; i++) {
			Paginations<String> paginations = new Paginations<String>(Elements.of(list));
			paginations.setPageSize(i);
			Assert.assertTrue(paginations.jumpToPage(paginations.getPages() + 1).getElements().count() == 0);
			Assert.assertTrue(!paginations.jumpToPage(paginations.getPages()).hasNext());
			Assert.assertArrayEquals(list.toArray(new String[0]),
					paginations.all().getElements().toArray(String[]::new));
		}
	}
}
