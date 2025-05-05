package monopolybank;
// El fichero con los codigos se encuentra en "config/MonopolyCode.txt"
// Los idiomas deben estar en la carpeta "config/languages/"
// Las partidas antiguas deberán estar en la carpeta "config/oldGames/"

import java.io.IOException;

/**
 *
 * @author
 */
public class MonopolyBank {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {//Crea el GameManager y llama al procedimiento start
        GameManager m = new GameManager();
        m.start();
    }

}
