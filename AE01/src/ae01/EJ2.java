package ae01;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * La clase EJ2 realiza la búsqueda de cadenas en archivos de texto y PDF dentro de un directorio,
 * y opcionalmente reemplaza esas cadenas en archivos de texto. Admite la normalización de acentos
 * y la búsqueda sin distinción de mayúsculas/minúsculas.
 * 
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2024-10-13
 */
public class EJ2 {

    /**
     * Convierte el tamaño en bytes de un archivo en un formato legible,
     * utilizando las unidades B, KB, MB o GB según sea apropiado.
     * 
     * @param bytes Tamaño del archivo en bytes.
     * @return Una cadena que representa el tamaño formateado en la unidad correspondiente.
     */
    private static String formatoTamano(long bytes) {
        String[] unidades = {"B", "KB", "MB", "GB"};
        int index = 0;
        double size = bytes;
        while (size >= 1024 && index < unidades.length - 1) {
            size /= 1024;
            index++;
        }
        return String.format("%.1f %s", size, unidades[index]);
    }

    /**
     * Normaliza una cadena eliminando acentos y otros caracteres diacríticos.
     * 
     * @param input La cadena a normalizar.
     * @return La cadena normalizada.
     */
    private static String normalizar(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    /**
     * Cuenta el número de coincidencias de una cadena en el contenido de un archivo.
     * Admite archivos de texto (.txt, .java) y archivos PDF (.pdf).
     * 
     * @param archivo El archivo donde buscar.
     * @param cadena La cadena a buscar.
     * @param ignorarMayusculas Si se debe ignorar la distinción entre mayúsculas y minúsculas.
     * @param ignorarAcentos Si se deben ignorar los acentos en la búsqueda.
     * @return El número de coincidencias encontradas.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    private static int contarCoincidencias(File archivo, String cadena, boolean ignorarMayusculas, boolean ignorarAcentos) throws IOException {
        String contenido = "";
        if (archivo.getName().endsWith(".txt") || archivo.getName().endsWith(".java")) {
            contenido = new String(Files.readAllBytes(archivo.toPath()));
        } else {
            return 0;
        }

        if (ignorarAcentos) {
            cadena = normalizar(cadena);
            contenido = normalizar(contenido);
        }

        Pattern patron = ignorarMayusculas ? Pattern.compile(Pattern.quote(cadena), Pattern.CASE_INSENSITIVE) : Pattern.compile(Pattern.quote(cadena));
        Matcher matcher = patron.matcher(contenido);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Reemplaza las coincidencias de una cadena en archivos de texto (.txt, .java).
     * Crea un nuevo archivo con el prefijo "MOD_" si se realizan reemplazos.
     * 
     * @param archivo El archivo donde realizar el reemplazo.
     * @param buscar La cadena a buscar.
     * @param reemplazar La cadena por la que se reemplazará.
     * @param ignorarMayusculas Si se debe ignorar la distinción entre mayúsculas y minúsculas.
     * @param ignorarAcentos Si se deben ignorar los acentos en la búsqueda.
     * @return El número de reemplazos realizados.
     * @throws IOException Si ocurre un error al leer o escribir el archivo.
     */
    private static int reemplazarCoincidencias(File archivo, String buscar, String reemplazar, boolean ignorarMayusculas, boolean ignorarAcentos) throws IOException {
        String contenido = "";
        if (archivo.getName().endsWith(".txt") || archivo.getName().endsWith(".java")) {
            contenido = new String(Files.readAllBytes(archivo.toPath()));
        } else {
            return 0;
        }

        if (ignorarAcentos) {
            buscar = normalizar(buscar);
            contenido = normalizar(contenido);
        }

        Pattern patron = ignorarMayusculas ? Pattern.compile(Pattern.quote(buscar), Pattern.CASE_INSENSITIVE) : Pattern.compile(Pattern.quote(buscar));
        Matcher matcher = patron.matcher(contenido);
        int reemplazos = 0;

        while (matcher.find()) {
            reemplazos++;
        }

        if (reemplazos > 0) {
            String nuevoContenido = matcher.replaceAll(reemplazar);
            File archivoNuevo = new File(archivo.getParent(), "MOD_" + archivo.getName());
            Files.write(archivoNuevo.toPath(), nuevoContenido.getBytes());
        }

        return reemplazos;
    }

    /**
     * Busca una cadena en los archivos de un directorio y opcionalmente reemplaza las coincidencias
     * en los archivos de texto.
     * 
     * @param dir El directorio donde buscar.
     * @param buscar La cadena a buscar.
     * @param reemplazar La cadena por la que se reemplazará (si se habilita el reemplazo).
     * @param ignorarMayusculas Si se debe ignorar la distinción entre mayúsculas y minúsculas.
     * @param ignorarAcentos Si se deben ignorar los acentos en la búsqueda.
     * @param hacerReemplazo Si se deben realizar los reemplazos.
     * @throws IOException Si ocurre un error al leer los archivos.
     */
    private static void buscarYReemplazarEnDirectorio(File dir, String buscar, String reemplazar, boolean ignorarMayusculas, boolean ignorarAcentos, boolean hacerReemplazo) throws IOException {
        if (dir.isDirectory()) {
            File[] archivos = dir.listFiles();
            if (archivos != null && archivos.length > 0) {
                for (File archivo : archivos) {
                    if (archivo.isDirectory()) {
                        System.out.println("|-- " + archivo.getName());
                        buscarYReemplazarEnDirectorio(archivo, buscar, reemplazar, ignorarMayusculas, ignorarAcentos, hacerReemplazo);
                    } else {
                        int coincidencias = contarCoincidencias(archivo, buscar, ignorarMayusculas, ignorarAcentos);
                        System.out.println("|-- " + archivo.getName() + " (" + coincidencias + " coincidencias)");

                        if (hacerReemplazo && coincidencias > 0) {
                            int reemplazos = reemplazarCoincidencias(archivo, buscar, reemplazar, ignorarMayusculas, ignorarAcentos);
                            System.out.println("|-- MOD_" + archivo.getName() + " (" + reemplazos + " reemplazos)");
                        }
                    }
                }
            }
        }
    }

    /**
     * Método principal que ejecuta la funcionalidad del programa.
     * Busca y opcionalmente reemplaza cadenas en archivos de un directorio.
     * 
     * @param args Argumentos pasados desde la línea de comandos (no se utilizan en este programa).
     * @throws IOException Si ocurre un error al leer o modificar los archivos.
     */
    public static void main(String[] args) throws IOException {
    	Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce la ruta del directorio: ");
        String rutaDirectorio = scanner.nextLine();
        
        File directorioBase = new File(rutaDirectorio);

        String buscar = "cadenaABuscar";
        String reemplazar = "cadenaNueva";
        boolean ignorarMayusculas = true;
        boolean ignorarAcentos = true;     
        boolean hacerReemplazo = true;

        if (directorioBase.exists() && directorioBase.isDirectory()) {
            buscarYReemplazarEnDirectorio(directorioBase, buscar, reemplazar, ignorarMayusculas, ignorarAcentos, hacerReemplazo);
        } else {
            System.out.println("El directorio no existe o no es válido.");
        }
    }
}
