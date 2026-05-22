package run.soeasy.framework.io.source;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

@RequiredArgsConstructor
final class DecodeInputSource<I extends InputStream> implements InputSource<I>, CharsetCapable {
    private final InputSource<I> inputSource;
    private final Charset charset;

    @Override
    public I getInputStream() throws IOException {
        return inputSource.getInputStream();
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public Reader getReader() throws IOException {
        return new InputStreamReader(getInputStream(), charset);
    }

    @Override
    public InputSource<I> decode(@NonNull Charset charset) {
        return new DecodeInputSource<>(inputSource, charset);
    }
}
