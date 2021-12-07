package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;

import io.basc.framework.util.stream.StreamProcessorSupport;

public class StreamConcatTest {
	@Test
	public void concat() {
		assertTrue(StreamProcessorSupport.concat(
				Arrays.asList(Arrays.asList(1, 2).stream(), Arrays.asList(4, 5).stream(), Arrays.asList(3, 4).stream()))
				.collect(Collectors.toList()).size() == 6);
	}
}
