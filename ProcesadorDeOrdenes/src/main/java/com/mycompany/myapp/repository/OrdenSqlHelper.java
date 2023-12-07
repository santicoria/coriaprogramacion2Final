package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class OrdenSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("cliente", table, columnPrefix + "_cliente"));
        columns.add(Column.aliased("accion_id", table, columnPrefix + "_accion_id"));
        columns.add(Column.aliased("accion", table, columnPrefix + "_accion"));
        columns.add(Column.aliased("operacion", table, columnPrefix + "_operacion"));
        columns.add(Column.aliased("precio", table, columnPrefix + "_precio"));
        columns.add(Column.aliased("cantidad", table, columnPrefix + "_cantidad"));
        columns.add(Column.aliased("fecha_operacion", table, columnPrefix + "_fecha_operacion"));
        columns.add(Column.aliased("modo", table, columnPrefix + "_modo"));

        return columns;
    }
}
