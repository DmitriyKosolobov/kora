package ru.tinkoff.kora.database.common.annotation.processor.jdbc;

import jakarta.annotation.Nullable;
import ru.tinkoff.kora.database.common.annotation.processor.entity.TestEntityRecord;
import ru.tinkoff.kora.database.jdbc.mapper.parameter.JdbcParameterColumnMapper;
import ru.tinkoff.kora.database.jdbc.mapper.result.JdbcResultColumnMapper;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class JdbcEntity {

    public record AllNativeTypesEntity(
        boolean booleanPrimitive,
        @Nullable Boolean booleanBoxed,
        short shortPrimitive,
        @Nullable Short shortBoxed,
        int integerPrimitive,
        @Nullable Integer integerBoxed,
        long longPrimitive,
        @Nullable Long longBoxed,
        double doublePrimitive,
        @Nullable Double doubleBoxed,
        String string,
        BigDecimal bigDecimal,
        byte[] byteArray,
        LocalDateTime localDateTime,
        LocalDate localDate
    ) {}


    public static final class TestEntityFieldJdbcResultColumnMapper implements JdbcResultColumnMapper<TestEntityRecord.MappedField1> {
        @Override
        public TestEntityRecord.MappedField1 apply(ResultSet row, int index) throws SQLException {
            return new TestEntityRecord.MappedField1();
        }
    }

    public static class TestEntityFieldJdbcResultColumnMapperNonFinal implements JdbcResultColumnMapper<TestEntityRecord.MappedField2> {
        @Override
        public TestEntityRecord.MappedField2 apply(ResultSet row, int index) throws SQLException {
            return null;
        }
    }

    public static final class TestEntityFieldJdbcParameterColumnMapper implements JdbcParameterColumnMapper<TestEntityRecord.MappedField1> {

        @Override
        public void set(PreparedStatement stmt, int index, TestEntityRecord.MappedField1 value) throws SQLException {

        }
    }

    public static class TestEntityFieldJdbcParameterColumnMapperNonFinal implements JdbcParameterColumnMapper<TestEntityRecord.MappedField2> {

        @Override
        public void set(PreparedStatement stmt, int index, TestEntityRecord.MappedField2 value) throws SQLException {

        }
    }
}
