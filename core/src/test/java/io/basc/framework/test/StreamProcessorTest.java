package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

import io.basc.framework.util.Cursor;
import io.basc.framework.util.StreamOptional;
import io.basc.framework.util.XUtils;

public class StreamProcessorTest {
	@Test
	public void test() throws Throwable {
		String a = "1";
		StreamOptional<String> streamProcessor = StreamOptional.of(() -> a);
		streamProcessor = streamProcessor.stream((value) -> {
			System.out.println("1");
			return "1-" + XUtils.getUUID();
		}).onClose(() -> {
			System.out.println("关闭1");
		});

		streamProcessor = streamProcessor.stream((value) -> {
			System.out.println("2");
			return "2-" + XUtils.getUUID();
		}).onClose(() -> {
			System.out.println("关闭2");
		});
		System.out.println(streamProcessor.get());
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

		Cursor<String> cursor = Cursor.create(list.stream().onClose(() -> {
			System.out.println("关闭游标");
		}).onClose(() -> {
			System.out.println("关闭游标2");
		}));
		try {
			cursor.forEachRemaining(
					(s) -> System.out.println("pos:" + cursor.getPosition() + ":" + s + "," + cursor.isClosed()));
			assertTrue(cursor.isClosed());
		} finally {
			cursor.close();
		}
	}
}
