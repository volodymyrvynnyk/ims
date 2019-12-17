package com.ita.if103java.ims.util;

import com.ita.if103java.ims.exception.CRUDException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.NumberUtils;

import java.util.Optional;
import java.util.function.Consumer;

public class JDBCUtils {
    public static <T extends Number> T createWithAutogeneratedId(Class<T> idType, Consumer<KeyHolder> consumer) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        consumer.accept(keyHolder);
        return Optional
            .ofNullable(keyHolder.getKey())
            .map(number -> NumberUtils.convertNumberToTargetClass(number, idType))
            .orElseThrow(() -> new CRUDException("Failed to generate PK"));
    }
}
