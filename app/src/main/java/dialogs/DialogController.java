package dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * Clase para manejar la construcci�n de dialogs de manera m�s sencilla
 * @author Javier
 *
 */
public abstract class DialogController {
	
	/**
	 * <b>Ejemplo:</b><br><br>
	 * 
	 * String s = "HOLA";<br>
	 * DialogController d = new DialogController(){<br>
		public void clickAccept() {<br>
					finish();<br>				
				}<br>
			};<br>
			d.showInformDialog(this, s);<br> 
	 */
	public DialogController(){
		
	}
	
	/**
	 * M�todo para definir la acci�n de pulsar el bot�n aceptar
	 */
	public abstract void clickAccept();
	
	/**
	 * M�todo que genera un InformDialog con un texto y un bot�n de Aceptar.
	 * La acci�n del bot�n Aceptar debe haberse definido en el m�todo
	 * <b>clickAccept()</b>
	 * @param context
	 * @param message
	 */
	public void showInformDialog(Context context, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		
		builder.setPositiveButton("Aceptar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				clickAccept();
			}
		});
		
		builder.setCancelable(false);
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * Crea un dialog que muestra un mensaje al usuario y un bot�n de "Aceptar"
	 * @param context la Activity sobre la que se quiere mostrar el dialog
	 * @param message el mensaje a mostrar
	 */
	public static void createInformDialog(Context context, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		
		builder.setPositiveButton("Aceptar", new OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		
		builder.setCancelable(false);
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
}
