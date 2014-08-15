package rd.com.huma.jenkinsjira;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;

/**
*
* @author Priamo Germosen
*/
public class JiraTicketParameter extends SimpleParameterDefinition {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public JiraTicketParameter(String name, String description) {
		super(name, description);
		// TODO Auto-generated constructor stub
	}

	public JiraTicketParameter(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ParameterValue createValue(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
		// TODO Auto-generated method stub
		return null;
	}



}
