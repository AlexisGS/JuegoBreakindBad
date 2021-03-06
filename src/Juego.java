
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
    private boolean bAvanzaBloque;
    private String[] strArrDatos;
    private boolean[] bPoderes={false,false,false,false};
    private BufferedReader bfrEntrada; // Variable para leer archivos
    private int iTime; //Manejador de tiempo
    private int iNumBloques;//Cuenta cuantos bloques hay
    private int iVidas; // Cantidad de oportunidades que tiene el jugador
    private int iScore; // Cantidad de puntos acumulados por el jugador
    private boolean bDireccionX; // Variable para la direccion X del proyectil
    private boolean bDireccionY; // Variable para la direccion Y del proyectil
    private boolean bPausado; // Indica si el juego esta en pausa o no
    private Objeto objBarra; // Representa la barra controlada por el jugador
    private Objeto objProyectil; // Representa el proyectil que destruye bloques
    private LinkedList lnkBloques; // Lista de bloques a destruir
    private SoundClip socSonidoChoqueBloque; // Sonido que emitira al chocar
    //el proyectil contra el bloque
    private SoundClip socSonidoChoqueBarra; // Sonido que emitira al chocar 
    //el proyectil con la barra
    private SoundClip socSonidoFondo; // Sonido de fondo del juego
    private boolean bEmpieza; // Booleana que se encendera cuando empieze el 
    // juego
    private int iNivel; // Nivel en el que va el jugador
    private boolean bPerdio; // Indicar cuando el usuario perdio

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
        bAvanzaBloque = false;
        iNumBloques = 54; // Cantidad exacta para que se forme la figura
        iVidas = 3; // El jugador tendra 3 oportunidades
        iScore = 0; // El score empieza en 0
        iNivel = 1; // Empezara en el 1er nivel
        // La direccion que nos interesa es: false: Abajo true: Arriba.
        bDireccionY = false;
        // La direccion que nos interesa es: false: Izq. true: Dererecha
        bDireccionX = true;

        // El juego empieza pausado
        bPausado = true;
        // El juego no empieza hasta que el usuario lo desee
        bEmpieza = false;
        // El jugador no ha perdido
        bPerdio = false;

        // se obtiene la imagen para la barra
        URL urlImagenBarra = this.getClass().getResource("remolqueBB.png");
        // se crea la barra tipo Objeto
        objBarra = new Objeto(0, 0,
                Toolkit.getDefaultToolkit().getImage(urlImagenBarra));
        // se posiciona la barra centrada en la parte de abajo
        objBarra.setX((getWidth() / 2) - (objBarra.getAncho() / 2));
        objBarra.setY(getHeight() - objBarra.getAlto());
        // se le asigna una velocidad de 9
        objBarra.setVelocidad(20);

        // se carga la imagen para el proyectil
        URL urlImagenProyectil
                = this.getClass().getResource("cristalAzulBB2.png");
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
        // se llena y acomoda la lista de bloques
        try {
            acomodaBloques();
        } catch (IOException ioeError) {
            System.out.println("Hubo un error al cargar el juego: "
                    + ioeError.toString());
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
            if (!bPausado) { // Si el juego no esta pausado
                actualiza(); // actualiza
                checaColision(); // checa colisiones
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
        //Si el indice de bloque a mover no es -1(no hay que mover bloque)...
        if(bAvanzaBloque) {
            for (Object objeBloque : lnkBloques) {
                Objeto objBloque = (Objeto) objeBloque;
                //Mueves los bloques que hay que mover
                if(objBloque.getVelocidad()!=0) {
                    objBloque.abajo();
                }
            }
        }
        //Si la direccion X es true(el proyectil va a la derecha)
        if (bDireccionX) {
            objProyectil.derecha();
        } //Si es false (va ala izquierda)
        else {
            objProyectil.izquierda();
        }

        //Si la direccion Y es true(el proyectil va hacia arriba)
        if (!bDireccionY) {
            objProyectil.arriba();
        } //Si es false (va hacia abajo)
        else {
            objProyectil.abajo();
        }

        if (iNumBloques == 0) { // Si se acaban los bloques
            iNumBloques = 54; // Se reinicia la variable de los bloques
            iNivel++; // Aumenta el nivel
            // Se reposiciona el proyectil
            objProyectil.reposiciona((objBarra.getX() + objBarra.getAncho() / 2
                    - (objProyectil.getAncho() / 2)), (objBarra.getY()
                    - objProyectil.getAlto()));
            // Se aumenta la velocidad del proyectil
            objProyectil.setVelocidad(objProyectil.getVelocidad() + 3);
            // Se reposiciona la barra
            objBarra.reposiciona(((getWidth() / 2)
                    - (objProyectil.getAncho() / 2)), (getHeight()
                    - objBarra.getAlto()));
            // se limpia la lista de bloques
            lnkBloques.clear();
            // se vuelve a llenar y se acomoda
            try {
                acomodaBloques();
            } catch (IOException ioeError) {
                System.out.println("Hubo un error al cargar el juego: "
                        + ioeError.toString());
            }
            // se mueve hacia arriba el proyectil
            bDireccionY = false;
            bDireccionX = true;
        }

        if (iVidas == 0) {
            bPerdio = true;
        }
        /*if (iVidas == 0) {
         bPerdio = true;
         bPausado = true;
         iNumBloques = 54; // se reinicia la cantidad de bloques
         iNivel = 1; // vuelve al nivel 1
         // La direccion del proyectil sera para arrib
         bDireccionY = false;
         // La direccion que nos interesa es: false: Izq. true: Dererecha
         bDireccionX = true;
         // Se reposiciona el proyectil
         objProyectil.reposiciona((objBarra.getX() + objBarra.getAncho() / 2
         - (objProyectil.getAncho() / 2)), (objBarra.getY()
         - objProyectil.getAlto()));
         // Se reincia la velocidad del proyectil
         objProyectil.setVelocidad(5);
         // Se reposiciona la barra
         objBarra.reposiciona(((getWidth() / 2) 
         - (objProyectil.getAncho() / 2)), (getHeight() 
         - objBarra.getAlto()));
         // se limpia la lista de bloques
         lnkBloques.clear();
         // se vuelve a llenar y se acomoda
         try {
         acomodaBloques();
         } catch (IOException ioeError) {
         System.out.println("Hubo un error al cargar el juego: "
         + ioeError.toString());
         }
         }
        
         if (!bPerdio) {
         iVidas = 3; // se reinicia la cantidad de vidas
         iScore = 0; // se reincia el score
         }*/
    }

    /**
     * checaColision
     *
     * Metodo usado para checar la colision de los objetos entre ellos y con las
     * orillas del <code>Applet</code>.
     *
     */
    public void checaColision() {
        //Si el proyectil colisiona con la barra entonces..
        if (objBarra.colisiona(objProyectil)) {
            //Guardo el centro x del proyectil para no facilitar su comparacion
            int iCentroProyectil = objProyectil.getX()
                    + objProyectil.getAncho() / 2;
            //Si el nivel de Y del lado inferior del proyectil es el mismo que
            //el nivel de Y del lado superior de la barra...
            if (objProyectil.getY() + objProyectil.getAlto()
                    >= objBarra.getY()) {
                //Dividimos el ancho de la barra en 2 secciones que otorgan 
                //diferente velocidad dependiendo que seccion toque el proyectil
                //Si el centro del proyectil toca la primera parte de la 
                //barra o el lado izquierdo del proyectil esta mas a la 
                //izquierda que el lado izquierdo de la barra...
                if ((iCentroProyectil > objBarra.getX() && iCentroProyectil
                        < objBarra.getX() + objBarra.getAncho() / 2)
                        || (objProyectil.getX() < objBarra.getX())) {
                    bDireccionX = false; // arriba
                    bDireccionY = false; // izquierda
                } //Si el centro del proyectil toca la ultima parte de la barra o
                //el lado derecho del proyectil esta mas a la derecha que el 
                //lado derecho de la barra
                else if ((iCentroProyectil > objBarra.getX()
                        + (objBarra.getAncho() / 2) && iCentroProyectil
                        < objBarra.getX() + (objBarra.getAncho()
                        - objBarra.getAncho() / 18)) || (objProyectil.getX()
                        + objProyectil.getAncho() > objBarra.getX()
                        + objBarra.getAncho())) {
                    bDireccionX = true; // arriba
                    bDireccionY = false; // derecha
                }
            }

        }
        for (Object objeBloque : lnkBloques) {
            Objeto objBloque = (Objeto) objeBloque;
        //Checa si la barra choca con los bloques (Choca con el poder)
        if(objBarra.colisiona(objBloque)) {
            bPoderes[objBloque.getPoder()] = true;
        }
            // Checa si el proyectil choca contra los bloques
            if (objBloque.colisiona(objProyectil)) {
                iScore++; // Se aumenta en 1 el score
                iNumBloques--; //Se resta el numero de bloques
                //Se activa el bloque con el poder para que se mueva para abajo
                if (objBloque.getPoder() != 0) {
                    URL urlImagenPoder
                            = this.getClass().getResource("metanfeta.png");
                    objBloque.setImagen(Toolkit.getDefaultToolkit()
                            .getImage(urlImagenPoder));
                    objBloque.setVelocidad(2);
                    bAvanzaBloque = true;
                }
                if(objProyectil.colisiona(objBloque.getX(), 
                        objBloque.getY()) ||
                        objProyectil.colisiona(objBloque.getX(), 
                                objBloque.getY()+objBloque.getAlto())) {
                    objBloque.setX(getWidth() + 50);
                    bDireccionX = false; //va hacia arriba
                }
                if(objProyectil.colisiona(objBloque.getX()+objBloque.getAncho(), 
                        objBloque.getY()) ||
                        objProyectil.colisiona(objBloque.getX()+objBloque.getAncho(), 
                                objBloque.getY()+objBloque.getAlto())) {
                    objBloque.setX(getWidth() + 50);
                    bDireccionX = true; //va hacia arriba
                }
                //Si la parte superior de proyectil es mayor o igual a la parte
                //inferior del bloque(esta golpeando por abajo del bloque...
                if((objProyectil.getY() <= objBloque.getY() 
                        + objBloque.getAlto()) && (objProyectil.getY() 
                        + objProyectil.getAlto() > objBloque.getY() 
                        + objBloque.getAlto())) {
                    objBloque.setX(getWidth() + 50);
                    bDireccionY = true; //va hacia abajo
                    
                    
                }
                //parte inferior del proyectil es menor o igual a la de la parte
                //superior del bloque(esta golpeando por arriba)...
                else if(( objProyectil.getY() + objProyectil.getAlto()
                        >= objBloque.getY())&&( objProyectil.getY() 
                        < objBloque.getY())) {
                    objBloque.setX(getWidth() + 50);
                    bDireccionY = false; //va hacia arriba
                }
                //Si esta golpeando por algun otro lugar (los lados)...
                else {
                    objBloque.setX(getWidth()+50);
                    bDireccionX = !bDireccionX;
                }
        }
        }
        //Si la barra choca con el lado izquierdo...
        if (objBarra.getX() < 0) {
            objBarra.setX(0); //Se posiciona al principio antes de salir
        } //Si toca el lado derecho del Jframe...
        else if (objBarra.getX() + objBarra.getAncho() - objBarra.getAncho() / 18
                > getWidth()) {
            objBarra.setX(getWidth() - objBarra.getAncho() + objBarra.getAncho()
                    / 18);// Se posiciciona al final antes de salir
        }
        //Si el Proyectil choca con cualquier limite de los lados...
        if (objProyectil.getX() < 0 || objProyectil.getX()
                + objProyectil.getAncho() > getWidth()) {
            //Cambias su direccion al contrario
            bDireccionX = !bDireccionX;
        } //Si el Proyectil choca con la parte superior del Jframe...
        else if (objProyectil.getY() < 0) {
            //Cambias su direccion al contrario
            bDireccionY = !bDireccionY;
        } //Si el proyectil toca el fondo del Jframe...
        else if (objProyectil.getY() + objProyectil.getAlto() > getHeight()) {
            iVidas--; //Se resta una vida.
            // se posiciona el proyectil en el centro arriba de barra
            objProyectil.reposiciona((objBarra.getX() + objBarra.getAncho() / 2
                    - (objProyectil.getAncho() / 2)), (objBarra.getY()
                    - objProyectil.getAlto()));
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
        if (lnkBloques != null && objBarra != null && objProyectil != null
                && bEmpieza) {
            //Si se acabaron las vidas o los bloques
            if (iVidas <= 0) {
                //Creo imagen de game over
                URL urlImagenFin
                        = this.getClass().getResource("finalBB2.jpg");
                Image imaImagenFin
                        = Toolkit.getDefaultToolkit().getImage(urlImagenFin);
                //Despliego la imagen
                graGraficaApplet.drawImage(imaImagenFin, 0, 0, getWidth(),
                        getHeight(), this);
            } else { //si el juego sigue corriendo
                //Dibuja la imagen de la barra en la posicion actualizada
                graGrafico.drawImage(objBarra.getImagen(),
                        objBarra.getX(), objBarra.getY(), this);

                //Dibuja la imagen del proyectil en la posicicion actualizada
                graGrafico.drawImage(objProyectil.getImagen(),
                        objProyectil.getX(), objProyectil.getY(), this);

                //Dibuja la lista de bloques en la posicion actualizada
                for (Object objeBloque : lnkBloques) {
                    Objeto objBloque = (Objeto) objeBloque;
                    graGrafico.drawImage(objBloque.getImagen(),
                            objBloque.getX(), objBloque.getY(), this);
                }

                //Se despliega el score y las vidas
                //Se crea estilo de Font para el texto
                Font strTexto = new Font("SansSerif", Font.PLAIN, 20);
                //Se asigna este estilo de Font al grafico
                graGrafico.setFont(strTexto);
                // Se cambia el color a blanco para el texto
                graGrafico.setColor(Color.WHITE);
                //Muestra el Score
                graGrafico.drawString("Score: " + iScore, 0, 50);
                //Muestra la cantidad de vidas
                graGrafico.drawString("Vidas: " + iVidas, (getWidth() / 3),
                        50);
                graGrafico.drawString("Nivel: " + iNivel, (getWidth()
                        - (getWidth() / 3)), 50);
            }
        } else {
            //Creo la imagen de inicio
            URL urlImagenInicio
                    = this.getClass().getResource("inicioBB.jpg");
            Image imaImagenInicio
                    = Toolkit.getDefaultToolkit().getImage(urlImagenInicio);
            //Despliego la imagen
            graGraficaApplet.drawImage(imaImagenInicio, 0, 0, getWidth(),
                    getHeight(), this);
        }
    }

    /**
     * Metodo que lee a informacion de un archivo y lo agrega a un vector.
     *
     * @throws IOException
     */
    public void leeArchivo() throws IOException {
        // defino el objeto de Entrada para tomar datos
        BufferedReader brwEntrada;
        try {
            // creo el objeto de entrada a partir de un archivo de texto
            brwEntrada = new BufferedReader(new FileReader("datos.txt"));
        } catch (FileNotFoundException e) {
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
     * En este metodo maneja el evento que se genera al presionar una tecla que
     * no es de accion.
     *
     * @param e es el <code>evento</code> que se genera en al presionar.
     *
     */
    public void keyTyped(KeyEvent keEvent) {
    }

    /**
     * keyPressed
     *
     * Metodo sobrescrito de la interface <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al dejar presionada alguna
     * tecla.
     *
     * @param keyEvent es el <code>evento</code> generado al presionar.
     *
     */
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == keyEvent.VK_LEFT) {
            objBarra.izquierda();
        }
        if (keyEvent.getKeyCode() == keyEvent.VK_RIGHT) {
            objBarra.derecha();
        }
    }

    /**
     * keyReleased
     *
     * Metodo sobrescrito de la interface <code>KeyListener</code>.<P>
     * En este metodo maneja el evento que se genera al soltar la tecla.
     *
     * @param keyEvent es el <code>evento</code> que se genera en al soltar las
     * teclas.
     */
    public void keyReleased(KeyEvent keyEvent) {
        // Si se da click en la tecla S - Start
        if (keyEvent.getKeyCode() == keyEvent.VK_S) {
            if (!bEmpieza) {
                bEmpieza = true; // Empieza el juego
                bPausado = false; // Se despausa el juego
            }
        }
        // Si se da click la tecla P - Pausa/DesPausa el juego
        if (keyEvent.getKeyCode() == KeyEvent.VK_P) {
            // la variable de pausa si esta en false cambia a true y viceversa
            bPausado = !bPausado;
        }
        // Si se da click la tecla R - Reset el juego
        if (keyEvent.getKeyCode() == KeyEvent.VK_R) {
            // se despausa el juego para que vuelva a empezar
            if (bPerdio) {
                reinicio();
                bPerdio = false;
            }
        }
    }

    /**
     * acomodaBloques
     *
     * Metodo usado para crear una forma con los objetos tipo Objeto en una
     * LinkedList del <code>JFrame</code>.
     *
     */
    public void acomodaBloques() throws IOException {
        try { // checa si encontro el archivo
            // se lee el archivo
            bfrEntrada = new BufferedReader(new FileReader("coordenadas.txt"));
        } catch (FileNotFoundException fnfEx) { // si no lo encuentra
            // Se crea el archivo con los datos iniciales del juego
            PrintWriter prwSalida
                    = new PrintWriter(new FileWriter("coordenadas.txt"));
            prwSalida.println("29,174");
            prwSalida.println("62,182");
            prwSalida.println("95,70");
            prwSalida.println("95,127");
            prwSalida.println("95,184");
            prwSalida.println("128,72");
            prwSalida.println("128,129");
            prwSalida.println("128,186");
            prwSalida.println("161,76");
            prwSalida.println("161,131");
            prwSalida.println("161,188");
            prwSalida.println("194,78");
            prwSalida.println("194,133");
            prwSalida.println("194,190");
            prwSalida.println("227,78");
            prwSalida.println("227,133");
            prwSalida.println("227,190");
            prwSalida.println("260,76");
            prwSalida.println("260,131");
            prwSalida.println("260,188");
            prwSalida.println("293,72");
            prwSalida.println("293,129");
            prwSalida.println("293,186");
            prwSalida.println("326,70");
            prwSalida.println("326,127");
            prwSalida.println("326,184");
            prwSalida.println("359,182");
            prwSalida.println("392,174");
            prwSalida.println("95,267");
            prwSalida.println("128,267");
            prwSalida.println("161,267");
            prwSalida.println("194,286");
            prwSalida.println("227,286");
            prwSalida.println("260,267");
            prwSalida.println("293,267");
            prwSalida.println("326,267");
            prwSalida.println("100,324");
            prwSalida.println("133,324");
            prwSalida.println("166,324");
            prwSalida.println("255,324");
            prwSalida.println("288,324");
            prwSalida.println("321,324");
            prwSalida.println("111,440");
            prwSalida.println("144,411");
            prwSalida.println("177,411");
            prwSalida.println("210,425");
            prwSalida.println("243,411");
            prwSalida.println("276,411");
            prwSalida.println("309,440");
            prwSalida.println("144,468");
            prwSalida.println("177,497");
            prwSalida.println("210,516");
            prwSalida.println("243,497");
            prwSalida.println("276,468");
            prwSalida.close();
            // se lee el archivo
            bfrEntrada = new BufferedReader(new FileReader("coordenadas.txt"));
        }
        // se llena la lista de bloques
        for (int iI = 0; iI < iNumBloques; iI++) {
            // se carga la imagen del alien corredor
            URL urlImagenCaminador
                    = this.getClass().getResource("barrilBB.png");
            // se crea un alien corredor
            Objeto objBloque = new Objeto(0, 0,
                    Toolkit.getDefaultToolkit().getImage(urlImagenCaminador));

            // Se lee la primera linea (vidas)
            String sDato = bfrEntrada.readLine();
            // se dividen los datos en un arreglo
            strArrDatos = sDato.split(",");
            // se asigna el primer dato a la posX
            objBloque.setX((Integer.parseInt(strArrDatos[0])));
            // se asigna el segundo dato a la posX
            objBloque.setY((Integer.parseInt(strArrDatos[1])));
            // Se agrega al caminador a la lista de corredores
            lnkBloques.add(objBloque);
        }
        //Se le agrega poderes a bloques al azar
        //Se le agrega poderes a bloques al azar
        for(int iI=0; iI<lnkBloques.size(); iI++) {
            if(1 == ((int) (1 + Math.random() * 2))) {
                Objeto objPoderosos = (Objeto) lnkBloques.get((int) 
                        (1 + Math.random() * 50));
                objPoderosos.setPoder((int) (1 + Math.random() * 3));
            }
            
        }
    }

    /**
     * reinicio
     *
     * Metodo usado para reiniciar el juego en el <code>JFrame</code>.
     *
     */
    public void reinicio() {
        iNumBloques = 54; // se reinicia la cantidad de bloques
        iNivel = 1; // vuelve al nivel 1
        iScore = 0; // se reinicia el score
        iVidas = 3; // se reinicia la cantidad de vidas
        // La direccion del proyectil sera para arriba
        bDireccionY = false;
        // la direccion del proyectil sera al contrario de donde iba
        bDireccionX = !bDireccionX;
        // Se reposiciona el proyectil
        objProyectil.reposiciona((objBarra.getX() + objBarra.getAncho() / 2
                - (objProyectil.getAncho() / 2)), (objBarra.getY()
                - objProyectil.getAlto()));
        // Se reincia la velocidad del proyectil
        objProyectil.setVelocidad(5);
        // Se reposiciona la barra
        objBarra.reposiciona(((getWidth() / 2)
                - (objProyectil.getAncho() / 2)), (getHeight()
                - objBarra.getAlto()));
        // se limpia la lista de bloques
        lnkBloques.clear();
        // se vuelve a llenar y se acomoda
        try {
            acomodaBloques();
        } catch (IOException ioeError) {
            System.out.println("Hubo un error al cargar el juego: "
                    + ioeError.toString());
        }
    }
}
