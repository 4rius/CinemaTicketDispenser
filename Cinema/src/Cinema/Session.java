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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Santiago Arias
 */
public class Session implements Serializable {
    
    private final String hour; //Pair esta deprecated
    //I believe new objects are added to this set when seats are being occupied
    //They start coming here so that we can quickly search if they are occupied by using just contains
    //occupiesSeat will add elements to the hashset and unoccupiesSeat will remove them
    private final Set<Seat> occupiedSeats = new HashSet<>();

    public Session(String hour) {
        this.hour = hour;
    }

    public String getHour() {
        return hour;
    }
    
    public boolean isOccupied(int i, int j) {
        return this.occupiedSeats.contains(new Seat(i, j));
    }
    
    public void occupiesSeat(int i, int j) {
        this.occupiedSeats.add(new Seat(i, j));
    }
    
    public void unoccupiesSeat(int i, int j) {
        this.occupiedSeats.remove(new Seat(i, j));
    }
}
