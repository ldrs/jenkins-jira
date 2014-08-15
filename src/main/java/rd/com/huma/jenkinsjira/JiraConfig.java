package rd.com.huma.jenkinsjira;

/**
*
* @author Priamo Germosen
*/
public class JiraConfig {

	private final String user;
	private final String password;
	private final String url;
	private final String field;


	public JiraConfig(String user, String password, String url, String field) {
		this.user = user;
		this.password = password;
		this.url = url;
		this.field = field;
	}


	public String getUser() {
		return user;
	}


	public String getPassword() {
		return password;
	}


	public String getUrl() {
		return url;
	}


	public String getField() {
		return field;
	}



}