package org.mainPackage;

import java.util.*;

public class Filter {
    public static ArrayList<Map<Class, ArrayList<Class>>> getUserFilters(UserInputHandler userInputHandler, ArrayList<Map<Class, ArrayList<Class>>> allClasses) {
        boolean excludeFull = userInputHandler.getExcludeFull(); // If the user wants to exclude full classes
        boolean excludeClosed = userInputHandler.getExcludeClosed(); // If the user wants to exclude closed classes
        int startTime = userInputHandler.getStartTime(); // The earliest time the user wants their classes across all days to start

        System.out.println("""
                
                Block-out times ex. "T:1030-1140", "MThF:820-930P"
                P = pm
                ONLY USE "P" FOR LATE CLASSES PAST 800pm
                """);

        // Get the number of block out times the user wants to have
        int numBOT = userInputHandler.getBlockOutCount();

        // Get all the user block out times
        String[] blockOutTimes = userInputHandler.getBlockOutTimes(numBOT);

        return Filter.filterClasses(allClasses, startTime, excludeFull, excludeClosed, blockOutTimes);
    }

    public static ArrayList<Map<Class, ArrayList<Class>>> filterClasses(ArrayList<Map<Class, ArrayList<Class>>> allClasses, int startTime, boolean excludeFull, boolean excludeClosed, String[] blockOutTimes) {
        ArrayList<Map<Class, ArrayList<Class>>> finalList = new ArrayList<>();

        for (Map<Class, ArrayList<Class>> dict : allClasses) { // For each course
            Map<Class, ArrayList<Class>> newDict = new HashMap<>();

            for (Class lecture : dict.keySet()) { // For each lecture in a course
                // Determine if the lecture is considered valid with the user's restrictions
                boolean validLecture = validClass(lecture, startTime, excludeFull, excludeClosed, blockOutTimes);

                // Of the lectures that the user wants and are not empty
                if (validLecture) {
                        // Get a list of the valid quizzes
                        ArrayList<Class> quizzes = filterValidQuizzes(dict.get(lecture), startTime, excludeFull, excludeClosed, blockOutTimes);

                    // Add the valid lecture and quizzes to a Map<Class, ArrayList<Class>>
                    newDict.put(lecture, quizzes);
                }
            }

            // Add the filtered dictionaries back to the list, recreate the ArrayList<Map<Class, ArrayList<Class>>>
            finalList.add(newDict);
        }

        return finalList;
    }

    private static ArrayList<Class> filterValidQuizzes(ArrayList<Class> quizzes, int startTime, boolean excludeFull, boolean excludeClosed, String[] blockOutTimes) {
        ArrayList<Class> validQuizzes = new ArrayList<>();

        for (Class quiz : quizzes) {
            // Determine if the quiz is considered valid with the user's restrictions
            boolean validQuiz = validClass(quiz, startTime, excludeFull, excludeClosed, blockOutTimes);

            // Create an ArrayList<Class> of quizzes
            if (validQuiz) {
                validQuizzes.add(quiz);
            }
        }

        return validQuizzes;
    }

    private static boolean validClass(Class c, int startTime, boolean excludeFull, boolean excludeClosed, String[] blockOutTimes) {
        // Filter through lectures
        if (excludeFull && !c.isNotFull()) { // If the lecture is full
            return false;
        } else if (excludeClosed && c.getStatus().equals("Closed")) { // If the lecture is closed
            return false;
        } else if (c.getTimeSlot().getFirst().contains("arranged")) { // If the lecture time is not confirmed
            return false;
        } else if (ClassUtils.timeConflict(c.getTimeSlot().toArray(new String[0]), blockOutTimes)) { // Exclude user block out times
            return false;
        }

        // Exclude lectures before a specified start time
        for (String slot : c.getTimeSlot()) {
            int[] time = ClassUtils.convertTime(slot.split(":")[1]);
            if (time[0] < startTime) {
                return false;
            }
        }

        return true;
    }
}
