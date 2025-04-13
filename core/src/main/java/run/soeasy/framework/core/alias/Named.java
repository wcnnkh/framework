package run.soeasy.framework.core.alias;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Wrapper;

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

		@Override
		public Named rename(String name) {
			return new Renamed<>(name, source);
		}
	}

	@Data
	@RequiredArgsConstructor
	public static class SimpleNamed implements Named, Serializable {
		private static final long serialVersionUID = 1L;
		@NonNull
		private String name;
	}

	public static Named of(String name) {
		return new SimpleNamed(name);
	}

	String getName();

	default Named rename(String name) {
		return new Renamed<Named>(name, this);
	}
}
