package scw.rabbitmq.method;

public @interface Rabbitmq {
	public String routingKey();

	public String queueName();

	public boolean durable() default true;

	public boolean exclusive() default false;

	public boolean autoDelete() default false;

	public String exchangeService();
}
