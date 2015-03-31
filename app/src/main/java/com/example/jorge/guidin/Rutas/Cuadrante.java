package com.example.jorge.guidin.rutas;


public class Cuadrante {
    private int ID;
    private Posicion posNW;
    private Posicion posSE;
    private boolean nodoSalida;

    public Cuadrante(){
        ID = 0;
        posNW = new Posicion();
        posSE = new Posicion();
        nodoSalida = false;
    }

    public Cuadrante(int id, Posicion nw, Posicion se, boolean b){
        ID = id;
        nodoSalida = b;
        posNW = nw;
        posSE = se;
    }

    public Cuadrante(Cuadrante c){
        ID = c.getID();
        nodoSalida = c.isNodoSalida();
        posNW = c.getPosNW();
        posSE = c.getPosSE();
    }

    public boolean equals(Object o){
        Cuadrante c = (Cuadrante)o;
        if ( (this.ID==c.getID())&&(posNW.equals(c.getPosNW()))&&(posSE.equals(c.getPosSE())) )
            return true;
        else return false;
    }

    public Cuadrante clone(){
		/*
		Cuadrante c = new Cuadrante();
		c.setID(this.ID);
		c.setNodoSalida(this.nodoSalida);
		c.setPosNW(this.posNW);
		c.setPosSE(this.posSE);
		return c;
		*/
        return new Cuadrante(this.ID, this.posNW, this.posSE, this.isNodoSalida());
    }

    /**
     * Compureba si una posición forma parte del cuadrante.
     * @param p Posición
     * @return True si la posición p está dentro del cuadrante.
     */
    public boolean pertenece(Posicion p){
        double xIzq = posNW.getPX();
        double xDcha = posSE.getPX();
        double ySup = posNW.getPY();
        double yInf = posSE.getPY();
        if ((xIzq <= p.getPX()) && (p.getPX() < xDcha) && (ySup > p.getPY()) && (p.getPY() >= yInf))
            return true;
        else return false;
    }

    public boolean pertenece(double pX, double pY){
        Posicion p = new Posicion(pX, pY);
        return pertenece(p);
    }

    public int getID() {
        return ID;
    }

    public void setID(int id) {
        ID = id;
    }

    public Posicion getPosNW() {
        return posNW;
    }

    public void setPosNW(Posicion posNW) {
        this.posNW = posNW;
    }

    public Posicion getPosSE() {
        return posSE;
    }

    public void setPosSE(Posicion posSE) {
        this.posSE = posSE;
    }

    public boolean isNodoSalida() {
        return nodoSalida;
    }

    public void setNodoSalida(boolean nodoSalida) {
        this.nodoSalida = nodoSalida;
    }


}
