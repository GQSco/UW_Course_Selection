package org.mainPackage;

import java.util.*;

public class ClassUtils {

    // Organizes an ArrayList of classes into a dictionary where the lecture is the key and the quizzes are the value
    public static Map<Class, ArrayList<Class>> toDictionary(ArrayList<Class> classes) {
        Map<Class, ArrayList<Class>> dict = new HashMap<>();

        Class lecture = null;
        ArrayList<Class> quizzes = null;

        for (Class c : classes) {
            if (!c.getType().equals("Quiz")) { // If it's a lecture
                if (lecture != null) {
                    // Add the previous lecture and quizzes to the dictionary
                    dict.put(lecture, quizzes);
                }
                lecture = c;
                quizzes = new ArrayList<>(); // Create a new ArrayList for the quizzes after finding a different lecture
            } else { // If it's a quiz
                if (quizzes != null) { // If a lecture was found
                    quizzes.add(c);
                }
            }
        }

        dict.put(lecture, quizzes);

        return dict;
    }

    // Convert time ex. ("1130 - 120") to an int list and converts to military time {1130, 1320}
    public static int[] convertTime(String timeSlot) {
        boolean add12 = false;

        if (timeSlot.contains("P")) { // P stands for pm
            timeSlot = timeSlot.substring(0, timeSlot.length() - 1);
            add12 = true;
        }

        String[] time = timeSlot.split("-");

        int[] intTime = new int[2];
        for (int i = 0; i < 2; i++) {
            intTime[i] = Integer.parseInt(time[i]); // Getting the time as an int
            if (add12 || intTime[i] < 830) { // Add 12 hours to turn the time to military time, there isn't going to be class before 8:30am
                intTime[i] = intTime[i] + 1200;
            }
        }

        return intTime;
    }

    // Determine if two lists of times have intersecting times & days
    public static boolean timeConflict(String[] timeSlots1, String[] timeSlots2) {
        for (String slot1 : timeSlots1) {
            for (String slot2 : timeSlots2) { // For every combination of timeslots
                // Get the times as integer lists, ex. "830-920" -> (830, 920)
                int[] times1 = convertTime(slot1.split(":")[1]);
                int[] times2 = convertTime(slot2.split(":")[1]);
                // Get the days of the timeslots as sets
                Set<String> days1 = daysToSet(slot1.split(":")[0]);
                Set<String> days2 = daysToSet(slot2.split(":")[0]);

                // If the times of the first timeslot are in between the times of the second timeslot
                for (int time : times1) {
                    if (times2[0] <= time && time <= times2[1]) {

                        days1.retainAll(days2); // Retain all the similar days

                        if (!days1.isEmpty()) { // If it's empty, it doesn't share common days
                            return true;
                        }
                    }
                }
                // If the times of the second timeslot are in between the times of the first timeslot
                for (int time : times2) {
                    if (times1[0] <= time && time <= times1[1]) {

                        days1.retainAll(days2); // Retain all the similar days

                        if (!days1.isEmpty()) { // If it's empty, it doesn't share common days
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    // Turn a string of days ex. ("MTTH") into a set {"M", "T", "Th"}
    private static Set<String> daysToSet(String days) {
        Set<String> daySet = new HashSet<>();

        // Thursday is weird because it has two characters; it's best to get it out of the way prior
        if (days.contains("Th")) {
            daySet.add("Th");
            days = days.replace("Th", "");
        }

        for (char c : days.toCharArray()) {
            daySet.add(c + "");
        }

        return daySet;
    }
}
