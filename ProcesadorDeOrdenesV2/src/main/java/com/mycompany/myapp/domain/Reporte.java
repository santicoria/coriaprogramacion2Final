package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Reporte.
 */
@Table("reporte")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Reporte implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("cliente")
    private Integer cliente;

    @NotNull(message = "must not be null")
    @Column("accion_id")
    private Integer accionId;

    @NotNull(message = "must not be null")
    @Column("accion")
    private String accion;

    @NotNull(message = "must not be null")
    @Column("operacion")
    private String operacion;

    @NotNull(message = "must not be null")
    @Column("cantidad")
    private Integer cantidad;

    @NotNull(message = "must not be null")
    @Column("precio")
    private Float precio;

    @NotNull(message = "must not be null")
    @Column("fecha_operacion")
    private LocalDateTime fechaOperacion;

    @NotNull(message = "must not be null")
    @Column("modo")
    private String modo;

    @NotNull(message = "must not be null")
    @Column("operacion_exitosa")
    private Boolean operacionExitosa;

    @NotNull(message = "must not be null")
    @Column("operacion_observaciones")
    private String operacionObservaciones;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Reporte id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCliente() {
        return this.cliente;
    }

    public Reporte cliente(Integer cliente) {
        this.setCliente(cliente);
        return this;
    }

    public void setCliente(Integer cliente) {
        this.cliente = cliente;
    }


    public Integer getAccionId() {
        return this.accionId;
    }

    public Reporte accionId(Integer accionId) {
        this.setAccionId(accionId);
        return this;
    }

    public void setAccionId(Integer accionId) {
        this.accionId = accionId;
    }

    public String getAccion() {
        return this.accion;
    }

    public Reporte accion(String accion) {
        this.setAccion(accion);
        return this;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getOperacion() {
        return this.operacion;
    }

    public Reporte operacion(String operacion) {
        this.setOperacion(operacion);
        return this;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public Integer getCantidad() {
        return this.cantidad;
    }

    public Reporte cantidad(Integer cantidad) {
        this.setCantidad(cantidad);
        return this;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Float getPrecio() {
        return this.precio;
    }

    public Reporte precio(Float precio) {
        this.setPrecio(precio);
        return this;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public LocalDateTime getFechaOperacion() {
        return this.fechaOperacion;
    }

    public Reporte fechaOperacion(LocalDateTime fechaOperacion) {
        this.setFechaOperacion(fechaOperacion);
        return this;
    }

    public void setFechaOperacion(LocalDateTime fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public String getModo() {
        return this.modo;
    }

    public Reporte modo(String modo) {
        this.setModo(modo);
        return this;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public Boolean getOperacionExitosa() {
        return this.operacionExitosa;
    }

    public Reporte operacionExitosa(Boolean operacionExitosa) {
        this.setOperacionExitosa(operacionExitosa);
        return this;
    }

    public void setOperacionExitosa(Boolean operacionExitosa) {
        this.operacionExitosa = operacionExitosa;
    }

    public String getOperacionObservaciones() {
        return this.operacionObservaciones;
    }

    public Reporte operacionObservaciones(String operacionObservaciones) {
        this.setOperacionObservaciones(operacionObservaciones);
        return this;
    }

    public void setOperacionObservaciones(String operacionObservaciones) {
        this.operacionObservaciones = operacionObservaciones;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reporte)) {
            return false;
        }
        return id != null && id.equals(((Reporte) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Reporte{" +
            "id=" + getId() +
            ", cliente=" + getCliente() +
            ", accionId=" + getAccionId() +
            ", accion='" + getAccion() + "'" +
            ", operacion='" + getOperacion() + "'" +
            ", cantidad=" + getCantidad() +
            ", precio=" + getPrecio() +
            ", fechaOperacion='" + getFechaOperacion() + "'" +
            ", modo='" + getModo() + "'" +
            ", operacionExitosa='" + getOperacionExitosa() + "'" +
            ", operacionObservaciones='" + getOperacionObservaciones() + "'" +
            "}";
    }
}
