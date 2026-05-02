package com.unrn.gpiv.model;

import jakarta.persistence.*;

@Entity
@Table(name = "representantes_empresa")
public class RepresentanteEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String dni;
    private String cuitPersonal; // Su CUIL/CUIT personal (20-xxx, etc.)
    private String telefono;
    private String emailContacto;

    // Relación inversa para saber a qué empresa representa (opcional)
    @OneToOne(mappedBy = "representante")
    private Empresa empresa;

    // Constructores
    public RepresentanteEmpresa() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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