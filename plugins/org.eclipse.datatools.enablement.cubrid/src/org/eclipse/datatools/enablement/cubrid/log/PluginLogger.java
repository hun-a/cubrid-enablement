package org.eclipse.datatools.enablement.cubrid.log;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.enablement.cubrid.CUBRIDPlugin;

/**
 * Logger class for plug-in
 * 
 * @author Seunghun
 *
 */
public class PluginLogger {
	private String id;
	private ILog log;
	private final int INFO = IStatus.INFO;
	private final int ERROR = IStatus.ERROR;
	private final int WARNING = IStatus.WARNING;
	
	public PluginLogger(String id) {
		this.id = id;
		log = CUBRIDPlugin.getDefault().getLog();
		log.log(new Status(INFO, id, "New PluginLogger is created."));
	}
	
	public void info(String message) {
		log.log(new Status(INFO, id, message));
	}
	
	public void error(String message) {
		log.log(new Status(ERROR, id, message));
	}
	
	public void warn(String message) {
		log.log(new Status(WARNING, id, message));
	}
}
