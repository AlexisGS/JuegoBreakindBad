/**
 * Clase Objeto
 *
 * Modela la definición de todos los objetos de tipo
 * <code>Objeto</code>
 *
 * @authors Alexis García Soria (A00813330) & Diego Mayorga (A00813211)
 * @version 1.00 17/09/2014
 * 
 */
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Rectangle;

public class Objeto {
    private int iX;     //posicion en x.       
    private int iY;     //posicion en y.
    private int iVelocidad;   // velocidad
    private ImageIcon imiIcono;	//icono.   
    
    /**
     * Objeto
     * 
     * Metodo constructor usado para crear el objeto tipo Objeto
     * creando el icono a partir de una imagen
     * 
     * @param iX es la <code>posicion en x</code> del objeto.
     * @param iY es la <code>posicion en y</code> del objeto.
     * @param imaImagen es la <code>imagen</code> del objeto.
     * 
     */
    public Objeto(int iX, int iY ,Image imaImagen) {
        this.iX = iX;
        this.iY = iY;
        imiIcono = new ImageIcon(imaImagen);
        this.iVelocidad = 1;   // default 1 en velocidad
    }
    
    /**
     * Objeto
     * 
     * Metodo constructor usado para crear el objeto tipo Objeto
     * creando el icono de imagen de un objeto igual
     * 
     * @param iX es la <code>posicion en x</code> del objeto.
     * @param iY es la <code>posicion en y</code> del objeto.
     * @param icoImagen es la <code>imagen tipo icono</code> del objeto.
     * 
     */
    public Objeto(int iX, int iY ,ImageIcon icoImagen) {
        this.iX = iX;
        this.iY = iY;
        imiIcono = icoImagen;
        this.iVelocidad = 1;    // default 1 en velocidad
    }
    
    /**
     * setX
     * 
     * Metodo modificador usado para cambiar la posicion en x del objeto
     * 
     * @param iX es la <code>posicion en x</code> del objeto.
     * 
     */
    public void setX(int iX) {
        this.iX = iX;
    }

    /**
     * getX
     * 
     * Metodo de acceso que regresa la posicion en x del objeto 
     * 
     * @return iX es la <code>posicion en x</code> del objeto.
     * 
     */
    public int getX() {
        return iX;
    }

    /**
     * setY
     * 
     * Metodo modificador usado para cambiar la posicion en y del objeto 
     * 
     * @param iY es la <code>posicion en y</code> del objeto.
     * 
     */
    public void setY(int iY) {
            this.iY = iY;
    }

    /**
     * getY
     * 
     * Metodo de acceso que regresa la posicion en y del objeto 
     * 
     * @return posY es la <code>posicion en y</code> del objeto.
     * 
     */
    public int getY() {
        return iY;
    }

    /**
     * setImageIcon
     * 
     * Metodo modificador usado para cambiar el icono del objeto
     * 
     * @param imiIcono es el <code>icono</code> del objeto.
     * 
     */
    public void setImageIcon(ImageIcon imiIcono) {
        this.imiIcono = imiIcono;
    }

    /**
     * getImageIcon
     * 
     * Metodo de acceso que regresa el icono del objeto 
     * 
     * @return imiIcono es el <code>icono</code> del objeto.
     * 
     */
    public ImageIcon getImageIcon() {
        return imiIcono;
    }

    /**
     * setImagen
     * 
     * Metodo modificador usado para cambiar el icono de imagen del objeto
     * tomandolo de un objeto imagen
     * 
     * @param imaImagen es la <code>imagen</code> del objeto.
     * 
     */
    public void setImagen(Image imaImagen) {
        this.imiIcono = new ImageIcon(imaImagen);
    }

    /**
     * getImagen
     * 
     * Metodo de acceso que regresa la imagen que representa el icono del objeto
     * 
     * @return la imagen a partide del <code>icono</code> del objeto.
     * 
     */
    public Image getImagen() {
        return imiIcono.getImage();
    }

