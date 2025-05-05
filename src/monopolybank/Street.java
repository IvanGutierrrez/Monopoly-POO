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
public class Street extends Property implements Serializable {

    private int builtHouses;//guarda el número de casas u hoteles que tiene
    private int housePrice;//guarda el valor de comprar una casa

    public void setBuiltHouses(int builtHouses) {
        this.builtHouses = builtHouses;
    }

    public int getBuiltHouses() {
        return this.builtHouses;
    }

    public void setHousePrice(int housePrice) {
        this.housePrice = housePrice;
    }

    public int getHousePrice() {
        return this.housePrice;
    }

    public void setCostStayingWithHouses(int[] costStayingWithHouses) {
        this.costStayingWithHouses = costStayingWithHouses;
    }

    public int[] getCostStayingWithHouses() {
        return this.costStayingWithHouses;
    }
    private int[] costStayingWithHouses;

    public Street(String linea, Terminal terminal) {//crea un objeto de la clase Street con los valores según la linea leida del fichero txt
        String[] result = linea.split(";");
        this.setBuiltHouses(0);
        this.setHousePrice(Integer.parseInt(result[9]));
        int[] arrayV = new int[6];
        for (int i = 0; i < 6; i++) {
            arrayV[i] = Integer.parseInt(result[i + 3]);
        }
        this.setOwner(null);
        this.setCostStayingWithHouses(arrayV);
        this.setDescription(result[2]);
        this.setTerminal(terminal);
        this.setId(Integer.parseInt(result[0]));
        this.setPrice(Integer.parseInt(result[11]) * 2);
        this.setMortaged(false);
        this.setMortgageValue(Integer.parseInt(result[11]));
    }

    private void comprarPropiedad(Player p) {//Entra si la carta tiene el dueño como null y compra la propiedad o no
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
            if (ini != fin) {//si la cantidad de dinero es distinta antes y despues de entrar en pagar es que se ha ejecutado
                this.setOwner(p);
                p.getProperty().put(this.getId(), this);
            }
        } else {
            this.getTerminal().show("Operacion cancelada");
        }
    }

    private void comprarCasas(Player p) {//entra si el jugador es el dueño de la carta y el dueño a elegido la opción de comprar casas
        if (this.getBuiltHouses() < 5) {//si tiene mas de 5 casas no puede comprar mas
            this.getTerminal().showM("Se va a realizar la compra de una casa para la propiedad ", this.getDescription(), " por parte del jugador " + p.getColor() + " por un importe de", this.getHousePrice());
            this.getTerminal().show("1. Aceptar");
            this.getTerminal().show("2. Cancelar");
            int respuesta = this.getTerminal().read();
            if (respuesta == 1) {
                int ini = p.getBalance();
                p.pay(this.getHousePrice(), false);
                int fin = p.getBalance();
                if (ini != fin) {
                    this.setBuiltHouses(this.getBuiltHouses() + 1);//le añadimos una casa
                }
            } else {
                this.getTerminal().show("Operacion cancelada");
            }
        } else {
            this.getTerminal().show("Esta propiedad no puede tener mas casas");
        }
    }

    private void venderCasas(Player p) {//entra si el jugador es el dueño de la carta y el dueño a elegido la opción de vender casas
        if (this.getBuiltHouses() > 0) {//solo entra si tiene al menos una casa
            this.getTerminal().showM("Se va a realizar la venta de una casa para la propiedad ", this.getDescription(), " por parte del jugador " + p.getColor() + " por un importe de", this.getHousePrice() / 2);
            this.getTerminal().show("1. Aceptar");
            this.getTerminal().show("2. Cancelar");
            int respuesta = this.getTerminal().read();
            if (respuesta == 1) {
                p.setBalance(p.getBalance() + this.getHousePrice() / 2);//el precio por vender una casa es la mitad del que se pago por ella
                this.setBuiltHouses(this.getBuiltHouses() - 1);//le quitamos una casa
            } else {
                this.getTerminal().show("Operacion cancelada");
            }
        } else {
            this.getTerminal().show("Esta propiedad no tiene casas para vender");
        }
    }

    private void hipotecar(Player p) {//entra si el jugador es el dueño de la carta, si no esta hipotecada y si el dueño ha elegido esta opción 
        if (this.getBuiltHouses() == 0) {//si tiene casas no puede ser hipotecada
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
        } else {
            this.getTerminal().show("Una propiedad no puede ser hipotecada si tiene aun casas");
        }
    }

    private void deshipotecar(Player p) {//entra inmediatamente si el jugador es el dueño de esta propiedad y si esta hipotecada, luego el jugador puede elegir si seguir o no
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

    private void doOwnerOperation(Player p) {//el jugador es el dueño de la calle
        if (this.getMortaged()) {//se va a deshipotecar la propiedad, si esta hipotecada no se puede comprar ni vender casas
            this.deshipotecar(p);
        } else { //se va a hipotecar la propiedad, comprar o vender casas
            this.getTerminal().show("Que operacion quiere ralizar");
            this.getTerminal().show("1. Hipotecar");
            this.getTerminal().show("2. Comprar casas");
            this.getTerminal().show("3. Vender casas");
            int respuesta = this.getTerminal().read();
            switch (respuesta) {
                case 1 -> //hipotecar
                    this.hipotecar(p);
                case 2 -> //comprar casas
                    this.comprarCasas(p);
                case 3 -> //vender casas
                    this.venderCasas(p);
                default -> //si no coincide sale
                    this.getTerminal().show("Operacion cancelada");
            }
        }
    }

    private void alquilarPropiedad(Player p) {//si el dueño de la calle es distinto al jugador
        int precio = this.getCostStayingWithHouses()[this.getBuiltHouses()];
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
    }

    @Override
    public void doOperation(Player p) {
        if (this.getOwner() == null) {//comprar la propiedad
            this.comprarPropiedad(p);
        } else if (this.getOwner() == p) {//el dueño es el mismo jugador
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
