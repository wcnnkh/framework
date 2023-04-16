package io.basc.framework.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeightedElement<E> implements Weighted {
	private final int weight;
	private final E element;
}
