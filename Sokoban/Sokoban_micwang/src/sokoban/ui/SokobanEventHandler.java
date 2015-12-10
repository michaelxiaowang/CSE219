package sokoban.ui;

import application.Main.SokobanPropertyType;
import java.io.IOException;
import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import sokoban.file.SokobanFileLoader;
import sokoban.game.SokobanGameStateManager;
import xml_utilities.InvalidXMLFileFormatException;

public class SokobanEventHandler {

    private SokobanUI ui;
    private SokobanBlockDestination SokobanBlockDestination;

    /**
     * Constructor that simply saves the ui for later.
     *
     * @param initUI
     */
    public SokobanEventHandler(SokobanUI initUI) {
        ui = initUI;
    }

    /**
     * This method responds to when the user wishes to switch between the Game,
     * Stats, and Help screens.
     *
     * @param uiState The ui state, or screen, that the user wishes to switch
     * to.
     */
    public void respondToSwitchScreenRequest(SokobanUI.SokobanUIState uiState) {
        ui.getDestinations().clear(); //Make sure there are no residual destinations
        ui.getUndoManager().getStates().clear(); //Make sure undo states were cleared
        SokobanGameStateManager gsm = ui.getGSM();
        gsm.endGame();
        ui.changeScreen(uiState);
    }

    /**
     * This method responds to when the user presses the new game method.
     */
    public void respondToNewGameRequest() {
        SokobanGameStateManager gsm = ui.getGSM();
        gsm.makeNewGame();
    }
    
    public void respondToSelectLevelRequest(String level, SokobanUI.SokobanUIState uiState)
    {
        SokobanGameStateManager gsm = ui.getGSM();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        gsm.makeNewGame();
        
        ArrayList<String> levels = props.getPropertyOptionsList(SokobanPropertyType.LEVEL_OPTIONS);
        ArrayList<String> levelData = props.getPropertyOptionsList(SokobanPropertyType.LEVEL_FILES);
        ArrayList<String> levelTextData = props.getPropertyOptionsList(SokobanPropertyType.LEVEL_TEXT_FILES);
        int levelIndex = levels.indexOf(level);
        String levelDataFile = levelData.get(levelIndex);
        String levelTextFile = levelTextData.get(levelIndex);
        try
        {
            ui.setGrid(SokobanFileLoader.loadSokobanFile(levelDataFile));
            ui.initSokobanUI();
            ui.drawGrid(ui.getGrid());
            for(int r = 0; r < ui.getGrid().length; r++)
            {
                for(int c = 0; c < ui.getGrid()[0].length; c++)
                {
                    if (ui.getGrid()[r][c] == 4)
                    {
                        ui.setSokRow(r);
                        ui.setSokCol(c);
                    }
                    else if (ui.getGrid()[r][c] == 3)
                    {
                        SokobanBlockDestination dest = new SokobanBlockDestination(r,c);
                        ui.getDestinations().add(dest);
                    }
                }
            }
        }
        catch(IOException ioe)
        {
            ui.getErrorHandler().processError(SokobanPropertyType.INVALID_DICTIONARY_ERROR_TEXT);
            System.exit(0);
        }  
    }
    
    /**
     * This method responds to a move request from arrow keys, it will move sokoban if the move is valid
     * Move is valid if: space to move to is empty, or contains a box and the next space after the box is empty
     * @param ke the KeyEvent
     */
    public void respondToMoveRequest(KeyEvent ke)
    {
        KeyCode keyCode = ke.getCode();
        Image sokobanImage = new Image("file:images/Sokoban.png");
        
        SokobanGameStateManager gsm = ui.getGSM();
        if(gsm.isGameInProgress())
        {
            if (keyCode == KeyCode.UP)
            {
                respondToMoveUpRequest();
            }
        
            if (keyCode == KeyCode.DOWN)
            {
                respondToMoveDownRequest();
            }
        
            if (keyCode == KeyCode.LEFT)
            {
                respondToMoveLeftRequest();
            }
        
            if (keyCode == KeyCode.RIGHT)
            {
                respondToMoveRightRequest();
            }
        
            if (keyCode == KeyCode.U)
            {
                respondToUndoRequest();
            }
            
            checkGameStatus();
        }
    }
    
