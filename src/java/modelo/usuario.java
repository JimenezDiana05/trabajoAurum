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

public class usuario {

    private int id_usuario;
    private LocalDateTime fecha_creacion;
    private String contrasena;
    private int id_rol;
    private int id_cliente;
    private String estado;

    public usuario() {
    }

    public usuario(int id_usuario, LocalDateTime fecha_creacion,
            String contrasena, int id_rol, int id_cliente, String estado) {
        this.id_usuario = id_usuario;
        this.fecha_creacion = fecha_creacion;
        this.contrasena = contrasena;
        this.id_rol = id_rol;
        this.id_cliente = id_cliente;
        this.estado = estado;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public LocalDateTime getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(LocalDateTime fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getId_rol() {
        return id_rol;
    }

    public void setId_rol(int id_rol) {
        this.id_rol = id_rol;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
