/*
 * The MIT License
 *
 * Copyright 2022 Santiago Arias.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package Cinema;

import java.util.ArrayList;
import java.util.List;
import sienens.CinemaTicketDispenser;

/**
 *
 * @author Santiago Arias
 */
public class MainMenu extends Operation {
    
    /**
     *
     * Contains the list of all available operations on the main menu
     */
    public List<Operation> OperationList = new ArrayList();

    /**
     *
     * @param ctd
     * @param mp
     * 
     * Creates the object and adds its options
     */
    public MainMenu(CinemaTicketDispenser ctd, Multiplex mp) {
        super(ctd, mp);
        //This class creates the remaining objects
        OperationList.add(new LanguageSelection(super.getDispenser(), super.getMultiplex()));
        OperationList.add(new MovieTicketSale(super.getDispenser(), super.getMultiplex()));
    }
    
    @Override
    public void doOperation() {
        OperationList.get(0).doOperation();
        this.presentMenu();
    }
    
    /**
     *
     * Shows the main menu, with its available options
     */
    public void presentMenu() {
        
        while (true) {
            
        super.getDispenser().setTitle(this.getTitle());
        super.getDispenser().setImage(null);
        super.getDispenser().setDescription("");
        for (int cont = 0; cont < OperationList.size(); cont++) { //Print all options
            super.getDispenser().setOption(cont, OperationList.get(cont).getTitle());
           }
        for (int cont = OperationList.size(); cont < 6; cont++) { //Remove non-existing options, there can only be 6 options
            super.getDispenser().setOption(cont, null);
           }
        

        char c = super.getDispenser().waitEvent(30);
        switch (c) { //This way if we create any new option, we would be able to access it, but not if it doesn't exist
            case 'A' -> OperationList.get(0).doOperation();
            case 'B' -> OperationList.get(1).doOperation();
            case 'C' -> OperationList.get(2).doOperation();
            case 'D' -> OperationList.get(3).doOperation();
            case 'E' -> OperationList.get(4).doOperation();
            case 'F' -> OperationList.get(5).doOperation();
            default -> this.presentMenu();
        }   
        }
    }
    
    @Override
    public String getTitle() {
        return java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("WHAT WOULD YOU LIKE TO DO?");
    }
}
