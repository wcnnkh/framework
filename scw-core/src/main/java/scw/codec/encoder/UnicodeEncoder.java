package scw.codec.encoder;

import scw.codec.EncodeException;
import scw.codec.Encoder;

public class UnicodeEncoder implements Encoder<CharSequence, CharSequence>{
	private static final String EMPTY = "";
	public static final UnicodeEncoder DEFAULT = new UnicodeEncoder();

	@Override
	public CharSequence encode(CharSequence source) throws EncodeException {
		if(source == null) {
			return null;
		}
		
		int len = source.length();
		if(len == 0) {
			return EMPTY;
		}
		
		StringBuilder builder = new StringBuilder(len);
		for(int i=0; i<len; i++) {
			char ch = source.charAt(i);
			if ( ch < 256 ) {
				builder.append( ch );
			} else {
				builder.append( "\\u" +  Integer.toHexString( ch& 0xffff ) );
			}
		}
		return builder;
	}
}
