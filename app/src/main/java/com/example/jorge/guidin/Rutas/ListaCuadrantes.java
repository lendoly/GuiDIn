package com.example.jorge.guidin.rutas;


import java.util.ArrayList;

public class ListaCuadrantes {

    public static final int MAX= 9999;

    private ArrayList<Cuadrante> lista;
    private int[][] matrizAdy;
    private int[][] matrizOriginal;
    private ArrayList<Arista> aristas;

    public ListaCuadrantes(){
        lista = new ArrayList<Cuadrante>();
        iniciarLista();
    }

    public ListaCuadrantes(ArrayList<Cuadrante> a, int[][] m, ArrayList<Arista> ar){
        lista = a;
        matrizAdy = m;
        aristas = ar;
    }

    public void iniciarLista(){
        iniciarCuadrantes();
        //	fijarSalidas();
        unirCuadrantes(lista.size());
        matrizOriginal = matrizAdy.clone();
    }

    private void iniciarCuadrantes(){
        iniciarPasillo1();
		/*iniciarPasillo2();
		iniciarPasillo3();
		iniciarPasillo4();
		iniciarPasillo5();
		iniciarSalaJuntas();
		iniciarSalaGrados();*/
    }


    private void iniciarPasillo1(){

        double largoPasillo = 48;
        double anchoPasillo = 4;

        int id = 0;
        Posicion posNW = new Posicion(0,16);
        Posicion posSE = new Posicion(4,13);
        lista.add(new Cuadrante(id,posNW,posSE,false));
        while (posSE.getPX()<=largoPasillo){
            id++;
            posNW = new Posicion(posNW.getPX() + anchoPasillo, posNW.getPY());
            posSE = new Posicion(posSE.getPX()+anchoPasillo, posSE.getPY());
            lista.add(new Cuadrante(id,posNW,posSE,false));
        }
    }

    private void iniciarPasillo2(){
        int id = lista.size();
        double largoPasillo = 8.7;
        double anchoPasillo = 3.653846154;
        double largoCuadrante = largoPasillo/2;
        Posicion posNW = new Posicion();
        Posicion posSE = new Posicion(anchoPasillo,anchoPasillo);
        while (posSE.getPY()<=largoPasillo){
            posNW = new Posicion(0,posSE.getPY());
            posSE = new Posicion(posNW.getPX()+anchoPasillo, posNW.getPY()+largoCuadrante);
            lista.add(new Cuadrante(id,posNW,posSE,false));
            id++;
        }
        posNW = new Posicion(anchoPasillo,6.153846154);
        posSE = new Posicion(posNW.getPX()+anchoPasillo,posNW.getPY()+anchoPasillo);
        lista.add(new Cuadrante(id,posNW,posSE,true));

    }

    private void iniciarPasillo3(){
        int id = lista.size();
        double largoPasillo = 58.84615385;
        //double anchoPasillo = 2.692307692;
        double anchoPasillo = 3.653846154;
        double largoCuadrante = 3.653846154;
        double origenY = 12.5;
        Posicion posNW = new Posicion(0,origenY);
        Posicion posSE = new Posicion();
        while (posSE.getPX()<=largoPasillo){
            posNW = new Posicion(posSE.getPX(),origenY);
            posSE = new Posicion(posNW.getPX()+largoCuadrante, posNW.getPY()+anchoPasillo);
            lista.add(new Cuadrante(id,posNW,posSE,false));
            id++;
        }
    }

    private void iniciarPasillo4(){
        int id = lista.size();
        double largoPasillo = 8.7;
        double xPasillo = 49.7;
        double anchoPasillo = 2.30769231;
        double largoCuadrante = largoPasillo/2;
        double anchoAnt = 3.653846154;
        Posicion posNW = new Posicion(xPasillo,0);
        Posicion posSE = new Posicion(xPasillo+anchoPasillo,anchoAnt);
        while (posSE.getPY()<=largoPasillo){
            posNW = new Posicion(xPasillo,posSE.getPY());
            posSE = new Posicion(posNW.getPX()+anchoPasillo, posNW.getPY()+largoCuadrante);
            lista.add(new Cuadrante(id,posNW,posSE,false));
            id++;
        }
        posNW = new Posicion(xPasillo-anchoAnt,6.153846154);
        posSE = new Posicion(posNW.getPX()+anchoAnt,posNW.getPY()+anchoAnt);
        lista.add(new Cuadrante(id,posNW,posSE,true));
    }

