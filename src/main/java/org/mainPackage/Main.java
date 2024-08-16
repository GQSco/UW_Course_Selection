package org.mainPackage;

import java.util.*;
/*
Subject = CHEMISTRY
Course = CHEM 142
Class = LECTURE OR QUIZ
 */


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Organize getting the users inputs
        UserInputHandler userInputHandler = new UserInputHandler(sc);

        // Retrieve user courses
        ArrayList<Map<Class, ArrayList<Class>>> allClasses = CourseInfo.getUserCourses(userInputHandler);

        // Filter courses based on user input
        ArrayList<Map<Class, ArrayList<Class>>> filteredClasses = Filter.getUserFilters(sc, allClasses);

        // Find the "best" schedule
        findAndPrintSchedule(filteredClasses);

        sc.close(); // Close the scanner
    }

    private static void findAndPrintSchedule(ArrayList<Map<Class, ArrayList<Class>>> filteredClasses) {
        // Get an array list of classes that are in the "best" schedule
        ArrayList<Class> classes = Schedule.findSchedule(filteredClasses);

        System.out.println();

        if (classes != null) { // If a possible schedule was found
            for (Class c : classes) { // Print the information of each class
                System.out.println(c.toString());
            }
        } else {
            System.out.println("No valid schedule found");
        }

        System.out.println();
    }
}