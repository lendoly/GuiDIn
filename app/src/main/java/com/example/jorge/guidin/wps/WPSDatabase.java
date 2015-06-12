package com.example.jorge.guidin.wps;

/**
 * Created by Jorge on 19/03/2015.
 */

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;

public class WPSDatabase {

    public SQLiteDatabase database;
    public final static int NUMERO_RESULTADOS_FINGERPRINT = 10;

    public WPSDatabase() throws SQLException{
        database = SQLiteDatabase.create(null);
        String query = "CREATE TABLE `repository` (" +
                "`MAC` varchar(17) NOT NULL," +
                "`x` double NOT NULL,"+
                "`y` double NOT NULL,"+
                "`z` double NOT NULL,"+
                "`strength` int(11) NOT NULL,"+
                "PRIMARY KEY (`MAC`,`x`,`y`,`z`,`strength`))";
        database.execSQL(query);
    }

    /**
     * Inserta las coordenadas en la BD
     * @param datos
     * @throws JSONException
     */
    public void insertCoordenates(JSONArray datos) throws JSONException{
        JSONArray dato;

        if(datos.length()>0){
            String query = "";
            for(int i = 0; i < datos.length(); i++){
                dato = datos.getJSONArray(i);
                query = "INSERT INTO repository VALUES ('";
                query = query + dato.getString(0) + "',";
                query = query + dato.getDouble(1) + ",";
                query = query + dato.getDouble(2) + ",";
                query = query + dato.getDouble(3) + ",";
                query = query + dato.getInt(4) + ");";
                database.execSQL(query);
            }
        }

    }

    /**
     * Devuelve las coordenadas más cercanas con la distancia al punto de referencia<br>
     * ya calculada
     * @param macs
     * @return
     */
    private ArrayList<Coordenada> getClosestNeighbors(List<ScanResult> macs){

        ArrayList<ArrayList<Coordenada>> coordenadas = new ArrayList<ArrayList<Coordenada>>();
        /**
         * Guardo las coordenadas de las macs registradas con sus distancias eculideas almacenadas
         */
        ArrayList<Coordenada> arrAux = new ArrayList<Coordenada>();

        for(int i = 0; i < macs.size(); i++){
            coordenadas.add(this.getCoordenadasRegistradasPunto(macs.get(i)));
        }


        double suma = 0;
        ArrayList<Coordenada> result = new ArrayList<Coordenada>();

        String[] columns = new String[3];
        columns[0] = "x";
        columns[1] = "y";
        columns[2] = "z";
        String groupBy = "x,y,z";

        Cursor c = database.query("repository", columns, null,null, groupBy, null, null);

        Coordenada coordActual;
        double distancia;
        double aux;

        c.moveToFirst();
        int j = 0;
        boolean fin = false;
        int h= 0;
        int index;
        while(j < c.getCount() && !fin){
            coordActual = new Coordenada();
            coordActual.setX(c.getDouble(0));
            coordActual.setY(c.getDouble(1));
            coordActual.setZ(c.getDouble(2));
            suma = 0;
            ScanResult l;

            for(int i = 0; i < macs.size(); i++){
                index = coordenadas.get(i).indexOf(coordActual);
                if(index!=-1){
                    l = macs.get(i);
                    h = l.level - coordenadas.get(i).get(index).getStrength();
                    suma+= Math.pow(h,2);
                }
            }
            aux = ((double)1/macs.size());
            distancia = (aux)*Math.sqrt(suma);
            coordActual.setDistanciaAlmacenada(distancia);
            arrAux.add(coordActual);
            fin = !c.moveToNext();

        }
        c.close();

        int position;
        double min;
        fin = (arrAux.size()==0);
        while((result.size() < WPSDatabase.NUMERO_RESULTADOS_FINGERPRINT) && !fin){
            position = 0;
            min = Double.MAX_VALUE;
            for(int k = 0; k < arrAux.size(); k++){
                if(min > arrAux.get(k).getDistanciaAlmacenada()){
                    min = arrAux.get(k).getDistanciaAlmacenada();
                    position = k;
                }
            }
            result.add(arrAux.get(position));
            arrAux.remove(position);
            if(arrAux.size()==0) fin = true;
        }

        return result;
    }

