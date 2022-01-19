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

/**
 *
 * @author Santiago Arias
 */
public class Seat implements Serializable {
    
    private final int row;
    private final int col;

    public Seat(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.row;
        hash = 53 * hash + this.col;
        return hash;
    }

    /**
     *
     * @param obj
     * @return
     * 
     * Equals has been overridden so that we could use contains with the HashSet
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Seat other = (Seat) obj;
        if (this.row != other.row) {
            return false;
        }
        return this.col == other.col;
    }
    
}
