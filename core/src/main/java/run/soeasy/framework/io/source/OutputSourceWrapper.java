package run.soeasy.framework.io.source;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import lombok.NonNull;

public interface OutputSourceWrapper<O extends OutputStream, W extends OutputSource<O>> extends OutputSource<O>, WriterSourceWrapper<Writer, W> {
    @Override
    @NonNull
    default O getOutputStream() throws IOException{
        return getSource().getOutputStream();
    }

    @Override
    @NonNull
    default Writer getWriter() throws IOException {
        return getSource().getWriter();
    }

    @Override
    default OutputSource<O> encode(@NonNull Charset charset) {
        return getSource().encode(charset);
    }

    @Override
    default @NonNull WritableByteChannel writableChannel() throws IOException {
        return getSource().writableChannel();
    }
}
