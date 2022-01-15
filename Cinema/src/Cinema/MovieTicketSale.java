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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import sienens.CinemaTicketDispenser;

/**
 *
 * @author Santiago Arias
 */
public class MovieTicketSale extends Operation {
    
    /**
     *
     * The core of the cinema, it contains a new state in case it's a new day,
     * or a backup information in case it's a recovery.
     */
    public static MultiplexState state;
    
    /**
     *
     * @param ctd
     * @param mp
     * 
     * Creates the movie selection menu, and checks wether we have to recover
     * a previous state.
     */
    public MovieTicketSale(CinemaTicketDispenser ctd, Multiplex mp) {
        super(ctd, mp);
                
        File f = new File("assets/backup.bin"); //If the backup file exists, we will recover its previous state
        if(f.exists()) {
            try {
                MovieTicketSale.deserializeMultiplexState();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(MovieTicketSale.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
        
        try {
            state = new MultiplexState(super.getDispenser(), super.getMultiplex());
            state.loadMoviesAndSessions();
        } catch (IOException ex) {
            Logger.getLogger(MovieTicketSale.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        
    }

    @Override
    public void doOperation() {
        Theater th = this.selectTheater();
        Session ss = null;
        Set<Seat> seats = null;
        if (th instanceof Theater) { //Check if the user has chosen to return to the options menu
            ss = this.selectSession(th);
        }
        if (ss instanceof Session) { try {
            //Check if the user has chosen to return to the main menu
            seats = this.selectSeats(th, ss); //Could return a filenotfound exception
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MovieTicketSale.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Avoid possible NP Exceptions
        if (seats instanceof Set<Seat> && th != null && ss != null) { try {
            //Check if the user chooses to go back to the main menu
            if (this.performPayment(seats, th, ss)) super.getDispenser().setMenuMode();
            else { //If the payment doesn't go through, we free the seats again
                try { // O(n) :(
                    for (int i = 1; i < th.getMaxRows()+1; i++) { //ctd starts from 1,1
                        for (int j = 1; j < th.getMaxCols()+1; j++) {
                            if (seats.contains(new Seat(i, j))) {
                                ss.unoccupiesSeat(i, j);
                            }
                        }
                    }       } catch (FileNotFoundException ex) {
                        Logger.getLogger(MovieTicketSale.class.getName()).log(Level.SEVERE, null, ex);
                    }
                super.getDispenser().setMenuMode();
                boolean expelled = super.getDispenser().expelCreditCard(30);
                if (!expelled) super.getDispenser().retainCreditCard(true);
            }
            } catch (IOException ex) { //When we serialize we can get this error, and performPayment serializes if payment is correct
                Logger.getLogger(MovieTicketSale.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private Theater selectTheater() {
        
        List<Theater> theaterList = state.getTheaterList();
        
        super.getDispenser().setTitle(this.getTitle());
        super.getDispenser().setImage(null);
        super.getDispenser().setDescription("");
        for (int i = 0; i < theaterList.size(); i++) {
            //The names are printed following the object's creation order, and not the actual room number, this one will only be used for the room disposition
            super.getDispenser().setOption(i, theaterList.get(i).getFilm().getName());
        }
        super.getDispenser().setOption(5, java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("MAIN MENU"));
        //We can't have more than 4 rooms, the cinema is limited.
        
        while (true) {
        int sel;
        char c = super.getDispenser().waitEvent(30);
        
        switch (c) { //Only the available films will be printed, existence of the objects is already checked
            case 'A' -> sel = 0; //Rooms start at 0
            case 'B' -> sel = 1;
            case 'C' -> sel = 2;
            case 'D' -> sel = 3;
            case 'F' -> sel = 5; //There can only be up to 4 movies, therefore number 5 will never be used
            default -> sel = 5; //If the user doesn't perform any action within 30 seconds, the system goes back to the main menu
        }
        if (sel <= theaterList.size()) {
        return state.getTheater(sel);
        }
        
        //User chooses to go back to the main menu
        return null;
        
        } 
    }
    

    private Session selectSession(Theater th) {
        
        List<Session> sessionList = th.getSessionList();
        
        super.getDispenser().setTitle(th.getFilm().getName()+java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString(" - SESSIONS"));
        super.getDispenser().setImage(th.getFilm().getPoster());
        super.getDispenser().setDescription(th.getFilm().getDescription()+"\n\n"+
                java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("PRICE: ")+th.getPrice()+"€");
        
        //Print the available hours
        for (int i = 0; i < sessionList.size(); i++) {
            super.getDispenser().setOption(i, sessionList.get(i).getHour());
        }
        
        //Clear the buttons that won't be used
        for (int i = sessionList.size(); i < 6; i++) {
            super.getDispenser().setOption(i, null);
        }
        
        super.getDispenser().setOption(5, java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("MAIN MENU"));
        
        while (true) {
        int sel;
        char c = super.getDispenser().waitEvent(30);
        
        switch (c) { //Only the available films will be printed, existence of the objects is already checked
            case 'A' -> sel = 0; //Rooms start at 0
            case 'B' -> sel = 1;
            case 'C' -> sel = 2;
            case 'D' -> sel = 3;
            case 'E' -> sel = 4;
            case 'F' -> sel = 5; //Number 5 should never be used for other than returning to the main menu
            default -> sel = 5;
        }
        
        if (sel <= sessionList.size()) {
        return sessionList.get(sel);
        }
        
        //User chooses to go back to the main menu || 30 secs no action
        return null;
        
        } 
    }
    
    /**
     * 
     * Returns the selected seats instead of the number of seats, so we can
     * use unoccupiesSeat in case the payment fails
     */
    private Set<Seat> selectSeats(Theater th, Session ss) throws FileNotFoundException {
        super.getDispenser().setTitle(th.getFilm().getName()+ " "+ss.getHour()+java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString(" - CHOOSE YOUR SEATS"));
        this.presentSeats(th, ss);
        super.getDispenser().setOption(0, java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("CANCEL"));
        super.getDispenser().setOption(1, java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("PAY"));
        
        
        //Temporal set to prevent the user from eliminating a previously bought seat
        Set<Seat> tempseats= new HashSet<>();
        while (true) {
            char c = super.getDispenser().waitEvent(30);
            if (c != 0 && c != 'A') {
                byte row = (byte)((c & 0xFF00) >> 8);
                byte col = (byte)(c & 0xFF);
                //We check if the user can select more seats, or if he's trying to delete a previous selection
                if (tempseats.size() < 4 || c == 'B' || tempseats.contains(new Seat(row, col))) {
                    if (tempseats.contains(new Seat(row, col))) {
                        super.getDispenser().setTitle(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("SELECTION REMOVED"));
                        super.getDispenser().markSeat(row, col, 2);
                        tempseats.remove(new Seat(row, col));
                        ss.unoccupiesSeat(row, col);
                    } else if (ss.isOccupied(row, col)) { //The seat was already occupied
                        super.getDispenser().setTitle(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("THAT SEAT IS ALREADY TAKEN"));
                        } else if (c == 'B' && !tempseats.isEmpty()) { //User chooses to pay
                                return tempseats;
                                } else if ( c =='B' && tempseats.isEmpty()) super.getDispenser().setTitle(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("SELECT AT LEAST 1 SEAT"));
                                else {
                                    super.getDispenser().setTitle("Row: " + row + " Col: " + col);
                                    super.getDispenser().markSeat(row, col, 1);
                                    tempseats.add(new Seat(row, col));//This temporal sets let us know the seats that are being bought during this session
                                    ss.occupiesSeat(row, col);
                                }
                } 
            } else { //User chooses to cancel or 30 secs no action, removes current selection
                for (int i = 1; i < th.getMaxRows()+1; i++) { //Ctd starts on 1,1, starts and finishes 1+ the seat set value
                    for (int j = 1; j < th.getMaxCols()+1; j++) {
                        if (tempseats.contains(new Seat(i, j))) {
                            ss.unoccupiesSeat(i, j);
                        }
                    }
                }
                super.getDispenser().setMenuMode();
                return null;
            }
        }
    }
    
    /**
     * 
     * This method requires the number of seats to be charged, as well as the price
     * and information about the seats and room, so we can print them
     */
    private boolean performPayment(Set<Seat> seats, Theater th, Session ss) throws IOException { //Tiene que pasarsele algun puto numero
        Operation payment = new PerformPayment(super.getDispenser(), super.getMultiplex(), this.computePrice(seats.size(), th.getPrice()), seats.size(), th.getFilm().getName());
        try {
            payment.doOperation();
            this.serializeMultiplexState(); //We save the current occupation, as well as fimls, distribution and rooms
            List <String> ticket;
            /*I could have created a getter on the seat but the complexity is the same
            only difference is the amount of items the program iterates, in this case
            it iterates and searches for the seats that were bought during this session.
            The session occupied seats start on 1 due to the ctd configuration, therefore
            they end 1 up the values of the rows and colums*/
            for (int i = 1; i < th.getMaxRows()+1; i++) { //Fors start on 1 because the occupied seats always start from 1,1
                for (int j = 1; j < th.getMaxCols()+1; j++) {
                    if (seats.contains(new Seat(i, j))) {
                        ticket = new ArrayList<>();
                        ticket.add(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("  TICKETS FOR ")+th.getFilm().getName());
                        ticket.add(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("  ROOM: ")+th.getNumber());
                        ticket.add("  "+ss.getHour());
                        ticket.add(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("  ROW: ")+i);
                        ticket.add(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("  SEAT: ")+j);
                        ticket.add(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("  PRICE: ")+th.getPrice()+"€");
                        super.getDispenser().print(ticket);
                    }
                }
            }
            return true;
        } catch (RuntimeException e) { //User chose to exit or payment failed, either way we catch the exception
            return false;
        }
    }
    
    private void presentSeats(Theater th, Session ss) throws FileNotFoundException {
        //Este método también necesita estos dos parámetros no incluidos en el diagrama
        super.getDispenser().setTheaterMode(th.getMaxRows(), th.getMaxCols());
        Set<Seat> seatSet = th.getSeatSet();
        for (int i = 0; i < th.getMaxRows(); i++) {
            /*uses +1 because the first row and columns on the ctd are 1, so we are not 1 seat behind
            and the seat set starts on 0, so the seats will be on 0,x, but the occupied and the
            room can't represent that, instead they locatte them starting on 1,x*/
            for (int j = 0; j < th.getMaxCols(); j++) {
                if (seatSet.contains(new Seat(i, j))) {
                    //Check wether the seats are occupied or not
                    if (ss.isOccupied(i+1, j+1)) super.getDispenser().markSeat(i+1, j+1, 1);
                    else super.getDispenser().markSeat(i+1, j+1, 2);
                } else {
                    super.getDispenser().markSeat(i+1, j+1, 0);
                }
            }
        }
    }
    
    private int computePrice(int seats, int price) {
        //He considerado necesario que este método recibiera estos 2 parámetros no incluidos en el diagrama
        return seats * price;
    }
    
    @Override
    public String getTitle() {
        return java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("CHOOSE A MOVIE");
    }
    
    /**
     *
     * @throws FileNotFoundException
     * @throws IOException
     * 
     * Saves the state of the rooms after a successful payment
     */
    public void serializeMultiplexState() throws FileNotFoundException, IOException {
        
        FileOutputStream foutputStream = new FileOutputStream("assets/backup.bin");
	ObjectOutputStream outputStream = new ObjectOutputStream(foutputStream);
		
	outputStream.writeObject(state);
    }
    
    /**
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     * 
     * Loads the state of the cinema from a backup file
     */
    public static void deserializeMultiplexState() throws FileNotFoundException, IOException, ClassNotFoundException {
        
        FileInputStream loadBackup = new FileInputStream("assets/backup.bin");
	ObjectInputStream extractBackup = new ObjectInputStream(loadBackup);
        
        state = (MultiplexState) extractBackup.readObject();
    }
}
