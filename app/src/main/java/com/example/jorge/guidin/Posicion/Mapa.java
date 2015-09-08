package com.example.jorge.guidin.posicion;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.jorge.guidin.R;
import com.example.jorge.guidin.rutas.Arista;
import com.example.jorge.guidin.rutas.Cuadrante;
import com.example.jorge.guidin.rutas.ListaCuadrantes;

public class Mapa extends View{

    private float x;
    private float y;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ImageView img;
    private Bitmap image;
    private double posX = Double.MAX_VALUE;
    private double posY = Double.MAX_VALUE;
    private double posZ = Double.MAX_VALUE;
    private int numMedidasIncorrectas = 0;
    private int cuadrante;
    private ListaCuadrantes listaCuadrantes;
    //	private FireList fireList = new FireList();
    private Paint mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * Coordenada X dentro del mapa que es en la BD la coordenadaX = 0;
     */
    public static final int POSX_0_MAPA = 42;
    public static final int POSX_ORIGEN_REAL=2100;
    public static final int POSY_ORIGEN_REAL=1060;

    /**
     * Coordenada Y dentro del mapa que es en la BD la coordenadaY = 0;
     */
    public static final int POSY_0_MAPA = 198;

    /**
     * Distancia en píxeles en el mapa de un metro en la realidad
     */
    //public static final double escalaMetro = 10.904225532;
    public static final double escalaMetro = 45;
    //public static final double escalaMetro = 1;

