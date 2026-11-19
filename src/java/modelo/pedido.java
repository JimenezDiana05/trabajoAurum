/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author jimen
 */

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class pedido {

    private int id_pedido;
    private int id_cliente;
    private int id_direccion;
    private LocalDateTime fecha_pedido;
    private BigDecimal total;

    public pedido() {
    }

    public pedido(int id_pedido, int id_cliente, int id_direccion,
            LocalDateTime fecha_pedido, BigDecimal total) {
        this.id_pedido = id_pedido;
        this.id_cliente = id_cliente;
        this.id_direccion = id_direccion;
        this.fecha_pedido = fecha_pedido;
        this.total = total;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getId_direccion() {
        return id_direccion;
    }

    public void setId_direccion(int id_direccion) {
        this.id_direccion = id_direccion;
    }

    public LocalDateTime getFecha_pedido() {
        return fecha_pedido;
    }

    public void setFecha_pedido(LocalDateTime fecha_pedido) {
        this.fecha_pedido = fecha_pedido;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}

