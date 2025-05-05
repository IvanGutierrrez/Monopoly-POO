/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monopolybank;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vanre
 */
public class Translator {

    private final Map<String, String> dictionary;

    public Translator(String nombreArchivo) throws IOException {//creas un objeto de la clase Translator al leer el fichero correspondiente
        Reader in = new FileReader(nombreArchivo);
        BufferedReader buf = new BufferedReader(in);
        String linea;
        dictionary = new HashMap<>();
        while ((linea = buf.readLine()) != null) {
            String[] result = linea.split(",");
            if (result.length == 2) {
                dictionary.put(result[0], result[1]);
            }
        }
    }

    public String translate(String frase) {//traduce la frase si existe en el mapa translator, si no devuelve la frase sin traducir
        if (dictionary.containsKey(frase)) {
            return dictionary.get(frase);
        } else {
            return (frase);
        }
    }
}
