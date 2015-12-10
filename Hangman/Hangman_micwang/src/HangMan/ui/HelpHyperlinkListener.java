package HangMan.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class HelpHyperlinkListener  {
	private HangManUI ui;
	
	public HelpHyperlinkListener(HangManUI initUI){
            ui = initUI;
            ui.getHelpPane().addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        /*if (Desktop.isDesktopSupported()) {
                            try {
                                Desktop.getDesktop().browse(e.getURL().toURI());
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            } catch (URISyntaxException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }*/
                        ui.loadRemoteHelpPage(e.getURL());
                    }
                }
            });
	}  
        
}
