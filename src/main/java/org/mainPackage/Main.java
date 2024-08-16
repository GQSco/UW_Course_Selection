package org.mainPackage;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        ArrayList<Map<Class, ArrayList<Class>>> allClasses = CourseInfo.getUserCourses(sc);

        System.out.println();

        ArrayList<Map<Class, ArrayList<Class>>> filteredClasses = Filter.getUserFilters(sc, allClasses);

        ArrayList<Class> classes = Schedule.findSchedule(filteredClasses);

       System.out.println();

        if (classes != null) {
            for (Class c : classes) {
                System.out.println(c.toString());
            }
        } else {
            System.out.println("No valid schedule found");
        }

        System.out.println();

        sc.close(); // Closing the scanner
    }
}