package run.soeasy.framework.core.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.soeasy.framework.core.collection.Elements;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomizeCursor<K, T> implements Cursor<K, T> {
	private K cursorId;
	private Elements<T> elements;
	private K nextCursorId;

	public CustomizeCursor(Cursor<K, T> cursor) {
		this(cursor.getCursorId(), cursor.getElements(), cursor.getNextCursorId());
	}

}