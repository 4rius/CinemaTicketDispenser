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
import java.io.Serializable;
import java.util.Scanner;

/**
 *
 * @author Santiago Arias
 */
public class Film implements Serializable {
    
    private String name;
    private String poster; //FileName should be acquired from the movie file
    private int duration; //Where the fuck is the duration
    private String description;

    /**
     *
     * @param film
     * @throws FileNotFoundException
     * 
     * Extracts the required information from the provided movie file
     */
    public Film(String film) throws FileNotFoundException {
        FileReader details = new FileReader(film);
        //Cycle through the lines and select the data we need for each attribute and remove what we don't need from the line
        try (Scanner sc = new Scanner(details)) {
            //Cycle through the lines and select the data we need for each attribute and remove what we don't need from the line
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith("Title")) {
                    String n = line.replaceAll("Title: ", "");
                    this.name = n;
                } else if (line.startsWith("Descripción")) {
                    String n = line.replaceAll("Descripción: ", "");
                    this.description = n;
                } else if (line.startsWith("Poster")) {
                    String n = line.replaceAll("Poster: ", ""); 
                    this.poster = "assets/"+n;
                }
                /*
                En uno de los archivos ponía "Descripción: ", así que consideré que faltaba en el resto,
                si este no era el caso, y no debe poner nada, se puede poner
                else if (!line.startsWith("")) this.description = line;
                al final de todas las declaraciones,
                cambiando la de descripción. Comprobar, básicamente, que no empieza como ninguna
                otra, y no es una línea vacía.
                */
            }
        }
        
    }

    public String getDescription() {
        return description;
    }

    public int getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public String getPoster() {
        return poster;
    }
    
}
