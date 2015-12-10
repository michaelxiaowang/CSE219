/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sokoban.ui;

/**
 *
 * @author Michael
 */
public class SokobanBlockDestination {
        private boolean occupied;
        private int row;
        private int col;
        
        public SokobanBlockDestination()
        {
            occupied = false;
            row = 0;
            col = 0;
        }
        
        public SokobanBlockDestination(int r, int c)
        {
            occupied = false;
            row = r;
            col = c;
        }
        
        /*
        Mutator methods
        */
        public void setOccupied(boolean b)
        {
            occupied = b;
        }
        
        public void setRow(int r)
        {
            row = r;
        }
        
        public void setCol(int c)
        {
            col = c;
        }
        
        /*
        Accesor methods
        */
        public boolean isOccupied()
        {
            return occupied;
        }
        
        public int getRow()
        {
            return row;
        }
        
        public int getCol()
        {
            return col;
        }
}