    /**
     * setVelocidad
     * 
     * Metodo modificador usado para cambiar la velocidad del objeto 
     * 
     * @param iVelocidad es un <code>entero</code> con la velocidad del objeto.
     * 
     */
    public void setVelocidad(int iVelocidad) {
            this.iVelocidad = iVelocidad;
    }

    /**
     * getVelocidad
     * 
     * Metodo de acceso que regresa la velocidad del objeto 
     * 
     * @return iVelocidad un <code>entero</code> con velocidad del objeto.
     * 
     */
    public int getVelocidad() {
        return iVelocidad;
    }

    /**
     * getAncho
     * 
     * Metodo de acceso que regresa el ancho del icono 
     * 
     * @return un <code>entero</code> que es el ancho del icono.
     * 
     */
    public int getAncho() {
        return imiIcono.getIconWidth();
    }

    /**
     * getAlto
     * 
     * Metodo que  da el alto del icono 
     * 
     * @return un <code>entero</code> que es el alto del icono.
     * 
     */
    public int getAlto() {
        return imiIcono.getIconHeight();
    }
    
    /**
     * arriba
     * 
     * Metodo que sube al objeto de acuerdo a la velocidad
     * 
     */
    public void arriba() {
        this.setY(this.getY() - iVelocidad);
    }
    
    /**
     * abajo
     * 
     * Metodo que baja al objeto de acuerdo a la velocidad
     * 
     */
    public void abajo() {
        this.setY(this.getY() + iVelocidad);
    }
    
    /**
     * derecha
     * 
     * Metodo que mueve a la derecha al objeto de acuerdo a la velocidad
     * 
     */
    public void derecha() {
        this.setX(this.getX() + iVelocidad);
    }
    
    /**
     * izquierda
     * 
     * Metodo que mueve a la izquierda al objeto de acuerdo a la velocidad
     * 
     */
    public void izquierda() {
        this.setX(this.getX() - iVelocidad);
    }
    
    /**
     * reposiciona
     * 
     * Metodo que reposiciona al personaje en coordenadas especificas
     * 
     */
    public void reposiciona(int iX, int iY) {
        this.setX(iX);
        this.setY(iY);
    }
    
    /** 
     * colisiona
     * 
     * Metodo para revisar si un objeto <code>Objeto</code> colisiona con 
     * otro esto se logra con un objeto temporal de la clase 
     * <code>Rectangle</code>
     * 
     * @param objParametro es el objeto <code>Objeto</code> con el que se 
     * compara
     * @return  un valor true si esta colisionando y false si no
     * 
     */
    public boolean colisiona(Objeto objParametro) {
        // creo un objeto rectangulo a partir de este objeto
        Rectangle recObjeto = new Rectangle(this.getX(),this.getY(),
                this.getAncho(), this.getAlto());
        
        // creo un objeto rectangulo a partir del objeto parametro
        Rectangle recParametro = new Rectangle(objParametro.getX(),
                objParametro.getY(), objParametro.getAncho(),
                objParametro.getAlto());
        
        // si se colisionan regreso verdadero, sino regreso falso
        return recObjeto.intersects(recParametro);
    }
    
    /** 
     * colisiona
     * 
        * Metodo para revisar si un objeto <code>Objeto</code> colisiona con una
     * coordenada que tiene valor de x y valor de y
     * 
     * @param iX es el valor <code>entero</code> de x
     * @param iY es el valor <code>entero</code> de x
     * @return  un valor true si esta colisionando y false si no
     * 
     */
    public boolean colisiona(int iX, int iY) {
        // creo un objeto rectangulo a partir de este objeto
        Rectangle recObjeto = new Rectangle(this.getX(),this.getY(),
                this.getAncho(), this.getAlto());
               
        // si se colisionan regreso verdadero, sino regreso falso
        return recObjeto.contains(iX, iY);
    }    
}
