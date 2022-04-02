/*
 * TemplateWindow.java
 *
 * Created on Fri Oct 10 15:12:03 EDT 2003
 *
 * Copyright (c) 2003 Spallation Neutron Source
 * Oak Ridge National Laboratory
 * Oak Ridge, TN 37830
 */

package xal.app.mysql2accelerator;

import xal.extension.application.*;
import xal.extension.application.smf.*;

import javax.swing.*;

/**
 * TemplateViewerWindow
 *
 * @author  somebody
 */
class Mysql2acceleratorWindow extends AcceleratorWindow implements SwingConstants {
    /** serialization ID */
    private static final long serialVersionUID = 1L;
    /** Creates a new instance of MainWindow */
    public Mysql2acceleratorWindow(final XalDocument aDocument) {
        super(aDocument);
        setSize(800, 600);
    }
}




