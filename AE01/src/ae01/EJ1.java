package ae01;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Scanner;

/**
 * La clase EJ1 proporciona métodos para listar los archivos y subdirectorios
 * de un directorio dado, mostrando detalles como el tamaño del archivo y la fecha de la última modificación.
 * 
 * El formato de salida es similar al de una estructura de árbol, donde los subdirectorios y archivos
 * se muestran con un formato jerárquico.
 * 
 * @author Álvaro Gálvez Barba 
 * @version 1.0
 * @since 2024-10-13
 */
public class EJ1 {

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
     * Lista de manera recursiva los archivos y subdirectorios del directorio especificado,
     * mostrando detalles como el nombre, tamaño y fecha de última modificación para los archivos.
     * 
     * @param dir El directorio a listar.
     * @param prefijo El prefijo utilizado para mostrar la estructura jerárquica de los archivos y carpetas.
     */
    private static void listarArchivos(File dir, String prefijo) {
        if (dir.isDirectory()) {
            File[] archivos = dir.listFiles();
            if (archivos != null && archivos.length > 0) {
                for (File archivo : archivos) {
                    if (archivo.isDirectory()) {
                        System.out.println(prefijo + "|-- " + archivo.getName());
                        listarArchivos(archivo, prefijo + "|   ");
                    } else {
                        String nombre = archivo.getName();
                        long tamano = archivo.length();
                        String tamanoFormateado = formatoTamano(tamano);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String ultimaModificacion = sdf.format(archivo.lastModified());

                        System.out.println(prefijo + "|-- " + nombre + " (" + tamanoFormateado + " - " + ultimaModificacion + ")");
                    }
                }
            }
        }
    }

    /**
     * Método principal que ejecuta la aplicación. 
     * Solicita una ruta de directorio y muestra su contenido en formato jerárquico,
     * incluyendo archivos y subdirectorios con información adicional.
     * 
     * @param args Argumentos pasados desde la línea de comandos (no se utilizan en este programa).
     */
    public static void main(String[] args) {
    	Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce la ruta del directorio: ");
        String rutaDirectorio = scanner.nextLine();
        
        File directorioBase = new File(rutaDirectorio);

        if (directorioBase.exists() && directorioBase.isDirectory()) {
            System.out.println(directorioBase.getName());
            listarArchivos(directorioBase, "");
        } else {
            System.out.println("El directorio no existe o no es válido.");
        }
    }
}
