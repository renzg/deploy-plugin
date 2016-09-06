package hudson.plugins.deploy.tomcat;

import hudson.EnvVars;
import hudson.plugins.deploy.PasswordProtectedAdapterCargo;
<<<<<<< HEAD
import hudson.util.Scrambler;

import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.tomcat.TomcatWAR;
=======
import hudson.util.VariableResolver;
>>>>>>> jenkinsci/master

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.tomcat.TomcatWAR;

/**
 * Base class for Tomcat adapters.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class TomcatAdapter extends PasswordProtectedAdapterCargo {
<<<<<<< HEAD
	/**
	 * Top URL of Tomcat.
	 */
	public final String url;

	public TomcatAdapter(String url, String password, String userName) {
		super(userName, password);
		this.url = url;
	}

	public void configure(Configuration config) {
		super.configure(config);
		try {
			URL _url = new URL(buildUrl("manager"));
			config.setProperty(RemotePropertySet.URI, _url.toExternalForm());
			//config.setProperty(RemotePropertySet.PASSWORD,
			//		Scrambler.descramble(this.getPassword()));
			//config.setProperty(RemotePropertySet.USERNAME, this.userName);
		} catch (MalformedURLException e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * Create a Tomcat-specific Deployable object from the given file object.
	 * 
	 * @param deployableFile
	 *            The file to deploy.
	 * @return A Tomcat-specific Deployable object.
	 * @see hudson.plugins.deploy.CargoContainerAdapter#createWAR(java.io.File)
	 */
	@Override
	protected WAR createWAR(File deployableFile) {
		return new TomcatWAR(deployableFile.getAbsolutePath());
	}

	protected String buildUrl(String commandPath) {
		StringBuilder sb = new StringBuilder();
		if (url != null) {
			String turl = url.trim();
			sb.append(turl);

			sb.append(turl.endsWith("/") ? commandPath : "/" + commandPath);
		}

		return sb.toString();
	}
=======
    /**
     * Top URL of Tomcat.
     */
    public final String url;

    public TomcatAdapter(String url, String password, String userName) {
        super(userName, password);
        this.url = url;
    }

    public void configure(Configuration config, EnvVars envVars, VariableResolver<String> resolver) {
        super.configure(config, envVars, resolver);
        try {
            URL _url = new URL(expandVariable(envVars, resolver, this.url) + "/manager");
            config.setProperty(RemotePropertySet.URI, _url.toExternalForm());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Create a Tomcat-specific Deployable object from the given file object.
     * @param deployableFile The file to deploy.
     * @return A Tomcat-specific Deployable object.
     * @see hudson.plugins.deploy.CargoContainerAdapter#createWAR(java.io.File)
     */
    @Override
    protected WAR createWAR(File deployableFile) {
        return new TomcatWAR(deployableFile.getAbsolutePath());
    }
>>>>>>> jenkinsci/master
}
