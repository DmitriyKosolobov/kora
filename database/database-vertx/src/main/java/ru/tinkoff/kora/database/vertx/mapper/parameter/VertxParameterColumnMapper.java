package ru.tinkoff.kora.database.vertx.mapper.parameter;

import ru.tinkoff.kora.common.Mapping;

import jakarta.annotation.Nullable;

public interface VertxParameterColumnMapper<T> extends Mapping.MappingFunction {
    @Nullable
    Object apply(@Nullable T value);
}
