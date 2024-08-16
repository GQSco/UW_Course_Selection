package org.mainPackage;

import java.util.ArrayList;
import java.util.Map;

public class Schedule {
    public static ArrayList<Class> findSchedule(ArrayList<Map<Class, ArrayList<Class>>> allClasses) {
        ArrayList<ArrayList<Class>> allSchedules = new ArrayList<>();
        ArrayList<Class> schedule = new ArrayList<>();

        recursiveSearch(allClasses, 0, schedule, allSchedules);

        int bestScore = Integer.MAX_VALUE;
        ArrayList<Class> finalSchedule = null;

        for (ArrayList<Class> possibleSchedule : allSchedules) {
            int newScore = score(possibleSchedule);
            if (newScore < bestScore) {
                bestScore = newScore;
                finalSchedule = ClassUtils.cloneClasses(possibleSchedule);
            }
        }

        return finalSchedule;
    }

    private static void recursiveSearch(ArrayList<Map<Class, ArrayList<Class>>> allClasses, int index, ArrayList<Class> schedule, ArrayList<ArrayList<Class>> allSchedules) {
        if (index == allClasses.size()) {
            allSchedules.add(new ArrayList<>(schedule)); // Add the valid schedule to the list of all possible schedules
            return;
        }

        Map<Class, ArrayList<Class>> focusCourse = allClasses.get(index);

        for (Class lecture : focusCourse.keySet()) {
            if (noConflict(schedule, lecture)) {
                schedule.add(lecture);

                if (!focusCourse.get(lecture).isEmpty()) {
                    for (Class quiz : focusCourse.get(lecture)) {
                        if (noConflict(schedule, quiz)) {
                            schedule.add(quiz);

                            recursiveSearch(allClasses, index + 1, schedule, allSchedules);

                            schedule.removeLast(); // Remove the last quiz added
                        }
                    }
                } else {
                    recursiveSearch(allClasses, index + 1, schedule, allSchedules);
                }

                schedule.removeLast(); // Remove the last lecture added
            }
        }
    }

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
        // TODO run test cases and see if this function works
        int score = 0;
        int numberOfDays = 0;

        for (Class c : schedule) {
            for (String timeDay : c.getTimeSlot()) {
                String[] info =  timeDay.split(":");
                String time = info[1];
                String days = info[0];

                // finding out how many days there are the classes
                if (days.contains("Th")) {
                    numberOfDays -= 1;
                }
                for (int i = 0; i < days.length(); i++) {
                    numberOfDays += 1;
                }

                int[] times = ClassUtils.convertTime(time);
                for (int t : times) {
                    score += t * numberOfDays;
                }
            }
        }

        return score;
    }
}
