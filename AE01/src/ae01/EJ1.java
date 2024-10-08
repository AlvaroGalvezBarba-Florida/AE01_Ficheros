package ae01;
import java.io.File;
import java.text.SimpleDateFormat;

public class EJ1 {

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

    public static void main(String[] args) {
        String rutaDirectorio = "E:\\Acceso a datos\\AE01";

        File directorioBase = new File(rutaDirectorio);

        if (directorioBase.exists() && directorioBase.isDirectory()) {
            System.out.println(directorioBase.getName());
            listarArchivos(directorioBase, "");
        } else {
            System.out.println("El directorio no existe o no es válido.");
        }
    }
}
