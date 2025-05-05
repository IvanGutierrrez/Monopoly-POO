/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monopolybank;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vanre
 */
public class RepairsCard extends MonopolyCode implements Serializable {

    private int amountForHouse;
    private int amountForHotel;

    public RepairsCard(String linea, Terminal terminal) {//crea un objeto de la clase RepairsCard con los valores según la linea leida del fichero txt
        String[] result = linea.split(";");
        this.setDescription(result[2]);
        this.setTerminal(terminal);
        this.setId(Integer.parseInt(result[0]));
        String patron = "(\\d+)";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(result[2]);
        if (matcher.find()) {//siempre vas a encontrar 2 números, el primero es el de las casas y el segundo es el de los hoteles
            this.setAmountForHouse(Integer.parseInt(matcher.group()));
        }
        if (matcher.find()) {
            this.setAmountForHotel(Integer.parseInt(matcher.group()));
        }
    }

    public void setAmountForHouse(int amountForHouse) {
        this.amountForHouse = amountForHouse;
    }

    public void setAmountForHotel(int amountForHotel) {
        this.amountForHotel = amountForHotel;
    }

    public int getAmountForHouse() {
        return this.amountForHouse;
    }

    public int getAmountForHotel() {
        return this.amountForHotel;
    }

    @Override
    public void doOperation(Player p) {
        int tC = this.totalCasas(p);
        if (tC != 0) {//si total casas es 0 no va a pagar nada y tampoco va a tener hoteles
            int pC = this.getAmountForHouse() * tC;
            int pH = this.getAmountForHotel() * this.totalHoteles(p);
            this.getTerminal().showM("El jugador " + p.getColor() + " va a realizar el pago por casas de ", String.valueOf(pC), " y por hoteles de", pH);
            this.getTerminal().show("1. Aceptar");
            this.getTerminal().show("2. Cancelar");
            int respuesta = this.getTerminal().read();
            if (respuesta == 1) {
                p.pay(pC + pH, true); //es obligatorio pagar
                if (p.getbankrupt()) {
                    p.vaciarJugador();//ponemos el dueño de sus propiedades a null y vaciamos su mapa de propiedades
                    this.getTerminal().show("Te has quedado sin activos para hipotecar por lo que has perdido y te has quedado en bancarrota");
                }
            } else {
                this.getTerminal().show("Operacion cancelada");
            }
        } else {
            this.getTerminal().show("No tienes ninguna casa ni hotel");
        }
    }

    private int totalCasas(Player p) {//devuelve el número de casas que tiene 
        int num = 0;
        for (Map.Entry<Integer, Property> entry : p.getProperty().entrySet()) {
            Property valor = entry.getValue();
            if (valor instanceof Street) {
                Street calle = (Street) valor;
                if (calle.getBuiltHouses() > 0) {//si tiene casa
                    if (calle.getBuiltHouses() == 5) {//si tiene igual a 5 la última es un hotel y no se cuenta
                        num = num + 4;
                    } else {//no tiene hoteles pero si casas
                        num = num + calle.getBuiltHouses();
                    }
                }
            }
        }
        return num;
    }

    private int totalHoteles(Player p) {//devuelve el número de hoteles que tiene
        int num = 0;
        for (Map.Entry<Integer, Property> entry : p.getProperty().entrySet()) {
            Property valor = entry.getValue();
            if (valor instanceof Street) {
                Street calle = (Street) valor;
                if (calle.getBuiltHouses() == 5) {
                    num++;
                }
            }
        }
        return num;
    }
}
