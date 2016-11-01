package ca.concordia.jsdeodorant.eclipseplugin.util;

import java.net.URL;
import java.util.EnumSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.osgi.framework.Bundle;

import ca.concordia.jsdeodorant.analysis.decomposition.Method;
import ca.concordia.jsdeodorant.analysis.decomposition.MethodType;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclaration;
import ca.concordia.jsdeodorant.analysis.decomposition.TypeDeclarationKind;
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

	public static Image getMethodImage(Method method) {
		ImageDescriptor descriptor = getImageDescriptor(Constants.METHOD_ICON_IMAGE);
		EnumSet<MethodType> kinds = method.getKinds();
		if (kinds.contains(MethodType.ABSTRACT_METHOD) && !method.getOwner().getKinds().contains(TypeDeclarationKind.INTERFACE)) {
			ImageDescriptor overlay = getImageDescriptor(Constants.ABSTRACT_OVERLAY);
			Point overlayPosition = new Point(descriptor.getImageData().width - overlay.getImageData().width, 0);
			descriptor = getOverlayedImagesDescriptor(descriptor, overlay, overlayPosition);
		}
		if (kinds.contains(MethodType.DECRALRED_OUTSIDE_OF_CLASS_BODY)) {
			ImageDescriptor overlay = getImageDescriptor(Constants.PROTOTYPE_OVERLAY);
			Point overlayPosition = new Point(descriptor.getImageData().width - overlay.getImageData().width, 
					descriptor.getImageData().height - overlay.getImageData().height);
			descriptor = getOverlayedImagesDescriptor(descriptor, overlay, overlayPosition);
		}
		if (kinds.contains(MethodType.OVERRIDEN_METHOD)) {
			ImageDescriptor overlay = getImageDescriptor(Constants.OVERRIDING_OVERLAY);
			Point overlayPosition = new Point(0, descriptor.getImageData().height - overlay.getImageData().height);
			descriptor = getOverlayedImagesDescriptor(descriptor, overlay, overlayPosition);
		}
		if (kinds.contains(MethodType.OVERRIDING_METHOD)) {
			ImageDescriptor overlay = getImageDescriptor(Constants.OVERRIDDEN_OVERLAY);
			Point overlayPosition = new Point(0, 1);
			descriptor = getOverlayedImagesDescriptor(descriptor, overlay, overlayPosition);			
		}
		return descriptor.createImage();
	}

	public static Image getTypeImage(TypeDeclaration typeDeclaration) {
		if (typeDeclaration.getKinds().contains(TypeDeclarationKind.CLASS)) {
			return getImageDescriptor(Constants.CLASS_ICON_IMAGE).createImage();
		} else if (typeDeclaration.getKinds().contains(TypeDeclarationKind.ABSTRACT_CLASS)) {
			ImageDescriptor descriptor = getImageDescriptor(Constants.CLASS_ICON_IMAGE);
			ImageDescriptor overlay = getImageDescriptor(Constants.ABSTRACT_OVERLAY);
			Point overlayPosition = new Point(descriptor.getImageData().width - overlay.getImageData().width, 0);
			return getOverlayedImagesDescriptor(descriptor, overlay, overlayPosition).createImage(); 
		} else if (typeDeclaration.getKinds().contains(TypeDeclarationKind.INTERFACE)) {
			return getImageDescriptor(Constants.INTERFACE_ICON_IMAGE).createImage();
		}
		return null;
	}

}
