package com.aixtra.couchcode.util.data.option;

import com.aixtra.couchcode.util.data.result.Ok;
import com.aixtra.couchcode.util.data.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class Some<T> extends Option<T> {
    private final T value;

    private Some(@NotNull T value) {
        this.value = value;
    }

    @Contract("_ -> new")
    @NotNull
    public static <T> Option<T> of(@Nullable T value) {
        return value != null ? new Some<>(value) : None.create();
    }

    /**
     * Gets the wrapped value.
     *
     * @return the Options value.
     */
    @NotNull
    public T getValue() {
        return value;
    }

    @Override
    @NotNull
    public <U> Option<U> mapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable U> mapper) {
        if (mapper == null) {
            return None.create();
        }
        try {
            U result = mapper.apply(value);
            return result == null ? None.create() : new Some<>(result);
        } catch (Exception e) {
            LOGGER.error("Error thrown in mapper of Option.map(mapper)", e);
            return None.create();
        }
    }

    @Override
    @NotNull
    public <U> Option<U> flatMapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable Option<U>> mapper) {
        if (mapper == null) {
            return None.create();
        }
        try {
            Option<U> option = mapper.apply(value);
            return option == null ? None.create() : option;
        } catch (Exception e) {
            LOGGER.error("Error thrown in mapper of Option.flatMap(mapper)", e);
            return None.create();
        }
    }

    @Override
    @NotNull
    public Option<T> mapNone(@Nullable Supplier<@Nullable T> supplier) {
        return this;
    }

    @Override
    @NotNull
    public Option<T> flatMapNone(@Nullable Supplier<? extends @Nullable Option<T>> supplier) {
        return this;
    }

    @Override
    @NotNull
    public Option<T> doOnSome(@Nullable Consumer<@NotNull T> consumer) {
        if (consumer != null) {
            try {
                consumer.accept(value);
            } catch (Exception e) {
                LOGGER.error("Error thrown in consumer of Option.doOnSome(consumer)", e);
            }
        }
        return this;
    }

    @Override
    @NotNull
    public Option<T> doOnNone(@Nullable Runnable runnable) {
        return this;
    }

    @Override
    @NotNull
    public Result<T> asResult() {
        return Ok.of(value);
    }

    @Override
    public boolean isPresent() {
        return true;
    }
}