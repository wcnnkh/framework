package run.soeasy.framework.logging;

import java.util.logging.Level;

import run.soeasy.framework.core.Assert;

public class CustomLevel extends Level {
	private static final long serialVersionUID = 1L;
	public static final CustomLevel TRACE = new CustomLevel("TRACE", Level.FINER.intValue(),
			Level.FINE.getResourceBundleName());
	public static final CustomLevel DEBUG = new CustomLevel("DEBUG", FINE.intValue(),
			Level.CONFIG.getResourceBundleName());
	public static final CustomLevel WARN = new CustomLevel("WARN", Level.WARNING.intValue(),
			Level.WARNING.getResourceBundleName());
	public static final CustomLevel ERROR = new CustomLevel("ERROR", Level.SEVERE.intValue(),
			Level.SEVERE.getResourceBundleName());

	public CustomLevel(String name, int value) {
		super(name, value);
	}

	public CustomLevel(String name, int value, String resourceBundleName) {
		super(name, value, resourceBundleName);
	}

	public static boolean isGreaterOrEqual(Level origin, Level target) {
		return origin.intValue() >= target.intValue();
	}

	public static Level parse(String levelName) {
		Assert.requiredArgument(levelName != null, levelName);
		return Level.parse(levelName.toUpperCase());
	}
}
