 /*******************************************************************************
  * Copyright (c) 2016. Seunghun
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Seunghun - initial API and implementation
  *******************************************************************************/
package org.eclipse.datatools.enablement.cubrid.ddl;

import org.eclipse.datatools.enablement.cubrid.ddl.shared.CUBRIDDdlGeneratorHelper;

/**
 * This class uses the CUBRIDDilBuilder to generates the sql scripts.
 * 
 * @author seunghun_kim@cubrid.com
 *
 */
public class CUBRIDDdlGenerator extends CUBRIDDdlGeneratorHelper {
	public CUBRIDDdlGenerator() {
		super();
		this.setBuilder(new CUBRIDDdlBuilder());
	}
}
