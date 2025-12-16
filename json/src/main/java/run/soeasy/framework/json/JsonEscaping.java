package run.soeasy.framework.json;

import run.soeasy.framework.codec.MultipleCodec;
import run.soeasy.framework.core.spi.SystemServiceDiscoverer;

/**
 * JSON字符串转义/反转义SPI接口，定义符合JSON规范的字符串编解码能力，
 * 继承{@link MultipleCodec<String>}实现对称的encode（转义）和decode（反转义）操作。
 * 
 * @author soeasy.run
 * @see MultipleCodec
 * @see RFC8259Escaping
 */
public interface JsonEscaping extends MultipleCodec<String> {

	/**
	 * 默认JSON转义实现（优先通过SPI加载，兜底为{RFC8259Escaping#INSTANCE}）
	 */
	JsonEscaping DEFAULT = SystemServiceDiscoverer.getInstance().getServices(JsonEscaping.class).findFirst()
			.orElseGet(() -> RFC8259Escaping.INSTANCE);
}