    public Mapa(Activity father) {
        super(father);
        //listaCuadrantes = ((Position)father).getCuadrantes();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(20);
        mPaint2.setColor(Color.BLACK);
        mPaint.setTextSize(16);
        this.setClickable(true);

        this.setOnTouchListener(new OnTouchListener(){

            float xTouch = 0;
            float yTouch = 0;
            float xInicial = 0;
            float yInicial = 0;
            //int xImageView = 0;
            //int yImageView = 0;


            public boolean onTouch(View arg0, MotionEvent arg1) {

                if(arg1.getEdgeFlags() == 0){
                    int action = arg1.getAction();
                    switch(action){
                        case MotionEvent.ACTION_DOWN:
                            xTouch = arg1.getX();
                            yTouch = arg1.getY();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            float xActual = arg1.getX();
                            float yActual = arg1.getY();
                            if((xActual!=xTouch) || (yActual!=yTouch)){

                                float x1 = xInicial - (xTouch - xActual);
                                float y1 = yInicial - (yTouch - yActual);
                                //if(x1 <= 10){ //&& x1 >= (image.getWidth())){
                                x = x1;
                                //}
                                //if(y1 <= 10){ //&& y1 >= (image.getHeight())){
                                y = y1;
                                //}

                                //AbsoluteLayout.LayoutParams layout = new AbsoluteLayout.LayoutParams(params.width,params.height,x1,y1);
                                //img.setLayoutParams(layout);
                                ((Mapa)arg0).invalidate();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            xInicial = x;
                            yInicial = y;
                            break;
                    }
                }

                return false;
            }

        });
        img = new ImageView(father);
        img.setImageResource(R.drawable.mapa);
        image = BitmapFactory.decodeResource(img.getResources(), R.drawable.mapa);

    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawBitmap(image, x, y, mPaint);
        //pintarCuadrantes(canvas);
//		pintarFuegos(canvas);
        if(posX==Double.MAX_VALUE && posY==Double.MAX_VALUE){
		/*
			canvas.drawText("x: INACCESIBLE" , 30, 230, mPaint);
			canvas.drawText("y: INACCESIBLE" , 30, 260, mPaint);
			canvas.drawText("Cuadrante: INACCESIBLE", 30, 290, mPaint);
		*/
            canvas.drawText("x: INACCESIBLE" , 20, 180, mPaint);
            canvas.drawText("y: INACCESIBLE" , 20, 200, mPaint);
            canvas.drawText("z: INACCESIBLE" , 20, 220, mPaint);
            canvas.drawText("Cuadrante: INACCESIBLE", 20, 240, mPaint);
            /* Prueba de colocar el punto en las cuatro esquinas del pasillo largo
            canvas.drawCircle((float)((POSX_ORIGEN_REAL-(45*Mapa.escalaMetro))+ Mapa.POSX_0_MAPA) + x, (float)((POSY_ORIGEN_REAL - (15.5*Mapa.escalaMetro)) + Mapa.POSY_0_MAPA) + y, 5, mPaint);
            canvas.drawCircle((float)((POSX_ORIGEN_REAL-(45*Mapa.escalaMetro))+ Mapa.POSX_0_MAPA) + x, (float)((POSY_ORIGEN_REAL - (12.5*Mapa.escalaMetro)) + Mapa.POSY_0_MAPA) + y, 5, mPaint);
            canvas.drawCircle((float)((POSX_ORIGEN_REAL-(1*Mapa.escalaMetro))+ Mapa.POSX_0_MAPA) + x, (float)((POSY_ORIGEN_REAL - (15*Mapa.escalaMetro)) + Mapa.POSY_0_MAPA) + y, 5, mPaint);
            canvas.drawCircle((float)((POSX_ORIGEN_REAL-(1*Mapa.escalaMetro))+ Mapa.POSX_0_MAPA) + x, (float) ((POSY_ORIGEN_REAL - (12.5*Mapa.escalaMetro)) + Mapa.POSY_0_MAPA) + y, 5, mPaint);
            */

        }else{
		/*
			canvas.drawText("x:" + posX , 30, 230, mPaint);
			canvas.drawText("y:" + posY, 30, 260, mPaint);
			canvas.drawText("Cuadrante: " + cuadrante,30, 290, mPaint);
			mPaint.setColor(Color.RED);
		*/
            canvas.drawText("x:" + posX , 20, 180, mPaint);
            canvas.drawText("y:" + posY, 20, 200, mPaint);
            canvas.drawText("z:" + posZ, 20, 220, mPaint);
            canvas.drawText("Cuadrante: " + cuadrante,20, 240, mPaint);
            mPaint.setColor(Color.RED);

            canvas.drawCircle((float)((POSX_ORIGEN_REAL-(posX*Mapa.escalaMetro))+ Mapa.POSX_0_MAPA) + x, (float)((POSY_ORIGEN_REAL - (posY*Mapa.escalaMetro)) + Mapa.POSY_0_MAPA) + y, 5, mPaint);
            /* Prueba de colocar el punto en las cuatro esquinas del pasillo largo
            canvas.drawCircle((float)((POSX_ORIGEN_REAL-(45*Mapa.escalaMetro))+ Mapa.POSX_0_MAPA) + x, (float)((POSY_ORIGEN_REAL - (15.5*Mapa.escalaMetro)) + Mapa.POSY_0_MAPA) + y, 5, mPaint);
            canvas.drawCircle((float)((POSX_ORIGEN_REAL-(45*Mapa.escalaMetro))+ Mapa.POSX_0_MAPA) + x, (float)((POSY_ORIGEN_REAL - (12.5*Mapa.escalaMetro)) + Mapa.POSY_0_MAPA) + y, 5, mPaint);
            canvas.drawCircle((float)((POSX_ORIGEN_REAL-(1*Mapa.escalaMetro))+ Mapa.POSX_0_MAPA) + x, (float)((POSY_ORIGEN_REAL - (15*Mapa.escalaMetro)) + Mapa.POSY_0_MAPA) + y, 5, mPaint);
            canvas.drawCircle((float)((POSX_ORIGEN_REAL-(1*Mapa.escalaMetro))+ Mapa.POSX_0_MAPA) + x, (float) ((POSY_ORIGEN_REAL - (12.5*Mapa.escalaMetro)) + Mapa.POSY_0_MAPA) + y, 5, mPaint);
            */
            mPaint.setColor(Color.BLACK);
            @SuppressWarnings("unused")
            int origen = listaCuadrantes.numCuadrante(posX, posY);
        }
    }



    public void pintarCuadrantes(Canvas canvas){
        mPaint.setColor(Color.BLACK);
        ArrayList<Cuadrante> l = listaCuadrantes.getLista();
        for (int i= 0; i<l.size(); i++){
            Cuadrante c = l.get(i);
            float upX = ((float)(c.getPosNW().getPX()*Mapa.escalaMetro) + Mapa.POSX_0_MAPA) + x;
            float upY = ((float)(c.getPosNW().getPY()*Mapa.escalaMetro) + Mapa.POSY_0_MAPA) + y;
            float downX = ((float)(c.getPosSE().getPX()*Mapa.escalaMetro) + Mapa.POSX_0_MAPA) + x;
            float downY = ((float)(c.getPosSE().getPY()*Mapa.escalaMetro) + Mapa.POSY_0_MAPA) + y;
            mPaint2.setStyle(Paint.Style.STROKE);
            canvas.drawRect(upX, upY, downX, downY, mPaint2);
            float mX = (upX + downX)/2;
            float mY = (upY + downY)/2;
            canvas.drawText(String.valueOf(c.getID()), mX-5, mY, mPaint2);
        }
    }

    public void rutaEscape(ArrayList<Integer> camino, Canvas canvas){
        ArrayList<Cuadrante> ruta = new ArrayList<Cuadrante>();
        for (int i = camino.size()-1; i>=0; i--){
            int id = camino.get(i);
            ruta.add(listaCuadrantes.getCuadrante(id));
        }
        pintarRecorrido(ruta,canvas);
    }

    private void pintarRecorrido(ArrayList<Cuadrante> ruta, Canvas canvas){
        int idOrigen;
        int idDestino;
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.GREEN);
        for (int i= 0; i<ruta.size()-1; i++){
            idOrigen = ruta.get(i).getID();
            idDestino = ruta.get(i+1).getID();
            int dir = listaCuadrantes.getDirArista(idOrigen, idDestino);
            if (dir!=-1){
                pintarFlecha(idOrigen,idDestino,dir,canvas,p);
            }
        }
    }

