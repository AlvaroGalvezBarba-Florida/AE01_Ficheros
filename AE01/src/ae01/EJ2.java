package ae01;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class EJ2 {

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

    private static String normalizar(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    private static String leerTextoPDF(File archivo) throws IOException {
    	PDDocument documento = Loader.loadPDF(archivo);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String texto = pdfStripper.getText(documento);
        documento.close();
        return texto;
        }

    private static int contarCoincidencias(File archivo, String cadena, boolean ignorarMayusculas, boolean ignorarAcentos) throws IOException {
        String contenido = "";
        if (archivo.getName().endsWith(".txt") || archivo.getName().endsWith(".java")) {
            contenido = new String(Files.readAllBytes(archivo.toPath()));
        } else if (archivo.getName().endsWith(".pdf")) {
            contenido = leerTextoPDF(archivo);
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

    public static void main(String[] args) throws IOException {
        String rutaDirectorio = "E:\\Acceso a datos\\AE01";
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



