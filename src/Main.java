
/**
 * Clase Main
 *
 * Sirve para cargar el JFrame en el que se controlara una barra para destruir
 * los bloques de metanfetamina que Walter usa para controlar su imperio 
 *
 * @authors Alexis García Soria (A00813330) & Diego Mayorga (A00813211)
 * @version 1.00 30/09/2014
 * 
 */

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args){
        // se crea appJuego 
        Juego appJuego = new Juego();
        // se hace visible a appJuego
        appJuego.setVisible(true); 
        // permite cerrar appJuego cuando el usuario lo desea
        appJuego.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
    }
}
