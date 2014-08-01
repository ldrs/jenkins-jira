package rd.com.huma.jenkinsjira;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.VariableResolver;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import  org.apache.commons.codec.binary.Base64;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link JenkinsJira} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked.
 *
 * @author Kohsuke Kawaguchi
 */

public class JenkinsJira extends Builder {


    private String issue;
	private String nameField;
	private String value;
	private String jiraUser;
	private String password;
	private String url;

	// Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public JenkinsJira(String url, String jiraUser,  String password,  String issue, String nameField, String value) {
    	this.issue = issue;
    	this.nameField = nameField;
    	this.value = value;
    	this.url = url;
    	this.jiraUser= jiraUser;
    	this.password = password;
    }
    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getIssue() {
		return issue;
	}

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getNameField() {
		return nameField;
	}
    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getValue() {
		return value;
	}

    public String getJiraUser() {
		return jiraUser;
	}

    public String getPassword() {
		return password;
	}
    public String getUrl() {
		return url;
	}


    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
//    	if (getDescriptor().getUrl() == null){
//    		listener.getLogger().append("URL must have a value");
//    	}
    	VariableResolver<String> resolve = build.getBuildVariableResolver();

    	String issueConvertido =Util.replaceMacro(issue, resolve);
    	String valorConvertido = Util.replaceMacro(value, resolve);

    	String urlJira = "http://"+ this.jiraUser + ":" + password + "@" + url;

    	;
    	URL url;
		try {
			url = new URL(urlJira + "rest/api/2/issue/" +issueConvertido );


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

			String valor =  new StringBuilder().append("{\"fields\" : {\"").append(nameField).append("\" :  \"").append(valorConvertido).append("\"}}").toString();
			listener.getLogger().println("valor");

			out.write(valor);
			out.flush();
			out.close();
			System.err.println(httpCon.getResponseCode());
			System.err.println(httpCon.getResponseMessage());

		} catch (IOException e) {

			listener.getLogger().append("Error");
			listener.getLogger().println(e.getMessage());

			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
        return true;
    }

    private String convertir(VariableResolver resolve, String valor){
    	Object tmp = resolve.resolve(valor);
    	if (tmp == null){
    		return valor;
    	}
    	return tmp.toString();

    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link JenkinsJira}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private String jiraUser;

        private String password;

        private String url;



        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
         *      <p>
         *      Note that returning {@link FormValidation#error(String)} does not
         *      prevent the form from being saved. It just means that a message
         *      will be displayed to the user.
         */
        public FormValidation doCheckName(@QueryParameter String value)     throws IOException, ServletException {
            if (value.length() == 0){
                return FormValidation.error("Please fill the value");
            }

            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Configuracion de Jenkins-Jira";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
        	this.jiraUser = formData.getString("jiraUser");
        	this.password = formData.getString("password");
        	this.url = formData.getString("url");

            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }


        public String getJiraUser() {
			return jiraUser;
		}

        public String getPassword() {
			return password;
		}
        public String getUrl() {
			return url;
		}
    }
}