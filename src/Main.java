import java.sql.*;
import java.io.*;
import oracle.jdbc.driver.OracleDriver;

public class Main {
	public static void main(String[] args) {
		SteamAPI api = null;
		try {
			api = new SteamAPI("jdbc:oracle:thin:@localhost:1521:orbis", "./pass");
		}
		catch (IOException ioEx) {
			System.out.println(ioEx.getMessage()); 
		}
		catch (ClassNotFoundException cnfEx) {
			System.out.println(cnfEx.getMessage()); 
		}
		catch (SQLException sqlEx) {
			System.out.println(sqlEx.getMessage()); 
		}
		api.createAccount("WKLE", "EWeq", "email@mail.mail");
	}
}