/* MULTITHREADING <Theater.java>
 * EE422C Project 6 submission by
 * Raiyan Chowdhury
 * rac4444
 * Unique #: 16235
 * Slip days used: <0>
 * Spring 2017
 */
package assignment6;

import java.util.ArrayList;
import java.util.List;

public class Theater {
	/*
	 * Represents a seat in the theater
	 * A1, A2, A3, ... B1, B2, B3 ...
	 */
	static class Seat {
		private int rowNum;
		private int seatNum;
		private boolean taken;

		public Seat(int rowNum, int seatNum) {
			this.rowNum = rowNum;
			this.seatNum = seatNum;
			this.taken = false;
		}

		public int getSeatNum() {
			return seatNum;
		}

		public int getRowNum() {
			return rowNum;
		}
		
		public boolean isTaken() {
			return taken;
		}
		
		public void setTaken() {
			taken = true;
		}

		@Override
		public String toString() {
			String seat_location = evaluateSeatLocation(rowNum);
			seat_location += seatNum;
			return seat_location;
		}
		
		/**
		 * Evaluates the seat location and what row it corresponds to.
		 * 1 -> A, 2 -> B, 26 -> Z, 27 -> AA, 52 -> AZ, etc. 
		 * @param r, row number
		 * @return string representing row in which seat is located
		 */
		private String evaluateSeatLocation(int r) {
			String s = "";
			r--;
			while (r >= 0) {
				char c = (char) ('A' + (r%26));//start from rightmost char
				s = Character.toString(c) + s;
				r /= 26;//divide by 26 to get preceding char
				r--;//necessary for A-Z to continue to be represented by 0-25 and for loop termination
			}
			return s;
		}
	}

  /*
	 * Represents a ticket purchased by a client
	 */
	static class Ticket {
		private String show;
		private String boxOfficeId;
		private Seat seat;
		private int client;

		public Ticket(String show, String boxOfficeId, Seat seat, int client) {
			this.show = show;
			this.boxOfficeId = boxOfficeId;
			this.seat = seat;
			this.client = client;
		}

		public Seat getSeat() {
			return seat;
		}

		public String getShow() {
			return show;
		}

		public String getBoxOfficeId() {
			return boxOfficeId;
		}

		public int getClient() {
			return client;
		}
		
		/**
		 * @return String representing ticket purchased by client, containing info about 
		 * the show, seat, and box office from which the ticket was purchased.
		 */
		@Override
		public String toString() {
			String ticket = "-------------------------------\n";
			ticket += addShow();
			ticket += addBoxOffice();
			ticket += addSeat();
			ticket += addClient();
			ticket += "-------------------------------";
			return ticket;
		}
		
		/**
		 * @return show information for the ticket in the correct format, 
		 * which is added to the ticket in toString()
		 */
		private String addShow() {
			String ticket_show = "| Show: " + show;
			int length = ticket_show.length();
			for (int i = length; i < 30; i++) {
				ticket_show += " ";
			}
			ticket_show += "|\n";
			return ticket_show;
		}
		
		/**
		 * @return box office information for the ticket in the correct format
		 */
		private String addBoxOffice() {
			String ticket_BX = "| Box Office ID: " + boxOfficeId;
			int length = ticket_BX.length();
			for (int i = length; i < 30; i++) {
				ticket_BX += " ";
			}
			ticket_BX += "|\n";
			return ticket_BX;
		}
		
		/**
		 * @return seat information for the ticket in the correct format
		 */
		private String addSeat() {
			String ticket_seat = "| Seat: " + seat.toString();
			int length = ticket_seat.length();
			for (int i = length; i < 30; i++) {
				ticket_seat += " ";
			}
			ticket_seat += "|\n";
			return ticket_seat;
		}
		
		/**
		 * @return client information for the ticket in the correct format
		 */
		private String addClient() {
			String ticket_client = "| Client: " + client;
			int length = ticket_client.length();
			for (int i = length; i < 30; i++) {
				ticket_client += " ";
			}
			ticket_client += "|\n";
			return ticket_client;
		}
	}
	
	private int numRows;
	private int seatsPerRow;
	private String show;
	private ArrayList<Seat> seat_list = new ArrayList<Seat>();
	private ArrayList<Ticket> transaction_log = new ArrayList<Ticket>();

	public Theater(int numRows, int seatsPerRow, String show) {
		this.numRows = numRows;
		this.seatsPerRow = seatsPerRow;
		this.show = show;
		generateSeats(numRows, seatsPerRow);
	}
	
	/**
	 * Generates an array list of seats in the theater using the parameters provided.
	 * @param r, number of rows
	 * @param s, number of seats per row
	 */
	private void generateSeats(int r, int s) {
		for (int i = 1; i <= r; i++) {
			for (int j = 1; j <= s; j++) {
				seat_list.add(new Seat(i,j));
			}
		}
	}
	
	public String getShow() {
		return show;
	}
	
	public int getNumRows() {
		return numRows;
	}
	
	public int getSeatsPerRow() {
		return seatsPerRow;
	}

	/*
	 * Calculates the best seat not yet reserved
	 *
 	 * @return the best seat or null if theater is full
   */
	public Seat bestAvailableSeat() {
		synchronized(this) {
			for (Seat s : seat_list) {
				if (!s.isTaken()) {
					s.setTaken();
					return s;
				}
			}
		}
		return null;
	}

	/*
	 * Prints a ticket for the client after they reserve a seat
   * Also prints the ticket to the console
	 *
   * @param seat a particular seat in the theater
   * @return a ticket or null if a box office failed to reserve the seat
   */
	public Ticket printTicket(String boxOfficeId, Seat seat, int client) {
		synchronized(this) {
			Ticket ticket = new Ticket(getShow(), boxOfficeId, seat, client);
			transaction_log.add(ticket);
			System.out.println(ticket.toString());
			System.out.println();
			return ticket;
		}
	}

	/*
	 * Lists all tickets sold for this theater in order of purchase
	 *
   * @return list of tickets sold
   */
	public List<Ticket> getTransactionLog() {
		return transaction_log;
	}
}
