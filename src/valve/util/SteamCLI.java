package valve.util;

import valve.steam.Account;

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
			default:
				System.err.println(input[0] + ": no such command");
				System.exit(1);
		}
	}

	private void processAccountCommands(String[] input) {
		if (input.length != 3) {
			System.out.println("error: insufficient argument count (exepected: steam account <command> <account>");
			System.exit(1);
		}

		switch (input[1]) {
			case "printdata":
				printAccountData(input[2]);
				break;
			case "ownedproducts":
				printOwnedProducts(input[2]);
				break;
			default:
				System.err.println(input[0] + " " + input[1] + ": no such command");
		}
	}

	public void printHelp() {
		System.out.println("Available commands: ");
		System.out.println("help");
		System.out.println("account printdata <accountname>");
		System.out.println("account ownedproducts <accountname>");
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
			System.err.println("Could not ferch data for this account");
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	private void printOwnedProducts(String accountName) {
		try {
			Account a = Account.getFromName(accountName);

		}
		catch (Exception e) {
			System.err.println("Could not ferch data for this account");
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