    private void pintarFlecha(int idOrigen, int idDestino, int dir, Canvas canvas, Paint p){
        switch (dir){
            case Arista.UP:
                pintarFlechaArriba(idOrigen,idDestino,dir,canvas,p);
                break;
            case Arista.UP_RIGHT:
                pintarFlechaArribaDcha(idOrigen,idDestino,dir,canvas,p);
                break;
            case Arista.RIGHT:
                pintarFlechaDcha(idOrigen,idDestino,dir,canvas,p);
                break;
            case Arista.DOWN_RIGHT:
                pintarFlechaAbajoDcha(idOrigen,idDestino,dir,canvas,p);
                break;
            case Arista.DOWN:
                pintarFlechaAbajo(idOrigen,idDestino,dir,canvas,p);
                break;
            case Arista.DOWN_LEFT:
                pintarFlechaAbajoIzda(idOrigen,idDestino,dir,canvas,p);
                break;
            case Arista.LEFT:
                pintarFlechaIzda(idOrigen,idDestino,dir,canvas,p);
                break;
            case Arista.UP_LEFT:
                pintarFlechaArribaIzda(idOrigen,idDestino,dir,canvas,p);
                break;
        }
    }

    private void pintarFlechaArriba(int idOrigen, int idDestino, int dir, Canvas canvas, Paint p){
        Cuadrante cO = listaCuadrantes.getCuadrante(idOrigen);
        Cuadrante cD = listaCuadrantes.getCuadrante(idDestino);

        double left1 = cO.getPosNW().getPX();
        double right1 = cO.getPosSE().getPX();
        double up1 = cO.getPosNW().getPY();
        double down1 = cO.getPosSE().getPY();
        double left2 = cD.getPosNW().getPX();
        double right2 = cD.getPosSE().getPX();
        double up2 = cD.getPosNW().getPY();
        double down2 = cD.getPosSE().getPY();

        double posXIni = left1 + 0.5*(right1-left1);
        double posYIni = up1 + 0.3*(down1-up1);
        double posXFin = left2 + 0.5*(right2-left2);
        double posYFin = up2 + 0.7*(down2-up2);

        pintarFlecha(posXIni, posYIni, posXFin, posYFin, dir, canvas, p);
    }

    private void pintarFlechaArribaDcha(int idOrigen, int idDestino, int dir, Canvas canvas, Paint p){
        Cuadrante cO = listaCuadrantes.getCuadrante(idOrigen);
        Cuadrante cD = listaCuadrantes.getCuadrante(idDestino);

        double left1 = cO.getPosNW().getPX();
        double right1 = cO.getPosSE().getPX();
        double up1 = cO.getPosNW().getPY();
        double down1 = cO.getPosSE().getPY();
        double left2 = cD.getPosNW().getPX();
        double right2 = cD.getPosSE().getPX();
        double up2 = cD.getPosNW().getPY();
        double down2 = cD.getPosSE().getPY();

        double posXIni = left1 + 0.7*(right1-left1);
        double posYIni = up1 + 0.3*(down1-up1);
        double posXFin = left2 + 0.3*(right2-left2);
        double posYFin = up2 + 0.7*(down2-up2);

        pintarFlecha(posXIni, posYIni, posXFin, posYFin, dir, canvas, p);
    }

