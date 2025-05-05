/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monopolybank;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vanre
 */
public class Game implements Serializable {

    private static final long serialVersionUID = 123456789L;
    private List<Player> players;
    private Map<Integer, MonopolyCode> monopolyCodesquareMap;
    private transient Terminal terminal;

    public List<Player> getPlayers() {
        return players;
    }

    public Map<Integer, MonopolyCode> getMonopolyCodesquareMap() {
        return monopolyCodesquareMap;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setPlayers(int num, Terminal textT) {
        players = new ArrayList();
        for (int i = 0; i < num; i++) {
            Player player = new Player(i, textT);
            players.add(player);
        }
    }

    public void setMonopolyCodesquareMap() throws IOException {
        monopolyCodesquareMap = new HashMap();
        this.loadMonopolyCodes();
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public Game(TextTerminal textT) throws IOException {//Inicializa un objeto de la clase Game, y elige cuantos jugadores son 
        this.setTerminal(textT);
        this.setMonopolyCodesquareMap();
        int num;
        boolean opcionValida;
        do {
            this.getTerminal().show("Cuantos jugadores sois");
            num = textT.read();
            if (num <= 4 && num >= 2) {
                opcionValida = true;
                if (num == 4) {
                    this.getTerminal().show("Los colores son rojo verde azul y negro");
                } else if (num == 3) {
                    this.getTerminal().show("Los colores son rojo verde y azul");
                } else {
                    this.getTerminal().show("Los colores son rojo y verde");
                }
            } else {
                this.getTerminal().show("Solo podeis ser de 2 a 4 jugadores");
                opcionValida = false;
            }
        } while (!opcionValida);
        this.setPlayers(num, textT);
    }

    private void loadMonopolyCodes() throws IOException {//carga los monopolyCodes y les añade la terminal
        Reader in = new FileReader("config/MonopolyCode.txt");
        BufferedReader buf = new BufferedReader(in);
        String linea;
        while ((linea = buf.readLine()) != null) {
            if (linea.contains("STREET")) {
                Street street = new Street(linea, this.getTerminal());
                this.monopolyCodesquareMap.put(street.getId(), street);
            } else if (linea.contains("SERVICE")) {
                Service service = new Service(linea, this.getTerminal());
                this.monopolyCodesquareMap.put(service.getId(), service);
            } else if (linea.contains("TRANSPORT")) {
                Transport transport = new Transport(linea, this.getTerminal());
                this.monopolyCodesquareMap.put(transport.getId(), transport);
            } else if (linea.contains("PAYMENT_CHARGE_CARD")) {
                PaymentCharge paymentCharge = new PaymentCharge(linea, this.getTerminal());
                this.monopolyCodesquareMap.put(paymentCharge.getId(), paymentCharge);
            } else if (linea.contains("REPAIRS_CARD")) {
                RepairsCard repairsCard = new RepairsCard(linea, this.getTerminal());
                this.monopolyCodesquareMap.put(repairsCard.getId(), repairsCard);
            }
        }
    }

    public static Game loadGame(String nombreFichero, TextTerminal textT) throws IOException, ClassNotFoundException {//deserializa la partida que el jugador ha elegido y le añade el terminal a cada jugador y carta, y al game
        Game game = deserializarGame(nombreFichero);//deserializamos 
        game.loadTerminal(textT);//añadimos el terminal a todo
        return game;
    }

    private void loadTerminal(Terminal textT) {//carga la terminal en el game y a cada jugador y carta del objeto de la calse game que se ha deserializado
        List<Player> jugadores = this.getPlayers();
        Map<Integer, MonopolyCode> monopolyCodes = this.getMonopolyCodesquareMap();
        this.setTerminal(textT);
        for (int i = 0; i < jugadores.size(); i++) {
            jugadores.get(i).setTerminal(textT);
        }
        for (Map.Entry<Integer, MonopolyCode> entry : monopolyCodes.entrySet()) {
            MonopolyCode carta = entry.getValue();
            carta.setTerminal(textT);
        }
    }

    private int Jugadores() {//te devuelve el número de jugadores que quedan sin estar en bancarrota
        int n = 0;
        for (int i = 0; i < this.getPlayers().size(); i++) {
            Player j = this.getPlayers().get(i);
            if (!j.getbankrupt()) {
                n++;
            }
        }
        return n;
    }

    public void play(String rutaArchivo) {//es el procedimiento principal del programa, coges el código de la carta y el jugador y haces la operación correspondiente
        Terminal textT = this.getTerminal();
        while (this.Jugadores() > 1) {
            boolean opcionValida;
            int codigoTargeta;
            textT.show("Introduzca codigo de targeta");
            codigoTargeta = textT.read();
            int jugador;
            if (this.getMonopolyCodesquareMap().containsKey(codigoTargeta)) {//si el codigo es válido pide jugador y entra en doOperation
                do {
                    textT.show("¿Que jugador?");
                    int[] num = new int[4];
                    for (int i = 0; i < this.getPlayers().size(); i++) {
                        String c = this.getPlayers().get(i).getColor();
                        num[i] = -1;//en caso de que haya un jugador en bancarrota se comprueba que no se le pueda llamar
                        if (!this.getPlayers().get(i).getbankrupt()) {//si esta en bancarrota no se muestra
                            num[i] = i;
                            this.getTerminal().show(i + 1 + ". " + c);
                        }
                    }
                    jugador = textT.read();
                    opcionValida = jugador > 0 && jugador <= this.getPlayers().size() && (num[jugador - 1] != -1);
                } while (!opcionValida);
                this.getMonopolyCodesquareMap().get(codigoTargeta).doOperation(this.getPlayers().get(jugador - 1));
                this.serializarGame(rutaArchivo);
            } else {//muestra el resumen si el codigo de carta es nulo
                for (int i = 0; i < this.getPlayers().size(); i++) {
                    this.getPlayers().get(i).showResume();
                }
            }
        }
        textT.show("El jugador " + this.ganador() + " ha ganado la partida");
        eliminarFichero(rutaArchivo);//cuando se ha terminado una partida se elimina el fichero
    }

    private String ganador() {//devuelve el jugador que queda, es decir, el único que no esta en bancarrota
        for (int i = 0; i < this.getPlayers().size(); i++) {
            if (!this.getPlayers().get(i).getbankrupt()) {//se muestra si bancarrota es false
                return this.getPlayers().get(i).getColor();
            }
        }
        return null;
    }

    public static void eliminarFichero(String nombreArchivo) {//se encarga de eliminar el fichero si se ha terminado la partida
        try {
            Path path = Paths.get("config/oldGames/", nombreArchivo);
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serializarGame(String nombreArchivo) {//serializa la partida game
        try {
            ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream("config/oldGames/" + nombreArchivo));
            salida.writeObject(this);
            salida.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Game deserializarGame(String nombreArchivo) throws IOException, ClassNotFoundException {//deserializa la partida game
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("config/oldGames/" + nombreArchivo))) {
            Game game = (Game) ois.readObject();
            return game;
        }
    }
}
