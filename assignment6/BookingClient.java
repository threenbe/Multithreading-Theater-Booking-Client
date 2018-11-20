/* MULTITHREADING <BookingClient.java>
 * EE422C Project 6 submission by
 * Raiyan Chowdhury
 * rac4444
 * Unique #: 16235
 * Slip days used: <0>
 * Spring 2017
 */
package assignment6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Thread;

public class BookingClient implements Runnable {
	Map<String, Integer> office = new HashMap<String, Integer>();
	Theater theater = null;
	private boolean is_running;//all threads run while true, all stop when false
	private int num_of_clients;
	private Object client_lock = new Object();
	
  /*
   * @param office maps box office id to number of customers in line
   * @param theater the theater where the show is playing
   */
	public BookingClient(Map<String, Integer> office, Theater theater) {
		num_of_clients = 0;
		is_running = true;
		this.office.putAll(office);
		this.theater = theater;
	}
	
	/**
	 * updates the total number of clients
	 * @return the current client number being processed
	 */
	private int getClientNumber() {
		synchronized(client_lock) {
			return ++num_of_clients;
		}
	}
	
	/**
	 * @return current box office ID (current thread shares same name as box office)
	 */
	private String getBoxOfficeId() {
		return Thread.currentThread().getName();
	}
	
	/**
	 * While running, each box office (represented by a thread) attempts to find the best 
	 * available seat for its current client. If a seat is found, it is reserved for that client
	 * and their ticket is printed, displaying their reserved seat, unique client number, and the 
	 * box office at which they reserved their seat. If all seats run out, all threads terminate
	 * and a "sold out" message is printed. If a box office runs out of clients, its associated
	 * thread stops running as it need not reserve any more seats.
	 */
	public void run() {
		int clients_at_office = office.get(Thread.currentThread().getName());
		while (is_running && clients_at_office > 0) {
			int client_num;
			//synchronized to ensure that client numbering is consistent across threads
			synchronized(client_lock) { 
				client_num = getClientNumber();
			}
			Theater.Seat s = null;
			String boxOfficeId = getBoxOfficeId();
			synchronized(theater) {
				//a necessary if statement in case one of the threads has already entered the
				//while loop after is_running has been set to false
				if (is_running == false) break;
				
				s = theater.bestAvailableSeat();
				if (s != null) {
					theater.printTicket(boxOfficeId, s, client_num);
					clients_at_office--;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					is_running = false;
					System.out.println("Sorry, we are sold out!");
				}
			}
		}
	}

  /*
   * Starts the box office simulation by creating (and starting) threads
   * for each box office to sell tickets for the given theater
   *
   * @return list of threads used in the simulation,
   *         should have as many threads as there are box offices
   */
	public List<Thread> simulate() {
		List<Thread> thread_list = new ArrayList<Thread>();
		for (String b : office.keySet()) {//create a thread for each box office
			thread_list.add(new Thread(this, b));
		}
		for (Thread t : thread_list) {
			t.start();
		}
		return thread_list;
	}
	
	/**
	 * Initializes an instance of BookingClient to test the multi-threaded application
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, Integer> box_office = new HashMap<String, Integer>();
		box_office.put("BX1", 3);
		box_office.put("BX3", 3);
		box_office.put("BX2", 4);
		box_office.put("BX5", 3);
		box_office.put("BX4", 3);
		Theater theatre = new Theater(3, 5, "Ouija");
		BookingClient booking_client = new BookingClient(box_office, theatre);
		booking_client.simulate();
	}
}
