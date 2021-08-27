package scw.test;

import io.basc.framework.util.XUtils;
import io.basc.framework.util.stream.StreamProcessor;
import io.basc.framework.util.stream.StreamProcessorSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

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
		System.out.println(streamProcessor.process());

		List<String> list = new ArrayList<>();
		list.add(a);
		
		Stream<String> stream = StreamSupport.stream(list.spliterator(), false).map((value)->{
			System.out.println("s1");
			return "s1" + XUtils.getUUID();
		}).onClose(()->{
			System.out.println("关闭s1");
		});
		
		stream = stream.map((value) -> {
			System.out.println("s2");
			return "s2-" + XUtils.getUUID();
		}).onClose(() -> {
			System.out.println("关闭s2");
		});
		System.out.println(stream.filter((e) -> true).findFirst().get());
	}
}