    public void respondToMoveUpRequest()
    {
        SokobanGameStateManager gsm = ui.getGSM();
        int sokRow = ui.getSokRow();
        int sokCol = ui.getSokCol();
        
        if((ui.getGrid()[sokRow][sokCol-1] != 1 && ui.getGrid()[sokRow][sokCol-1] != 2) || 
           ((ui.getGrid()[sokRow][sokCol-1] == 2) && (ui.getGrid()[sokRow][sokCol-2] != 1) && (ui.getGrid()[sokRow][sokCol-2] != 2)))
        {
            int [][] undoState = new int[ui.getGrid().length][ui.getGrid()[0].length];
            for(int r = 0; r < ui.getGrid().length; r++)
            {
                for(int c = 0; c < ui.getGrid()[0].length; c++)
                {
                    undoState[r][c] = ui.getGrid()[r][c];
                }
            }
            ui.getUndoManager().addState(undoState);
            if(ui.getGrid()[sokRow][sokCol-1] == 2)
            {
                MediaPlayer play = new MediaPlayer(ui.getMoveBlockSound());
                ui.getGrid()[sokRow][sokCol-2] = 2;
                play.play();
                if((ui.getGrid()[sokRow][sokCol-3] == 1 && ui.getGrid()[sokRow+1][sokCol-2] == 1)
                        || (ui.getGrid()[sokRow][sokCol-3] == 1 && ui.getGrid()[sokRow-1][sokCol-2] == 1 ))
                {
                    boolean occupiesDestination = false;
                    for(SokobanBlockDestination b: ui.getDestinations())
                    {
                        if(b.getCol()==sokCol-2 && b.getRow() ==sokRow)
                        {
                            occupiesDestination = true;
                            break;
                        }
                    }
                    if(!occupiesDestination)
                    {
                        gsm.endGame();
                        loseBox(ui.getStage());
                        play = new MediaPlayer(ui.getLoseSound());
                        play.play();
                    }
                }
            }
            else
            {
                MediaPlayer play = new MediaPlayer(ui.getMoveSound());
                play.play();
            }
            ui.getGrid()[sokRow][sokCol] = 0;
            ui.setSokCol(ui.getSokCol()-1);
            sokCol = ui.getSokCol();
            ui.getGrid()[sokRow][sokCol] = 4;
        }
        else
        {
            MediaPlayer play = new MediaPlayer(ui.getFailSound());
            play.play();
        }
    }
    
    public void respondToMoveDownRequest()
    {
        SokobanGameStateManager gsm = ui.getGSM();
        int sokRow = ui.getSokRow();
        int sokCol = ui.getSokCol();
        if((ui.getGrid()[sokRow][sokCol+1] != 1 && ui.getGrid()[sokRow][sokCol+1] != 2) || 
           ((ui.getGrid()[sokRow][sokCol+1] == 2) && (ui.getGrid()[sokRow][sokCol+2] != 1) && (ui.getGrid()[sokRow][sokCol+2] != 2)))
        {
            int [][] undoState = new int[ui.getGrid().length][ui.getGrid()[0].length];
            for(int r = 0; r < ui.getGrid().length; r++)
            {
                for(int c = 0; c < ui.getGrid()[0].length; c++)
                {
                    undoState[r][c] = ui.getGrid()[r][c];
                }
            }
            ui.getUndoManager().addState(undoState);
            if(ui.getGrid()[sokRow][sokCol+1] == 2)
            {
                MediaPlayer play = new MediaPlayer(ui.getMoveBlockSound());
                ui.getGrid()[sokRow][sokCol+2] = 2;
                if((ui.getGrid()[sokRow][sokCol+3] == 1 && ui.getGrid()[sokRow+1][sokCol+2] == 1)
                        || (ui.getGrid()[sokRow][sokCol+3] == 1 && ui.getGrid()[sokRow-1][sokCol+2] == 1 ))
                {
                    boolean occupiesDestination = false;
                    for(SokobanBlockDestination b: ui.getDestinations())
                    {
                        if(b.getCol()==sokCol+2 && b.getRow() ==sokRow)
                        {
                            occupiesDestination = true;
                            break;
                        }
                    }
                    if(!occupiesDestination)
                    {
                        gsm.endGame();
                        loseBox(ui.getStage());
                        play = new MediaPlayer(ui.getLoseSound());
                        play.play();
                    }
                }
                play.play();;
            }
            else
            {
                MediaPlayer play = new MediaPlayer(ui.getMoveSound());
                play.play();
            }
            ui.getGrid()[sokRow][sokCol] = 0;
            ui.setSokCol(ui.getSokCol()+1);
            sokCol = ui.getSokCol();
            ui.getGrid()[sokRow][sokCol] = 4;
        }
        else
        {
            MediaPlayer play = new MediaPlayer(ui.getFailSound());
            play.play();
        }
    }
    
