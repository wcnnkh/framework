package io.basc.framework.redis;

public enum ScoreOption {
	/**
	 * Only update existing elements if the new score is greater than the current
	 * score. This flag doesn't prevent adding new elements.
	 */
	GT,
	/**
	 * Only update existing elements if the new score is less than the current
	 * score. This flag doesn't prevent adding new elements.
	 */
	LT
}