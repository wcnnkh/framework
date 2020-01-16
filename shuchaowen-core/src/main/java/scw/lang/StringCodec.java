package scw.lang;

/**
 * string编解码器
 * 
 * @author shuchaowen
 *
 */
public interface StringCodec {
	byte[] encode(String text);

	String decode(byte[] bytes);
}
