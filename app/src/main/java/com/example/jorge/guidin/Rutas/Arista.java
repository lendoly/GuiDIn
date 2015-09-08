package com.example.jorge.guidin.rutas;


public class Arista {

    private int origen;
    private int destino;
    private int direccion;

    public static final int UP = 1;
    public static final int UP_RIGHT = 2;
    public static final int RIGHT = 3;
    public static final int DOWN_RIGHT = 4;
    public static final int DOWN = 5;
    public static final int DOWN_LEFT = 6;
    public static final int LEFT = 7;
    public static final int UP_LEFT = 8;


    public Arista(){
        origen = 0;
        destino = 0;
        direccion = 0;
    }

    public Arista(int o, int d, int dir){
        this.origen = o;
        this.destino = d;
        this.direccion = dir;
    }

    public Arista(Arista a){
        this.origen = a.origen;
        this.destino = a.destino;
        this.direccion = a.direccion;
    }

    public static int opuesto(int direccion){
        int dirOpuesta = 0;
        switch(direccion){
            case Arista.UP:
                dirOpuesta= Arista.DOWN;
                break;
            case Arista.UP_RIGHT:
                dirOpuesta = Arista.DOWN_LEFT;
                break;
            case Arista.RIGHT:
                dirOpuesta = Arista.LEFT;
                break;
            case Arista.DOWN_RIGHT:
                dirOpuesta = Arista.UP_LEFT;
                break;
            case Arista.DOWN:
                dirOpuesta = Arista.UP;
                break;
            case Arista.DOWN_LEFT:
                dirOpuesta = Arista.UP_RIGHT;
                break;
            case Arista.LEFT:
                dirOpuesta = Arista.RIGHT;
                break;
            case Arista.UP_LEFT:
                dirOpuesta = Arista.DOWN_RIGHT;
                break;
        }
        return dirOpuesta;
    }

    public int getOrigen(){
        return origen;
    }

    public void setOrigen(int o){
        origen = o;
    }

    public int getDestino(){
        return destino;
    }

    public void setDestino(int d){
        destino = d;
    }

    public int getDireccion(){
        return direccion;
    }

    public void setDireccion(int dir){
        direccion= dir;
    }
}
