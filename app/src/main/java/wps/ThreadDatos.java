package wps;

/**
 * Created by Jorge on 19/03/2015.
 */




import java.sql.SQLException;

import org.json.JSONException;

import http.HttpServices;
import http.HttpServicesException;


public class ThreadDatos extends Thread{

    public static final int ESTADO_EXITO = 0;
    public static final int ESTADO_EJECUCION = 1;
    public static final int ESTADO_ERROR = 2;
//	public FireList listaFuegos;

    private WPSDatabase db;
    private int estado;

    /**
     * Estado del thread. Normalmente será ESTADO_EJECUCION, pero para pruebas si no se quiere esperar a
     * la conexión y descarga de datos se puede poner ESTADO_EXITO.
     * @param estado
     */
    public ThreadDatos(int estado){
        super("HiloDatos");
        this.estado = estado;
    }

    public void run(){
        try {

            db = new WPSDatabase();
            HttpServices service = new HttpServices();
            try {
                db.insertCoordenates(service.getCoordenadas());
                estado = ESTADO_EXITO;
            } catch (HttpServicesException e) {
                e.printStackTrace();
                estado = ESTADO_ERROR;
            } catch (JSONException e) {
                e.printStackTrace();
                estado = ESTADO_ERROR;
            }

//			listaFuegos = new FireList();

/*			while(true){
				listaFuegos.loadFires();
				sleep(2000);
			}
*/
        } catch (SQLException e1) {
            e1.printStackTrace();
            estado = ESTADO_ERROR;
        }

    }


    public WPSDatabase getDatabase() throws WPSException{
        if(db == null){
            throw new WPSException("La base de datos está sin inicializar");
        }else{
            if(estado == ESTADO_ERROR){
                throw new WPSException("Ha ocurrido un error mientras se intentaba crear la BBDD");
            }else if(estado == ESTADO_EJECUCION){
                throw new WPSException("Se están recuperando los datos...");
            }
            return db;
        }
    }

    public int getEstado(){
        return estado;
    }


}
