package scw.redis.connection;

public class ClusterStateFailureException extends RedisSystemException {

	private static final long serialVersionUID = 333399051713240852L;

	/**
	 * Creates new {@link ClusterStateFailureException}.
	 *
	 * @param msg the detail message.
	 */
	public ClusterStateFailureException(String msg) {
		super(msg);
	}

	/**
	 * Creates new {@link ClusterStateFailureException}.
	 *
	 * @param msg   the detail message.
	 * @param cause the nested exception.
	 */
	public ClusterStateFailureException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
