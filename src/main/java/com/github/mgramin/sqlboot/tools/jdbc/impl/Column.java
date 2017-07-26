package com.github.mgramin.sqlboot.tools.jdbc.impl;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import com.github.mgramin.sqlboot.tools.jdbc.CustomResultSet;
import com.github.mgramin.sqlboot.tools.jdbc.CustomResultSetImpl;
import com.github.mgramin.sqlboot.tools.jdbc.JdbcDbObject;
import com.github.mgramin.sqlboot.tools.jdbc.JdbcDbObjectImpl;
import com.github.mgramin.sqlboot.tools.jdbc.JdbcDbObjectType;
import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.ToString;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 * Created by MGramin on 13.07.2017.
 */
@ToString
public class Column implements JdbcDbObjectType {

    private static final String COLUMN_NAME_PROPERTY = "COLUMN_NAME";
    private final DataSource dataSource;
    private final CustomResultSet customResultSet;

    public Column(final DataSource dataSource) {
        this(dataSource, new CustomResultSetImpl());
    }

    public Column(final DataSource dataSource, CustomResultSet customResultSet) {
        this.dataSource = dataSource;
        this.customResultSet = customResultSet;
    }

    @Override
    public String name() {
        return "column";
    }

    @Override
    public List<JdbcDbObject> read(List<String> params) throws SQLException {
        ResultSet columns = dataSource.getConnection().getMetaData().
            getColumns(null, params.get(0), params.get(1), params.get(2));
        return customResultSet.toMap(columns).stream()
            .map(v -> new JdbcDbObjectImpl(v.get(COLUMN_NAME_PROPERTY),
                asList(v.get("SCHEMA_NAME"), v.get("TABLE_NAME"),
                    v.get(COLUMN_NAME_PROPERTY)), v))
            .collect(toList());
    }

}
