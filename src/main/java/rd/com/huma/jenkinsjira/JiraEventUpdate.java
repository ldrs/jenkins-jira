package rd.com.huma.jenkinsjira;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;

/**
*
* @author Priamo Germosen
*/
public class JiraEventUpdate {

	private final JiraConfig jiraConfig;

	public JiraEventUpdate(JiraConfig jiraConfig) {
		this.jiraConfig = jiraConfig;
	}


	public boolean update(String issue, String value, Appendable logger){



    	String urlJira = "http://"+ jiraConfig.getUser()+ ":" + jiraConfig.getPassword() + "@" + jiraConfig.getUrl();

    	URL url;
		try {
			url = new URL(urlJira + "rest/api/2/issue/" +issue );


			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection() ;
			httpCon.setRequestMethod("PUT");
			if (url.getUserInfo() != null) {
				String basicAuth = "Basic " + new String(new Base64().encode(url.getUserInfo().getBytes()));
				httpCon.setRequestProperty("Authorization", basicAuth);
			}

			httpCon.setDoOutput(true);
			httpCon.setDoInput(true);
			httpCon.setInstanceFollowRedirects(false);
			httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setRequestProperty("Accept", "application/json");
		//	httpCon.setRequestProperty("Authorization", "Basic " + authStringEnc);



			OutputStreamWriter out = new OutputStreamWriter(
		    httpCon.getOutputStream());

			String restValue =  new StringBuilder().append("{\"fields\" : {\"").append(jiraConfig.getField()).append("\" :  \"").append(value).append("\"}}").toString();
			logger.append(restValue);

			out.write(restValue);
			out.flush();
			out.close();
			if (httpCon.getResponseCode()==200 || httpCon.getResponseCode()==204){
				return true;
			}else{
				logger.append("Error -> Ejecutando el servicio web, codigo ").append(httpCon.getResponseCode()+"").append(httpCon.getResponseMessage());
			}
		} catch (IOException e) {
			try {
				logger.append("Error -> ").append(e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return false;
		}


		return true;
	}

}