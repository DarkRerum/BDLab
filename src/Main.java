import java.io.*;
import java.sql.*;
import valve.steam.Account;
import valve.steam.Language;
import valve.steam.Steam;
import valve.util.PasswordExtractor;

public class Main {
	
	public static void main(String[] args) {
		PasswordExtractor passwordExtractor = new PasswordExtractor();
		
		try {
			Steam.getInstance().resetConnection("jdbc:oracle:thin:@localhost:1521:orbis",
					passwordExtractor.getUsername(), passwordExtractor.getPassword());
			//Steam.getInstance().createAccount("WKLE", "EWeq", "email@mail.mail");
			Language l = Language.getFromName("en");
			System.out.println(l.getId());
			System.out.println(l.getName());
			
			Account a = Account.getFromName("rerum");
			System.out.println(a.getId());
			System.out.println(a.getName());
			System.out.println(a.getUserName());
			System.out.println(a.getEmail());
			System.out.println(a.getLanguage().getName());

			a.setUserName("BOSS");
			System.out.println(a.getUserName());
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
