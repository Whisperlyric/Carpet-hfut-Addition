package dev.whisperlyric.carpet_hfut_addition.logger;

import carpet.logging.LoggerRegistry;
import dev.whisperlyric.carpet_hfut_addition.HFUTServer;
import dev.whisperlyric.carpet_hfut_addition.logger.annotation.Logger;

import java.lang.reflect.Field;

/**
 * Loggers registered by Carpet HFUT Addition. Every {@code public static boolean}
 * field annotated with {@link Logger} is registered into Carpet's {@code /log}
 * manager at game start; the field name becomes the logger name.
 */
public class HFUTLoggers {
    // declare @Logger fields here

    public static void registerLoggers() {
        for (Field field : HFUTLoggers.class.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Logger.class)) {
                continue;
            }
            Logger anno = field.getAnnotation(Logger.class);
            String defaultValue = "".equals(anno.defaultValue()) ? null : anno.defaultValue();
            String[] options = "".equals(anno.options()[0]) ? null : anno.options();
            LoggerRegistry.registerLogger(field.getName(), new carpet.logging.Logger(
                    field, field.getName(), defaultValue, options, anno.strictOptions()
            ));
        }
        HFUTServer.LOGGER.debug("{} loggers registered", HFUTServer.fancyName);
    }
}
