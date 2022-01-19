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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author Santiago Arias
 */
public class Theater implements Serializable {
    
    private final int number;
    private int price;
    private final Set<Seat> seatSet = new HashSet<>();
    private Film film;
    private final List<Session> sessionList = new ArrayList<>();

    public Theater(String fileName) throws FileNotFoundException, IOException {
        //De los files se saca la informacion del numero y el precio
        FileReader details = new FileReader(fileName);
        try (Scanner sc = new Scanner(details)) {
            sc.useDelimiter("Theatre:\\s|\n");
            this.number = sc.nextInt(); //The first int on the text file is always the room number
            
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.contains("Price")) {
                    String pr = line.replaceAll("[^0-9]", ""); //Just take the numbers, replace the rest for nothing
                    this.price = Integer.parseInt(pr); //Get the integer
                }
            }
        }
        
        this.loadSeats();
    }

    public int getNumber() {
        return number;
    }

    public int getPrice() {
        return price;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Set<Seat> getSeatSet() {
        return seatSet;
    }
    
    public void addSession(Session s) {
        sessionList.add(s);
    }
    
    public List<Session> getSessionList() {
        return sessionList;
    }
    
    /**
     *
     * @return
     * @throws FileNotFoundException
     * 
     * Returns maximum number of rows on the room
     */
    public int getMaxRows() throws FileNotFoundException {
        int rows = 0;
        FileReader loadRows = new FileReader("assets/Theater"+this.getNumber()+".txt");
        try (Scanner sc = new Scanner(loadRows)) {
            while (sc.hasNextLine()) {
                sc.nextLine();
                rows++;
            }
        }
        
        return rows;
    }
    
    /**
     *
     * @return
     * @throws FileNotFoundException
     * 
     * Returns maximum number of columns on the room
     */
    public int getMaxCols() throws FileNotFoundException {
        String maxcol = new String();
        FileReader loadCols = new FileReader("assets/Theater"+this.getNumber()+".txt");
        try (Scanner sc = new Scanner(loadCols)) {
            //This way we find the longest row on the provided file
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (maxcol.length() < line.length()) maxcol = line;
                /*Without this if, Netbeans doesn't trust there is another
                line, and returns an exception on movies 2 and 4,
                adding that "if", weirdly solves the issue
                */
                if (sc.hasNextLine()) {
                        sc.nextLine();
                }
            }
        }
        
        return maxcol.length();
    }
    
    private void loadSeats() throws FileNotFoundException, IOException {
        
        FileReader loadSeats = new FileReader("assets/Theater"+this.getNumber()+".txt");
        Scanner sc = new Scanner(loadSeats);
        
        //Traverse the whole "matrix" of the longest rows and cols, and checks wether we have a * on that position
        for (int i = 0; i < this.getMaxRows(); i++) {
            String line = sc.nextLine();
            char[] toCharArray = line.toCharArray();
            for (int j = 0; j < toCharArray.length; j++) {
                if (toCharArray[j] == '*') seatSet.add(new Seat(i, j));
            }
        }
    }
    
}
