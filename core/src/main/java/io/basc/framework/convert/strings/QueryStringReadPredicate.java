package io.basc.framework.convert.strings;

public interface QueryStringReadPredicate<V> {
	boolean test(CharSequence key, V value);
}
