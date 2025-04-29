package run.soeasy.framework.core.convert.support;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConditionalConversionService;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConvertiblePair;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypedValue;

class ByteBufferConversionService extends AbstractConversionService implements ConditionalConversionService {

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

	public ByteBufferConversionService(ConversionService conversionService) {
		setConversionService(conversionService);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return CONVERTIBLE_PAIRS;
	}

	@Override
	public Object apply(@NonNull TypedValue value, @NonNull TypeDescriptor targetType) throws ConversionException {
		Object source = value.get();
		TypeDescriptor sourceType = value.getReturnTypeDescriptor();
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

	private Object convertFromByteBuffer(ByteBuffer source, TypeDescriptor targetType) {
		byte[] bytes = new byte[source.remaining()];
		source.get(bytes);

		if (targetType.isAssignableTo(BYTE_ARRAY_TYPE)) {
			return bytes;
		}
		return this.getConversionService().convert(bytes, BYTE_ARRAY_TYPE, targetType);
	}

	private Object convertToByteBuffer(Object source, TypeDescriptor sourceType) {
		byte[] bytes = (byte[]) (source instanceof byte[] ? source
				: this.getConversionService().convert(source, sourceType, BYTE_ARRAY_TYPE));

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
