package scw.retry;

/**
 * Interface for statistics reporting of retry attempts. Counts the number of retry
 * attempts, successes, errors (including retries), and aborts.
 * @author shuchaowen
 *
 */
public interface RetryStatistics {
	/**
	 * @return the number of completed successful retry attempts.
	 */
	int getCompleteCount();

	/**
	 * Get the number of times a retry block has been entered, irrespective of how many
	 * times the operation was retried.
	 * @return the number of retry blocks started.
	 */
	int getStartedCount();

	/**
	 * Get the number of errors detected, whether or not they resulted in a retry.
	 * @return the number of errors detected.
	 */
	int getErrorCount();

	/**
	 * Get the number of times a block failed to complete successfully, even after retry.
	 * @return the number of retry attempts that failed overall.
	 */
	int getAbortCount();

	/**
	 * Get the number of times a recovery callback was applied.
	 * @return the number of recovered attempts.
	 */
	int getRecoveryCount();

	/**
	 * Get an identifier for the retry block for reporting purposes.
	 * @return an identifier for the block.
	 */
	String getName();
}