    private void iniciarPasillo5(){
        int id = lista.size();
        double largoPasillo = 5.384615385;
        Posicion posNW = lista.get(36).getPosNW();
        Posicion posSE = lista.get(36).getPosSE();
        double anchoPasillo = posSE.getPX()-posNW.getPX();
        double xPasillo = posNW.getPX();
        double largoCuadrante = 2.43;
        double x = 0;
        while (x<=largoPasillo){
            posNW = new Posicion(xPasillo,posSE.getPY());
            posSE = new Posicion(posNW.getPX()+anchoPasillo, posNW.getPY()+largoCuadrante);
            lista.add(new Cuadrante(id,posNW,posSE,false));
            id++;
            x = x + largoCuadrante;
        }
    }

    private void iniciarSalaJuntas(){
        double largoSala = 15;
        double anchoSala = 8.846153846;
        double largoCuadrante = largoSala/5;
        double anchoCuadrante = anchoSala/3;
        int id = lista.size();
        Posicion posNW = new Posicion(lista.get(36).getPosNW());
        Posicion posBaseNW = new Posicion(posNW);
        Posicion posSE = new Posicion(lista.get(36).getPosSE());
        double anchoAntiguo = posSE.getPX()-posNW.getPX();
        for (int i= 0; i<5; i++){
            posNW = new Posicion(posBaseNW.getPX(), posBaseNW.getPY()+(i*largoCuadrante));
            posSE = new Posicion(posBaseNW.getPX() + anchoAntiguo, posNW.getPY()+largoCuadrante);
            for (int j= 0; j<3;j++){
                posNW = new Posicion(posSE.getPX(),posNW.getPY());
                posSE = new Posicion(posNW.getPX()+anchoCuadrante,posNW.getPY()+largoCuadrante);
                lista.add(new Cuadrante(id,posNW,posSE,false));
                id++;
            }
        }
    }

    private void iniciarSalaGrados(){
        //3 cuadrantes de ancho, 4 de largo.
        double largoSala = 11.92307692;
        double anchoSala = 7.692307692;
        double largoCuadrante = largoSala/4;
        double anchoCuadrante = anchoSala/3;
        int id = lista.size();
        Posicion posNW = new Posicion(lista.get(30).getPosNW());
        Posicion posBaseNW = new Posicion(posNW.getPX()+1,posNW.getPY()+4.3);
        Posicion posSE = new Posicion(posBaseNW.getPX()+anchoCuadrante,posBaseNW.getPY()+largoCuadrante);
        double anchoAntiguo = posSE.getPX()-posNW.getPX();
        for (int i= 0; i<4; i++){
            posNW = new Posicion(posBaseNW.getPX(), posBaseNW.getPY()+(i*largoCuadrante));
            posSE = new Posicion(posBaseNW.getPX() + anchoAntiguo, posNW.getPY()+largoCuadrante);
            for (int j= 0; j<3;j++){
                posNW = new Posicion(posSE.getPX(),posNW.getPY());
                posSE = new Posicion(posNW.getPX()+anchoCuadrante,posNW.getPY()+largoCuadrante);
                lista.add(new Cuadrante(id,posNW,posSE,false));
                id++;
            }
        }
    }

    private void fijarSalidas(){
        Cuadrante c;
        c = lista.get(19);
        c.setNodoSalida(true);
        lista.set(19, c);
        c = lista.get(39);
        c.setNodoSalida(true);
        lista.set(39, c);
    }

    private void unirCuadrantes(int size){
        matrizAdy = new int[size][size];
        aristas = new ArrayList<Arista>();
        for (int i = 0; i<size; i++){
            for (int j = 0; j<size; j++){
                matrizAdy[i][j] = MAX;
            }
        }
        unirPasillo1();
		/*unirPasillo2();
		unirPasillo3();
		unirPasillo4();
		unirPasillo5();
		unirSalaJuntas();
		unirSalaGrados();*/
    }


    private void unirPasillo1(){
        //Primer pasillo: cuadrantes 0 a 16.
        for (int i= 0; i<=11; i++){
            if ((i-1)>=0){
                crearArista(i,i-1,Arista.LEFT);
            }
            if ((i+1)<=11){
                crearArista(i,i+1,Arista.RIGHT);
            }
        }
    }

    private void unirPasillo2(){
        //Segundo pasillo: cuadrantes 17 a 19.
        crearAristas(0,17,Arista.DOWN);
        crearAristas(17,18,Arista.DOWN);
        crearAristas(18,20,Arista.DOWN);
        crearAristas(17,19,Arista.DOWN_RIGHT);
        crearAristas(18,19,Arista.UP_RIGHT);
    }

