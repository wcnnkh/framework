package io.basc.framework.logger;

import java.util.logging.Level;

import io.basc.framework.util.Assert;

public class CustomLevel extends Level {
	private static final long serialVersionUID = 1L;
	public static final CustomLevel TRACE = new CustomLevel("TRACE", java.util.logging.Level.FINER.intValue(),
			java.util.logging.Level.FINE.getResourceBundleName());
	public static final CustomLevel DEBUG = new CustomLevel("DEBUG", java.util.logging.Level.FINE.intValue(),
			java.util.logging.Level.CONFIG.getResourceBundleName());
	public static final CustomLevel WARN = new CustomLevel("WARN", java.util.logging.Level.WARNING.intValue(),
			java.util.logging.Level.WARNING.getResourceBundleName());
	public static final CustomLevel ERROR = new CustomLevel("ERROR", java.util.logging.Level.SEVERE.intValue(),
			java.util.logging.Level.SEVERE.getResourceBundleName());

	public CustomLevel(String name, int value) {
		super(name, value);
	}

	public CustomLevel(String name, int value, String resourceBundleName) {
		super(name, value, resourceBundleName);
	}

	/**
	 * 大于或等于
	 * 
	 * @param origin
	 * @param target
	 * @return
	 */
	public static boolean isGreaterOrEqual(Level origin, Level target) {
		return origin.intValue() >= target.intValue();
	}

	public static Level parse(String levelName) {
		Assert.requiredArgument(levelName != null, levelName);
		return Level.parse(levelName.toUpperCase());
	}
}
