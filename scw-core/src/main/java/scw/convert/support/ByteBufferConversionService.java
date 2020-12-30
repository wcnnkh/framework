package scw.convert.support;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.lang.Nullable;

public class ByteBufferConversionService extends ConditionalConversionService{

	private static final TypeDescriptor BYTE_BUFFER_TYPE = TypeDescriptor.valueOf(ByteBuffer.class);

	private static final TypeDescriptor BYTE_ARRAY_TYPE = TypeDescriptor.valueOf(byte[].class);

	private static final Set<ConvertiblePair> CONVERTIBLE_PAIRS;

	static {
		Set<ConvertiblePair> convertiblePairs = new HashSet<ConvertiblePair>(4);
		convertiblePairs.add(new ConvertiblePair(ByteBuffer.class, byte[].class));
		convertiblePairs.add(new ConvertiblePair(byte[].class, ByteBuffer.class));
		convertiblePairs.add(new ConvertiblePair(ByteBuffer.class, Object.class));
		convertiblePairs.add(new ConvertiblePair(Object.class, ByteBuffer.class));
		CONVERTIBLE_PAIRS = Collections.unmodifiableSet(convertiblePairs);
	}

	private final ConversionService conversionService;


	public ByteBufferConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return CONVERTIBLE_PAIRS;
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		boolean byteBufferTarget = targetType.isAssignableTo(BYTE_BUFFER_TYPE);
		if (source instanceof ByteBuffer) {
			ByteBuffer buffer = (ByteBuffer) source;
			return (byteBufferTarget ? buffer.duplicate() : convertFromByteBuffer(buffer, targetType));
		}
		if (byteBufferTarget) {
			return convertToByteBuffer(source, sourceType);
		}
		// Should not happen
		throw new IllegalStateException("Unexpected source/target types");
	}

	@Nullable
	private Object convertFromByteBuffer(ByteBuffer source, TypeDescriptor targetType) {
		byte[] bytes = new byte[source.remaining()];
		source.get(bytes);

		if (targetType.isAssignableTo(BYTE_ARRAY_TYPE)) {
			return bytes;
		}
		return this.conversionService.convert(bytes, BYTE_ARRAY_TYPE, targetType);
	}

	private Object convertToByteBuffer(@Nullable Object source, TypeDescriptor sourceType) {
		byte[] bytes = (byte[]) (source instanceof byte[] ? source :
				this.conversionService.convert(source, sourceType, BYTE_ARRAY_TYPE));

		if (bytes == null) {
			return ByteBuffer.wrap(new byte[0]);
		}

		ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
		byteBuffer.put(bytes);

		// Extra cast necessary for compiling on JDK 9 plus running on JDK 8, since
		// otherwise the overridden ByteBuffer-returning rewind method would be chosen
		// which isn't available on JDK 8.
		return ((Buffer) byteBuffer).rewind();
	}

}
