package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor,Long> {
    Optional<Autor> findAuthorByAutor(String autor);

    @Query ("SELECT DISTINCT a FROM Autor a LEFT JOIN FETCH a.libros")
    List<Autor> findAllAutorWithLibros();

    @Query("SELECT a FROM Autor a WHERE a.fechaNacimiento <= :fecha AND a.fechaMuerte > :fecha ")
    List<Autor> findAutoresWithAge(@Param("fecha") int fecha);

}