    /**
     * Calcula la distancia entre el resultado del escaneado pos1 y la coordenada definida por x,y y z
     * @param pos1
     * @param x
     * @param y
     * @param z
     * @return
     */
    public int getEuclideanDistance(ScanResult pos1, double x, double y, double z){

        String[] columns = new String[1];
        columns[0] = "strength";
        String selection = "x = ? AND y = ? AND z = ? AND MAC = ?;";//'" + pos1.BSSID + "'";
        String selectionArgs[] = new String[4];
        selectionArgs[0] = "" + x;
        selectionArgs[1] = "" + y;
        selectionArgs[2] = "" + z;
        selectionArgs[3] = pos1.BSSID;

        Cursor c = database.query("repository", columns, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        int result=0;
        if(c!=null){
            //result = (100 - pos1.level) - (100 - c.getInt(0));
            result = (pos1.level) - (c.getInt(0));
            c.close();
        }
        return result;
    }

    /**
     * Recupera todas la coordenadas almacenadas que tienen el BBSID del punto pasado cómo parámetro
     * @param punto
     * @return
     */
    public ArrayList<Coordenada> getCoordenadasRegistradasPunto(ScanResult punto){

        String[] columns = new String[4];
        columns[0] = "x";
        columns[1] = "y";
        columns[2] = "z";
        columns[3] = "strength";
        String selection = "MAC = ?;";
        String selectionArgs[] = new String[1];
        selectionArgs[0] = punto.BSSID;
        Cursor c = database.query("repository", columns, selection, selectionArgs, null, null, null);
        c.moveToFirst();
        ArrayList<Coordenada> result = new ArrayList<Coordenada>();
        Coordenada coord;
        for(int i = 0; i < c.getCount(); i++){
            coord = new Coordenada();
            coord.setMac(punto.BSSID);
            coord.setX(c.getDouble(0));
            coord.setY(c.getDouble(1));
            coord.setZ(c.getDouble(2));
            coord.setStrength(c.getInt(3));
            result.add(coord);
            c.moveToNext();
        }
        c.close();
        return result;
    }

    /**
     * Comprueba si una mac está registrada en una posicion
     * @param mac
     * @param coord
     * @return
     */
    public boolean isMacRegistered(String mac, Coordenada coord){
        String[] columns = new String[1];
        columns[0] = "MAC";
        String selection = "x = ? AND y = ? AND z = ? AND MAC = ?;";
        String selectionArgs[] = new String[4];
        selectionArgs[0] = "" + coord.getX();
        selectionArgs[1] = "" + coord.getY();
        selectionArgs[2] = "" + coord.getZ();
        selectionArgs[3] = mac;
        Cursor c = database.query("repository", columns, selection, selectionArgs, null, null,null);
        int rows = c.getCount();
        c.close();
        if(rows>0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Método que recupera los vecinos más cercanos con sus distancias euclideas y calcula<br>
     * la estimación de posición del terminal
     * @return
     */
    public Coordenada calculatePosition(List<ScanResult> macs){
        ArrayList<Coordenada> coordenadas = this.getClosestNeighbors(macs);

        double sumaX = 0;
        double sumaY = 0;
        double sumaZ = 0;
        double aux = 0;
        Coordenada c = new Coordenada();
        if(coordenadas.size()>0){
            for(int i = 0; i < coordenadas.size(); i++){
                sumaX +=((1/coordenadas.get(i).getDistanciaAlmacenada())*coordenadas.get(i).getX());
                sumaY +=((1/coordenadas.get(i).getDistanciaAlmacenada())*coordenadas.get(i).getY());
                sumaZ +=((1/coordenadas.get(i).getDistanciaAlmacenada())*coordenadas.get(i).getZ());
                aux += (1/coordenadas.get(i).getDistanciaAlmacenada());
            }
            sumaX = sumaX / aux;
            sumaY = sumaY / aux;
            sumaZ = sumaZ / aux;
            sumaX = redondear(sumaX);
            sumaY = redondear(sumaY);
            sumaZ = redondear(sumaZ);
        }else{
            sumaX = Double.MAX_VALUE;
            sumaY = Double.MAX_VALUE;
            sumaZ = Double.MAX_VALUE;
        }
        c.setX(sumaX);
        c.setY(sumaY);
        c.setZ(sumaZ);

        return c;
    }

    /**
     * Recupera las 15 coordenadas de la base de datos más cercanas a la pasada como parámetro.
     * @param c
     * @return
     */
    public ArrayList<Coordenada> getClosetsCoords(Coordenada c){
        String[] columns = new String[3];
        columns[0] = "x";
        columns[1] = "y";
        columns[2] = "z";
        String selection = "x < ? AND x > ? AND y < ? AND y > ?";
        String selectionArgs[] = new String[4];
        selectionArgs[0] = "" + (c.getX()+5);
        selectionArgs[1] = "" + (c.getX()-5);
        selectionArgs[2] = "" + (c.getY()+5);
        selectionArgs[3] = "" + (c.getY()-5);
        String groupBy = "x,y,z";
        Cursor cursor = database.query("repository", columns, selection, selectionArgs, groupBy, null, null);
        cursor.moveToFirst();
        ArrayList<Coordenada> result = new ArrayList<Coordenada>();
        Coordenada coord;
        double distancia;
        int j = 0;
        for(int i = 0; i < cursor.getCount(); i++){
            coord = new Coordenada();

            coord.setX(cursor.getDouble(0));
            coord.setY(cursor.getDouble(1));
            coord.setZ(cursor.getDouble(2));
            distancia = Math.sqrt(Math.pow(coord.getX()-c.getX(),2) + Math.pow(coord.getY()-c.getY(), 2));
            coord.setDistanciaAlmacenada(distancia);
            j = 0;
            while(j < result.size()){
                if(result.get(j).getDistanciaAlmacenada()<= coord.getDistanciaAlmacenada()){
                    j++;
                }else{
                    result.add(j, coord);
                    if(result.size()>15){
                        result.remove(result.size()-1);
                    }
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
        return result;
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
