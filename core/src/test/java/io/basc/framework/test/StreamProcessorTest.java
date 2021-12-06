package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

import io.basc.framework.util.XUtils;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.StreamProcessor;
import io.basc.framework.util.stream.StreamProcessorSupport;

public class StreamProcessorTest {
	@Test
	public void test() {
		String a = "1";
		StreamProcessor<String, RuntimeException> streamProcessor = StreamProcessorSupport.stream(a);

		streamProcessor = streamProcessor.map((value) -> {
			System.out.println("1");
			return "1-" + XUtils.getUUID();
		}).onClose(() -> {
			System.out.println("关闭1");
		});

		streamProcessor = streamProcessor.map((value) -> {
			System.out.println("2");
			return "2-" + XUtils.getUUID();
		}).onClose(() -> {
			System.out.println("关闭2");
		});
		streamProcessor.setAutoClose(true);
		System.out.println(streamProcessor.process());

		List<String> list = new LinkedList<>();
		list.add(a);
		for (int i = 0; i < 10; i++) {
			list.add(XUtils.getUUID());
		}

		Stream<String> stream = StreamSupport.stream(list.spliterator(), false).map((value) -> {
			System.out.println("s1");
			return "s1" + XUtils.getUUID();
		}).onClose(() -> {
			System.out.println("关闭s1");
		});

		stream = stream.map((value) -> {
			System.out.println("s2");
			return "s2-" + XUtils.getUUID();
		}).onClose(() -> {
			System.out.println("关闭s2");
		});
		System.out.println(stream.filter((e) -> true).findFirst().get());

		Cursor<String> cursor = new Cursor<>(list.stream().onClose(() -> {
			System.out.println("关闭游标");
		}).onClose(() -> {
			System.out.println("关闭游标2");
		}));
		try {
			assertTrue(cursor.isAutoClose());
			cursor.forEach(
					(s) -> System.out.println("pos:" + cursor.getPosition() + ":" + s + "," + cursor.isClosed()));
			assertTrue(cursor.isClosed());
		} finally {
			cursor.close();
		}
	}
}
