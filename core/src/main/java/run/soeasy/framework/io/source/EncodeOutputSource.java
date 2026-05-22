package run.soeasy.framework.io.source;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

@RequiredArgsConstructor
final class EncodeOutputSource<O extends OutputStream> implements OutputSource<O>, CharsetCapable {
    private final OutputSource<O>  outputSource;
    private final Charset charset;

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public O getOutputStream() throws IOException {
        return outputSource.getOutputStream();
    }

    @Override
    public Writer getWriter() throws IOException {
        return new OutputStreamWriter(getOutputStream(), charset);
    }

    @Override
    public OutputSource<O> encode(@NonNull Charset charset) {
        return new EncodeOutputSource<>(this.outputSource, charset);
    }
}
