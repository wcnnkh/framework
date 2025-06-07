package run.soeasy.framework.core.page;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.collection.Listable;
import run.soeasy.framework.core.collection.ListableWrapper;

@Data
public class Cursor<K, V> implements Pageable<K, V>, ListableWrapper<V, Listable<V>>, Serializable {
	private static final long serialVersionUID = 1L;
	private final K cursorId;
	@NonNull
	private final Listable<V> source;
	private final K nextCursorId;
}
