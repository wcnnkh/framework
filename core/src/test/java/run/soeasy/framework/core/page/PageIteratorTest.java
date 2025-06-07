package run.soeasy.framework.core.page;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

public class PageIteratorTest {
	@Test
	public void test() {
		List<UUID> list = new ArrayList<>();
		for (int i = 0; i < 1001; i++) {
			list.add(UUID.randomUUID());
		}

		for (int pageSize = 200; pageSize < 2002; pageSize++) {
			PageIterator<UUID> pageIterator = new PageIterator<>(list.iterator(), pageSize);
			long total = 0;
			while (pageIterator.hasNext()) {
				total += pageIterator.next().getElements().count();
			}
			assert total == list.size();
		}
	}
}
