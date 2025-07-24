package run.soeasy.framework.messaging.convert.support;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.messaging.convert.MessageConverter;
import run.soeasy.framework.messaging.convert.MessageConverterAware;
import run.soeasy.framework.messaging.convert.MessageConverters;

/**
 * 嵌套消息转换器抽象基类，继承自{@link AbstractMessageConverter}并实现{@link MessageConverterAware}接口，
 * 支持在转换过程中嵌套使用其他消息转换器，适用于复杂消息结构的转换场景（如消息体中包含嵌套对象）。
 * 
 * <p>
 * 核心特性： -
 * 内置一个{@link MessageConverter}实例，默认使用系统级转换器集合（{@link SystemMessageConverters#getInstance()}）；
 * - 实现{@link MessageConverterAware}接口，支持外部注入消息转换器，灵活替换嵌套的转换逻辑； -
 * 作为抽象类，需子类实现具体的消息读写逻辑，同时可利用嵌套的转换器处理子对象转换。
 * 
 * @author soeasy.run
 * @see AbstractMessageConverter
 * @see MessageConverterAware
 * @see SystemMessageConverters
 */
@Getter
@Setter
public abstract class AbstractNestedMessageConverter extends AbstractMessageConverter implements MessageConverterAware {

	/**
	 * 嵌套使用的消息转换器，用于处理子对象的转换（非空），
	 * 默认使用系统级消息转换器集合，可通过{@link #setMessageConverter}方法替换。
	 */
	@NonNull
	private MessageConverter messageConverter = MessageConverters.system();

}