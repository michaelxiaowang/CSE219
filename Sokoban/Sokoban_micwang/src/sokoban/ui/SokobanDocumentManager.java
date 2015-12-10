package sokoban.ui;

import application.Main.SokobanPropertyType;
import java.io.IOException;
import java.io.Serializable;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import sokoban.game.SokobanGameData;
import sokoban.game.SokobanGameStateManager;

public class SokobanDocumentManager implements Serializable{

    private SokobanUI ui;

    public SokobanDocumentManager(SokobanUI initUI) {
        ui = initUI;
    }

    // TODO: sokoban document manager
}
