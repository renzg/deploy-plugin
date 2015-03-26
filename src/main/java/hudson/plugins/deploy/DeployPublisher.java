package hudson.plugins.deploy;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.VariableResolver;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Deploys WAR to a container.
 * 
 * @author Kohsuke Kawaguchi
 */
public class DeployPublisher extends Notifier implements Serializable {
	private List<ContainerAdapter> adapters;
	public final String contextPath;

	public final String war;
	public final boolean onFailure;

	/**
	 * @deprecated Use {@link #getAdapters()}
	 */
	public final ContainerAdapter adapter = null;

	@DataBoundConstructor
	public DeployPublisher(List<ContainerAdapter> adapters, String war,
			String contextPath, boolean onFailure) {
		this.adapters = adapters;
		this.war = war;
		this.onFailure = onFailure;
		this.contextPath = contextPath;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		if (build.getResult().equals(Result.SUCCESS) || onFailure) {
			// expand context path using build env variables
			String contextPath = expandVariable(
					build.getBuildVariableResolver(),
					build.getEnvironment(listener), this.contextPath);
			for (FilePath warFile : build.getWorkspace().list(this.war)) {
				for (ContainerAdapter adapter1 : this.getAdapters()) {
					System.out.println("准备部署：" + warFile.getName()
							+ " 到容器,上下文:" + contextPath);
					if (!adapter1.redeploy(warFile, contextPath, build,
							launcher, listener)) {
						build.setResult(Result.FAILURE);
					}
				}
			}
		}

		return true;
	}

	protected String expandVariable(VariableResolver<String> variableResolver,
			EnvVars envVars, String variable) {
		return Util.replaceMacro(envVars.expand(variable), variableResolver);
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	public Object readResolve() {
		if (adapter != null) {
			if (adapters == null) {
				adapters = new ArrayList<ContainerAdapter>();
			}
			adapters.add(adapter);
		}
		return this;
	}

	/**
	 * Get the value of the adapterWrappers property
	 *
	 * @return The value of adapterWrappers
	 */
	public List<ContainerAdapter> getAdapters() {
		return adapters;
	}

	@Extension
	public static final class DescriptorImpl extends
			BuildStepDescriptor<Publisher> {
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		public String getDisplayName() {
			return Messages.DeployPublisher_DisplayName();
		}

		/**
		 * Sort the descriptors so that the order they are displayed is more
		 * predictable
		 */
		public List<ContainerAdapterDescriptor> getAdaptersDescriptors() {
			List<ContainerAdapterDescriptor> r = new ArrayList<ContainerAdapterDescriptor>(
					ContainerAdapter.all());
			Collections.sort(r, new Comparator<ContainerAdapterDescriptor>() {
				public int compare(ContainerAdapterDescriptor o1,
						ContainerAdapterDescriptor o2) {
					return o1.getDisplayName().compareTo(o2.getDisplayName());
				}
			});
			return r;
		}
	}

	private static final long serialVersionUID = 1L;
}
