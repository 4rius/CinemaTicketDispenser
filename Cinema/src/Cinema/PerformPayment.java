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

import javax.naming.CommunicationException;
import sienens.CinemaTicketDispenser;
import urjc.UrjcBankServer;

/**
 *
 * @author Santiago Arias
 */
public class PerformPayment extends Operation {
    
    private final UrjcBankServer bank = new UrjcBankServer();
    /* I couldn't find another way to get the price here and set the description,
    these 3 attributes are not on the diagram but they are necessary*/
    private int price;
    private int seats;
    private String movie;

    /**
     * @param ctd
     * @param mp
     * 
     */
    public PerformPayment(CinemaTicketDispenser ctd, Multiplex mp) {
	    super(ctd, mp);
    }

	public void setPrice(int price) {
		this.price = price;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public void setMovie(String movie) {
		this.movie = movie;
	}

    @Override
    public void doOperation() { //Figura 9
        super.getDispenser().setMessageMode();
        super.getDispenser().setTitle(this.getTitle());
        super.getDispenser().setDescription(this.seats+java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString(" TICKETS FOR ")+this.movie+": "+this.price + "€");
        super.getDispenser().setOption(0, java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("CONFIRM"));
        super.getDispenser().setOption(1, java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("CANCEL"));
        
        OUTER: //One break needs to exit the switch and the while
        while (true) {
            char c = super.getDispenser().waitEvent(30);
            switch (c) {
                case '1' -> super.getDispenser().retainCreditCard(false);
                case 'A' -> {
                    if (this.bank.comunicationAvaiable()) {
                        //Commavailable
                        boolean payment = false;
                        try {
                            payment = bank.doOperation(super.getDispenser().getCardNumber(), this.price);
                        } catch (CommunicationException ex) {
                            super.getDispenser().setTitle(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("ERROR"));
                            super.getDispenser().setDescription(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("CONNECTION INTERRUPTED"));
                            super.getDispenser().setOption(0, java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("TRY AGAIN"));
                            super.getDispenser().setOption(1, java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("CANCEL"));
                        }   if (payment) {
                            super.getDispenser().setTitle(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("SUCCESS!"));
                            super.getDispenser().setDescription(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("YOU CAN SAFELY REMOVE YOUR CARD NOW"));
                            super.getDispenser().setOption(0, null);
                            super.getDispenser().setOption(1, null);
                            boolean expelled2 = super.getDispenser().expelCreditCard(30);
                            if (!expelled2) super.getDispenser().retainCreditCard(true);
                            break OUTER; //Exit the loop without triggering the exception
                        } else {
                            super.getDispenser().setTitle(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("ERROR"));
                            super.getDispenser().setDescription(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("LOW BALANCE OR CARD NOT FOUND"));
                            super.getDispenser().setOption(0, java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("TRY AGAIN"));
                            super.getDispenser().setOption(1, java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("CANCEL"));
                        }
                    } else {
                        super.getDispenser().setDescription(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("THIS SERVICE IS TEMPORARILY DOWN"));
                        super.getDispenser().setTitle(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("ERROR"));
                        super.getDispenser().setOption(0, null);
                        super.getDispenser().setOption(1, java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("MAIN MENU"));
                    }
                }
                default -> { //Inactivity or cancellations, either way we try to expel a credit card if there is one, and we throw the exception
                        super.getDispenser().setTitle(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("REMOVE YOUR CARD"));
                        super.getDispenser().setDescription(java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("CARDTHREAT2"));
                        super.getDispenser().setOption(0, null);
                        super.getDispenser().setOption(1, null);
                        boolean expelled = super.getDispenser().expelCreditCard(30); //Expell the card if inserted
                        if (!expelled) super.getDispenser().retainCreditCard(true);
                        throw new RuntimeException("To be catched");  //The user cancels the process, or payment failed, prevents an error if the user clicks try again and then cancel, as it broke out when clicking try again
                }
            }
        }
    }

    @Override
    public String getTitle() {
        return (java.util.ResourceBundle.getBundle(super.getMultiplex().getLanguage()).getString("INSERT YOUR CREDIT CARD"));
    }

}
