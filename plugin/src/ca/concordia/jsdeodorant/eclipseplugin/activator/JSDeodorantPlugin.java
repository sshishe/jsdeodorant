package ca.concordia.jsdeodorant.eclipseplugin.activator;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import ca.concordia.jsdeodorant.eclipseplugin.util.Constants;

/**
 * The activator class controls the plug-in life cycle
 */
public class JSDeodorantPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "jsdeodorant-eclipseplugin"; //$NON-NLS-1$
	
	// The shared instance
	private static JSDeodorantPlugin plugin;
	
	/**
	 * The constructor
	 */
	public JSDeodorantPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static JSDeodorantPlugin getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String name) {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		IPath path = new Path(Constants.ICON_PATH + "/" + name);
		URL fileURL = FileLocator.find(bundle, path, null);
		return ImageDescriptor.createFromURL(fileURL);
	}

}
