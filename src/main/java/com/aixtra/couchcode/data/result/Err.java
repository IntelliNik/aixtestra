package com.aixtra.couchcode.data.result;

import com.aixtra.couchcode.data.option.None;
import com.aixtra.couchcode.data.option.Option;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class Err<T> extends Result<T> {
    private final Throwable error;

    private Err(@NotNull Throwable error) {
        this.error = error;
    }

    @Contract("_ -> new")
    @NotNull
    public static <T> Err<T> of(@Nullable Throwable value) {
        return value != null ?
                new Err<>(value) :
                new Err<>(new NullPointerException("Err.of() error is null"));

    }

    @Contract("_ -> new")
    @NotNull
    public static <T> Err<T> of(@Nullable String message) {
        return message != null ?
                new Err<>(new RuntimeException(message)) :
                new Err<>(new NullPointerException("Err.of() message is null"));
    }

    /**
     * Gets the wrapped error.
     *
     * @return the result error
     */
    public Throwable getError() {
        return error;
    }

    @Override
    @NotNull
    public <U> Result<U> mapOk(@Nullable Function<@NotNull T, @Nullable U> mapper) {
        return new Err<>(error);
    }

    @Override
    @NotNull
    public <U> Result<U> flatMapOk(@Nullable Function<@NotNull T, @Nullable Result<U>> mapper) {
        return new Err<>(error);
    }

    @Override
    @NotNull
    public Result<T> mapErr(@Nullable Function<@NotNull Throwable, @Nullable T> mapper) {
        if (mapper == null) {
            return new Err<>(new NullPointerException("Mapper for Result.mapErr(mapper) is null"));
        }
        try {
            T result = mapper.apply(error);
            return result == null ? new Err<>(new NullPointerException("Mapper for Result.mapErr(mapper) returned null")) : Ok.of(result);
        } catch (Throwable throwable) {
            return new Err<>(throwable);
        }
    }

    @Override
    @NotNull
    public Result<T> flatMapErr(@Nullable Function<@NotNull Throwable, @Nullable Result<T>> mapper) {
        if (mapper == null) {
            return new Err<>(new NullPointerException("Mapper for Result.flatMapErr(mapper) is null"));
        }
        try {
            Result<T> result = mapper.apply(error);
            return result == null ? new Err<>(new NullPointerException("Mapper for Result.flatMapErr(mapper) returned null")) : result;
        } catch (Throwable throwable) {
            return new Err<>(throwable);
        }
    }

    @Override
    @NotNull
    public Result<T> doOnErr(@Nullable Consumer<@NotNull Throwable> consumer) {
        if (consumer != null) {
            try {
                consumer.accept(error);
            } catch (Throwable e) {
                LOGGER.error("Error thrown in consumer of Result.doOnErr(consumer)", e);
            }
        }
        return this;
    }

    @Override
    @NotNull
    public Result<T> doOnOk(@Nullable Consumer<@NotNull T> consumer) {
        return this;
    }

    @Override
    @NotNull @Unmodifiable
    public Option<T> asOption() {
        LOGGER.error("Error dropped when converting Result.asOption()", error);
        return None.create();
    }

    @Override
    public boolean isPresent() {
        return false;
    }
}