    private void pintarFlechaDcha(int idOrigen, int idDestino, int dir, Canvas canvas, Paint p){
        Cuadrante cO = listaCuadrantes.getCuadrante(idOrigen);
        Cuadrante cD = listaCuadrantes.getCuadrante(idDestino);

        double left1 = cO.getPosNW().getPX();
        double right1 = cO.getPosSE().getPX();
        double up1 = cO.getPosNW().getPY();
        double down1 = cO.getPosSE().getPY();
        double left2 = cD.getPosNW().getPX();
        double right2 = cD.getPosSE().getPX();
        double up2 = cD.getPosNW().getPY();
        double down2 = cD.getPosSE().getPY();

        double posXIni = left1 + 0.7*(right1-left1);
        double posYIni = up1 + 0.5*(down1-up1);
        double posXFin = left2 + 0.3*(right2-left2);
        double posYFin = up2 + 0.5*(down2-up2);

        pintarFlecha(posXIni, posYIni, posXFin, posYFin, dir, canvas, p);
    }

    private void pintarFlechaAbajoDcha(int idOrigen, int idDestino, int dir, Canvas canvas, Paint p){
        Cuadrante cO = listaCuadrantes.getCuadrante(idOrigen);
        Cuadrante cD = listaCuadrantes.getCuadrante(idDestino);

        double left1 = cO.getPosNW().getPX();
        double right1 = cO.getPosSE().getPX();
        double up1 = cO.getPosNW().getPY();
        double down1 = cO.getPosSE().getPY();
        double left2 = cD.getPosNW().getPX();
        double right2 = cD.getPosSE().getPX();
        double up2 = cD.getPosNW().getPY();
        double down2 = cD.getPosSE().getPY();

        double posXIni = left1 + 0.7*(right1-left1);
        double posYIni = up1 + 0.7*(down1-up1);
        double posXFin = left2 + 0.3*(right2-left2);
        double posYFin = up2 + 0.3*(down2-up2);

        pintarFlecha(posXIni, posYIni, posXFin, posYFin, dir, canvas, p);
    }

    private void pintarFlechaAbajo(int idOrigen, int idDestino, int dir, Canvas canvas, Paint p){
        Cuadrante cO = listaCuadrantes.getCuadrante(idOrigen);
        Cuadrante cD = listaCuadrantes.getCuadrante(idDestino);

        double left1 = cO.getPosNW().getPX();
        double right1 = cO.getPosSE().getPX();
        double up1 = cO.getPosNW().getPY();
        double down1 = cO.getPosSE().getPY();
        double left2 = cD.getPosNW().getPX();
        double right2 = cD.getPosSE().getPX();
        double up2 = cD.getPosNW().getPY();
        double down2 = cD.getPosSE().getPY();

        double posXIni = left1 + 0.5*(right1-left1);
        double posYIni = up1 + 0.7*(down1-up1);
        double posXFin = left2 + 0.5*(right2-left2);
        double posYFin = up2 + 0.3*(down2-up2);

        pintarFlecha(posXIni, posYIni, posXFin, posYFin, dir, canvas, p);
    }

    private void pintarFlechaAbajoIzda(int idOrigen, int idDestino, int dir, Canvas canvas, Paint p){
        Cuadrante cO = listaCuadrantes.getCuadrante(idOrigen);
        Cuadrante cD = listaCuadrantes.getCuadrante(idDestino);

        double left1 = cO.getPosNW().getPX();
        double right1 = cO.getPosSE().getPX();
        double up1 = cO.getPosNW().getPY();
        double down1 = cO.getPosSE().getPY();
        double left2 = cD.getPosNW().getPX();
        double right2 = cD.getPosSE().getPX();
        double up2 = cD.getPosNW().getPY();
        double down2 = cD.getPosSE().getPY();

        double posXIni = left1 + 0.3*(right1-left1);
        double posYIni = up1 + 0.7*(down1-up1);
        double posXFin = left2 + 0.7*(right2-left2);
        double posYFin = up2 + 0.3*(down2-up2);

        pintarFlecha(posXIni, posYIni, posXFin, posYFin, dir,canvas, p);
    }

