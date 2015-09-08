package com.example.jorge.guidin.wps;

/**
 * Created by Jorge on 19/03/2015.
 */


/**
 * Objeto que se utiliza para guardar las coordenadas (x,y,z), la MAC y la fuerza de la señal
 *
 * @version 1.0
 * @author Javier
 *
 */
public class Coordenada {

    private double x;
    private double y;
    private double z;
    private String mac;
    private int strength;
    /**
     * Atributo que sirve para almacenar la distancia a otro punto de referencia.
     */
    private double distanciaAlmacenada;

    public Coordenada(){
        x = 0;
        y = 0;
        z = 0;
        mac = "";
        strength = 0;
    }

    public Coordenada(double x, double y, double z, String mac, int strength){
        this.x = x;
        this.y = y;
        this.z = z;
        this.mac = mac;
        this.strength = strength;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setDistanciaAlmacenada(double distanciaAlmacenada) {
        this.distanciaAlmacenada = distanciaAlmacenada;
    }

    public double getDistanciaAlmacenada() {
        return distanciaAlmacenada;
    }

    public boolean equals(Object o){
        Coordenada c = (Coordenada)o;
        if(this.x == c.getX() && y == c.getY() && z == c.getZ()/* && mac.equals(c.getMac())*/){
            return true;
        }else{
            return false;
        }
    }

    public Coordenada clone(){
        Coordenada c = new Coordenada();
        c.setDistanciaAlmacenada(distanciaAlmacenada);
        c.setMac(new String(mac));
        c.setStrength(strength);
        c.setX(x);
        c.setY(y);
        c.setZ(z);
        return c;
    }

}
