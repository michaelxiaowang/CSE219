package sokoban.ui;

import application.Main;
import application.Main.SokobanPropertyType;
import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import properties_manager.PropertiesManager;
import sokoban.file.SokobanFileLoader;
import sokoban.game.SokobanGameData;
import sokoban.game.SokobanGameStateManager;

public class SokobanUI extends Pane {

    /**
     * The SokobanUIState represents the four screen states that are possible
     * for the Sokoban game application. Depending on which state is in current
     * use, different controls will be visible.
     */
    public enum SokobanUIState {

        SPLASH_SCREEN_STATE, PLAY_GAME_STATE, VIEW_STATS_STATE, UNDO_STATE,
        HANG1_STATE, HANG2_STATE, HANG3_STATE, HANG4_STATE, HANG5_STATE, HANG6_STATE,
    }

    // mainStage
    private Stage primaryStage;

    // mainPane
    private BorderPane mainPane;
    private BorderPane hmPane;

    // SplashScreen
    private ImageView splashScreenImageView;
    private HBox splashScreenPane;
    private Label splashScreenImageLabel;
    private HBox levelSelectionPane;
    private ArrayList<Button> levelButtons;

    // NorthToolBar
    private HBox northToolbar;
    private Button backButton;
    private Button statsButton;
    private Button undoButton;
    private Button exitButton;

    // GamePane
    private Label SokobanLabel;
    private Button newGameButton;
    private HBox letterButtonsPane;
    private HashMap<Character, Button> letterButtons;
    private StackPane gamePanel;
    private int[][] gameGrid;
    private Canvas canvas;
    private int sokobanRow;
    private int sokobanColumn;
    private ArrayList<SokobanBlockDestination> destinations;
    private SokobanUndoManager undoManager;
    private Media splash = new Media(new File("./media/splash.mp3").toURI().toString());
    private MediaPlayer splashSoundPlayer = new MediaPlayer(splash);
    private Media move = new Media(new File("./media/move.wav").toURI().toString());
    private Media moveBlock = new Media(new File("./media/movebox.wav").toURI().toString());
    private Media fail = new Media(new File("./media/fail.wav").toURI().toString());
    private Media win = new Media(new File("./media/win.wav").toURI().toString());
    private Media lose = new Media(new File("./media/lose.wav").toURI().toString());
    private Label clock;

    //StatsPane
    private ScrollPane statsScrollPane;
    private JEditorPane statsPane;

    //HelpPane
    private BorderPane undoPanel;
    private JScrollPane undoScrollPane;
    private JEditorPane undoPane;
    private Button homeButton;

    // Padding
    private Insets marginlessInsets;

    // Image path
    private String ImgPath = "file:images/";

    // mainPane weight && height
    private int paneWidth;
    private int paneHeigth;

    // THIS CLASS WILL HANDLE ALL ACTION EVENTS FOR THIS PROGRAM
    private SokobanEventHandler eventHandler;
    private SokobanErrorHandler errorHandler;
    private SokobanDocumentManager docManager;

    SokobanGameStateManager gsm;

    public SokobanUI() {
        gsm = new SokobanGameStateManager(this);
        eventHandler = new SokobanEventHandler(this);
        errorHandler = new SokobanErrorHandler(primaryStage);
        docManager = new SokobanDocumentManager(this);
        initMainPane();
        initSplashScreen();
    }

    public void SetStage(Stage stage) {
        primaryStage = stage;
    }
    
    public Stage getStage()
    {
        return primaryStage;
    }

    public BorderPane GetMainPane() {
        return this.mainPane;
    }

    public SokobanGameStateManager getGSM() {
        return gsm;
    }

    public SokobanDocumentManager getDocManager() {
        return docManager;
    }

    public SokobanErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public JEditorPane getHelpPane() {
        return undoPane;
    }

