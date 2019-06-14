package scw.mq.support.rabbit;

public interface SingleExchangeChannelFactory extends ChannelFactory {
	String getExchange();

	String getExchangeType();
}
