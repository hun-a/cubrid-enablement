/*******************************************************************************
 * Copyright (c) 2016 Seunghun.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Seunghun - initial API and implementation
 *******************************************************************************/
package org.eclipse.datatools.enablement.cubrid.internal.ui.connection;

import org.eclipse.datatools.connectivity.ui.wizards.ExtensibleProfileDetailsPropertyPage;
import org.eclipse.datatools.enablement.internal.cubrid.connection.ICUBRIDConnectionProfileConstants;

public class CUBRIDDBProfilePropertyPage extends ExtensibleProfileDetailsPropertyPage {
	public CUBRIDDBProfilePropertyPage() {
		super(ICUBRIDConnectionProfileConstants.CUBRID_CATEGORY_ID);
	}
}
