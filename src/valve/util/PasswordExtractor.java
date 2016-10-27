package valve.util;

import java.io.*;

public class PasswordExtractor {
	private String m_passPath = "./pass";
	
	private String m_username;
	private String m_password;
	
	public PasswordExtractor() {
		loadLoginInfoFromFile();
	}
	
	public PasswordExtractor(String path) {
		m_passPath = path;
		loadLoginInfoFromFile();
	}
	
	public String getUsername() {
		return m_username;
	}
	
	public String getPassword() {
		return m_password;
	}
	
	private void loadLoginInfoFromFile() {
		File loginFile = null;
		BufferedReader br = null;
		try {
			loginFile = new File(m_passPath);
			br = new BufferedReader(new FileReader(loginFile));
			try {
				String line;
				if ((line = br.readLine()) != null) {
					m_username = line;
				}
				if ((line = br.readLine()) != null) {
					m_password = line;
				}
			}
			finally {
				br.close();
			}
		}
		catch (IOException e) {
			System.err.println("Could not load file: " + m_passPath + ".");
			return;
		}
	}
	
}