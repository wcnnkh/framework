package run.soeasy.framework.core.domain;

import lombok.NonNull;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 这是一个抽象的版本，意味的是可以进行比较的
 * 
 * @author soeasy.run
 *
 */
public interface Version extends Value, Comparable<Value> {
	/**
	 * 默认使用字符串的方式比较，如果有更合理的方式请重写
	 */
	@Override
	default int compareTo(@NonNull Value other) {
		return getAsString().compareTo(other.getAsString());
	}

	default Version join(@NonNull Version version) {
		return new JoinVersion(Streamable.array(this, version), null);
	}

	@Override
	default Version getAsVersion() {
		return this;
	}
}
