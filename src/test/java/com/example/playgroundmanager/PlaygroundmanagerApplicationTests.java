package com.example.playgroundmanager;

import com.example.playgroundmanager.model.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class PlaygroundmanagerApplicationTests {

	/**
	 * Test verifies that VIP and non-VIP balance is maintained as required (1 VIP skip for every 3 non-VIPs)
	 * 1. Check if kid is not allowed to attend playsite without a ticket
	 * 2. Add 6 kids without VIP, let them go to Slide (forms a queue of 5 kids, since only 1 kid per time is allowed)
	 * 3. Add 2 VIP kids, let them go to Slide
	 * 4. Verify that the sequence is as required
	 */
	@Test
	void testVipMechanism() {
		var kidList =  new Utils().getKids();

		Playsite slide = new Playsite(PlaysiteType.SLIDE, 1, 1);

		// Eric tries to attend SLIDE without a ticket
		slide.attendBy(kidList.get(0));

		// Ticket validity time
		LocalDateTime timeValidFrom = LocalDateTime.now();
		LocalDateTime timeValidTo = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(1).minusMinutes(1);

		var ticketList = new ArrayList<Ticket>();
		for (int i = 0; i < kidList.size(); i++) {
			ticketList.add(new Ticket(timeValidFrom, timeValidTo, 0, TicketType.INCLUDE_ALL));
			if (i >= kidList.size() - 2) { // Last two kids will have VIP tickets
				ticketList.get(i).setNumberOfTimesVIP(5);
			}

			// Create kid-to-ticket relationship
			kidList.get(i).getTickets().add(ticketList.get(i));
			ticketList.get(i).setKid(kidList.get(i));

			//  Kids go to SLIDE
			slide.attendBy(kidList.get(i));
		}

		var correctOrderOfKidsInQueue = new ArrayList<>(
				List.of("Lucy", "Anna", "John", "Jonathan", "Bob", "Katherine", "Peter")); // Order: VNNNVNN

		var actualOrderOfKidsInQueue = new ArrayList<>();

		for (int i = 0; i < kidList.size() - 1; i++) {
			actualOrderOfKidsInQueue.add(slide.getNextKidFromQueue().getFirstName());
		}
		Assertions.assertEquals(correctOrderOfKidsInQueue, actualOrderOfKidsInQueue);
	}

	/**
	 * Test outputs several reports:
	 * - for a list of kids: history of play sites and how long they played
	 * - total visitor count per day for a list playsites
	 * - utilization snapshots for a list of playsites: date 2021-08-15, start time 10:00, end time 20:00, every 60 min
	 * Historical data is loaded from a csv file
	 */
	@Test
	void testReporting() {
		var kidList =  new Utils().getKids();
		var playsites = new Utils().getPlaysites();

		List<KidInPlaysite> kp = new Utils().loadHistoricalData("src/main/resources/static/historical_data.csv",
				kidList, playsites);

		for (Kid kid : kidList) {
			kid.getHistory();
		}

		for (Playsite playsite : playsites) {
			playsite.getTotalVisitorCountPerDay();
		}

		Integer timeIntervalInMinutes = 60;
		LocalTime startTime = LocalTime.of(10,0);
		LocalTime endTime = LocalTime.of(20,0);

		var theDate = kp.get(0).getStartTime().toLocalDate();
		for (Playsite playsite : playsites) {
			playsite.getUtilizationSnapshot(theDate, startTime, endTime, timeIntervalInMinutes);
		}
	}

	/**
	 * Test verifies that regulations for DOUBLE_SWINGS playsite work as expected:
	 * - there must be exactly 2 kids at playsite - more or less is not considered safe
	 * 1. Add 1 kid to DOUBLE_SWINGS
	 * 2. Verify that he waits in the queue, since there must be 2 kids at this playsite
	 * 3. Add 1 more kid to DOUBLE_SWINGS
	 * 4. Verify that queue is empty, since both kids entered the playsite
	 * 5. Remove one kid from playsite
	 * 6. Verify that the other kid waits in the queue, since there must be 2 kids at this playsite
	 */
	@Test
	void testDoubleSwingsMinKidCount() {
		var kidList =  new ArrayList<>(List.of(
					new Kid("Eric", "Evans", true),
					new Kid("Anna", "Smith", true)));

		Playsite doubleSwings = new Playsite(PlaysiteType.DOUBLE_SWINGS, 2, 2);

		// Ticket validity time
		LocalDateTime timeValidFrom = LocalDateTime.now();
		LocalDateTime timeValidTo = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(1).minusMinutes(1);

		var ticketList = new ArrayList<Ticket>();
		for (int i = 0; i < kidList.size(); i++) {
			ticketList.add(new Ticket(timeValidFrom, timeValidTo, 0, TicketType.INCLUDE_ALL));

			// Create kid-to-ticket relationship
			kidList.get(i).getTickets().add(ticketList.get(i));
			ticketList.get(i).setKid(kidList.get(i));
		}

		// Eric goes to DOUBLE_SWINGS
		doubleSwings.attendBy(kidList.get(0));
		// He waits in the queue, since there need to be 2 kids to enter the playsite
		Assertions.assertEquals(1, doubleSwings.getMainQueue().size());
		// Anna goes to DOUBLE_SWINGS
		doubleSwings.attendBy(kidList.get(1));
		// Both kids can enter the playsite, since the required minimum of kids was reached. Queue should be empty
		Assertions.assertEquals(0, doubleSwings.getMainQueue().size());
		// Eric leaves DOUBLE_SWINGS
		doubleSwings.leaveBy(kidList.get(0));
		// Since there need to be 2 kids to play at the playsite, Anna must leave as well and joins the queue
		Assertions.assertEquals(1, doubleSwings.getMainQueue().size());
	}
}
