import java.sql.*;
import java.io.*;
import oracle.jdbc.driver.OracleDriver;
import valve.steam.Steam;
import valve.steam.Language;
import valve.util.PasswordExtractor;

public class Main {
	
	public static void main(String[] args) {
		//SteamAPI api = null;
		
		PasswordExtractor passwordExtractor = new PasswordExtractor();			
		

		
		try {
			//api = new SteamAPI("jdbc:oracle:thin:@localhost:1521:orbis", passwordExtractor.getUsername(), passwordExtractor.getPassword());
			Steam.getInstance().resetConnection("jdbc:oracle:thin:@localhost:1521:orbis", passwordExtractor.getUsername(),
				passwordExtractor.getPassword());
			//Steam.getInstance().createAccount("WKLE", "EWeq", "email@mail.mail");
			Language l = new Language("de");
			System.out.println(l.getId());
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
		//api.createAccount("WKLE", "EWeq", "email@mail.mail");
	}
}