    public void respondToMoveLeftRequest()
    {
        SokobanGameStateManager gsm = ui.getGSM();
        int sokRow = ui.getSokRow();
        int sokCol = ui.getSokCol();
        if((ui.getGrid()[sokRow-1][sokCol] != 1 && ui.getGrid()[sokRow-1][sokCol] != 2) || 
           ((ui.getGrid()[sokRow-1][sokCol] == 2) && (ui.getGrid()[sokRow-2][sokCol] != 1) && (ui.getGrid()[sokRow-2][sokCol] != 2)))
        {
            int [][] undoState = new int[ui.getGrid().length][ui.getGrid()[0].length];
            for(int r = 0; r < ui.getGrid().length; r++)
            {
                for(int c = 0; c < ui.getGrid()[0].length; c++)
                {
                    undoState[r][c] = ui.getGrid()[r][c];
                }
            }
            ui.getUndoManager().addState(undoState);
            if(ui.getGrid()[sokRow-1][sokCol] == 2)
            {
                MediaPlayer play = new MediaPlayer(ui.getMoveBlockSound());
                ui.getGrid()[sokRow-2][sokCol] = 2;
                play.play();
                if((ui.getGrid()[sokRow-3][sokCol] == 1 && ui.getGrid()[sokRow-2][sokCol-1] == 1)
                        || (ui.getGrid()[sokRow-3][sokCol] == 1 && ui.getGrid()[sokRow-2][sokCol+1] == 1 ))
                {
                    boolean occupiesDestination = false;
                    for(SokobanBlockDestination b: ui.getDestinations())
                    {
                        if(b.getCol()==sokCol && b.getRow() ==sokRow-2)
                        {
                            occupiesDestination = true;
                            break;
                        }
                    }
                    if(!occupiesDestination)
                    {
                        gsm.endGame();
                        loseBox(ui.getStage());
                        play = new MediaPlayer(ui.getLoseSound());
                        play.play();
                    }
                }
            }
            else
            {
                MediaPlayer play = new MediaPlayer(ui.getMoveSound());
                play.play();
            }
            ui.getGrid()[sokRow][sokCol] = 0;
            ui.setSokRow(ui.getSokRow()-1);
            sokRow = ui.getSokRow();
            ui.getGrid()[sokRow][sokCol] = 4;
        }
        else
        {
            MediaPlayer play = new MediaPlayer(ui.getFailSound());
            play.play();
        }
    }
    
    public void respondToMoveRightRequest()
    {
        SokobanGameStateManager gsm = ui.getGSM();
        int sokRow = ui.getSokRow();
        int sokCol = ui.getSokCol();
        if((ui.getGrid()[sokRow+1][sokCol] != 1 && ui.getGrid()[sokRow+1][sokCol] != 2) || 
           ((ui.getGrid()[sokRow+1][sokCol] == 2) && (ui.getGrid()[sokRow+2][sokCol] != 1) && (ui.getGrid()[sokRow+2][sokCol] != 2)))
        {
            int [][] undoState = new int[ui.getGrid().length][ui.getGrid()[0].length];
            for(int r = 0; r < ui.getGrid().length; r++)
            {
                for(int c = 0; c < ui.getGrid()[0].length; c++)
                {
                    undoState[r][c] = ui.getGrid()[r][c];
                }
            }
            ui.getUndoManager().addState(undoState);
            if(ui.getGrid()[sokRow+1][sokCol] == 2)
            {
                MediaPlayer play = new MediaPlayer(ui.getMoveBlockSound());
                ui.getGrid()[sokRow+2][sokCol] = 2;
                play.play();
                if((ui.getGrid()[sokRow+3][sokCol] == 1 && ui.getGrid()[sokRow+2][sokCol-1] == 1)
                        || (ui.getGrid()[sokRow+3][sokCol] == 1 && ui.getGrid()[sokRow+2][sokCol+1] == 1 ))
                {
                    boolean occupiesDestination = false;
                    for(SokobanBlockDestination b: ui.getDestinations())
                    {
                        if(b.getCol() == sokCol && b.getRow() == sokRow+2)
                        {
                            occupiesDestination = true;
                            break;
                        }
                    }
                    if(!occupiesDestination)
                    {
                        gsm.endGame();
                        loseBox(ui.getStage());
                        play = new MediaPlayer(ui.getLoseSound());
                        play.play();
                    }
                }
            }
            else
            {
                MediaPlayer play = new MediaPlayer(ui.getMoveSound());
                play.play();
            }
            ui.getGrid()[sokRow][sokCol] = 0;
            ui.setSokRow(ui.getSokRow()+1);
            sokRow = ui.getSokRow();
            ui.getGrid()[sokRow][sokCol] = 4;
        }
        else
        {
            MediaPlayer play = new MediaPlayer(ui.getFailSound());
            play.play();
        }
    }
    
    public void respondToUndoRequest()
    {
        SokobanGameStateManager gsm = ui.getGSM();
        if(gsm.isGameInProgress())
        {
            if(ui.getUndoManager().getStates().size() > 0)
            {
                int[][] undo = ui.getUndoManager().undo();
                ui.setGrid(undo);
                ui.drawGrid(ui.getGrid());
                for(int r = 0; r < ui.getGrid().length; r++)
                {
                    for(int c = 0; c < ui.getGrid()[0].length; c++)
                    {
                        if (ui.getGrid()[r][c] == 4)
                        {
                            ui.setSokRow(r);
                            ui.setSokCol(c);
                        }
                    }
                }   
            }
        }
    }
    