    private void pintarFlechaIzda(int idOrigen, int idDestino, int dir, Canvas canvas, Paint p){
        Cuadrante cO = listaCuadrantes.getCuadrante(idOrigen);
        Cuadrante cD = listaCuadrantes.getCuadrante(idDestino);

        double left1 = cO.getPosNW().getPX();
        double right1 = cO.getPosSE().getPX();
        double up1 = cO.getPosNW().getPY();
        double down1 = cO.getPosSE().getPY();
        double left2 = cD.getPosNW().getPX();
        double right2 = cD.getPosSE().getPX();
        double up2 = cD.getPosNW().getPY();
        double down2 = cD.getPosSE().getPY();

        double posXIni = left1 + 0.3*(right1-left1);
        double posYIni = up1 + 0.5*(down1-up1);
        double posXFin = left2 + 0.7*(right2-left2);
        double posYFin = up2 + 0.5*(down2-up2);

        pintarFlecha(posXIni, posYIni, posXFin, posYFin, dir, canvas, p);
    }

    private void pintarFlechaArribaIzda(int idOrigen, int idDestino, int dir, Canvas canvas, Paint p){
        Cuadrante cO = listaCuadrantes.getCuadrante(idOrigen);
        Cuadrante cD = listaCuadrantes.getCuadrante(idDestino);

        double left1 = cO.getPosNW().getPX();
        double right1 = cO.getPosSE().getPX();
        double up1 = cO.getPosNW().getPY();
        double down1 = cO.getPosSE().getPY();
        double left2 = cD.getPosNW().getPX();
        double right2 = cD.getPosSE().getPX();
        double up2 = cD.getPosNW().getPY();
        double down2 = cD.getPosSE().getPY();

        double posXIni = left1 + 0.2*(right1-left1);
        double posYIni = up1 + 0.2*(down1-up1);
        double posXFin = left2 + 0.4*(right2-left2);
        double posYFin = up2 + 0.4*(down2-up2);

        pintarFlecha(posXIni, posYIni, posXFin, posYFin, dir, canvas, p);
    }

    /**
     * Método auxiliar para pintar las líneas, aplicando la escala.
     * @param xIni
     * @param yIni
     * @param xFin
     * @param yFin
     * @param canvas
     * @param p
     */
    private void pintarFlecha(double xIni, double yIni, double xFin, double yFin, int dir, Canvas canvas, Paint p){
        float xO = ((float)(xIni*Mapa.escalaMetro) + Mapa.POSX_0_MAPA) + x;
        float yO = ((float)(yIni*Mapa.escalaMetro) + Mapa.POSY_0_MAPA) + y;
        float xD = ((float)(xFin*Mapa.escalaMetro) + Mapa.POSX_0_MAPA) + x;
        float yD = ((float)(yFin*Mapa.escalaMetro) + Mapa.POSY_0_MAPA) + y;
        canvas.drawLine(xO, yO, xD, yD,p);

        float deltaX = xD - xO;
        float deltaY = yD - yO;
        double frac = 0.2;

        switch (dir){
            case Arista.UP_RIGHT:
            case Arista.RIGHT:
            case Arista.LEFT:
            case Arista.UP_LEFT:
                canvas.drawLine(xO + (int)((1-frac)*deltaX - frac*deltaY),
                        yO + (int)((1-frac)*deltaY - frac*deltaX),
                        xD, yD, p);
                canvas.drawLine(xO + (int)((1-frac)*deltaX + frac*deltaY),
                        yO + (int)((1-frac)*deltaY + frac*deltaX),
                        xD, yD, p);
                break;
            case Arista.DOWN_RIGHT:
            case Arista.DOWN_LEFT:
            case Arista.UP:
            case Arista.DOWN:
                canvas.drawLine(xO + (int)((1-frac)*deltaX + frac*deltaY),
                        yO + (int)((1-frac)*deltaY - frac*deltaX),
                        xD, yD, p);
                canvas.drawLine(xO + (int)((1-frac)*deltaX - frac*deltaY),
                        yO + (int)((1-frac)*deltaY + frac*deltaX),
                        xD, yD, p);
                break;
        }
    }




    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public double getPosZ() {
        return posZ;
    }


    public int getCuadrante(){
        return cuadrante;
    }

    public void setCuadrante(int c){
        this.cuadrante = c;
    }

    public void setListaCuadrantes(ListaCuadrantes l){
        listaCuadrantes = l;
    }

    public void setNumMedidasIncorrectas(int numMedidasIncorrectas) {
        this.numMedidasIncorrectas = numMedidasIncorrectas;
    }

    public int getNumMedidasIncorrectas() {
        return numMedidasIncorrectas;
    }
}
