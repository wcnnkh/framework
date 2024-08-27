package io.basc.framework.util.select;

import io.basc.framework.util.Wrapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WeightedWrapper<W> implements Weighted, Wrapper<W> {
	private final W source;
	private final int weight;
}
