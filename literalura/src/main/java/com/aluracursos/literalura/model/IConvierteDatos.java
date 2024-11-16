package com.aluracursos.literalura.model;

public interface IConvierteDatos {

    <T> T obtenerDatos(String json,Class<T> clase);

}
