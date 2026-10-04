package dev.whisperlyric.carpet_hfut_addition.logger.annotation;

import dev.whisperlyric.carpet_hfut_addition.logger.callback.LoggerCallback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@code public static boolean} field in
 * {@link dev.whisperlyric.carpet_hfut_addition.logger.HFUTLoggers} as a Carpet
 * logger. The field name becomes the logger name shown in {@code /log}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Logger {
    String defaultValue() default "";

    String[] options() default {""};

    boolean strictOptions() default false;

    Class<? extends LoggerCallback> callback() default LoggerCallback.class;
}
