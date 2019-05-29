package scw.mq.rabbit;

public class RabbitException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RabbitException(Throwable e) {
		super(e);
	}
}
