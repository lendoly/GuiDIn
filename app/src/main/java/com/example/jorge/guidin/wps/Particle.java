package com.example.jorge.guidin.wps;


public class Particle {

    /**
     * En metros
     */
    private double x;

    /**
     * En metros
     */
    private double y;
    private double velocityX;
    private double velocityY;
    private double weight;
    /**
     * Probabilidad de Zk dado Xk (posición de la particula en el momento K)
     */
    private double prob_Zk_Xk;

    /**
     * Probabilidad de que la particula esté en Xk dado Xk-1
     */
    private double prob_Xk_Xk1;

    public Particle(){
        x = 0;
        y = 0;
        velocityX = 0;
        velocityY = 0;
        weight = 0;
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

    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getProb_Zk_Xk() {
        return prob_Zk_Xk;
    }

    public void setProb_Zk_Xk(double prob_Zk_Xk) {
        this.prob_Zk_Xk = prob_Zk_Xk;
    }

    public double getProb_Xk_Xk1() {
        return prob_Xk_Xk1;
    }

    public void setProb_Xk_Xk1(double prob_Xk_Xk1) {
        this.prob_Xk_Xk1 = prob_Xk_Xk1;
    }

    public Particle clone(){
        Particle p = new Particle();
        p.setX(getX());
        p.setVelocityX(getVelocityX());
        p.setY(getY());
        p.setVelocityY(getVelocityY());
        p.setWeight(getWeight());
        p.setProb_Xk_Xk1(getProb_Xk_Xk1());
        p.setProb_Zk_Xk(getProb_Zk_Xk());
        return p;
    }


}
