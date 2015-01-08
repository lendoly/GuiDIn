package http;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class PingConnect extends AsyncTask<String, Integer, Long> {

	private boolean respuesta = false;
	
	public boolean getRespuesta() {
		return respuesta;
	}
	public void setRespuesta(boolean respuesta) {
		this.respuesta = respuesta;
	}
	
	// M�todo para ejecutar el ping en segundo plano, vemos si exite conexi�n con el servidor.
	@Override
	protected Long doInBackground(String... strings) {
		// TODO Auto-generated method stub
		String host = strings[0];
		String puerto = strings[1];
		
		int port = Integer.parseInt(puerto);
		
		Socket t;
		try {
			t = new Socket(host,port);
			t.close();
			setRespuesta(true);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    

		
		return null;
	}

}
