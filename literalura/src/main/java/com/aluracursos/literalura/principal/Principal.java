package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumoApi;
import com.aluracursos.literalura.service.ConvierteDatos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    private List<Libro> libros;
    private List<Autor> autores;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
       boolean control=true;


        while (control) {
            var menu = """
                    
                    
                    ****************************************
                    Bienvenido a LITERALURA                    
                    Estas son las opciones disponibles para Ud.
                                        
                    1- Buscar Libros
                    2- Mostrar Libros Guardados
                    3- Mostrar Autores Guardados
                    4- Autores vivos en determinado año
                    5- Lista de libros guardados por idioma
                    0- Salir
                    
                    Por favor digite la opcion deseada
                    """;
            System.out.println(menu);
            int opcion=obtenerOpcion();

            switch (opcion) {
                case 1:
                    buscarLibrosWeb();
                    break;
                case 2:
                    mostrarLibrosGuardados();
                    break;
                case 3:
                    mostrarAutoresGuardados();
                    break;
                case 4:
                    mostrarAutoresVivosenFecha();
                    break;
                case 5:
                    mostrarLibrosPorIdioma();
                    break;
                case 0:
                    control=false;
                    System.out.println("Cerrando la aplicacion. Gracias por usar LITERALURA");
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }
    }

    private void mostrarLibrosPorIdioma() {

        List<String> idiomasDisponibles = new ArrayList<String>(4);
        idiomasDisponibles.add("de");
        idiomasDisponibles.add("es");
        idiomasDisponibles.add("fr");
        idiomasDisponibles.add("en");

        var idiomaRequerido=" ";

        var solicitarIdioma = """ 
                Ingresa por favor el codigo del idioma que deseas filtrar.
                
                Codigo      Idioma
                ------      ------
                de          Aleman
                es          Español
                fr          Frances
                en          Ingles                
                """;
        boolean control=false;
        do {
            System.out.println(solicitarIdioma);
            idiomaRequerido = teclado.nextLine();

            control = idiomasDisponibles.contains(idiomaRequerido);}
        while (!control);

        System.out.println(String.format("%-40s %-40s %-30s","Titulo","Autor","Idioma"));
        var idiomas=idiomaRequerido;
        List<Libro> librosPorIdiomas=libroRepository.findByIdiomas(idiomas);
        librosPorIdiomas.stream()
                .forEach(libro -> System.out.println(mostrarLibro(libro)));
    }

    private void mostrarAutoresVivosenFecha() {
        var fecha=0;
        String entrada;
        do{
            System.out.println("Ingresa el año en el que quieres buscar autores vivos (Por favor usa formato YYYY)");
            entrada=teclado.nextLine();}

        while (!entrada.matches("\\d{4}"));
        fecha=Integer.parseInt(entrada);
        System.out.println(String.format("%-35s %-20s %-20s","Autor","Año Nacimiento","Año Muerte"));
        List<Autor> autoresporFecha=autorRepository.findAutoresWithAge(fecha);
        autoresporFecha.stream()
                .forEach(autor -> System.out.println(mostrarAutor(autor)));
    }

    private void mostrarAutoresGuardados() {
        System.out.println(String.format("%-35s %-20s %-20s","Autor","Año Nacimiento","Año Muerte"));
        autores=autorRepository.findAllAutorWithLibros();
        autores.stream()
                .forEach(autor -> System.out.println(mostrarAutor(autor)));
    }

    private String mostrarAutor(Autor autor){
        return String.format("%-35s %-20s %-20s",autor.getAutor(),autor.getFechaNacimiento(),autor.getFechaMuerte());
    }

    private void mostrarLibrosGuardados() {
        System.out.println(String.format("%-40s %-40s %-30s","Titulo","Autor","Idioma"));
        libros =libroRepository.findAll();
        libros.stream()
                .forEach(libro -> System.out.println(mostrarLibro(libro)));
    }

    private String mostrarLibro(Libro libro){
        return String.format("%-40s %-40s %-40s",
                libro.getTitulo(),libro.getAutores().stream().map(Autor::getAutor).collect(Collectors.joining(",")),libro.getIdiomas());
    }

    private int obtenerOpcion(){
        int opcion = -1;
        boolean opcionValida = false;
        while (!opcionValida){
            try {
                opcion=Integer.parseInt(teclado.nextLine().trim());
                if (opcion>=0 && opcion<=5){
                    opcionValida=true;
                } else {
                    System.out.println(" Por favor elije una opcion entre 0 y 5");
                }
            }   catch (NumberFormatException e) {
                System.out.println("Opcion no valida.Por favor ingrese un numero");
            }
        }
        return opcion;
    }

    private DatosLibro obtenerDatosLibro() {
        System.out.println("Por favor escribe el nombre del libro que deseas buscar");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos("https://gutendex.com/books/?search=" + nombreLibro.replace(" ", "%20"));
        var datos = conversor.obtenerDatos(json, DatosGeneral.class);
        //System.out.println(datos);

        DatosLibro librounico = datos.resultados().stream()
                .filter(l -> l.titulo().equalsIgnoreCase(nombreLibro))
                .findFirst()
                .orElse(null);
        return librounico;
    }

    private void buscarLibrosWeb() {
        DatosLibro librounico = obtenerDatosLibro();

        if (librounico != null) {
            Libro libro = new Libro(librounico);
            for (DatosAutor datosAutor : librounico.datosautor()) {
                Autor autor;
                Optional<Autor> autorExistente = autorRepository.findAuthorByAutor(datosAutor.autor());
                if (autorExistente.isPresent()) {
                    autor = autorExistente.get();

                } else {
                    autor = new Autor(librounico);
                    autorRepository.save(autor);
                }
                libro.agregarAutor(autor);
            }
            libroRepository.save(libro);
            System.out.println("Libro encontrado y guardado \n" + mostrarLibro(libro));
        } else {
            System.out.println("libro no encontrado");
        }
    }
}



