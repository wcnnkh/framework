package run.soeasy.framework.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.page.Paginations;
import run.soeasy.framework.sequences.uuid.UUIDSequences;

public class InMemoryPaginationsTest {
	@Test
	public void test() {
		int count = 100;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			list.add(UUIDSequences.global().next());
		}

		for (int i = 1; i < count; i++) {
			Paginations<String> paginations = new Paginations<String>(Elements.of(list));
			paginations.setPageSize(i);
			Assert.assertTrue(
					paginations.jumpToPage(paginations.getPages() + 1).getElements().count().longValue() == 0);
			Assert.assertTrue(!paginations.jumpToPage(paginations.getPages()).hasNext());
			Assert.assertArrayEquals(list.toArray(new String[0]),
					paginations.all().getElements().toArray(String[]::new));
		}
	}
}
