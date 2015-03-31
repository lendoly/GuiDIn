package com.example.jorge.guidin.wps;

import java.util.ArrayList;
import android.content.Context;
import com.example.jorge.guidin.posicion.Mapa;


public class ParticleFilter {
    private static final int numParticulas = 1000;
    private static final double sigma = 0.5;

    private ArrayList<Particle> particulas;

    /**
     * Mapa con las zonas permitidas en blanco
     */
    //private Bitmap mapaMascara;

    /**
     * Tiempo actual
     */
    private double time;

    private WPSDatabase database;

    public final static int colorBlanco = -1025;

    public ParticleFilter(double x, double y, WPSDatabase database, Context context){
        setParticulas(new ArrayList<Particle>());
        this.database = database;
        Particle p;
        double randomX;
        double randomY;
        for(int i = 0; i < numParticulas; i++){
            p = new Particle();
            p.setWeight(1/numParticulas);
            randomX = Math.random();
            randomY = Math.random();
            if(randomX < 0.5){
                p.setX(x + randomX*0.5);
            }else{
                p.setX(x - randomX*0.5);
            }
            if(randomY < 0.5){
                p.setY(y + randomY*0.5);
            }else{
                p.setY(y - randomY*0.5);
            }

            p.setVelocityX(0);
            p.setVelocityY(0);
            particulas.add(p);
        }

    }

    /**
     * Método que realiza el paso de predicción, es decir, predice la nueva posición y velocidad de las partículas
     * @param accX aceleración del terminal en las coordenadas X del mapa, dado por el acelerómetro
     * @param accY aceleración del terminal en las coordenadas Y del mapa, dado por el acelerómetro
     * @param time segundos actuales
     */
    public void prediction(double accX, double accY, double time){

        double incTime;

        if(this.time == 0){
            incTime = 0;
        }else{
            incTime = time - this.time;
        }
        double incTimePow = (Math.pow(incTime, 2))/2;
        Particle p;
        double x_old;
        double y_old;
        for(int i = 0; i < particulas.size(); i++){
            p = particulas.get(i);
            /**
             * Xk = Xk-1 + At*vx + At2/2*ax
             */
            x_old = p.getX();
            y_old = p.getY();
            p.setX(/*Math.round(*/p.getX() + incTime*p.getVelocityX() + incTimePow*accX/*)*/);
            p.setY(/*Math.round(*/p.getY() + incTime*p.getVelocityY() + incTimePow*accY/*)*/);
            p.setVelocityX(p.getVelocityX() + incTime*accX);
            p.setVelocityY(p.getVelocityY() + incTime*accY);
            p.setProb_Xk_Xk1(getP_Xk_Xk1(x_old,y_old,p.getX(),p.getY()));
        }
        this.time = time;
    }

    /**
     * Método que corrije las probabilidades de las partículas que definen la probabilidad
     * de que esté en la posición dada por el algoritmo k-closest neibourghs.
     * @param c posición devuelta por el algoritmos de posicionamiento estándar
     */
    public void correction(Coordenada c){
        Particle p;
        double probBase =  1 / (Math.sqrt(2*Math.PI)*sigma);
        double exp;
        for(int i = 0; i < particulas.size(); i++){
            p = particulas.get(i);
            exp = (Math.pow(p.getX() - c.getX(),2) + Math.pow(p.getY() - c.getY(), 2))/(2*sigma);
            p.setProb_Zk_Xk(Math.pow(probBase, exp));
        }
    }

    /**
     * Actualiza los pesos de las partículas dependiendo de las probabilidades anteriormente calculadas
     */
    public void particleUpdate(){
        Particle p;
        for(int i = 0; i < particulas.size(); i++){
            p = particulas.get(i);
            p.setWeight(p.getWeight()* p.getProb_Xk_Xk1() * p.getProb_Zk_Xk());
        }
    }