    /**
     * This method sets destination points back if something was previously on it but moved off
     * This methods checks to see if all destination points are covered with boxes;
     * If they are, the game is won
     */
    public void checkGameStatus()
    {
        SokobanGameStateManager gsm = ui.getGSM();
        for(SokobanBlockDestination b: ui.getDestinations())
        {
            int r = b.getRow(); int c = b.getCol();
            if(ui.getGrid()[r][c] == 0)
            {
                ui.getGrid()[r][c] = 3;
                b.setOccupied(false);
            }
            else
            {
                b.setOccupied(true);
            }
        }
        
        int destinationsLeft = 0;
        for(SokobanBlockDestination b: ui.getDestinations())
        {
            int r = b.getRow(); int c = b.getCol();
            if (ui.getGrid()[r][c] != 2)
                destinationsLeft++;

        }
        
        ui.drawGrid(ui.getGrid());
        
        if(destinationsLeft == 0)
        {
            MediaPlayer play = new MediaPlayer(ui.getWinSound());
            gsm.endGame();
            winBox(ui.getStage());
            play.play();
        }
    }
    
    public void winBox(Stage primaryStage) {
        String ok = new String("OK");
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane exitPane = new BorderPane();
        HBox optionPane = new HBox();
        optionPane.setAlignment(Pos.CENTER);
        Button okButton = new Button(ok);
        optionPane.setSpacing(10.0);
        optionPane.getChildren().add(okButton);
        Label exitLabel = new Label("You win!");
        exitPane.setCenter(exitLabel);
        exitPane.setBottom(optionPane);
        Scene scene = new Scene(exitPane, 100, 100);
        dialogStage.setScene(scene);
        dialogStage.show();
        // WHAT'S THE USER'S DECISION?
        okButton.setOnAction(e -> {
            // YES, LET'S EXIT
            dialogStage.close();
        });
    }
    
    public void loseBox(Stage primaryStage) {
        String ok = new String("OK");
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane exitPane = new BorderPane();
        HBox optionPane = new HBox();
        optionPane.setAlignment(Pos.CENTER);
        Button okButton = new Button(ok);
        optionPane.setSpacing(10.0);
        optionPane.getChildren().add(okButton);
        Label exitLabel = new Label("You lose!");
        exitPane.setCenter(exitLabel);
        exitPane.setBottom(optionPane);
        Scene scene = new Scene(exitPane, 100, 100);
        dialogStage.setScene(scene);
        dialogStage.show();
        // WHAT'S THE USER'S DECISION?
        okButton.setOnAction(e -> {
            // YES, LET'S EXIT
            dialogStage.close();
        });
    }

    /**
     * This method responds to when the user requests to exit the application.
     *
     * @param window The window that the user has requested to close.
     */
    public void respondToExitRequest(Stage primaryStage) {
        // ENGLIS IS THE DEFAULT
        String options[] = new String[]{"Yes", "No"};
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        options[0] = props.getProperty(SokobanPropertyType.DEFAULT_YES_TEXT);
        options[1] = props.getProperty(SokobanPropertyType.DEFAULT_NO_TEXT);
        String verifyExit = props.getProperty(SokobanPropertyType.DEFAULT_EXIT_TEXT);

        // NOW WE'LL CHECK TO SEE IF LANGUAGE SPECIFIC VALUES HAVE BEEN SET
        if (props.getProperty(SokobanPropertyType.YES_TEXT) != null) {
            options[0] = props.getProperty(SokobanPropertyType.YES_TEXT);
            options[1] = props.getProperty(SokobanPropertyType.NO_TEXT);
            verifyExit = props.getProperty(SokobanPropertyType.EXIT_REQUEST_TEXT);
        }

        // FIRST MAKE SURE THE USER REALLY WANTS TO EXIT
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane exitPane = new BorderPane();
        HBox optionPane = new HBox();
        Button yesButton = new Button(options[0]);
        Button noButton = new Button(options[1]);
        optionPane.setSpacing(10.0);
        optionPane.getChildren().addAll(yesButton, noButton);
        Label exitLabel = new Label(verifyExit);
        exitPane.setCenter(exitLabel);
        exitPane.setBottom(optionPane);
        Scene scene = new Scene(exitPane, 300, 150);
        dialogStage.setScene(scene);
        dialogStage.show();
        // WHAT'S THE USER'S DECISION?
        yesButton.setOnAction(e -> {
            // YES, LET'S EXIT
            System.exit(0);
        });
        noButton.setOnAction(e -> {
           // NO, LET'S NOT
            dialogStage.close();
        });

    }

}