    private void unirPasillo3(){
        //Tercer pasillo: cuadrantes 20 a 36.
        for (int i= 20; i<=36; i++){
            if ((i-1)>=20)
                crearArista(i,i-1,Arista.LEFT);
            if ((i+1)<=36)
                crearArista(i,i+1,Arista.RIGHT);
        }
    }

    private void unirPasillo4(){
        //Cuarto pasillo: cuadrantes 37 a 39.
        crearAristas(13,37,Arista.DOWN_RIGHT);
        crearAristas(14,37,Arista.DOWN_LEFT);
        crearAristas(37,38,Arista.DOWN);
        crearAristas(38,33,Arista.DOWN_LEFT);
        crearAristas(38,34,Arista.DOWN_RIGHT);
        crearAristas(37,39,Arista.DOWN_LEFT);
        crearAristas(38,39,Arista.UP_RIGHT);
    }

    private void unirPasillo5(){
        //Pasillo 5: cuadrantes 40 a 42.
        crearAristas(36,40,Arista.DOWN);
        crearAristas(40,41,Arista.DOWN);
        crearAristas(41,42,Arista.DOWN);
    }

    private void unirSalaJuntas(){
        //Sala de Juntas: cuadrante 43 a 57.
        crearAristas(36,43,Arista.RIGHT);
        crearAristas(43,44,Arista.RIGHT);
        crearAristas(44,45,Arista.RIGHT);
        crearAristas(43,46,Arista.DOWN);
        crearAristas(44,47,Arista.DOWN);
        crearAristas(45,48,Arista.DOWN);
        crearAristas(46,47,Arista.RIGHT);
        crearAristas(47,48,Arista.RIGHT);
        crearAristas(46,49,Arista.DOWN);
        crearAristas(47,50,Arista.DOWN);
        crearAristas(48,51,Arista.DOWN);
        crearAristas(41,49,Arista.RIGHT);
        crearAristas(49,50,Arista.RIGHT);
        crearAristas(50,51,Arista.RIGHT);
        crearAristas(49,52,Arista.DOWN);
        crearAristas(50,53,Arista.DOWN);
        crearAristas(51,54,Arista.DOWN);
        crearAristas(52,53,Arista.RIGHT);
        crearAristas(53,54,Arista.RIGHT);
        crearAristas(52,55,Arista.DOWN);
        crearAristas(53,56,Arista.DOWN);
        crearAristas(54,57,Arista.DOWN);
        crearAristas(55,56,Arista.RIGHT);
        crearAristas(56,57,Arista.RIGHT);
    }

    private void unirSalaGrados(){
        crearAristas(31,58,Arista.DOWN);
        crearAristas(33,60,Arista.DOWN_LEFT);
        crearAristas(58,59,Arista.RIGHT);
        crearAristas(59,60,Arista.RIGHT);
        crearAristas(58,61,Arista.DOWN);
        crearAristas(59,62,Arista.DOWN);
        crearAristas(60,63,Arista.DOWN);
        crearAristas(61,62,Arista.LEFT);
        crearAristas(62,63,Arista.LEFT);
        crearAristas(61,64,Arista.DOWN);
        crearAristas(62,65,Arista.DOWN);
        crearAristas(63,66,Arista.DOWN);
        crearAristas(64,65,Arista.RIGHT);
        crearAristas(65,66,Arista.RIGHT);
        crearAristas(64,67,Arista.DOWN);
        crearAristas(65,68,Arista.DOWN);
        crearAristas(66,69,Arista.DOWN);
        crearAristas(67,68,Arista.RIGHT);
        crearAristas(68,69,Arista.RIGHT);
    }

    public ArrayList<Cuadrante> listaAdyacentes(Cuadrante c){
        ArrayList<Cuadrante> solucion = new ArrayList<Cuadrante>();
        int id = c.getID();
        for (int i=0; i<lista.size(); i++){
            if (matrizAdy[id][i]!=MAX){
                //si es adyacente, lo añadimos a la lista de adyacencia.
                Cuadrante aux = lista.get(i);
                solucion.add(aux);
            }
        }
        return solucion;
    }

    public ArrayList<Integer> listaAdyacentes(int id){
        ArrayList<Integer> solucion = new ArrayList<Integer>();
        for (int i= 0; i<lista.size(); i++){
            if (matrizAdy[id][i]!= MAX){
                solucion.add(i);
            }
        }
        return solucion;
    }

