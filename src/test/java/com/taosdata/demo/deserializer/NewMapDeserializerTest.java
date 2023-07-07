package com.taosdata.demo.deserializer;

import com.taosdata.demo.entity.RecordMap;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewMapDeserializerTest {

    @Test
    public void deserialize() throws SQLException {
        // given
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        when(metaData.getColumnCount()).thenReturn(15);
        when(metaData.getColumnLabel(1)).thenReturn("ts");
        when(metaData.getColumnType(1)).thenReturn(Types.TIMESTAMP);
        when(metaData.getColumnLabel(2)).thenReturn("f1");
        when(metaData.getColumnType(2)).thenReturn(Types.TINYINT);
        when(metaData.getColumnLabel(3)).thenReturn("f2");
        when(metaData.getColumnType(3)).thenReturn(Types.SMALLINT);
        when(metaData.getColumnLabel(4)).thenReturn("f3");
        when(metaData.getColumnType(4)).thenReturn(Types.INTEGER);
        when(metaData.getColumnLabel(5)).thenReturn("f4");
        when(metaData.getColumnType(5)).thenReturn(Types.BIGINT);
        when(metaData.getColumnLabel(6)).thenReturn("f5");
        when(metaData.getColumnType(6)).thenReturn(Types.FLOAT);
        when(metaData.getColumnLabel(7)).thenReturn("f6");
        when(metaData.getColumnType(7)).thenReturn(Types.DOUBLE);
        when(metaData.getColumnLabel(8)).thenReturn("f7");
        when(metaData.getColumnType(8)).thenReturn(Types.TINYINT);
        when(metaData.getColumnLabel(9)).thenReturn("f8");
        when(metaData.getColumnType(9)).thenReturn(Types.SMALLINT);
        when(metaData.getColumnLabel(10)).thenReturn("f9");
        when(metaData.getColumnType(10)).thenReturn(Types.INTEGER);
        when(metaData.getColumnLabel(11)).thenReturn("f10");
        when(metaData.getColumnType(11)).thenReturn(Types.BIGINT);
        when(metaData.getColumnLabel(12)).thenReturn("f11");
        when(metaData.getColumnType(12)).thenReturn(Types.FLOAT);
        when(metaData.getColumnLabel(13)).thenReturn("f12");
        when(metaData.getColumnType(13)).thenReturn(Types.DOUBLE);
        when(metaData.getColumnLabel(14)).thenReturn("f13");
        when(metaData.getColumnType(14)).thenReturn(Types.BINARY);
        when(metaData.getColumnLabel(15)).thenReturn("f14");
        when(metaData.getColumnType(15)).thenReturn(Types.VARCHAR);

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(resultSet.getObject("ts")).thenReturn(123L);
        when(resultSet.getObject("f1")).thenReturn((byte) 1);
        when(resultSet.getObject("f2")).thenReturn((short) 2);
        when(resultSet.getObject("f3")).thenReturn(3);
        when(resultSet.getObject("f4")).thenReturn(4L);
        when(resultSet.getObject("f5")).thenReturn(5.55f);
        when(resultSet.getObject("f6")).thenReturn(6.6666d);
        when(resultSet.getObject("f7")).thenReturn((byte) 7);
        when(resultSet.getObject("f8")).thenReturn((short) 8);
        when(resultSet.getObject("f9")).thenReturn(9);
        when(resultSet.getObject("f10")).thenReturn(10L);
        when(resultSet.getObject("f11")).thenReturn(11.11f);
        when(resultSet.getObject("f12")).thenReturn(12.2222d);
        when(resultSet.getObject("f13")).thenReturn("abc");
        when(resultSet.getObject("f14")).thenReturn("北京");

        // when
        NewMapDeserializer d = new NewMapDeserializer();
        Map<String, Object> actual = d.deserialize(resultSet, null, null);

        // then
        Map<String, Object> expected = RecordMap.value;
        for (String key : expected.keySet()) {
            assertEquals(expected.get(key), actual.get(key));
        }
    }

}