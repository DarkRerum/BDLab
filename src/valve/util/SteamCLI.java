package valve.util;

import valve.steam.Account;
import valve.steam.Currency;
import valve.steam.Language;
import valve.steam.Product;

import java.util.List;

/**
 * Created by Nikita on 28.10.2016.
 */
public class SteamCLI {

	public void executeCommand(String[] input) {
		switch (input[0]) {
			case "account":
				processAccountCommands(input);
				break;
			case "help":
				printHelp();
				break;
			case "product":
				processProductCommands(input);
				break;
			default:
				System.err.println(input[0] + ": no such command");
				System.exit(1);
		}
	}

	private void processAccountCommands(String[] input) {
		if (input.length < 3) {
			System.out.println("error: insufficient argument count (exepected: steam account <command> <arg1> <arg2> .. <argN>");
			System.exit(1);
		}

		switch (input[1]) {
			case "printdata":
				printAccountData(input[2]);
				break;
			case "ownedproducts":
				printOwnedProducts(input[2]);
				break;
			case "add":
				addNewAccount(input);
				break;
			default:
				System.err.println(input[0] + " " + input[1] + ": no such command");
		}
	}

	private void processProductCommands(String[] input) {
		if (input.length < 3) {
			System.out.println("error: insufficient argument count (exepected: steam product <command> <arg1> <arg2> .. <argN>");
			System.exit(1);
		}

		switch (input[1]) {
			case "price":
				printProductPrice(input[2], input[3]);
				break;
			default:
				System.err.println(input[0] + " " + input[1] + ": no such command");
		}
	}

	private  void printProductPrice(String productName, String currency) {
		try {
			Currency c = Currency.getFromName(currency);
			Product p = Product.getFromName(productName);
			System.out.println(p.getPrice(c));
		}
		catch (Exception e) {
			System.err.println("Could not fetch data for this product and price");
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	public void printHelp() {
		System.out.println("Available commands: ");
		System.out.println("help");
		System.out.println("account add <accountname> <username> <email> <language>");
		System.out.println("account ownedproducts <accountname>");
		System.out.println("account printdata <accountname>");
		System.out.println("product price <productname> <currency>");

	}

	private void printAccountData(String accountName) {
		try {
			Account a = Account.getFromName(accountName);
			System.out.println("Account name: " + a.getName());
			System.out.println("Account username: " + a.getUserName());
			System.out.println("Account email: " + a.getEmail());
			System.out.println("Account language: " + a.getLanguage().getName());
			System.out.println("Account phone number: " + a.getPhoneNumber());
		}
		catch (Exception e) {
			System.err.println("Could not fetch data for this account");
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	private void printOwnedProducts(String accountName) {
		try {
			Account a = Account.getFromName(accountName);
			List<Product> productList = a.getOwnedProducts();

			if (productList.isEmpty()) {
				System.out.println(accountName + " owns no products:");
				System.exit(0);
			}
			System.out.println(accountName + " owns these products:");

			for (Product p : productList) {
				System.out.println(p.getName());
			}
		}
		catch (Exception e) {
			System.err.println("Could not fetch data for this account: " + accountName);
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	private void addNewAccount(String[] input) {
		try {
			Language l = Language.getFromName(input[5]);
			Account a = new Account(input[2], input[3], input[4], l);
		}
		catch (Exception e) {
			System.out.println("Could not create an account");
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
}
