package rd.com.huma.jenkinsjira;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;

public class Prueba {



	private static String jiraUser = "pds";
	private static String password = "pdsv229";
	//private static String urlJira = "http://172.16.7.42:8080/";
	private static String urlJira = "http://" + jiraUser + ":" + password +"@"+ "172.16.7.42:8080/";
	private static String issue = "FRAMEWORK-424";


	public static void main(String[] args) {


    	String authString = jiraUser + ":" + password;

		byte[] authEncBytes = new Base64().encode(authString.getBytes());
		String authStringEnc = new String(authEncBytes);

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

			out.write("{\"fields\" : {\"customfield_10810\" : \"http://google.com\"}}");
			out.flush();
			out.close();
			System.err.println(httpCon.getResponseCode());
			System.err.println(httpCon.getResponseMessage());


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
