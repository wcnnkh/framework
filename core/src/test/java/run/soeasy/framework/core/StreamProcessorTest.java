package run.soeasy.framework.core;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

public class StreamProcessorTest {
	@Test
	public void test() throws Throwable {
		String a = "1";
		List<String> list = new LinkedList<>();
		list.add(a);
		for (int i = 0; i < 10; i++) {
			list.add(RandomUtils.uuid());
		}

		Stream<String> stream = StreamSupport.stream(list.spliterator(), false).map((value) -> {
			System.out.println("s1");
			return "s1" + RandomUtils.uuid();
		}).onClose(() -> {
			System.out.println("关闭s1");
		});

		stream = stream.map((value) -> {
			System.out.println("s2");
			return "s2-" + RandomUtils.uuid();
		}).onClose(() -> {
			System.out.println("关闭s2");
		});
		System.out.println(stream.filter((e) -> true).findFirst().get());
	}
}
