package com.aluracursos.literalura.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "autores")

public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    String autor;
    Integer fechaNacimiento;
    Integer fechaMuerte;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "autores")
    private Set<Libro> libros= new HashSet<>();

    public Autor() {

    }

    public Autor(DatosLibro librounico) {
        this.autor = librounico.datosautor().get(0).autor();
        this.fechaNacimiento = librounico.datosautor().get(0).fechaNacimiento();
        this.fechaMuerte = librounico.datosautor().get(0).fechaMuerte();;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Integer getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Integer fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getFechaMuerte() {
        return fechaMuerte;
    }

    public void setFechaMuerte(Integer fechaMuerte) {
        this.fechaMuerte = fechaMuerte;
    }

    public Set<Libro> getLibros() {
        return libros;
    }

    public void setLibros(Set<Libro> libros) {
        this.libros = libros;
    }

    public void agregarLibro (Libro  libro){
        libros.add(libro);
        libro.getAutores().add(this);
    }
}