    /**
     * Remuestrea las partículas en el caso de que hayan perdido mucho peso.
     */
    public void resampling(){
        double sum = 0;
        for(int i = 0; i < particulas.size(); i++){
            sum += Math.pow(particulas.get(i).getWeight(),2);
        }
        sum = 1/sum;

        if(sum < 0.5){
            ArrayList<Particle> particulasNuevas = new ArrayList<Particle>();
            quicksort(0,particulas.size()-1);
            int mitad = numParticulas/2;
            Particle p;
            for(int i = 0; i < numParticulas; i++){
                p = particulas.get(i % mitad).clone();
                p.setWeight(1/numParticulas);
                particulasNuevas.add(p);
            }
            if(particulasNuevas.size()<numParticulas){
                p = particulas.get(0).clone();
                p.setWeight(1/numParticulas);
                particulasNuevas.add(p);
            }
            this.particulas = particulasNuevas;
        }
    }

    /**
     * Ordena el vector de partículas por peso.
     * @param primero
     * @param ultimo
     */
    public void quicksort(int primero, int ultimo){

        int i=primero, j=ultimo;
        //int pivote=vector[(primero+ultimo)/2];
        double pivote = (particulas.get(primero).getWeight() + particulas.get(ultimo).getWeight())/2;
        Particle auxiliar;

        do{
            while(particulas.get(i).getWeight()<pivote) i++;
            while(particulas.get(j).getWeight()>pivote) j--;

            if (i <= j){
                auxiliar = particulas.get(j);
                particulas.set(j, particulas.get(i));
                particulas.set(i, auxiliar);
                i++;
                j--;
            }

        } while (i<=j);

        if(primero<j) quicksort(primero, j);
        if(ultimo>i) quicksort(i, ultimo);

    }

    /**
     * Calcula la probabilidad P de que la particula esté en una nueva posición a partir de la anterior.
     * Si atraviesa una pared le asigna una probabilidad de 0, si no de 1
     * @param x_old posición en metros de la x antigua;
     * @param y_old posición en metros de la y antigua;
     * @param x_new posición en metros de la x nueva;
     * @param y_new posición en metros de la y nueva;
     * @return
     */
    public int getP_Xk_Xk1(double x_old1, double y_old1, double x_new1, double y_new1){

        long x_old = Math.round(x_old1 * Mapa.escalaMetro + Mapa.POSX_0_MAPA);
        long x_new = Math.round(x_new1 * Mapa.escalaMetro + Mapa.POSX_0_MAPA);
        long y_old = Math.round(y_old1 * Mapa.escalaMetro + Mapa.POSY_0_MAPA);
        long y_new = Math.round(y_new1 * Mapa.escalaMetro + Mapa.POSY_0_MAPA);

        long incX = Math.abs(x_new - x_old);
        boolean positivoX = x_new >= x_old;
        boolean positivoY = y_new >= y_old;
        long incY = Math.abs(y_new - y_old);

        double rel;
        long xPincel = x_old;
        long yPincel = y_old;
        //double dist_hipotenusa = Math.sqrt(Math.pow(incX,2) + Math.pow(incY, 2));
        double acc = 0;
        boolean flag = false;
//		int color;
        boolean fin = false;
        @SuppressWarnings("unused")
        int blue;
        @SuppressWarnings("unused")
        int red;
        @SuppressWarnings("unused")
        int green;

        if(incX>incY){
            if(incX!=0){
                rel = (double)incY/(double)incX;
            }else{
                rel = 0;
            }
            while(!fin && !flag){
                if(xPincel==x_new){
                    if(positivoY){
                        yPincel = yPincel + 1;
                    }else{
                        yPincel = yPincel - 1;
                    }
                }else{
                    if(positivoX) xPincel = xPincel + 1;
                    else xPincel = xPincel - 1;
                    acc = acc + rel;
                    if(acc>=1){
                        if(positivoY){
                            yPincel = yPincel + 1;
                            acc = acc - 1;
                        }
                        else{
                            yPincel = yPincel - 1;
                            acc = acc - 1;
                        }
                    }
                }
//				color = ThreadUbicacion.mapaMascara.getPixel(60,220);
//				color = ThreadUbicacion.mapaMascara.getPixel(124,187);
//				color = ThreadUbicacion.mapaMascara.getPixel((int)xPincel, (int)yPincel);
//				blue = Color.blue(color);
//				red = Color.red(color);
//				green = Color.green(color);
//				if(ThreadUbicacion.mapaMascara.getPixel((int)xPincel, (int)yPincel) != colorBlanco){
//					flag = true;
//				}
                if(xPincel==x_new && yPincel==y_new) fin = true;
            }
        }else{
            if(incY!=0){
                rel = (double)incX/(double)incY;
            }else{
                rel = 0;
            }
            while(!fin && !flag){
                if(yPincel==y_new){
                    if(positivoX){
                        xPincel = xPincel + 1;
                    }else{
                        xPincel = xPincel - 1;
                    }
                }else{
                    if(positivoY) yPincel = yPincel + 1;
                    else yPincel = yPincel - 1;
                    acc = acc + rel;
                    if(acc>=1){
                        if(positivoX){
                            xPincel = xPincel + 1;
                            acc = acc - 1;
                        }
                        else{
                            xPincel = xPincel - 1;
                            acc = acc - 1;
                        }
                    }
                }
//				color = ThreadUbicacion.mapaMascara.getPixel(60,220);
//				color = ThreadUbicacion.mapaMascara.getPixel((int)xPincel, (int)yPincel);
//				if(ThreadUbicacion.mapaMascara.getPixel((int)xPincel, (int)yPincel) != colorBlanco){
//					flag = true;
//				}
                if(xPincel==x_new && yPincel==y_new) fin = true;
            }
        }

        if(flag) return 0;
        else return 1;
    }

