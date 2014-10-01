/** 
 * Juego
 *
 * Juego tipo "Brick Breaker" de Atari con tematica de la serie "Breaking Bad"
 * de ACM. El objetivo del juego es ayudar a Hawk a acabar el contrabando de 
 * Walter destruyendo sus metanfetaminas
 * 
 * @author Alexis García Soria (A00813330) & Diego Mayorga (A00813211)
 * @version 1.00 31/09/2014
 * 
 */

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.LinkedList;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Juego extends JFrame implements KeyListener, Runnable {
    
    // Variables empleadas por Juego
    private int iTime; //Manejador de tiempo
    private int iVidas; // Cantidad de oportunidades que tiene el jugador
    private int iVelocidadY; //Velocidad en Y
    private int iVelocidadX; //Velocidad en X
    private int iVelExtra; //Velocidad Extra
    private int iScore; // Cantidad de puntos acumulados por el jugador
    private boolean bDireccionX; // Variable para la direccion X del proyectil
    private boolean bDireccionY; // Variable para la direccion Y del proyectil
    boolean bPausado; // Indica si el juego esta en pausa o no
    private Objeto objBarra; // Representa la barra controlada por el jugador
    private Objeto objProyectil; // Representa el proyectil que destruye bloques
    private LinkedList lnkBloques; // Lista de bloques a destruir
    private SoundClip socSonidoChoqueBloque; // Sonido que emitira al chocar
            //el proyectil contra el bloque
    private SoundClip socSonidoChoqueBarra; // Sonido que emitira al chocar 
            //el proyectil con la barra
    private SoundClip socSonidoFondo; // Sonido de fondo del juego
    
    /* objetos para manejar el buffer del Applet y este no parpadee */
    private Image imaImagenApplet;   // Imagen a proyectar en Applet	
    private Graphics graGraficaApplet;  // Objeto grafico de la Imagen

    /**
     * Juego
     *
     * Metodo constructor usado para crear un objeto tipo Juego
     */
    public Juego() {
        init(); // Llama al metodo init para inicializar todas las variables
        start(); // Llama al metodo start para crear el hilo
    }
    
    /**
     * init
     *
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se inizializan las variables o se crean los objetos a
     * usarse en el <code>Applet</code> y se definen funcionalidades.
     */
    public void init() {
        setSize(466, 700); // Hago el applet de un tamaño 900, 500
        iVidas = 3; // El jugador tendra 3 oportunidades
        iScore = 0; // El score empieza en 0
        iVelocidadX = 0; //La velocidad en X empieza en 0, la pelota solo cae
        iVelocidadY = 2; //La velocidad en Y empieza en 2
        iVelExtra = 0; //Aun no hay velocidad extra
        // La direccion que nos interesa es: false: Abajo true: Arriba.
        bDireccionY = true;
        // La direccion que nos interesa es: false: Izq. true: Dererecha
        bDireccionX = true;
        
        // se obtiene la imagen para la barra
        URL urlImagenBarra = this.getClass().getResource("remolqueBB.png");
        // se crea la barra tipo Objeto
        objBarra = new Objeto(0, 0,
                Toolkit.getDefaultToolkit().getImage(urlImagenBarra));
        // se posiciona la barra centrada en la parte de abajo
        objBarra.setX((getWidth() / 2) - (objBarra.getAncho() / 2));
        objBarra.setY( getHeight() - (getHeight() / 6));
        // se le asigna una velocidad de 6
        objBarra.setVelocidad(9);

        // se carga la imagen para el proyectil
        URL urlImagenProyectil = 
                this.getClass().getResource("cristalAzulBB.png");
        // se crea al objeto Proyectil de la clase objeto
        objProyectil = new Objeto(0, 0,
                Toolkit.getDefaultToolkit().getImage(urlImagenProyectil));
        // se posiciona el proyectil en el centro arriba de barra
        objProyectil.setX((getWidth() / 2) - (objProyectil.getAncho() / 2));
        objProyectil.setY(objBarra.getY() - objProyectil.getAlto());
        // se le asigna una velocidad de 5
        objProyectil.setVelocidad(5);
        
        // se crea la lista de bloques a destruir
        lnkBloques = new LinkedList();
        // se asigna un numero random de 10 a 30 para la cantidad de bloques
        int iRandom = (int) (10 + Math.random() * 21);
        // el primer bloque ira en la esquina superior izquierda
        int iPosBlocX = getWidth() / 8;
        int iPosBlocY = getHeight() / 8;
        // se llena la lista de bloques
        for (int iI = 1; iI <= iRandom; iI++) {
            // se carga la imagen del bloque
            URL urlImagenBloque
                    = this.getClass().getResource("barrilBB.png");
            // se crea un bloque
            Objeto objBloque = new Objeto(0, 0,
                    Toolkit.getDefaultToolkit().getImage(urlImagenBloque));
            // se posiciona al bloque en cadena
            objBloque.setX(iPosBlocX);
            objBloque.setY(iPosBlocY);
            
            // se aumenta la posicion para el siguiente bloque
            if(iPosBlocX < (getWidth() - getWidth() / 8 
                    - objBloque.getAncho())) {
                    iPosBlocX = iPosBlocX + objBloque.getAncho();
            }
            else {
                iPosBlocY = iPosBlocY + objBloque.getAlto();
                iPosBlocX = getWidth() / 8;
            }
            // Se agrega al bloque a la lista
            lnkBloques.add(objBloque);
        }
        
        // se crea el sonido para el choque de con la barra
        socSonidoChoqueBarra = new SoundClip("ChoqueBarra.wav");

        // se crea el sonido para el choque con los bloques
        socSonidoChoqueBloque = new SoundClip("ChoqueBloque.wav");

        
        /* se le añade la opcion al applet de ser escuchado por los eventos
        /* del teclado */
        addKeyListener(this);
    }
    
    /**
     * start
     *
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se crea e inicializa el hilo para la animacion este metodo
     * es llamado despues del init o cuando el usuario visita otra pagina y
     * luego regresa a la pagina en donde esta este <code>Applet</code>
     *
     */
    public void start() {
        // Declaras un hilo
        Thread th = new Thread(this);
        // Empieza el hilo
        th.start();
    }
    
    /**
     * run
     *
     * Metodo sobrescrito de la clase <code>Thread</code>.<P>
     * En este metodo se ejecuta el hilo, que contendrá las instrucciones de
     * nuestro juego.
     *
     */
    public void run() {
        // se realiza el ciclo del juego hasta que el usuario lo cierre
        while (true) {
            /* mientras el jugador tenga vidas, se actualizan posiciones de 
             objetos se checa si hubo colisiones para desaparecer objetos o 
            corregir movimientos y se vuelve a pintar todo
             */
            if(!bPausado) {
            //Si no esta pausado realiza la actualizacion y revisa la colision
                actualiza();
                checaColision();
            }
            repaint();
            try {
                // El thread se duerme.
                Thread.sleep(20);
            } catch (InterruptedException iexError) {
                System.out.println("Hubo un error en el juego "
                        + iexError.toString());
            }
        }
    }
    
    /**
     * actualiza
     *
     * Metodo que actualiza la posicion del objeto
     *
     */
    public void actualiza() {
        //Si la direccion X es true(el proyectil va a la derecha)
        if(bDireccionX) {
            objProyectil.derecha();
        }
        //Si es false (va ala izquierda)
        else
            objProyectil.izquierda();
        
        //Si la direccion Y es true(el proyectil va hacia arriba)
        if(bDireccionY) {
            objProyectil.arriba();
        }
        //Si es false (va hacia abajo)
        else
            objProyectil.abajo();
    }
    
    /**
     * checaColision
     *
     * Metodo usado para checar la colision de los objetos entre ellos y
     * con las orillas del <code>Applet</code>.
     *
     */
    public void checaColision() {
        //Si el proyectil colisiona con la barra entonces..
        if(objBarra.colisiona(objProyectil)) {
            //Guardo el centro x del proyectil para no facilitar su comparacion
            int iCentroProyectil = objProyectil.getX() + 
                    objProyectil.getAncho() / 2;
            //Si el nivel de Y del lado inferior del proyectil es el mismo que
            //el nivel de Y del lado superior de la barra...
            if(objProyectil.getY() + objProyectil.getAlto() 
                    >= objBarra.getY()) {
                //Dividimos el ancho de la barra en 2 secciones que otorgan 
                //diferente velocidad dependiendo que seccion toque el proyectil
                //Si el centro del proyectil toca la primera parte de la 
                //barra o el lado izquierdo del proyectil esta mas a la 
                //izquierda que el lado izquierdo de la barra...
                if((iCentroProyectil > objBarra.getX() && iCentroProyectil < 
                        objBarra.getX() + objBarra.getAncho() / 2)
                                || (objProyectil.getX() < objBarra.getX())) {
                    bDireccionX = true; // arriba
                    bDireccionY = false; // izquierda
                }
                //Si el centro del proyectil toca la ultima parte de la barra o
                //el lado derecho del proyectil esta mas a la derecha que el 
                //lado derecho de la barra
                else if ((iCentroProyectil > objBarra.getX() +  
                        (objBarra.getAncho() / 2) && iCentroProyectil < 
                                objBarra.getX() + (objBarra.getAncho() -
                                        objBarra.getAncho() / 18)) || 
                                                (objProyectil.getX() > 
                                                    objBarra.getX() +
                                                            objBarra.getAlto()))
                                                                    {
                    bDireccionX = true; // arriba
                    bDireccionY = true; // derecha
                }
            }

        }
        // Checa si el proyectil choca contra los bloques
        for (Object objeBloque : lnkBloques) {
            Objeto objBloque = (Objeto) objeBloque;
            if (objBloque.colisiona(objProyectil)) {
                iScore++; // Se aumenta en 1 el score
                //Si la parte superior de proyectil es mayor o igual a la parte
                //inferior del bloque(esta golpeando por abajo del bloque...
                if(objProyectil.getY() <= objBloque.getY() 
                        + objBloque.getAlto()) {
                    bDireccionY = false; //va hacia abajo
                }
                //parte inferior del proyectil es menor o igual a la de la parte
                //superior del bloque(esta golpeando por arriba)...
                else if( objProyectil.getY() + objProyectil.getAlto() 
                        >= objBloque.getY()) {
                    bDireccionY = true; //va hacia arriba
                }
                //Si esta golpeando por algun otro lugar (los lados)...
                else
                    bDireccionX = !bDireccionX; //Direccion contraria de X
        }
        }
        //Si la barra choca con el lado izquierdo...
        if(objBarra.getX() < 0) {
            objBarra.setX(0); //Se posiciona al principio antes de salir
        }
        //Si toca el lado derecho del Jframe...
        else if(objBarra.getX() + objBarra.getAncho() - objBarra.getAncho() / 18 
                > getWidth()) {
            objBarra.setX(getWidth() - objBarra.getAncho() + objBarra.getAncho() 
                    / 18);// Se posiciciona al final antes de salir
        }
        //Si el Proyectil choca con cualquier limite de los lados...
        if(objProyectil.getX() < 0 || objProyectil.getX() 
                + objProyectil.getAncho() > getWidth()) {
            //Cambias su direccion al contrario
            bDireccionX = !bDireccionX;
        }
        //Si el Proyectil choca con la parte superior del Jframe...
        else if(objProyectil.getY() < 0) {
            //Cambias su direccion al contrario
            bDireccionY = !bDireccionY;
        }
        //Si el proyectil toca el fondo del Jframe...
        else if(objProyectil.getY() + objProyectil.getAlto() > getHeight()) {
            iVidas--; //Se resta una vida.
            // se posiciona el proyectil en el centro arriba de barra
            objProyectil.setX((objBarra.getX() + objBarra.getAncho()) / 2 
                    - (objProyectil.getAncho() / 2));
            objProyectil.setY(objBarra.getY() - objProyectil.getAlto());
        }
        
    }
    
    /**
     * paint
     *
     * Metodo sobrescrito de la clase <code>Applet</code>, heredado de la clase
     * Container.<P>
     * En este metodo lo que hace es actualizar el contenedor y define cuando
     * usar ahora el paint
     *
     * @param graGrafico es el <code>objeto grafico</code> usado para dibujar.
     *
     */
    public void paint(Graphics graGrafico) {
        // Inicializan el DoubleBuffer
        if (imaImagenApplet == null) {
            imaImagenApplet = createImage(this.getSize().width,
                    this.getSize().height);
            graGraficaApplet = imaImagenApplet.getGraphics();
        }

        // Se crea la imagen para el background
        URL urlImagenFondo = this.getClass().getResource("fondoBB.jpg");
        Image imaImagenFondo
                = Toolkit.getDefaultToolkit().getImage(urlImagenFondo);

        // Despliego la imagen
        graGraficaApplet.drawImage(imaImagenFondo, 0, 0,
                getWidth(), getHeight(), this);

        // Actualiza el Foreground.
        graGraficaApplet.setColor(getForeground());
        paint1(graGraficaApplet);

        // Dibuja la imagen actualizada
        graGrafico.drawImage(imaImagenApplet, 0, 0, this);
    }
    
    /**
     * paint1
     *
     * Metodo sobrescrito de la clase <code>Applet</code>, heredado de la clase
     * Container.<P>
     * En este metodo se dibuja la imagen con la posicion actualizada, ademas
     * que cuando la imagen es cargada te despliega una advertencia.
     *
     * @param graGrafico es el <code>objeto grafico</code> usado para dibujar.
     *
     */
    public void paint1(Graphics graGrafico) {
        // Si las imagenes ya se cargaron
        if (lnkBloques!= null&&objBarra!=null&&objProyectil!=null) {
            if(iVidas < 0) { //Si se acabaron las vidas
                //Creo imagen de game over
                URL urlImagenFin 
                        = this.getClass().getResource("breaking bit.jpg");
                Image imaImagenFin 
                        = Toolkit.getDefaultToolkit().getImage(urlImagenFin);
                //Despliego la imagen
                graGraficaApplet.drawImage(imaImagenFin, 0, 0, getWidth(),
                        getHeight(), this);
            }
            else { //si el juego sigue corriendo
                //Dibuja la imagen de la barra en la posicion actualizada
                graGrafico.drawImage(objBarra.getImagen(), 
                        objBarra.getX(), objBarra.getY(), this);
                
                //Dibuja la imagen del proyectil en la posicicion actualizada
                graGrafico.drawImage(objProyectil.getImagen(), 
                        objProyectil.getX(), objProyectil.getY(), this);
                
                //Dibuja la lista de bloques en la posicion actualizada
                for(Object objeBloque : lnkBloques) {
                    Objeto objBloque = (Objeto) objeBloque;
                    graGrafico.drawImage(objBloque.getImagen(), 
                            objBloque.getX(), objBloque.getY(), this);
                }
                
                //Se despliega el score y las vidas
                //Se crea estilo de Font para el texto
                Font strTexto= new Font("SansSerif", Font.PLAIN, 20);
                //Se asigna este estilo de Font al grafico
                graGrafico.setFont(strTexto);
                //Muestra el Score
                graGrafico.drawString("Score: "+ iScore, 25, 70);
                //Muestra la cantidad de vidas
                graGrafico.drawString("Vidas: "+iVidas, 25, 50);
            }
        } 
        else {
            //Da un mensaje mientras se carga el dibujo	
            graGrafico.drawString("No se cargo la imagen..", 20, 20);
        }
    }
    
    /**
     * Metodo que lee a informacion de un archivo y lo agrega a un vector.
     *
     * @throws IOException
     */
    public void leeArchivo() throws IOException{
        // defino el objeto de Entrada para tomar datos
    	BufferedReader brwEntrada;
    	try{
                // creo el objeto de entrada a partir de un archivo de texto
    		brwEntrada = new BufferedReader(new FileReader("datos.txt"));
    	} catch (FileNotFoundException e){
                // si marca error es que el archivo no existia entonces lo creo
    		File filPuntos = new File("datos.txt");
    		PrintWriter prwSalida = new PrintWriter(filPuntos);
                // le pongo datos ficticios o de default
                // lo cierro para que se grabe lo que meti al archivo
    		prwSalida.close();
                // lo vuelvo a abrir porque el objetivo es leer datos
    		brwEntrada = new BufferedReader(new FileReader("datos.txt"));
    	}
        // con el archivo abierto leo los datos que estan guardados
    	brwEntrada.close();
    }

    /**
     * keyTyped
     * 
     * Metodo sobrescrito de la interface <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al presionar una 
     * tecla que no es de accion.
     * @param e es el <code>evento</code> que se genera en al presionar.
     * 
     */
    public void keyTyped(KeyEvent e) {
        // no hay codigo pero se debe escribir el metodo
    }

    /**
     * keyPressed
     * 
     * Metodo sobrescrito de la interface <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al dejar presionada
     * alguna tecla.
     * @param keyEvent es el <code>evento</code> generado al presionar.
     * 
     */
    public void keyPressed(KeyEvent keEvent) {
        if(keEvent.getKeyCode() == keEvent.VK_LEFT) {
            objBarra.izquierda();
        }
        else if(keEvent.getKeyCode() == keEvent.VK_RIGHT) {
            objBarra.derecha();
        }
    }

    /**
     * keyReleased
     * 
     * Metodo sobrescrito de la interface <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al soltar la tecla.
     * @param keyEvent es el <code>evento</code> que se genera en al soltar las teclas.
     */
    public void keyReleased(KeyEvent keEvent) {
        if(keEvent.getKeyCode() == keEvent.VK_LEFT) {
            objBarra.izquierda();
        }
        else if(keEvent.getKeyCode() == keEvent.VK_RIGHT) {
            objBarra.derecha();
        }
    }
    
}
