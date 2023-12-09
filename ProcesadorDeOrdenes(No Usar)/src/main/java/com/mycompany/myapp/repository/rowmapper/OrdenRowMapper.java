package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Orden;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Orden}, with proper type conversions.
 */
@Service
public class OrdenRowMapper implements BiFunction<Row, String, Orden> {

    private final ColumnConverter converter;

    public OrdenRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Orden} stored in the database.
     */
    @Override
    public Orden apply(Row row, String prefix) {
        Orden entity = new Orden();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCliente(converter.fromRow(row, prefix + "_cliente", Integer.class));
        entity.setAccionId(converter.fromRow(row, prefix + "_accion_id", Integer.class));
        entity.setAccion(converter.fromRow(row, prefix + "_accion", String.class));
        entity.setOperacion(converter.fromRow(row, prefix + "_operacion", String.class));
        entity.setPrecio(converter.fromRow(row, prefix + "_precio", Float.class));
        entity.setCantidad(converter.fromRow(row, prefix + "_cantidad", Integer.class));
        entity.setFechaOperacion(converter.fromRow(row, prefix + "_fecha_operacion", LocalDateTime.class));
        entity.setModo(converter.fromRow(row, prefix + "_modo", String.class));
        return entity;
    }
}
