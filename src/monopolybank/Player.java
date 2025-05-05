/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monopolybank;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vanre
 */
public class Player implements Serializable {

    private Color color;
    private int balance;
    private boolean bankrupt;

    public String getColor() {
        if (null == this.color) {
            return "negro";
        } else {
            return switch (this.color) {
                case red ->
                    "rojo";
                case green ->
                    "verde";
                case blue ->
                    "azul";
                default ->
                    "negro";
            };
        }
    }
    private transient Terminal terminal;

    public void setColor(int i) {
        switch (i) {
            case 0:
                this.color = Color.red;
                break;
            case 1:
                this.color = Color.green;
                break;
            case 2:
                this.color = Color.blue;
                break;
            default:
                this.color = Color.black;
                break;
        }
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setBankrupt(boolean bankrupt) {
        this.bankrupt = bankrupt;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setProperty(Map<Integer, Property> property) {
        this.property = property;
    }

    public Map<Integer, Property> getProperty() {
        return this.property;
    }

    public int getBalance() {
        return this.balance;
    }

    public boolean getbankrupt() {
        return this.bankrupt;
    }

    private Map<Integer, Property> property;

    private enum Color {
        red, green, blue, black
    };

    public Player(int i, Terminal textT) {
        this.setColor(i);
        this.setBankrupt(false);
        this.setTerminal(textT);
        this.setBalance(1500);
        Map<Integer, Property> p = new HashMap();
        this.setProperty(p);
    }

    public void pay(int amount, boolean mandatory) {//se encarga de cobrar al jugador o no dependiendo de si mandatory es true o false
        if ((this.getBalance() < amount) && !mandatory) {
            this.getTerminal().show("No tiene suficiente dinero operacion cancelada");
        } else if ((this.getBalance() < amount) && mandatory) {
            if (this.getProperty().isEmpty()) {//si no tiene ninguna propiedad
                this.setBankrupt(true);//el jugador esta en bancarrota
            } else {
                while ((this.getBalance() < amount) && !this.allHipetado()) {
                    this.getTerminal().show("No tienes suficiente dinero ¿que propiedad quieres vender?");
                    for (Map.Entry<Integer, Property> entry : this.getProperty().entrySet()) {
                        Property valor = entry.getValue();
                        if (!valor.getMortaged()) {
                            this.getTerminal().show(valor.getId() + ". " + valor.getDescription());
                        }
                    }
                    int respuesta = this.getTerminal().read();
                    if (!this.getProperty().containsKey(respuesta)) {
                        this.getTerminal().show("Esa opcion no esta reflejada");
                    } else {
                        this.sellActives(this.getProperty().get(respuesta));
                    }
                }
                if (this.allHipetado()) {
                    this.setBankrupt(true);//el jugador esta en bancarrota
                } else {
                    this.setBalance(this.getBalance() - amount);//si no esta en bancarrota significa que si tiene dinero para pagar, y se lo quitamos
                    this.getTerminal().show("Operacion realizada");
                }
            }
        } else {
            this.setBalance(this.getBalance() - amount);
            this.getTerminal().show("Operacion realizada");
        }
    }

    private boolean allHipetado() {//te devuelve true si todo esta hipotecado y false si le queda alguna propiedad sin hipotecar
        for (Map.Entry<Integer, Property> entry : this.getProperty().entrySet()) {
            Property valor = entry.getValue();
            if (!valor.getMortaged()) {//si no esta hipotecada
                return false;
            }
        }
        return true;
    }

    private void sellActives(Property target) {//se encarga de hipotecar una propiedad si no tiene casas en caso de ser una calle, si las tiene vendemos una calle, y si es un transporte o un servicio la hipotecamos
        if (target instanceof Street) {//si es una calle hay q comprobar si tiene casas y en ese caso venderlas
            Street streetTarget = (Street) target; // Casting a Street
            if (streetTarget.getBuiltHouses() == 0) {
                this.getTerminal().show("Se ha hipotecado la propiedad");
                streetTarget.setMortaged(true);//la ponemos como hipotecada
                this.setBalance(this.getBalance() + streetTarget.getMortgageValue());
            } else {
                this.getTerminal().show("La propiedad tiene casas por lo que vendemos una casa");
                streetTarget.setBuiltHouses(streetTarget.getBuiltHouses() - 1);
                this.setBalance(this.getBalance() + (streetTarget.getHousePrice() / 2));//cuando vendes una casa se devuelve la mitad del valor por el que la compraste

            }
        } else { //si no es una calle no puede tener casas
            this.getTerminal().show("Se ha hipotecado la propiedad");
            target.setMortaged(true);//la ponemos como hipotecada
            this.setBalance(this.getBalance() + target.getMortgageValue());
        }

    }

    public void transpaseProperty(Player newOwner) {//se encarga de transpasar las propiedades cuando un jugador a perdido por culpa de otro jugador
        for (Map.Entry<Integer, Property> entry : this.getProperty().entrySet()) {
            Property valor = entry.getValue();
            valor.setOwner(newOwner);//le colocamos el nuevo dueño
            newOwner.getProperty().put(valor.getId(), valor);//la añadimos al mapa de propiedades del nuevo dueño
        }
        this.getProperty().clear();//vaciamos el mapa de propiedades del jugador que ha perdido
        this.getTerminal().show("Operacion realizada");
    }

    public void vaciarJugador() {//entra si el jugador ha perdido, por lo que sus propiedades se quedan sin dueño y se vacia el mapa de propiedades del jugador
        if (!this.getProperty().isEmpty()) {//si es vacia no se hace nada
            for (Map.Entry<Integer, Property> entry : this.getProperty().entrySet()) {
                Property valor = entry.getValue();
                valor.setOwner(null);
            }
            this.getProperty().clear();//vaciamos el mapa de propiedades
        }
    }

    public void showResume() {//muestra el resumen del jugador, si tiene propiedades, si esa propiedad esta hipotecada o si tiene casas
        Terminal textT = this.getTerminal();
        if (this.getbankrupt()) {//comprobamos si esta o no en bancarrota
            textT.show("El jugador " + this.getColor() + " esta en bancarrota");
        } else {
            textT.show("El jugador " + this.getColor() + " tiene:");
            textT.show(this.getBalance() + "€");
            if (this.getProperty().isEmpty()) {
                textT.show("No tiene propiedades");
            } else {
                textT.show("Propiedades:");
                for (Map.Entry<Integer, Property> entry : this.getProperty().entrySet()) {
                    Property valor = entry.getValue();
                    if (valor.getMortaged()) {
                        textT.show("La siguiente propiedad esta hipotecada");
                        textT.show(valor.getDescription());
                    } else if (valor instanceof Street) {
                        textT.show(valor.getDescription());
                        Street calle = (Street) valor;
                        if (calle.getBuiltHouses() == 0) {
                            textT.show("No tiene casas");
                        } else if (calle.getBuiltHouses() < 5) {
                            textT.showM("Casas: ", "", "", calle.getBuiltHouses());
                        } else {
                            textT.show("Tiene 4 casas y un hotel");
                        }
                    } else {
                        textT.show(valor.getDescription());
                    }
                }
            }
        }
    }
}
