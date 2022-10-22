package com.aixtra.couchcode.util.data.result;

import com.aixtra.couchcode.util.data.option.None;
import com.aixtra.couchcode.util.data.option.Option;
import com.aixtra.couchcode.util.data.option.Some;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <h1>A Result type that can be used to return a value or an error</h1>
 * <h2>A Result can be either a {@link Ok} or a {@link Err} which could be:</h2>
 * <h3>Checked with the isSuccess() method and then cast to the correct type</h3>
 * <pre>{@code
 * if (result.isSuccess()) {
 *    System.out.println(((Ok<String>) result).getValue());
 * }
 * }</pre>
 * <h3>Pattern matched with an if</h3>
 * <pre>{@code
 * if (result instanceof Ok<String> success) {
 *   System.out.println(success.getValue());
 * }
 * }</pre>
 * <h3>Pattern matched with a switch</h3>
 * <pre>{@code
 * String value = switch (result) {
 *    case Ok<String> success -> success.getValue();
 *    case Err<String> failure -> "Error: " + failure.getError().getMessage();
 * };
 * }</pre>
 *
 * @param <T> the type of the value
 */
@SuppressWarnings("unused")
public sealed abstract class Result<T> permits Err, Ok {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Result.class);

    /**
     * If getting the supplier didn't throw an exception creates an {@link Ok}, creates an {@link Err} otherwise
     *
     * @param supplier the supplier to get the value from
     * @param <T>      the type of the value
     * @return the result
     */
    @NotNull
    @Contract("_ -> new")
    public static <T> Result<T> of(@Nullable Supplier<@Nullable T> supplier) {
        try {
            if (supplier == null) {
                return Err.of(new NullPointerException("Supplier for Result.of(supplier) is null"));
            }
            T value = supplier.get();
            if (value == null) {
                return Err.of(new NullPointerException("Getting Supplier for Result.of(supplier) returned null"));
            }
            return Ok.of(value);
        } catch (Throwable error) {
            return Err.of(error);
        }
    }

    /**
     * Applies a map function to the result in a safe way (catches any exceptions as {@link Err})
     *
     * @param mapper the function to map the result
     * @return the mapped result
     */
    @NotNull
    public <U> Result<U> map(@Nullable Function<@NotNull Result<T>, @Nullable Result<U>> mapper) {
        if (mapper == null) {
            return Err.of(new NullPointerException("Mapper for Result.map(mapper) is null"));
        }
        try {
            Result<U> result = mapper.apply(this);
            if (result == null) {
                return Err.of(new NullPointerException("Mapper for Result.map(mapper) returned null"));
            }
            return result;
        } catch (Throwable throwable) {
            return Err.of(throwable);
        }
    }

    /**
     * If the result is {@link Ok} maps the value of Ok to a new Value or returns the {@link Err}
     * <br><br>
     * This follows the pattern of <a href="https://blog.logrocket.com/what-is-railway-oriented-programming/">railway oriented programming.</a>
     *
     * @param mapper the function to map the value
     * @return the new Result
     */
    @NotNull
    public abstract <U> Result<U> mapOk(@Nullable Function<@NotNull T, @Nullable U> mapper);

    /**
     * If the result is {@link Ok} maps the value of Ok to a new Result or returns the {@link Err}
     *
     * @param mapper the function to map the value
     * @return the new Result
     */
    @NotNull
    public abstract <U> Result<U> flatMapOk(@Nullable Function<@NotNull T, @Nullable Result<U>> mapper);

    /**
     * If the result is {@link Err} maps the error of Err to a new Value or returns the {@link Ok}
     * <br><br>
     * This is the possibility to switch back into the happy path
     *
     * @param mapper the function to map the error
     * @return the new Result
     */
    @NotNull
    public abstract Result<T> mapErr(@Nullable Function<@NotNull Throwable, @Nullable T> mapper);

    /**
     * If the result is {@link Err} maps the error of Err to a new Result or returns the {@link Ok}
     * <br><br>
     * This is the possibility to switch back into the happy path
     *
     * @param mapper the function to map the error
     * @return the new Result
     */
    @NotNull
    public abstract Result<T> flatMapErr(@Nullable Function<@NotNull Throwable, @Nullable Result<T>> mapper);

    /**
     * Sends a throwable to the consumer when the Result is an{@link Err}
     *
     * @param consumer the consumer to send the throwable to
     * @return the current result
     */
    @NotNull
    public abstract Result<T> doOnErr(@Nullable Consumer<@NotNull Throwable> consumer);

    /**
     * Sends a value to the consumer when the Result is an{@link Ok}
     *
     * @param consumer the consumer to send the value to
     * @return the current result
     */
    @NotNull
    public abstract Result<T> doOnOk(@Nullable Consumer<@NotNull T> consumer);

    /**
     * If the result is {@link Ok} maps the value of Ok to a new {@link Some} or logs the error and returns {@link None}
     *
     * @return the new Option
     */
    @NotNull
    public abstract Option<T> asOption();

    public abstract boolean isPresent();
}

