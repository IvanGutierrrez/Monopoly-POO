/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monopolybank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vanre
 */
public class TranslatorManager {

    private int currentIdiom;
    private List<Translator> idioms;

    public void setIdioms(List<Translator> idioms) {
        this.idioms = idioms;
    }

    public void setCurrentIdiom(int i) {
        this.currentIdiom = i;
    }

    public TranslatorManager() throws IOException {//Crea un objeto de la claseTranslatorManager y crea y añade a la lista los translator
        List<Translator> idioms = new ArrayList();//Creo el arrayList y añado los 4 traductores
        File directorio = new File("config/languages");//cogo la direccion de los ficheros
        if (directorio.exists() && directorio.isDirectory()) {
            // Obtener la lista de archivos en el directorio
            File[] archivos = directorio.listFiles();

            // Mostrar los nombres de los archivos por pantalla y guardarlos en un array para luego elegirlo
            int i = 0;
            if (archivos != null && archivos.length > 0) {
                int j = 0;
                for (File archivo : archivos) {
                    try {
                        if ("Español.txt".equals(archivo.getName())) {
                            i = j;
                        }
                        Translator t0 = new Translator("config/languages/" + archivo.getName());
                        idioms.add(t0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    j++;
                }
                this.setIdioms(idioms);
                this.setCurrentIdiom(i); //por defecto el idioma es el español;
            }
        }
    }

    public Translator getTranslator() {
        return this.idioms.get(this.currentIdiom);//devuelver el valor de la lista de array que esta activado;
    }

    public void changeIdiom(int newIdiom) {//cambia el idioma al principio de la ejecución del programa
        this.setCurrentIdiom(newIdiom);//cambiamos la variable currentIdiom
    }
}
