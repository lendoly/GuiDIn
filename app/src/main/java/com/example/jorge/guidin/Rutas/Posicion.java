package com.example.jorge.guidin.rutas;

public class Posicion {
    private double pX;
    private double pY;

    public Posicion(){
        pX = 0;
        pY = 0;
    }

    public Posicion(double x, double y){
        pX = x;
        pY = y;
    }

    public Posicion(Posicion p){
        pX = p.pX;
        pY = p.pY;
    }

    public Posicion clone(){
        Posicion p = new Posicion();
        p.setPX(pX);
        p.setPY(pY);
        return p;
    }

    public boolean equals(Object o){
        Posicion p = (Posicion)o;
        if ( (this.pX==p.getPX()) && (this.pY==p.getPY()) )
            return true;
        else return false;
    }

    public void modificar(double pX, double pY){
        this.pX = pX;
        this.pY = pY;
    }

    public double getPX(){
        return pX;
    }

    public double getPY(){
        return pY;
    }

    public void setPX(double x){
        pX = x;
    }

    public void setPY(double y){
        pY = y;
    }
}
