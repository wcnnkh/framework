package scw.lucene;

import java.io.Serializable;
import java.util.List;

public class Tops<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final long total;
	private final List<T> docs;

	public Tops(long total, List<T> docs) {
		this.total = total;
		this.docs = docs;
	}

	public long getTotal() {
		return total;
	}

	public List<T> getDocs() {
		return docs;
	}
}
