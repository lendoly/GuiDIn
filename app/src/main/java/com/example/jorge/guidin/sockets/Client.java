package com.example.jorge.guidin.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.os.AsyncTask;

import com.example.jorge.guidin.Login;

/**
 * Creat
 *
 * ed by Jorge on 20/04/2015.
 */
public class Client extends AsyncTask<String, Integer, Long> {

//	private int[] camino;

    private int caminoCorrecto;
    private String ruta;
    private String listaCuadrantes;
    private int cuadranteClave;



    @Override
    protected Long doInBackground(String... strings) {

        String adresaServer ="tot.fdi.ucm.es"; //"147.96.96.209";//"172.20.10.6";//"192.168.1.43";//"192.168.1.34"; //"147.96.102.38";//

        int PORT = 22;
        Socket socket = null;
        String origenX = strings[0];
        String origenY = strings[1];
        String origenZ = strings[2];
        String destino = strings[3];
        String orientacion = strings[4];
        String posActualX = strings[5];
        String posActualY = strings[6];
        String posActualZ = strings[7];
        String suerables = strings[8];
        String discapacidad = strings[9];


        DataInputStream in = null;
        DataOutputStream out = null;

        try {
            socket = new Socket();
            SocketAddress adr = new InetSocketAddress(adresaServer, PORT);
            socket.connect(adr, 1500);

            //System.out.println("Ha conectado");

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            out.flush();

            /*
            out.writeUTF(suerables);
            out.writeUTF(discapacidad);
            out.writeUTF(origenX);
            out.writeUTF(origenY);
            out.writeUTF(origenZ);
            out.writeUTF(destino);
            out.writeUTF(orientacion);
            out.writeUTF(posActualX);
            out.writeUTF(posActualY);
            out.writeUTF(posActualZ);
                                    */
            out.writeUTF(suerables);
            out.writeUTF(discapacidad);
            out.writeUTF("18");
            out.writeUTF("14.5");
            out.writeUTF("4");
            out.writeUTF("sala de juntas");
            out.writeUTF(orientacion);
            out.writeUTF("18");
            out.writeUTF("14.5");
            out.writeUTF("4");

            listaCuadrantes=in.readUTF();
            ruta  = in.readUTF();
            cuadranteClave  = in.readInt();
            in.close();
            out.close();
            socket.close();



        } catch (SocketTimeoutException e) {
            System.err.println(" Error al conectar: \n" + e);

        } catch (UnknownHostException e) {
            System.err.println(" Servidor inaccesible \n" + e);
            System.exit(1);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public int getCuadranteClave() {
        return cuadranteClave;
    }

    public String getRuta() {
        return ruta;
    }

    public String getCuadrantes() {
        return listaCuadrantes;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

}


