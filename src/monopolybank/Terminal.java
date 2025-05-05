/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monopolybank;

/**
 *
 * @author vanre
 */
public abstract class Terminal {

    private TranslatorManager traductor;

    abstract int read();

    abstract void show(String s);

    abstract void showM(String p1, String prop, String p2, int n);

    public TranslatorManager getTranslatorManager() {
        return this.traductor;
    }

    public void setTranslatorManager(TranslatorManager t) {
        this.traductor = t;
    }
}