    public void initMainPane() {
        marginlessInsets = new Insets(5, 5, 5, 5);
        mainPane = new BorderPane();

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        paneWidth = Integer.parseInt(props
                .getProperty(SokobanPropertyType.WINDOW_WIDTH));
        paneHeigth = Integer.parseInt(props
                .getProperty(SokobanPropertyType.WINDOW_HEIGHT));
        mainPane.resize(paneWidth, paneHeigth);
        mainPane.setPadding(marginlessInsets);
    }

    public void initSplashScreen() {

        // INIT THE SPLASH SCREEN CONTROLS
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String splashScreenImagePath = props
                .getProperty(SokobanPropertyType.SPLASH_SCREEN_IMAGE_NAME);
        props.addProperty(SokobanPropertyType.INSETS, "5");
        String str = props.getProperty(SokobanPropertyType.INSETS);

        splashScreenPane = new HBox();

        Image splashScreenImage = loadImage(splashScreenImagePath);
        splashScreenImageView = new ImageView(splashScreenImage);

        splashScreenImageLabel = new Label();
        splashScreenImageLabel.setGraphic(splashScreenImageView);
        // move the label position to fix the pane
        splashScreenImageLabel.setLayoutX(-45);
        splashScreenImageLabel.setAlignment(Pos.CENTER);
        splashScreenPane.getChildren().add(splashScreenImageLabel);
        splashScreenPane.setAlignment(Pos.CENTER);

        // GET THE LIST OF LEVEL OPTIONS
        ArrayList<String> levels = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_OPTIONS);
        ArrayList<String> levelImages = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_IMAGE_NAMES);
        ArrayList<String> levelFiles = props
                .getPropertyOptionsList(SokobanPropertyType.LEVEL_FILES);

        levelSelectionPane = new HBox();
        levelSelectionPane.setSpacing(10.0);
        levelSelectionPane.setAlignment(Pos.CENTER);
        // add key listener
        levelButtons = new ArrayList<Button>();
        for (int i = 0; i < levels.size(); i++) {

            // GET THE LIST OF LEVEL OPTIONS
            String level = levels.get(i);
            String levelImageName = levelImages.get(i);
            Image levelImage = loadImage(levelImageName);
            ImageView levelImageView = new ImageView(levelImage);

            // AND BUILD THE BUTTON
            Button levelButton = new Button();
            levelButton.setGraphic(levelImageView);
            
            // CONNECT THE BUTTON TO THE EVENT HANDLER
            levelButton.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    // TODO
                    eventHandler.respondToSelectLevelRequest(level, SokobanUIState.PLAY_GAME_STATE);
                }
            });
            // TODO
            levelSelectionPane.getChildren().add(levelButton);
            // TODO: enable only the first level
            //levelButton.setDisable(true);
        }
        
        mainPane.setCenter(splashScreenPane);
        mainPane.setBottom(levelSelectionPane);
        splashSoundPlayer.play();
    }
    
    /**
     * This method initializes the language-specific game controls, which
     * includes the three primary game screens.
     */
    public void initSokobanUI() {
        // FIRST REMOVE THE SPLASH SCREEN
        mainPane.getChildren().clear();

        // GET THE UPDATED TITLE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String title = props.getProperty(SokobanPropertyType.GAME_TITLE_TEXT);
        primaryStage.setTitle(title);

        // THEN ADD ALL THE STUFF WE MIGHT NOW USE
        initNorthToolbar();
        initGameScreen();
        initStatsPane();
        //initHelpPane();

        // WE'LL START OUT WITH THE GAME SCREEN
        changeScreen(SokobanUIState.PLAY_GAME_STATE);
    }

    /**
     * This function initializes all the controls that go in the north toolbar.
     */
    private void initNorthToolbar() {
        // MAKE THE NORTH TOOLBAR, WHICH WILL HAVE FOUR BUTTONS
        northToolbar = new HBox();
        northToolbar.setStyle("-fx-background-color:lightgray");
        northToolbar.setAlignment(Pos.CENTER);
        northToolbar.setPadding(marginlessInsets);
        northToolbar.setSpacing(10.0);

        // MAKE AND INIT THE BACK BUTTON
        backButton = initToolbarButton(northToolbar,
                SokobanPropertyType.BACK_IMG_NAME);
        //setTooltip(backButton, SokobanPropertyType.GAME_TOOLTIP);
        backButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                eventHandler
                        .respondToSwitchScreenRequest(SokobanUIState.SPLASH_SCREEN_STATE);
            }
        });
        // MAKE AND INIT THE UNDO BUTTON
        undoButton = initToolbarButton(northToolbar,
                SokobanPropertyType.UNDO_IMG_NAME);
        //setTooltip(undoButton, SokobanPropertyType.HELP_TOOLTIP);
        undoButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                eventHandler
                        .respondToUndoRequest();
            }

        });

        // MAKE AND INIT THE STATS BUTTON
        statsButton = initToolbarButton(northToolbar,
                SokobanPropertyType.STATS_IMG_NAME);
        //setTooltip(statsButton, SokobanPropertyType.STATS_TOOLTIP);

        statsButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                eventHandler
                        .respondToSwitchScreenRequest(SokobanUIState.VIEW_STATS_STATE);
            }

        });

        // MAKE AND INIT THE EXIT BUTTON
        exitButton = initToolbarButton(northToolbar,
                SokobanPropertyType.EXIT_IMG_NAME);
        //setTooltip(exitButton, SokobanPropertyType.EXIT_TOOLTIP);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                eventHandler.respondToExitRequest(primaryStage);
            }

        });
        
        clock = gsm.timeManager();
        northToolbar.getChildren().add(clock);

        // AND NOW PUT THE NORTH TOOLBAR IN THE FRAME
        mainPane.setTop(northToolbar);
    }

    /**
     * This method helps to initialize buttons for a simple toolbar.
     *
     * @param toolbar The toolbar for which to add the button.
     *
     * @param prop The property for the button we are building. This will
     * dictate which image to use for the button.
     *
     * @return A constructed button initialized and added to the toolbar.
     */
    private Button initToolbarButton(HBox toolbar, SokobanPropertyType prop) {
        // GET THE NAME OF THE IMAGE, WE DO THIS BECAUSE THE
        // IMAGES WILL BE NAMED DIFFERENT THINGS FOR DIFFERENT LANGUAGES
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imageName = props.getProperty(prop);

        // LOAD THE IMAGE
        Image image = loadImage(imageName);
        ImageView imageIcon = new ImageView(image);

        // MAKE THE BUTTON
        Button button = new Button();
        button.setGraphic(imageIcon);
        button.setPadding(marginlessInsets);

        // PUT IT IN THE TOOLBAR
        toolbar.getChildren().add(button);

        // AND SEND BACK THE BUTTON
        return button;
    }
    
    /**
     * Initializes the game screen, which is a border pane with the north
     * toolbar set as the top element and the level as the center element.
     */
    public void initGameScreen()
    {
        gamePanel = new StackPane();
        gamePanel.prefWidth(800);
        gamePanel.prefHeight(800);
        canvas = new Canvas(800, 800);
        gamePanel.getChildren().add(canvas);
        destinations = new ArrayList<SokobanBlockDestination>();
        undoManager = new SokobanUndoManager();
        
        mainPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                eventHandler.respondToMoveRequest(ke);
            }
        });
    }
    
    public void initStatsPane()
    {
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        statsScrollPane = new ScrollPane();
        statsScrollPane.setContent(browser);
        webEngine.loadContent("<b>Text goes here</b>");
    }
    
    public Media getMoveSound()
    {
        return move;
    }
    
    public Media getMoveBlockSound()
    {
        return moveBlock;
    }
    
    public Media getFailSound()
    {
        return fail;
    }
    
    public Media getWinSound()
    {
        return win;
    }
    
    public Media getLoseSound()
    {
        return lose;
    }
    
    /**
     * Initializes the stat screen, which is a border pane with the north
     * toolbar set as the top element and the stats as the center element.
     */

    public Image loadImage(String imageName) {
        Image img = new Image(ImgPath + imageName);
        return img;
    }

    /**
     * This function selects the UI screen to display based on the uiScreen
     * argument. Note that we have 3 such screens: game, stats, and help.
     *
     * @param uiScreen The screen to be switched to.
     */
    public void changeScreen(SokobanUIState uiScreen) {
        switch (uiScreen) {
            case PLAY_GAME_STATE:
                mainPane.setCenter(gamePanel);
                mainPane.setBottom(null);
                splashSoundPlayer.stop();
                clock.setVisible(true);
                break;
            case UNDO_STATE:
                mainPane.setCenter(undoPanel);
                break;
            case VIEW_STATS_STATE:
                mainPane.setCenter(statsScrollPane);
                splashSoundPlayer.stop();
                clock.setVisible(false);
                break;
            case SPLASH_SCREEN_STATE:
                mainPane.getChildren().clear();
                mainPane.setCenter(splashScreenPane);
                mainPane.setBottom(levelSelectionPane);
                splashSoundPlayer.play();
                break;
            default:
        }

    }
    
    /**
     * sets the gameGrid to grid
     * @param grid the array that contains the values to store in gameGrid
     */
    public void setGrid(int[][] grid)
    {
        gameGrid = grid;
    }
    
    /**
     * gets the game grid
     * @return the game grid
     */
    public int[][] getGrid()
    {
        return gameGrid;
    }
    
    /**
     * sets sokobanRow to r
     * @param r the row sokoban is in
     */
    public void setSokRow(int r)
    {
        sokobanRow = r;
    }
    
    /**
     * sets sokobanColumn to c
     * @param c the column sokoban is in
     */
    public void setSokCol(int c)
    {
        sokobanColumn = c;
    }
    
    /**
     * gets the row sokoban is in
     * @return the int value of the row sokoban is in
     */
    public int getSokRow()
    {
        return sokobanRow;
    }
    
    /**
     * gets the column sokoban is in
     * @return the int value of the column sokoban is in
     */
    public int getSokCol()
    {
        return sokobanColumn;
    }
    
    /**
     * Gets the canvas
     * @return the canvas
     */
    public void drawGrid(int[][] grid)
    {
        // images
        Image emptyImage = new Image("file:images/empty.png");
        Image wallImage = new Image("file:images/wall.png");
        Image boxImage = new Image("file:images/box.png");
        Image placeImage = new Image("file:images/place.png");
        Image sokobanImage = new Image("file:images/Sokoban.png");
        
        GraphicsContext gc;
        gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        // CALCULATE THE GRID CELL DIMENSIONS
            double w = canvas.getWidth() / grid.length;
            double h = canvas.getHeight() / grid[0].length;

            // NOW RENDER EACH CELL
            int x = 0, y = 0;
            for (int i = 0; i < grid.length; i++) {
                y = 0;
                for (int j = 0; j < grid[0].length; j++) {
                    // DRAW THE CELL
                    gc.setFill(Color.LIGHTBLUE);
                    //gc.strokeRoundRect(x, y, w, h, 10, 10);

                    switch (grid[i][j]) {
                        case 0:
                            gc.drawImage(emptyImage, x, y, w, h);
                            break;
                        case 1:
                            gc.drawImage(wallImage, x, y, w, h);
                            break;
                        case 2:
                            gc.drawImage(boxImage, x, y, w, h);
                            break;
                        case 3:
                            gc.drawImage(placeImage, x, y, w, h);
                            break;
                        case 4:
                            gc.drawImage(sokobanImage, x, y, w, h);
                            break;
                    }

                    // ON TO THE NEXT ROW
                    y += h;
                }
                // ON TO THE NEXT COLUMN
                x += w;
            }
    }
    
    public ArrayList<SokobanBlockDestination> getDestinations()
    {
        return destinations;
    }
    
    public SokobanUndoManager getUndoManager()
    {
        return undoManager;
    }
}
