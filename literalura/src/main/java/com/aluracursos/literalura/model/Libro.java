package com.aluracursos.literalura.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "libros")

public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    private String idiomas;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable(
            name = "libro_autor",joinColumns = @JoinColumn(name = "libro_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id", referencedColumnName = "id")
    )
    private Set<Autor> autores = new HashSet<>();

    public Libro() {

    }

    public Set<Autor> getAutores() {
        return autores;
    }

    public void setAutores(Set<Autor> autores) {
        this.autores = autores;
    }

    public Libro(DatosLibro librounico) {
        this.titulo = librounico.titulo();
        this.idiomas = librounico.idiomas().get(0).toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(String idiomas) {
        this.idiomas = idiomas;
    }



    public void agregarAutor(Autor autor){
        this.autores.add(autor);
        autor.getLibros().add(this);
    }
}