    public Coordenada stimation(Coordenada c){
        ArrayList<Coordenada> coordenadas = database.getClosetsCoords(c);
        double resX = 0;
        double resY = 0;
        double prob;
        Coordenada result = new Coordenada();
        for(int i = 0; i < coordenadas.size(); i++){
            prob = calculatePXkZ0k(coordenadas.get(i));
            resX += coordenadas.get(i).getX()*prob;
            resY += coordenadas.get(i).getY()*prob;
        }
        result.setX(redondear(resX));
        result.setY(redondear(resY));
        result.setZ(1);
        return result;
    }

    public int deltaDirac(double val){
        if(val==0){
            return Integer.MAX_VALUE;
        }else{
            return 0;
        }
    }

    public double calculatePXkZ0k(Coordenada pos){
        double result = 0;
        Coordenada c;
        double aux;
        pos.setX(redondear(pos.getX()));
        pos.setX(redondear(pos.getY()));
        pos.setZ(1);

        for(int i = 0; i < particulas.size(); i++){
            c = new Coordenada();
            c.setX(redondear(particulas.get(i).getX()));
            c.setY(redondear(particulas.get(i).getY()));
            c.setZ(1);
            if(c.equals(pos)) aux = 0;
            else aux = 1;
            result += particulas.get(i).getWeight()*deltaDirac(aux);
        }
        return result;
    }


    public void setParticulas(ArrayList<Particle> particulas) {
        this.particulas = particulas;
    }

    public ArrayList<Particle> getParticulas() {
        return particulas;
    }

    /**
     * Trunca el valor por parámetro de la siguiente manera: si la parte decimal es menor que 0.25 la elimina,
     * si está entre 0.25 y 0.75 la estima como 0.5 y si es mayor que 0.75 la estima como 1.
     * @param d
     * @return
     */
    private double redondear(double d){
        int aux = (int)d;
        double aux2 = d - aux;
        if(aux2<0.25){
            return aux;
        }else if(aux2 >=0.25 && aux2 < 0.5 || aux2>=0.5 && aux2 <= 0.75){
            return aux + 0.5;
        }else return aux + 1;
    }

}
