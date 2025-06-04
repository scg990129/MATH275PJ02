package com.asuscomm.scg990129.elac.hw.math272;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class DefaultDoubleTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public void setValue(Object value) {
        if (value instanceof Double) {
            setText( String.format("%07.4f", (Double) value));
        } else {
            setText(value == null ? "" : value.toString());
        }
        setHorizontalAlignment(SwingConstants.RIGHT);
    }
}
