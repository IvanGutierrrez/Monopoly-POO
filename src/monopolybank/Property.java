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
public abstract class Property extends MonopolyCode implements Serializable {

    private int price;
    private boolean mortaged;
    private int mortgageValue;
    private Player owner;

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return this.price;
    }

    public void setMortaged(boolean mortaged) {
        this.mortaged = mortaged;
    }

    public boolean getMortaged() {
        return this.mortaged;
    }

    public void setMortgageValue(int mortgageValue) {
        this.mortgageValue = mortgageValue;
    }

    public int getMortgageValue() {
        return this.mortgageValue;
    }

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(Player player) {
        this.owner = player;
    }

    @Override
    public abstract void doOperation(Player p);
}
