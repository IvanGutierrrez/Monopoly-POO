/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monopolybank;

import java.io.Serializable;

/**
 *
 * @author vanre
 */
public class Service extends Property implements Serializable {

    private int[] costStaying;

    public Service(String linea, Terminal terminal) {//crea un objeto de la clase Service con los valores según la linea leida del fichero txt
        String[] result = linea.split(";");
        int[] arrayV = new int[2];
        arrayV[0] = Integer.parseInt(result[3]);
        arrayV[1] = Integer.parseInt(result[4]);
        this.setOwner(null);
        this.setCostStaying(arrayV);
        this.setDescription(result[2]);
        this.setTerminal(terminal);
        this.setId(Integer.parseInt(result[0]));
        this.setPrice(150);
        this.setMortaged(false);
        this.setMortgageValue(Integer.parseInt(result[5]));
    }

    public void setCostStaying(int[] costStaying) {
        this.costStaying = costStaying;
    }

    public int[] getCostStaying() {
        return this.costStaying;
    }

    private void comprarPropiedad(Player p) {//Entra si el la carta tiene el dueño como null y compra la propiedad o no
        String nombre = this.getDescription();
        String color = p.getColor();
        int precio = this.getPrice();
        this.getTerminal().showM("Se va a realizar la compra de la propiedad ", nombre, " por parte del jugador " + color + " por un importe de", precio);
        this.getTerminal().show("1. Aceptar");
        this.getTerminal().show("2. Cancelar");
        int respuesta = this.getTerminal().read();
        if (respuesta == 1) {
            int ini = p.getBalance();
            p.pay(precio, false);//no es obligatorio
            int fin = p.getBalance();
            if (ini != fin) {//si la cantidad de dinero es distinta antes y después de entrar en pagar es que se ha ejecutado
                this.setOwner(p);
                p.getProperty().put(this.getId(), this);
            }
        } else {
            this.getTerminal().show("Operacion cancelada");
        }
    }

    private void hipotecar(Player p) {//entra si el dueño de la carta es el jugador y si no esta hipotecada la carta
        this.getTerminal().showM("Se va a realizar la hipoteca de la propiedad ", this.getDescription(), " por parte del jugador " + p.getColor() + " por un importe de", this.getMortgageValue());
        this.getTerminal().show("1. Aceptar");
        this.getTerminal().show("2. Cancelar");
        int respuesta = this.getTerminal().read();
        if (respuesta == 1) {
            this.setMortaged(true);//la ponemos como hipotecada
            p.setBalance(p.getBalance() + this.getMortgageValue());
            this.getTerminal().show("Operacion realizada");
        } else {
            this.getTerminal().show("Operacion cancelada");
        }
    }

    private void deshipotecar(Player p) {//entra si el dueño de la carta es el jugador y si esta hipotecada la carta
        this.getTerminal().showM("Se va a realizar la deshipoteca la propiedad ", this.getDescription(), " por parte del jugador " + p.getColor() + " por un importe de", (int) (this.getMortgageValue() + Math.round(this.getMortgageValue() * 0.1)));
        this.getTerminal().show("1. Aceptar");
        this.getTerminal().show("2. Cancelar");
        int respuesta = this.getTerminal().read();
        if (respuesta == 1) {
            int ini = p.getBalance();
            p.pay((int) (this.getMortgageValue() + Math.round(this.getMortgageValue() * 0.1)), false);
            int fin = p.getBalance();
            if (ini != fin) {
                this.setMortaged(false);//la deshipotecamos
            }
        }
    }

    private void doOwnerOperation(Player p) {//entra si el dueño de la carta es el jugador
        if (this.getMortaged()) {//se va a deshipotecar la propiedad
            this.deshipotecar(p);
        } else {//se va a hipotecar la propiedad
            this.hipotecar(p);
        }
    }

    private void alquilarPropiedad(Player p) {//si el dueño de la carta es distinto al jugador
        this.getTerminal().show("¿Que numero has sacado en los dados?");
        int precio = this.getTerminal().read();
        if (precio > 1 && precio < 13) {//el número de los dados solo puede ser entre 2 y 12
            if (this.getOwner().getProperty().containsKey(28) && this.getOwner().getProperty().containsKey(12)) {//si tiene los dos servicios
                precio *= this.getCostStaying()[1];
            } else {
                precio *= this.getCostStaying()[0];
            }
            this.getTerminal().showM("El jugador " + p.getColor() + " usara la propiedad ", this.getDescription(), " por un importe de", precio);
            this.getTerminal().show("1. Aceptar");
            this.getTerminal().show("2. Cancelar");
            int respuesta = this.getTerminal().read();
            if (respuesta == 1) {
                p.pay(precio, true);
                if (p.getbankrupt()) {
                    this.getOwner().setBalance(this.getOwner().getBalance() + p.getBalance());
                    this.getTerminal().show("Te has quedado sin activos para hipotecar por lo que has perdido y se le traspasaran las propiedades al jugador " + this.getOwner().getColor());
                    p.transpaseProperty(this.getOwner());//se le pasan todo lo que tiene al nuevo jugador
                } else {
                    this.getOwner().setBalance(this.getOwner().getBalance() + precio);
                }
            } else {
                this.getTerminal().show("Operacion cancelada");
            }
        } else {
            this.getTerminal().show("El numero debe ser del 1 al 12");
            this.getTerminal().show("Operacion cancelada");
        }
    }

    @Override
    public void doOperation(Player p) {
        if (this.getOwner() == null) {//si no tiene dueño
            this.comprarPropiedad(p);
        } else if (this.getOwner() == p) {//si el dueño es el propio jugador
            this.doOwnerOperation(p);
        } else {//si el dueño es otro jugador 
            if (!this.getMortaged()) {//si no esta hipotecada
                this.alquilarPropiedad(p);
            } else {
                this.getTerminal().show("La propiedad esta hipotecada no es necesario pagar");
            }
        }
    }

}
