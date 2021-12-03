package com.example.playgroundmanager.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A helper class for loading data for tests
 */
public class Utils {
    public Utils() {
    }

    public ArrayList<Kid> getKids() {
        return new ArrayList<>(List.of(
                new Kid("Eric", "Evans", true),
                new Kid("Anna", "Smith", true),
                new Kid("John", "Doe", true),
                new Kid("Jonathan", "Smith", true),
                new Kid("Katherine", "Alison", true),
                new Kid("Peter", "Luis", true),
                new Kid("Lucy", "Anthony", true),
                new Kid("Bob", "Miller", true)));
    }

    public List<Playsite> getPlaysites() {
        Playsite carousel = new Playsite(PlaysiteType.CAROUSEL, 20, 5);
        Playsite ballPit = new Playsite(PlaysiteType.BALL_PIT, 8, 1);
        Playsite doubleSwings = new Playsite(PlaysiteType.DOUBLE_SWINGS, 2, 2);
        Playsite slide = new Playsite(PlaysiteType.SLIDE, 1, 1);
        return Arrays.asList(carousel, ballPit, doubleSwings, slide);
    }

    /**
     * Method to load historical data of playsites and their attendance from a csv file
     * @param fileName String - path to file
     * @param kidList ArrayList<Kid> - file uses Integers Ids for specifying kid.
     *                A list of kid objects is necessary to map these Ids to concrete Kid object.
     * @param playsites List<Playsite> - file uses Integers Ids for specifying playsite.
     *      *                A list of playsite objects is necessary to map these Ids to concrete Playsite object.
     * @return List<KidInPlaysite> - a list of KidInPlaysite objects that store the loaded data
     */
    public List<KidInPlaysite> loadHistoricalData(String fileName, ArrayList<Kid> kidList, List<Playsite> playsites) {
        List<KidInPlaysite> kdList = new ArrayList<>();
        Path pathToFile = Paths.get(fileName);

        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8)) {
            // First line should contain date
            String line = br.readLine();
            LocalDate date = LocalDate.parse(line);

            line = br.readLine();
            while (line != null) {
                String[] attributes = line.split(",");
                KidInPlaysite kp = createKidInPlaysite(date, kidList, playsites, attributes);
                kdList.add(kp);
                line = br.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return kdList;
    }

    /**
     * Method to create a KidInPlaysite object
     * @param date LocalDate - date that is loaded from the files first row
     * @param kidList ArrayList<Kid> - file uses Integers Ids for specifying kid.
     *                A list of kid objects is necessary to map these Ids to concrete Kid object.
     * @param playsites List<Playsite> - file uses Integers Ids for specifying playsite.
     *                A list of playsite objects is necessary to map these Ids to concrete Playsite object.
     * @param fileData String[] - one row of file data that contains info for new KidInPlaysite object
     * @return KidInPlaysite - object that stores the loaded data
     */
    private KidInPlaysite createKidInPlaysite(LocalDate date, ArrayList<Kid> kidList, List<Playsite> playsites, String[] fileData) {
        int kidIndex = Integer.parseInt(fileData[0]);
        int playsiteIndex = Integer.parseInt(fileData[1]);
        var startTime = LocalDateTime.of(date, LocalTime.parse(fileData[2]));
        var endTime = LocalDateTime.of(date, LocalTime.parse(fileData[3]));

        KidInPlaysite kidInPlaysite = new KidInPlaysite(kidList.get(kidIndex), playsites.get(playsiteIndex),
                startTime, endTime);

        kidList.get(kidIndex).getKidInPlaysite().add(kidInPlaysite);
        playsites.get(playsiteIndex).getKidInPlaysite().add(kidInPlaysite);

        return kidInPlaysite;
    }
}
