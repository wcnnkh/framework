package scw.cloud.loadbalancer;

public enum State {
	/** Request was handled successfully. */
	SUCCESS,
	/** Request reached the server but failed due to timeout or internal error. */
	FAILED,
	/** Request did not go off box and should not be counted for statistics. */
	DISCARD
}
