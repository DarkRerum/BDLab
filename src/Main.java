import java.io.*;
import java.sql.*;
import java.util.List;

import valve.steam.*;
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

			Product p = Product.getFromName("Fallout: New Vegas");
			System.out.println(p.getId());
			System.out.println(p.getName());

			System.out.println(p.getPrice(Currency.getFromName("usd")));

			List<Product> productList = a.getOwnedProducts();

			for (Product item : productList) {
				System.out.println(item.getName());
			}

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
