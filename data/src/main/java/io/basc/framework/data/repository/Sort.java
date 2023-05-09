package io.basc.framework.data.repository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class Sort implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final SortSymbol symbol;
	private final List<Sort> withSorts;

	public Sort(String name, SortSymbol symbol, List<Sort> withSorts) {
		this.name = name;
		this.symbol = symbol;
		this.withSorts = withSorts == null ? Collections.emptyList() : Collections.unmodifiableList(withSorts);
	}
}
