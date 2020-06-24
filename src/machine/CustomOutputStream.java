package machine;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class CustomOutputStream extends OutputStream {
	static final char UNIT_SEPARATOR = 31;
    private JTextArea textArea;
    private JPanel panel;
    private int numTicks;
    
    public CustomOutputStream(JTextArea textArea, JPanel panel) {
        this.textArea = textArea;
        this.panel = panel;
        this.numTicks = 0;
    }
     
    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
    	char c = (char) b;
    	if (c==UNIT_SEPARATOR) {
    		textArea.setCaretPosition(textArea.getDocument().getLength());
    		textArea.update(textArea.getGraphics());
    	}
    	else {
    		textArea.append(String.valueOf(c));
    		textArea.setCaretPosition(textArea.getDocument().getLength());
    	}
    	this.numTicks++;
    }
}
