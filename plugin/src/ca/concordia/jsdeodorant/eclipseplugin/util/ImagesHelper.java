package ca.concordia.jsdeodorant.eclipseplugin.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;
import org.osgi.framework.Bundle;

import ca.concordia.jsdeodorant.eclipseplugin.activator.JSDeodorantPlugin;

public class ImagesHelper {

	public static ImageDescriptor getImageDescriptor(String name) {
		Bundle bundle = Platform.getBundle(JSDeodorantPlugin.PLUGIN_ID);
		IPath path = new Path(Constants.ICON_PATH + "/" + name);
		URL fileURL = FileLocator.find(bundle, path, null);
		return ImageDescriptor.createFromURL(fileURL);
	}

	public static ImageDescriptor getOverlayedImagesDescriptor(ImageDescriptor main, ImageDescriptor overlay, Point position) {
		CompositeImageDescriptor descriptor = new CompositeImageDescriptor() {
						
			@Override
			protected Point getSize() {
				return new Point(main.getImageData().width, main.getImageData().height);
			}
			
			@Override
			protected void drawCompositeImage(int arg0, int arg1) {
				drawImage(main.getImageData(), 0, 0);
				drawImage(overlay.getImageData(), position.x, position.y);
			}
		};
		
		return descriptor;
	}

}
