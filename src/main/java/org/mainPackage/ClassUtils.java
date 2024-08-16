package org.mainPackage;

import java.util.*;

public class ClassUtils {

    public static Map<Class, ArrayList<Class>> toDictionary(ArrayList<Class> classes) {
        Map<Class, ArrayList<Class>> dict = new HashMap<>();

        Class lecture = null;
        ArrayList<Class> quizzes = null;
        for (Class c : classes) {
            if (!c.getType().equals("Quiz")) {
                if (lecture != null) {
                    dict.put(lecture, quizzes);
                }
                lecture = c;
                quizzes = new ArrayList<>();
            } else { // This is a quiz
                if (quizzes != null) {
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

        if (timeSlot.contains("P")) {
            timeSlot = timeSlot.substring(0, timeSlot.length() - 1);
            add12 = true;
        }

        String[] time = timeSlot.split("-");

        int[] intTime = new int[2];
        for (int i = 0; i < 2; i++) {
            intTime[i] = Integer.parseInt(time[i]);
            if (add12 || intTime[i] < 830) { // Add 12 hours to turn the time to military time
                intTime[i] = intTime[i] + 1200;
            }
        }

        return intTime;
    }

    // Determine if two lists of times have intersecting times & days
    public static boolean timeConflict(String[] timeSlots1, String[] timeSlots2) {
        for (String slot1 : timeSlots1) {
            for (String slot2 : timeSlots2) {
                int[] times1 = convertTime(slot1.split(":")[1]);
                int[] times2 = convertTime(slot2.split(":")[1]);
                Set<String> days1 = daysToSet(slot1.split(":")[0]);
                Set<String> days2 = daysToSet(slot2.split(":")[0]);

                for (int time : times1) {
                    if (times2[0] <= time && time <= times2[1]) { // If the time of class1 is in between or equal to the time of class2
                        days1.retainAll(days2); // Retain all the similar days

                        if (!days1.isEmpty()) {
                            return true;
                        }
                    }
                }
                for (int time : times2) {
                    if (times1[0] <= time && time <= times1[1]) {

                        days1.retainAll(days2);

                        if (!days1.isEmpty()) {
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

        if (days.contains("Th")) {
            daySet.add("Th");
            days = days.replace("Th", "");
        }

        for (char c : days.toCharArray()) {
            daySet.add(c + "");
        }

        return daySet;
    }

    // TODO probably get rid of this function
    public static ArrayList<Class> cloneClasses(ArrayList<Class> classes) {
        if (classes == null) {
            return null;
        }
        return new ArrayList<>(classes);
    }
}
