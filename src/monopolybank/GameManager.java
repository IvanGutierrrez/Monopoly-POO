/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monopolybank;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static monopolybank.Game.loadGame;

/**
 *
 * @author vanre
 */
public class GameManager {

    public void start() throws IOException, ClassNotFoundException {//crea el TextTerminal, elige el idioma en mostrarFicheros y llama a AskForResumeGame
        TextTerminal textT = new TextTerminal();
        textT.mostrarFicheros();
        askForResumeGame(textT);//ahora preguntamos partida antigua o partida nueva
    }

    private void askForResumeGame(TextTerminal textT) throws IOException, ClassNotFoundException {//Eliges si quieres empezar una partida nueva o si quieres reanudar una partida antigua y luego llama a play
        boolean opcionValida;
        int num;
        do {
            textT.show("Partida nueva o partida antigua");
            textT.show("1.Partida nueva");
            textT.show("2.Partida antigua");
            num = textT.read();
            switch (num) {
                case 1 -> {
                    Game game = new Game(textT);//dentro se inicializa los jugadores y los codigos
                    LocalDateTime fechaActual = LocalDateTime.now();
                    game.play(fechaActual.format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss")));//se guarda tambien la hora para que se puedan jugar varias partidas el mismo dia
                    opcionValida = true;
                }
                case 2 -> {
                    String nombreArchivo = mostrarFicheros(textT);
                    if (nombreArchivo != null) {
                        Game game = loadGame(nombreArchivo, textT);//falta cargar los valores desde el fichero
                        textT.show("Resumen:");
                        for (int i = 0; i < game.getPlayers().size(); i++) {//mostrar resumen
                            game.getPlayers().get(i).showResume();
                        }
                        game.play(nombreArchivo);
                        opcionValida = true;
                    } else {
                        opcionValida = false;
                    }
                }
                default -> {
                    textT.show("Opcion no valida");
                    opcionValida = false;
                }
            }
        } while (!opcionValida);
    }

    private String mostrarFicheros(TextTerminal textT) {//muestra los ficheros que estan en la carpeta oldGames y le pide al jugador elegir
        // Crear un objeto File con la ruta de la carpeta
        File directorio = new File("config/oldGames");
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
                    textT.show(num + ". " + valor);
                }
                textT.show("Eliga la partida");
                int respuesta = textT.read();
                if (respuesta < 0 || respuesta > i) {//si el número leido no coincide se sale por si no queria elegir partida antigua
                    return null;
                } else {
                    return valores.get(respuesta - 1);
                }
            } else {
                textT.show("El directorio está vacío");
                return null;
            }
        }
        return null;
    }
}
