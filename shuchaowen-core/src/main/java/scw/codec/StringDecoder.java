package scw.codec;


public interface StringDecoder extends Decoder {

    /**
     * Decodes a String and returns a String.
     *
     * @param source
     *            the String to decode
     * @return the encoded String
     * @throws DecoderException
     *             thrown if there is an error condition during the Encoding process.
     */
    String decode(String source) throws DecoderException;
}
