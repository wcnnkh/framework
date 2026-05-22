package run.soeasy.framework.codec;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompositeCodec<S, T, E extends Encoder<S, T>, D extends Decoder<T, S>> implements Codec<S, T>{
    @NonNull
    private final E encoder;
    @NonNull
    private final D decoder;

    @Override
    public S decode(T source) throws CodecException {
        return decoder.decode(source);
    }

    @Override
    public T encode(S source) throws CodecException {
        return encoder.encode(source);
    }
}
