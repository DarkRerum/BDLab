import java.io.*;
import java.sql.*;

import valve.steam.*;
import valve.util.PasswordExtractor;
import valve.util.SteamCLI;

public class Main {
	
	public static void main(String[] args) {
		PasswordExtractor passwordExtractor = new PasswordExtractor();
		
		try {
			Steam.getInstance().resetConnection("jdbc:oracle:thin:@localhost:1521:orbis",
					passwordExtractor.getUsername(), passwordExtractor.getPassword());

			SteamCLI steamCli = new SteamCLI();
			steamCli.executeCommand(args);
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
