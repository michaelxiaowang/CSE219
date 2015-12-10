package sokoban.file;

import application.Main.SokobanPropertyType;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import properties_manager.PropertiesManager;
import sokoban.game.SokobanGameStateManager;
import sokoban.ui.SokobanUI;

public class SokobanFileLoader {
    
    private SokobanUI ui;
    
    public SokobanFileLoader(SokobanUI initUI)
    {
        ui = initUI;
    }
    
    public static int[][] loadSokobanFile(  String textFile) throws IOException {
       // ADD THE PATH TO THE FILE
       PropertiesManager props = PropertiesManager.getPropertiesManager();
       textFile = props.getProperty(SokobanPropertyType.DATA_PATH) + textFile; 
       File f = new File(textFile);
       
       byte[] bytes = new byte[300];
       ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
       FileInputStream fis = new FileInputStream(f);
       BufferedInputStream bis = new BufferedInputStream(fis);

       // HERE IT IS, THE ONLY READY REQUEST WE NEED
       bis.read(bytes);
       bis.close();

       // NOW WE NEED TO LOAD THE DATA FROM THE BYTE ARRAY
       DataInputStream dis = new DataInputStream(bais);

       // NOTE THAT WE NEED TO LOAD THE DATA IN THE SAME
       // ORDER AND FORMAT AS WE SAVED IT
       // FIRST READ THE GRID DIMENSIONS
       int initGridRows = dis.readInt();
       int initGridColumns = dis.readInt();
       
       int[][] newGrid = new int[initGridRows][initGridColumns];

       // AND NOW ALL THE CELL VALUES
       for (int i = 0; i < initGridRows; i++) {
            for (int j = 0; j < initGridColumns; j++) {
                newGrid[i][j] = dis.readInt();
            }
       }
       
       return newGrid;
   }
    
    public String loadTextFile(String textFile)
    {
        String s = "";
        
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        textFile = props.getProperty(SokobanPropertyType.DATA_PATH) + textFile; 
        File f = new File(textFile);
        
        
        
        return s;
    }
}
