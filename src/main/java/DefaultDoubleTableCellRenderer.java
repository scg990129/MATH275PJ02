import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.DecimalFormat;

public class DefaultDoubleTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public void setValue(Object value) {
        if (value instanceof Double) {
            setText( String.format("%07.4f", ((Double) value).doubleValue()) );
        } else {
            setText(value == null ? "" : value.toString());
        }
        setHorizontalAlignment(SwingConstants.RIGHT);
    }
}
