package com.unrn.gpiv.inventory.model;

import com.unrn.gpiv.model.Empresa;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "items_inventario")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ejemplo: "Llave Lote 14", "Motosierra Stihl"

    private String descripcion;

    private String numeroSerie; // Clave para no perder cosas

    private boolean disponible = true;

        // Relación: Un item puede estar prestado a una empresa (HU 18)
       // @ManyToOne
        //@JoinColumn(name = "empresa_id")
        //private Empresa prestadoA;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Recurso> existencias;

    public Item() {
    }

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

   // public Empresa getPrestadoA() { return prestadoA; }
    //public void setPrestadoA(Empresa prestadoA) { this.prestadoA = prestadoA; }
}