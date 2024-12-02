package io.basc.framework.util.alias;

import java.io.Serializable;

import io.basc.framework.util.Wrapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Named {

	public static interface NamedWrapper<W extends Named> extends Named, Wrapper<W> {
		@Override
		default String getName() {
			return getSource().getName();
		}
	}

	@RequiredArgsConstructor
	public static class Renamed<W extends Named> implements NamedWrapper<W> {
		@NonNull
		private final String name;
		@NonNull
		private final W source;

		@Override
		public String getName() {
			return name;
		}

		@Override
		public W getSource() {
			return source;
		}
	}

	@Data
	@NoArgsConstructor
	public static class SimpleNamed implements Named, Serializable {
		private static final long serialVersionUID = 1L;
		private String name;

		public SimpleNamed(Named name) {
			this.name = name.getName();
		}

		public SimpleNamed(String name) {
			this.name = name;
		}
	}

	public static Named of(String name) {
		return new SimpleNamed(name);
	}

	String getName();

	default Named rename(String name) {
		return new Renamed<Named>(name, this);
	}
}
