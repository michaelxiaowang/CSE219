/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sokoban.ui;

import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class SokobanUndoManager {
    
    private ArrayList<int[][]> pastStates;
    private int SokobanRow;
    private int SokobanCol;
    
    public SokobanUndoManager()
    {
        pastStates = new ArrayList<int[][]>();
    }
    
    public SokobanUndoManager(int[][] grid)
    {
        pastStates = new ArrayList<int[][]>();
        pastStates.add(grid);
    }
    
    public SokobanUndoManager(ArrayList<int[][]> states)
    {
        pastStates = states;
    }
    
    public void addState(int[][] grid)
    {
        pastStates.add(grid);
    }
    
    public ArrayList<int[][]> getStates()
    {
        return pastStates;
    }
    
    public int[][] undo()
    {
        int [][] undoState = pastStates.get(pastStates.size()-1);
        pastStates.remove(pastStates.size()-1);
        return undoState;
    }
    
}
