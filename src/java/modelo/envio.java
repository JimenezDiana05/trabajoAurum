/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author jimen
 */

import java.time.LocalDateTime;

public class envio {

    private int id_envio;
    private int id_pedido;
    private int id_direccion;
    private LocalDateTime fecha_envio;
    private String empresa;
    private String num_pedido;
    private LocalDateTime fecha_entrega;

    public envio() {
    }

    public envio(int id_envio, int id_pedido, int id_direccion,
            LocalDateTime fecha_envio, String empresa,
            String num_pedido, LocalDateTime fecha_entrega) {
        this.id_envio = id_envio;
        this.id_pedido = id_pedido;
        this.id_direccion = id_direccion;
        this.fecha_envio = fecha_envio;
        this.empresa = empresa;
        this.num_pedido = num_pedido;
        this.fecha_entrega = fecha_entrega;
    }

    public int getId_envio() {
        return id_envio;
    }

    public void setId_envio(int id_envio) {
        this.id_envio = id_envio;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public int getId_direccion() {
        return id_direccion;
    }

    public void setId_direccion(int id_direccion) {
        this.id_direccion = id_direccion;
    }

    public LocalDateTime getFecha_envio() {
        return fecha_envio;
    }

    public void setFecha_envio(LocalDateTime fecha_envio) {
        this.fecha_envio = fecha_envio;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getNum_pedido() {
        return num_pedido;
    }

    public void setNum_pedido(String num_pedido) {
        this.num_pedido = num_pedido;
    }

    public LocalDateTime getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(LocalDateTime fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }
}
