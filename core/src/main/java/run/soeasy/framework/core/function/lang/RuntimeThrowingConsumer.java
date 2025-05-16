package run.soeasy.framework.core.function.lang;

import java.util.function.Consumer;

public interface RuntimeThrowingConsumer<S, E extends RuntimeException> extends ThrowingConsumer<S, E>, Consumer<S> {
}
