/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monopolybank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author vanre
 */
public class TextTerminal extends Terminal {

    public TextTerminal() {//crea un objeto de la clase TextTerminal y el translatorManager donde se leeran los ficheros de los idiomas
        TranslatorManager traductor = null;
        try {
            traductor = new TranslatorManager();
        } catch (IOException ex) {
            ex.printStackTrace();  // Imprime la traza de la excepción para depuración
        }
        if (traductor != null) {
            this.setTranslatorManager(traductor);
        }
    }

    @Override
    public int read() {//se lee de pantalla un numero entero
        Scanner scanner = new Scanner(System.in);
        String valor = scanner.nextLine();
        int n = -1;//n debe estar declarada e inicializada fuera del bloque try
        try {
            n = Integer.parseInt(valor);
        } catch (Exception e) {
            this.show("Escriba un numero");
        }
        return n;
    }

    @Override
    public void show(String s) {//se escribe una frase simple
        TranslatorManager t = this.getTranslatorManager();//traes el translatorManager
        Translator trad = t.getTranslator();//traes el traductor activo
        System.out.println(trad.translate(s));//traduces la frase
    }

    @Override
    public void showM(String p1, String prop, String p2, int n) {//se escribe por pantalla una frase más compleja que necesita solo traducir algunas partes de la frase
        TranslatorManager t = this.getTranslatorManager();//traes el translatorManager
        Translator trad = t.getTranslator();//traes el traductor activo
        System.out.println(trad.translate(p1) + prop + trad.translate(p2) + " " + String.valueOf(n));//traduces la frase
    }

    public void mostrarFicheros() {//muestras los ficheros para eligir el idioma al principio de la ejecución del programa, este codigo valdria en caso de que se añadiera otro idioma distinto a Español, Inglés, Catalán o Euskera
        File directorio = new File("config/languages");
        if (directorio.exists() && directorio.isDirectory()) {
            // Obtener la lista de archivos en el directorio
            File[] archivos = directorio.listFiles();

            // Mostrar los nombres de los archivos por pantalla y guardarlos en un array para luego elegirlo
            if (archivos != null && archivos.length > 0) {
                int i = 0;
                List<String> valores = new ArrayList();
                for (File archivo : archivos) {
                    i++;//lo pongo antes para empezar en 1
                    String num = String.valueOf(i);
                    String valor = archivo.getName();
                    valores.add(valor);
                    this.show(num + ". " + valor.substring(0, valor.length() - 4));//sin el .txt
                }
                int respuesta;
                do {
                    this.show("Eliga el idioma");
                    respuesta = this.read();
                } while (respuesta <= 0 || respuesta > valores.size());
                this.getTranslatorManager().changeIdiom(respuesta - 1);
            }
        }
    }

}
