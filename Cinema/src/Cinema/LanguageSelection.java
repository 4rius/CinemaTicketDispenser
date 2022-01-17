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

import sienens.CinemaTicketDispenser;

/**
 *
 * @author Santiago Arias
 */
public class LanguageSelection extends Operation {

    public LanguageSelection(CinemaTicketDispenser ctd, Multiplex mp) {
        super(ctd, mp);
    }
    
    @Override
    public void doOperation() {
        super.getDispenser().setTitle(this.getTitle());
        super.getDispenser().setDescription("");
        //Languages don't change usually on conventional apps, so they will stay static
        super.getDispenser().setOption(0, "English");
        super.getDispenser().setOption(1, "Castellano");
        super.getDispenser().setOption(2, "Català");
        super.getDispenser().setOption(3, "Euskara");
        super.getDispenser().setOption(4, null);
        super.getDispenser().setOption(5, null);
        
        char c = super.getDispenser().waitEvent(30);
        
        switch (c) {
            case 'A' -> super.getMultiplex().setLanguage("Cinema/en");
            case 'B' -> super.getMultiplex().setLanguage("Cinema/es");
            case 'C' -> super.getMultiplex().setLanguage("Cinema/cat");
            case 'D' -> super.getMultiplex().setLanguage("Cinema/eus");
            case '1' -> super.getDispenser().retainCreditCard(false);
            default -> {} //If the user doesn't select a language, English is the default
        }
    }
    
    @Override
    public String getTitle() {
        return java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("SELECT LANGUAGE");
    }
}
