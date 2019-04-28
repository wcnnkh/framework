package scw.data.memcached;

import net.rubyeye.xmemcached.GetsResponse;

public final class CAS<T> {
	private final long cas;
	private final T value;

	public CAS(long cas, T value) {
		this.cas = cas;
		this.value = value;
	}

	public long getCas() {
		return cas;
	}

	public T getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.cas ^ (this.cas >>> 32));
		result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		GetsResponse<T> other = (GetsResponse<T>) obj;
		if (this.cas != other.getCas()) {
			return false;
		}
		if (this.value == null) {
			if (other.getValue() != null) {
				return false;
			}
		} else if (!this.value.equals(other.getValue())) {
			return false;
		}
		return true;
	}
}
