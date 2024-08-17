package org.mainPackage;

import java.util.ArrayList;
import java.util.Map;

public class Schedule {
    public static ArrayList<Class> findSchedule(ArrayList<Map<Class, ArrayList<Class>>> allClasses) {
        ArrayList<ArrayList<Class>> allSchedules = new ArrayList<>();
        ArrayList<Class> schedule = new ArrayList<>();

        // Getting a 2D ArrayList of all possible schedules with user courses and filters
        recursiveSearch(allClasses, allClasses.size() - 1, schedule, allSchedules);

        // Returns the best scoring schedule
        return findBestSchedule(allSchedules);
    }

    private static void recursiveSearch(ArrayList<Map<Class, ArrayList<Class>>> allClasses, int index, ArrayList<Class> schedule, ArrayList<ArrayList<Class>> allSchedules) {
        if (index < 0) { // If it was able to get classes from all courses
            allSchedules.add(new ArrayList<>(schedule)); // Add the valid schedule to the list of all possible schedules
            return;
        }

        // Choose a course to focus on based on the index
        Map<Class, ArrayList<Class>> focusCourse = allClasses.get(index);

        for (Class lecture : focusCourse.keySet()) {
            if (noConflict(schedule, lecture)) {
                schedule.add(lecture);

                if (!focusCourse.get(lecture).isEmpty()) { // If the lecture has quizzes
                    for (Class quiz : focusCourse.get(lecture)) {
                        if (noConflict(schedule, quiz)) {
                            schedule.add(quiz);

                            recursiveSearch(allClasses, index - 1, schedule, allSchedules);

                            schedule.removeLast(); // Remove the last quiz added
                        }
                    }
                } else {
                    recursiveSearch(allClasses, index - 1, schedule, allSchedules);
                }

                schedule.removeLast(); // Remove the last lecture added
            }
        }
    }

    private static ArrayList<Class> findBestSchedule(ArrayList<ArrayList<Class>> allSchedules) {
        // The lower the score, the earlier the class times
        int bestScore = Integer.MAX_VALUE;
        ArrayList<Class> finalSchedule = null;

        // Scores each schedule and returns the one with the lowest score; the earliest class time; the most concentrated classes
        for (ArrayList<Class> possibleSchedule : allSchedules) {
            int newScore = score(possibleSchedule);
            if (newScore < bestScore) {
                bestScore = newScore;
                finalSchedule = new ArrayList<>(possibleSchedule);
            }
        }

        return finalSchedule;
    }

    // Checks to see if there are time conflicts between an ArrayList of classes and another class
    private static boolean noConflict(ArrayList<Class> schedule, Class newClass) {
        for (Class c : schedule) {
            if (ClassUtils.timeConflict(c.getTimeSlot().toArray(new String[0]), newClass.getTimeSlot().toArray(new String[0]))) {
                return false;
            }
        }

        return true;
    }

    // Gives a schedule a score depending on class times. The lower the score, the earlier.
    private static int score(ArrayList<Class> schedule) {
        int score = 0;
        int numberOfDays = 0;

        for (Class c : schedule) {
            for (String timeAndDay : c.getTimeSlot()) {
                String[] info =  timeAndDay.split(":");
                String time = info[1];
                String days = info[0];

                // Find out how many days there are the classes
                // Subtract one for "Th" because it has two characters
                if (days.contains("Th")) {
                    numberOfDays -= 1;
                }
                for (int i = 0; i < days.length(); i++) {
                    numberOfDays += 1;
                }

                int[] times = ClassUtils.convertTime(time); // Get int values of the times
                for (int t : times) {
                    score += (t * numberOfDays); // The lower the score, the earlier the classes and the fewer days it takes up
                }
            }
        }

        return score;
    }
}
