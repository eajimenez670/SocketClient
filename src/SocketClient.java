import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;

public class SocketClient {

	private final String HOST = "192.168.0.6";
	private final int PUERTO = 9000;

	public void Initialize() {
		try {
			Socket sc = new Socket(HOST, PUERTO);
			Saldo saldo = getDataUser();

			ObjectOutputStream out = new ObjectOutputStream(sc.getOutputStream());
			out.writeObject(saldo);
			out.flush();

			if (saldo.getTypeOperation() == 1) {
				DataInputStream in = new DataInputStream(sc.getInputStream());
				int jsonLength = in.readInt();
				byte[] jsonBytes = new byte[jsonLength];
				in.readFully(jsonBytes);
				String listaObjetosJson = new String(jsonBytes);
				List<Saldo> listaObjetos = deserializarListaObjetos(listaObjetosJson);
				for (Saldo objeto : listaObjetos) {
					System.out.println("ID: " + objeto.getId() + ", Nombre: " + objeto.getNombrePersona() + ", Cuenta: "
							+ objeto.getCuenta() + ", Saldo: " + objeto.getSaldo());
				}
			} else {
				DataInputStream in = new DataInputStream(sc.getInputStream());
				String mensaje_recibido = in.readUTF();
				System.out.println(mensaje_recibido);
			}

			sc.close();

		} catch (IOException ex) {
			Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private Saldo getDataUser() {
		Saldo saldo = new Saldo();
		Scanner scanner = new Scanner(System.in);
		BigDecimal cuenta = null;
		String nombre = null;
		double saldoSc = 0;
		int id = 0;

		System.out.print(
				"Que operación desea realizar en el banco Poli: (1. Consultar. 2. Almacenar. 3. Editar. 4. Eliminar)");
		int type = scanner.nextInt();

		switch (type) {
		case 1:
			// Obtener datos.
			saldo.setTypeOperation(1);
			break;
		case 2:
			// Guardar.
			System.out.print("Cuenta:");
			cuenta = scanner.nextBigDecimal();
			System.out.print("Nombre Persona:");
			nombre = scanner.next();
			System.out.print("Saldo:");
			saldoSc = scanner.nextDouble();

			saldo.setTypeOperation(2);
			saldo.setCuenta(cuenta);
			saldo.setNombrePersona(nombre);
			saldo.setSaldo(saldoSc);

			break;
		case 3:
			// Actualizar.
			System.out.print("ID:");
			id = scanner.nextInt();
			System.out.print("Cuenta:");
			cuenta = scanner.nextBigDecimal();
			System.out.print("Nombre Persona:");
			nombre = scanner.next();
			System.out.print("Saldo:");
			saldoSc = scanner.nextDouble();

			saldo.setTypeOperation(3);
			saldo.setId(id);
			saldo.setCuenta(cuenta);
			saldo.setNombrePersona(nombre);
			saldo.setSaldo(saldoSc);
			break;
		case 4:
			// Eliminar.
			System.out.print("ID:");
			id = scanner.nextInt();

			saldo.setTypeOperation(4);
			saldo.setId(id);
			break;

		default:
			// Default secuencia de sentencias.
			break;
		}

		return saldo;
	}

	private static List<Saldo> deserializarListaObjetos(String json) {
		// Utiliza la biblioteca Gson para convertir la cadena JSON en una lista de
		// objetos
		Gson gson = new Gson();
		// Supongamos que MiObjeto.class es la clase de tus objetos
		return gson.fromJson(json, new TypeToken<List<Saldo>>() {
		}.getType());
	}

}
