package org.mainPackage;

import java.util.*;

public class Filter {
    public static ArrayList<Map<Class, ArrayList<Class>>> getUserFilters(Scanner sc, ArrayList<Map<Class, ArrayList<Class>>> allClasses) {
        System.out.print("Exclude full classes (Y/N): ");
        boolean excludeFull = sc.nextLine().equalsIgnoreCase("Y");
        System.out.print("Exclude Closed classes (Y/N): ");
        boolean excludeClosed = sc.nextLine().equalsIgnoreCase("Y");
        System.out.print("Start time (without colon ex. \"830\"): ");
        int startTime = Integer.parseInt(sc.nextLine());

        // Get user block out dates
        System.out.println("""
                
                Block-out times ex. "T:1030-1140", "MThF:820-930P"
                P = pm
                ONLY USE "P" FOR LATE CLASSES PAST 800pm
                """);
        System.out.print("Number of Block-out times: ");
        int numBOT = Integer.parseInt(sc.nextLine());

        String[] blockOutTimes = new String[numBOT];
        for (int i = 0; i < numBOT; i++) {
            System.out.print("Enter block-out time #" + (i + 1) + ": ");
            blockOutTimes[i] = sc.nextLine();
        }

        return Filter.userFilters(allClasses, startTime, excludeFull, excludeClosed, blockOutTimes);
    }

    public static ArrayList<Map<Class, ArrayList<Class>>> userFilters(ArrayList<Map<Class, ArrayList<Class>>> allClasses, int startTime, boolean excludeFull, boolean excludeClosed, String[] blockOutTimes) {
        ArrayList<Map<Class, ArrayList<Class>>> finalList = new ArrayList<>();

        for (Map<Class, ArrayList<Class>> dict : allClasses) {
            Map<Class, ArrayList<Class>> newDict = new HashMap<>();

            for (Class lecture : dict.keySet()) {
                // Determine if the lecture is considered valid with the user's restrictions
                boolean validLecture = validClass(lecture, startTime, excludeFull, excludeClosed, blockOutTimes);

                // Of the lectures that the user wants and are not empty
                if (!dict.get(lecture).isEmpty() && validLecture) {
                    ArrayList<Class> quizzes = new ArrayList<>();

                    for (Class quiz : dict.get(lecture)) {
                        // Determine if the quiz is considered valid with the user's restrictions
                        boolean validQuiz = validClass(quiz, startTime, excludeFull, excludeClosed, blockOutTimes);

                        // Create an ArrayList<Class> of quizzes
                        if (validQuiz) {
                            quizzes.add(quiz);
                        }
                    }

                    // Add the valid lecture and quizzes to a Map<Class, ArrayList<Class>>
                    newDict.put(lecture, quizzes);
                } else if (validLecture){
                    newDict.put(lecture, new ArrayList<>(0));
                }
            }

            // Add the filtered dictionaries back to the list, recreate the ArrayList<Map<Class, ArrayList<Class>>>
            finalList.add(newDict);
        }

        return finalList;
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