    public ArrayList<Integer> rutaMasCorta(int origen){
        //devuelve la lista de nodos comenzando por el final.
        int[] distancias = caminoMinimo(origen);
        //buscar destino: el más cercano al origen
        int destino = salidaMasCercana(origen,distancias);
        ArrayList<Integer> sol = new ArrayList<Integer>();
        int i = destino;
        while (i!= origen){
            ArrayList<Integer> ady = listaAdyacentes(i);
            int j = 0;
            int elem = ady.get(0);
            while (distancias[i]!= matrizAdy[i][elem]+distancias[elem]){
                j++;
                elem = ady.get(j);
            }
            sol.add(i);
            i = elem;
        }
        sol.add(origen);
        return sol;
    }

    private int[] caminoMinimo(int origen){
        int[] distancias = new int[lista.size()];
        boolean[] usados = new boolean[lista.size()];
        for (int i= 0; i<lista.size(); i++){
            distancias[i] = matrizAdy[origen][i];
        }
        distancias[origen] = 0;
        usados[origen] = true;
        while (!usadosTodos(usados)){
            int a = buscarMinimo(distancias,usados);
            usados[a] = true;
            ArrayList<Integer> sucesores = listaAdyacentes(a);
            for (int j = 0; j<sucesores.size(); j++){
                int w = sucesores.get(j);
                if ((distancias[w] > distancias[a] + matrizAdy[a][w]))
                    distancias[w] = distancias[a] + matrizAdy[a][w];
            }
        }
        return distancias;
    }

    private int buscarMinimo(int[] distancias, boolean[] usados){
        int sol = -1;
        int min = MAX;
        for (int i=0; i<distancias.length; i++){
            if (!usados[i]){
                if (distancias[i]<min){
                    sol = i;
                    min = distancias[i];
                }
            }
        }
        return sol;
    }

    private boolean usadosTodos(boolean[] usados){
        for (int i= 0; i<usados.length; i++){
            if (!usados[i]) return false;
        }
        return true;
    }

    public boolean hayArista(int origen, int destino){
        if (matrizAdy[origen][destino]!=MAX)
            return true;
        else return false;
    }

    public int numCuadrante(Posicion p){
        for (int i= 0; i<lista.size(); i++){
            Cuadrante c = lista.get(i);
            if (c.pertenece(p)){
                return c.getID();
            }
        }
        return -1;
    }

    public int numCuadrante(double pX, double pY){
        Posicion p = new Posicion(pX, pY);
        return numCuadrante(p);
    }

    public Cuadrante getCuadrante(int id){
        for (int i= 0; i<lista.size(); i++){
            if (lista.get(i).getID()==id){
                return lista.get(i);
            }
        }
        return null;
    }

    /**
     * Devuelve la dirección de una arista a partir de su origen y destino.
     * @param origen
     * @param destino
     * @return Dirección de la arista o -1 si no existe la arista con esos datos.
     */
    public int getDirArista(int origen, int destino){
        for (int i= 0; i<aristas.size(); i++){
            Arista a = aristas.get(i);
            if ((a.getOrigen()==origen)&&(a.getDestino()==destino)){
                return a.getDireccion();
            }
        }
        return -1;
    }

    private ArrayList<Integer> getSalidas(){
        ArrayList<Integer> a = new ArrayList<Integer>();
        for (int i= 0; i<lista.size(); i++){
            Cuadrante c = lista.get(i);
            if (c.isNodoSalida()){
                a.add(c.getID());
            }
        }
        return a;
    }

    private int salidaMasCercana(int origen, int[] distancias){
        int min = Integer.MAX_VALUE;
        int id = 0;
        ArrayList<Integer> salidas = getSalidas();
        for (int i=0; i<salidas.size(); i++){
            int k = salidas.get(i);
            if (distancias[k]<min){
                min = distancias[id];
                id = k;
            }
        }
        return id;
    }

    private void crearAristas(int origen, int destino, int dirInicial){
        crearArista(origen,destino,dirInicial);
        crearArista(destino,origen,Arista.opuesto(dirInicial));
    }

    private void crearArista(int origen, int destino, int direccion){
        matrizAdy[origen][destino] = 1;
        Arista a = new Arista(origen,destino,direccion);
        aristas.add(a);
    }


    public ArrayList<Cuadrante> getLista() {
        return lista;
    }

    public void setLista(ArrayList<Cuadrante> lista) {
        this.lista = lista;
    }

    public int[][] getMatrizAdyacencia() {
        return matrizAdy;
    }

    public void setMatrizAdyacencia(int[][] matrizAdyacencia) {
        this.matrizAdy = matrizAdyacencia;
    }


}
