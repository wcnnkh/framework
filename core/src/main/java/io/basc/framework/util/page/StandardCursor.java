package io.basc.framework.util.page;

import io.basc.framework.util.collection.Elements;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardCursor<K, T> implements Cursor<K, T> {
	private K cursorId;
	private Elements<T> elements;
	private K nextCursorId;

	public StandardCursor(Cursor<K, T> cursor) {
		this(cursor.getCursorId(), cursor.getElements(), cursor.getNextCursorId());
	}

}