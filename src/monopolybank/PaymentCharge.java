/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monopolybank;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vanre
 */
public class PaymentCharge extends MonopolyCode implements Serializable {

    private int amount;

    public PaymentCharge(String linea, Terminal terminal) {//crea un objeto de la clase PaymentCharge con los valores según la linea leida del fichero txt
        String[] result = linea.split(";");
        this.setDescription(result[2]);
        this.setTerminal(terminal);
        this.setId(Integer.parseInt(result[0]));
        String patron = "(-?\\d+)";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(result[2]);
        if (matcher.find()) {
            this.setAmount(Integer.parseInt(matcher.group()));
        } else {//hay un caso en el que el precio no esta en la frase si no después y hay 4 Strings separados por ; (código 38)
            if (result.length > 3) {
                this.setAmount(Integer.parseInt(result[3]));
            } else {
                this.setAmount(0);
            }
        }
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return this.amount;
    }

    @Override
    public void doOperation(Player p) {
        this.getTerminal().show(this.getDescription());
        if (this.getAmount() > 0) {//si el precio es cero no hace nada
            this.getTerminal().show("1. Aceptar");
            this.getTerminal().show("2. Cancelar");
            int respuesta = this.getTerminal().read();
            if (respuesta == 1) {
                p.setBalance(p.getBalance() + this.getAmount());
                this.getTerminal().show("Operacion realizada");
            } else {
                this.getTerminal().show("Operacion cancelada");
            }
        } else if (this.getAmount() < 0) {
            this.getTerminal().show("1. Aceptar");
            this.getTerminal().show("2. Cancelar");
            int respuesta = this.getTerminal().read();
            if (respuesta == 1) {
                p.pay(Math.abs(this.getAmount()), true);//es obligatorio y pasas a positivo el número
                if (p.getbankrupt()) {
                    p.vaciarJugador();//ponemos el dueño de sus propiedades a null y vaciamos su mapa de propiedades
                    this.getTerminal().show("Te has quedado sin activos para hipotecar por lo que has perdido y te has quedado en bancarrota");
                }
            } else {
                this.getTerminal().show("Operacion cancelada");
            }
        }
    }
}
