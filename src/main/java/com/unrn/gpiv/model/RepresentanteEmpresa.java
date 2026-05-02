package com.unrn.gpiv.model;

import com.unrn.gpiv.common.Rol; // Importamos tu Enum
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("REPRESENTANTE")
public class RepresentanteEmpresa extends Usuario {

    private String nombreCompleto;
    private String dni;
    private String cuitPersonal;
    private String telefono;
    private String emailContacto;

    @OneToOne(mappedBy = "representante")
    private Empresa empresa;

    // EL CONSTRUCTOR CORREGIDO:
    public RepresentanteEmpresa() {
        // Usamos "EMPRESA" porque así se llama en tu Enum Rol.java
        this.setRol(Rol.EMPRESA);
    }

    // --- GETTERS Y SETTERS ---
    // (Los que ya tenías)
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombre) { this.nombreCompleto = nombre; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getCuitPersonal() { return cuitPersonal; }
    public void setCuitPersonal(String cuit) { this.cuitPersonal = cuit; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String tel) { this.telefono = tel; }
    public String getEmailContacto() { return emailContacto; }
    public void setEmailContacto(String email) { this.emailContacto = email; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}