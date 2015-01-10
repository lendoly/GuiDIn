package http;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

class Conecta extends AsyncTask<HttpGet, Integer, Long> {
	
	private String response;
	
	// M�todo para ejecutar la conexi�n con la base de datos en segundo plano.
	
	protected Long doInBackground(HttpGet... httpgets) {

        response= new String();
		HttpGet httpget = httpgets[0];
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
	    BasicResponseHandler aux = new BasicResponseHandler();    
	    
	    try {
			response = httpclient.execute(httpget,aux);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    httpclient.getConnectionManager().shutdown();     

        return null;
    }
	
	protected void onPreExecute () {
		
	}
	
    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {
        //showDialog("Downloaded " + result + " bytes");
    }

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}