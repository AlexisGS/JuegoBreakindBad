/* 
 * Juego
 * Juego tipo "Brick Breaker" de Atari con tematica de la serie "Breaking Bad"
 * de ACM. El objetivo del juego es ayudar a Hawk a acabar el contrabando de 
 * Walter destruyendo sus metanfetaminas
 * 
 */

/**
 * @version 1.00 31/09/2014
 * @author
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

public class Juego extends JFrame implements KeyListener, Runnable, 
        MouseListener {
    //Variables empleadas por Juego
    private int iVidas; //cantidad de oportunidades que tiene el jugador
    private int iScore; //cantidad de puntos acumulados por el jugador
    boolean bPausado; //indica si el juego esta en pausa o no
    private Objeto oBarra; //representa la barra controlada por el jugador
    private Objeto oProyectil; //representa el proyectil que destruye bloques
    private LinkedList lnkBloques; //Lista de bloques a destruir
    private SoundClip souSonidoChoqueBloque; // Sonido que emitiran al chocar los
            //aliens caminadore con Nena
    private SoundClip souSonidoChoqueBarra; // Sonido que emitiran al chocar los
            //aliens corredores con Nena
    
    
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
        iVidas = 3; //El juego comienza con tres oportunidades
        
        iScore = 0; //El juego comienza con 0 puntos
        
        bPausado = false;//El juego no empieza pausado.
        
        // hago el applet de un tamaño 800, 600
        setSize(800, 600);
        
        // se carga la imagen para la barra golpeadora
        URL urlImagenBarra = this.getClass().getResource("barra neon.gif");
        // se crea al objeto Barra de la clase objeto
        oBarra = new Objeto(0, 0,
                Toolkit.getDefaultToolkit().getImage(urlImagenBarra));
        // se posiciona la barra en la parte inferior en el centro
        oBarra.setX((getWidth() / 2) - (oBarra.getAncho() / 2));
        oBarra.setY(getHeight() - oBarra.getAlto());
        
        // se carga la imagen para la barra golpeadora
        URL urlImagenProyectil = this.getClass().getResource("barra neon.gif");
        // se crea al objeto Proyectil de la clase objeto
        oProyectil = new Objeto(0, 0,
                Toolkit.getDefaultToolkit().getImage(urlImagenBarra));
        // se posiciona el proyectil en el centro arriba de barra
        oProyectil.setX((getWidth() / 2) - (oProyectil.getAncho() / 2));
        oProyectil.setY(getHeight() - oProyectil.getAlto());
        
        // se crea la lista de Bloques a destruir
        lnkBloques = new LinkedList();
        // se asigna un numero random de 10 a 12 para la cantidad de bloques
        int iRandom = (int) (1 + Math.random() * 2);
        // se llena la lista de aliens caminadores
        for (int iI = 0; iI < 20; iI++) {
            // se carga la imagen del alien caminador
            URL urlImagenCaminador
                    = this.getClass().getResource(iRandom+".png");
            // se crea un bloque
            Objeto oBloque = new Objeto(0, 0,
                    Toolkit.getDefaultToolkit().getImage(urlImagenCaminador));
            // se posiciona al caminador al azar en X
            oBloque.setX((int) (Math.random() * (getWidth()
                    - oBloque.getAncho())));
            // se posiciona al caminador al azar por arriba del applet
            oBloque.setY((int) (Math.random() * (0
                    - oBloque.getAlto() * 2)));
            // Se agrega al caminador a la lista de caminadores
            lnkBloques.add(oBloque);
            
        // se crea el sonido para los aliens caminadores
        souSonidoChoqueBarra = new SoundClip("ChoqueBarra.wav");

        // se crea el sonido para los aliens corredores
        souSonidoChoqueBloque = new SoundClip("ChoqueBloque.wav");
        }
        
        /* se le añade la opcion al applet de ser escuchado por los eventos
        /* del mouse  */
        addMouseListener(this);
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
        // se realiza el ciclo del juego hasta que se terminen las vidas
        while (true) {
            /* mientras dure el juego, se actualizan posiciones de objetos
             se checa si hubo colisiones para desaparecer objetos o corregir
             movimientos y se vuelve a pintar todo
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
     * Metodo que actualiza la posicion del objeto elefante
     *
     */
    public void actualiza() {
    }
    
    /**
     * checaColision
     *
     * Metodo usado para checar la colision de los objetos entre ellos y
     * con las orillas del <code>Applet</code>.
     *
     */
    public void checaColision() {
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
        URL urlImagenFondo = this.getClass().getResource("espacio.jpg");
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
        // Si las imagenes de Nena y los aliens ya se cargaron
        if (lnkBloques!= null&&oBarra!=null&&oProyectil!=null) {
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

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
    
    
}
