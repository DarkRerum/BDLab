package valve.util;

import valve.steam.*;

import java.util.List;

/**
 * Created by Nikita on 28.10.2016.
 */
public class SteamCLI {

	public void executeCommand(String[] input) {
		if (input.length < 1) {
			System.out.println("error: no Steam command supplied");
			System.exit(2);
		}
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
			case "price":
				processPriceCommands(input);
				break;
			case "order":
				processOrderCommands(input);
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

	private void processPriceCommands(String[] input) {
		if (input.length < 3) {
			System.out.println("error: insufficient argument count (exepected: steam price <command> <arg1> <arg2> .. <argN>");
			System.exit(1);
		}

		switch (input[1]) {
			case "add":
				addPriceData(input[2], input[3], input[4]);
				break;
			case "remove":
				removePriceData(input[2], input[3]);
				break;
			default:
				System.err.println(input[0] + " " + input[1] + ": no such command");
		}
	}

	private void processOrderCommands(String[] input) {
		if (input.length < 3) {
			System.out.println("error: insufficient argument count (exepected: steam order <command> <arg1> <arg2> .. <argN>");
			System.exit(1);
		}

		switch (input[1]) {
			case "create":
				createOrder(input[2]);
				break;
			case "additem":
				addItemToOrder(input[2], input[3], input[4]);
				break;
			case "removeitem":
				removeItemFromOrder(input[2], input[3]);
				break;
			default:
				System.err.println(input[0] + " " + input[1] + ": no such command");
				break;
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
		System.out.println("order create <accountname>");
		System.out.println("order additem <orderid> <productname> <currency>");
		System.out.println("order removeitem <orderid> <productname>");
		System.out.println("price add <productname> <currency> <value>");
		System.out.println("price remove <productname> <currency>");
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
			System.err.println("Could not create an account");
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	private void addPriceData(String productName, String currency, String value) {
		try {
			Product p = Product.getFromName(productName);
			Currency c = Currency.getFromName(currency);
			float v = Float.parseFloat(value);

			Price price = new Price(c, v);

			p.addPrice(price);
		}
		catch (Exception e) {
			System.err.println("Could not add a price");
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	private void removePriceData(String productName, String currency) {
		try {
			Product p = Product.getFromName(productName);
			Currency c = Currency.getFromName(currency);

			p.removePrice(c);
		}
		catch (Exception e) {
			System.err.println("Could not remove a price");
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	private void createOrder(String accountName) {
		try {
			Order o = new Order(Account.getFromName(accountName));
			System.out.println("Created an order with id " + o.getId());
		}
		catch (Exception e) {
			System.err.println("Could not create an order");
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	private void addItemToOrder(String orderIdStr, String productName, String currency) {
		try {
			long id = Long.parseLong(orderIdStr);
			Order o = Order.getFromId(id);
			Product p = Product.getFromName(productName);
			o.addItemToOrder(p, Currency.getFromName(currency));
			System.out.println("Successfully added " + productName + " to an order");
		}
		catch (Exception e) {
			System.err.println("Could not add product to an order");
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	private void removeItemFromOrder(String orderIdStr, String productName) {
		try {
			long id = Long.parseLong(orderIdStr);
			Order o = Order.getFromId(id);
			Product p = Product.getFromName(productName);
			o.removeItemFromOrder(p);
			//o.addItemToOrder(p, Currency.getFromName(currency));
			System.out.println("Successfully removed " + productName + " from order #" + id);
		}
		catch (Exception e) {
			System.err.println("Could not remove product from an order");
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
