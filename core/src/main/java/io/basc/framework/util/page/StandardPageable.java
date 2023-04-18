package io.basc.framework.util.page;

import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardPageable<K, T> implements Pageable<K, T> {
	private K cursorId;
	private Elements<T> elements;
	private K nextCursorId;

	public StandardPageable(Pageable<K, T> pageable) {
		this(pageable.getCursorId(), pageable.getElements(), pageable.getNextCursorId());
	}

}