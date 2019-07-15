package scw.core.utils;

import java.lang.reflect.Field;

import org.objenesis.ObjenesisException;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeUtils {
	private static final Unsafe unsafe;

	static {
		Field f;
		try {
			f = Unsafe.class.getDeclaredField("theUnsafe");
		} catch (NoSuchFieldException e) {
			throw new ObjenesisException(e);
		}
		f.setAccessible(true);
		try {
			unsafe = (Unsafe) f.get(null);
		} catch (IllegalAccessException e) {
			throw new ObjenesisException(e);
		}
	}

	private UnsafeUtils() {
	}

	public static Unsafe getUnsafe() {
		return unsafe;
	}
}
