package com.aixtra.couchcode.data.option;

import com.aixtra.couchcode.data.result.Err;
import com.aixtra.couchcode.data.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class None<T> extends Option<T> {

    private None() {
    }

    @Contract(" -> new")
    public static <T> @NotNull None<T> create() {
        return new None<>();
    }

    @Override
    @NotNull
    public <U> Option<U> mapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable U> mapper) {
        return None.create();
    }

    @Override
    @NotNull
    public <U> Option<U> flatMapSome(@Nullable Function<? super @NotNull T, ? extends @Nullable Option<U>> mapper) {
        return None.create();
    }

    @Override
    @NotNull
    public Option<T> mapNone(@Nullable Supplier<@Nullable T> supplier) {
        if (supplier == null) {
            return None.create();
        }
        T result = supplier.get();
        return result == null ? None.create() : Some.of(result);
    }

    @Override
    @NotNull
    public Option<T> flatMapNone(@Nullable Supplier<? extends @Nullable Option<T>> supplier) {
        if (supplier == null) {
            return None.create();
        }
        Option<T> option = supplier.get();
        return option == null ? None.create() : option;
    }

    @Override
    @NotNull
    public Option<T> doOnSome(@Nullable Consumer<@NotNull T> consumer) {
        return this;
    }


    @Override
    @NotNull
    public Option<T> doOnNone(@Nullable Runnable runnable) {
        if (runnable != null) {
            try {
                runnable.run();
            } catch (Exception e) {
                LOGGER.error("Error thrown in runnable of Option.doOnNone(runnable)", e);
            }
        }
        return this;
    }

    @Override
    @NotNull
    public Result<T> asResult() {
        return Err.of(new NullPointerException("Option is empty"));
    }

    @Override
    public boolean isPresent() {
        return false;
    }
}