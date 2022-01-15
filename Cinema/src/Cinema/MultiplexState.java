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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sienens.CinemaTicketDispenser;

/**
 *
 * @author Santiago Arias
 */
public final class MultiplexState implements Serializable {
    
    private final List<Theater> theaters = new ArrayList<>();

    /**
     *
     * @param ctd
     * @param mp
     * @throws IOException
     * 
     * Creates all the rooms (maximum of 4) that are available
     * on the assets folder.
     */
    public MultiplexState(CinemaTicketDispenser ctd, Multiplex mp) throws IOException {
        
        for (int i = 0; i < 4; i++) { //There can't be mroe than 4 movies
            File f = new File("assets/Movie"+(i+1)+".txt"); //Files start on 1
            if (f.exists()) { //Check if that movie exists
            theaters.add(new Theater("assets/Movie"+(i+1)+".txt"));
            }
        }
        
    }
    
    /**
     *
     * @throws FileNotFoundException
     * @throws IOException
     * 
     * Loads all available movies from the assets directory,
     * then it checks for the available sessions on each movie.
     */
    public void loadMoviesAndSessions() throws FileNotFoundException, IOException {
        
        for (int i = 0; i < theaters.size(); i++) {
            File f = new File("assets/Movie"+(i+1)+".txt");
                Film film = new Film("assets/Movie"+(i+1)+".txt");
                theaters.get(i).setFilm(film);
            
            try (Scanner sc = new Scanner(f)) { //The try was suggested by the IDE while trying to close the scanner
                Pattern pattern = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]"); //Matches HH:MM
                
                while (sc.hasNextLine()) { //Find the line, then check for sessions
                    String line = sc.nextLine();
                    if (line.startsWith("Sessions")) {
                        //Matcher because it has to search for all the appearances of the regex on the line
                        Matcher matcher = pattern.matcher(line); //Match the hour format
                        while (matcher.find()) {
                            int j = 0;
                            //If the matcher keeps finding matches, the program will keep adding them to the list of sessions on the theater object
                            Session session = new Session(matcher.group(j));
                            theaters.get(i).addSession(session);
                            j++;
                        }
                    }
                }
            }
        }
        
        
        
    }
    
    /**
     *
     * @param theater
     * @return
     * 
     * Returns a theater from the theaterlist.
     */
    public Theater getTheater(int theater) {
        return theaters.get(theater);
    }
    
    /**
     *
     * @return
     * 
     * Returns number of rooms
     */
    public int getNumberOfTheaters() {
        return theaters.size();
    }

    /**
     *
     * @return
     * 
     * Returns theaterList
     */
    public List<Theater> getTheaterList() {
        return theaters;
    }
    
}
