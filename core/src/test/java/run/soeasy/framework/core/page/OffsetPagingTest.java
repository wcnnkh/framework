package run.soeasy.framework.core.page;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import run.soeasy.framework.sequences.UUIDSequence;

public class OffsetPagingTest {
	@Test
	public void test() {
		int count = 100;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			list.add(UUIDSequence.random().next());
		}

		for (int i = 1; i < count; i++) {
			OffsetPaging<String> paginations = new OffsetPaging<String>(0, i, list);
			Assert.assertTrue(paginations.jumpToPage(paginations.getPages() + 1).getElements().count() == 0);
			Assert.assertTrue(!paginations.jumpToPage(paginations.getPages()).hasNextPage());
			String[] leftArray = list.toArray(new String[0]);
			String[] rightArray = paginations.pages().flatMap((e) -> e.getElements()).toArray(String[]::new);
			Assert.assertArrayEquals(leftArray, rightArray);
		}
	}
}
