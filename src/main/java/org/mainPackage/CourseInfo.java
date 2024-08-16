package org.mainPackage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class CourseInfo {
    public static ArrayList<Map<Class, ArrayList<Class>>> getUserCourses(UserInputHandler userInputHandler) {
        int courseQuantity = userInputHandler.getCourseQuantity(); // Getting the number of courses

        // When making a schedule, all classes are going to have the same year and quarter
        String year = userInputHandler.getYear();
        String quarter = userInputHandler.getQuarter();

        String[] urls = new String[courseQuantity];
        String[] courseNumbers = new String[courseQuantity];

        // Getting the urls and course numbers from each course that the user has selected
        userInputHandler.gatherCourseDetails(courseQuantity, year, quarter, urls, courseNumbers);

        System.out.println("Gathering course information...");

        // Connect to each course website, parse through the course information, and return it
        return gatherCourseInfo(urls, courseNumbers, courseQuantity);
    }

    private static ArrayList<Map<Class, ArrayList<Class>>> gatherCourseInfo(String[] urls, String[] courseNumbers, int courseQuantity) {

        // Create a 2D ArrayList of all the classes, separated by the course
        ArrayList<ArrayList<Class>> courses = new ArrayList<>();
        for (int i = 0; i < courseQuantity; i++) {
            courses.add(CourseInfo.getClasses(urls[i], courseNumbers[i])); // Get all the courses classes
        }

        // Organize the ArrayList<ArrayList<Class>> into a dictionary
        ArrayList<Map<Class, ArrayList<Class>>> allClasses = new ArrayList<>();
        for (ArrayList<Class> course : courses) {
            Map<Class, ArrayList<Class>> dict = ClassUtils.toDictionary(course);
            allClasses.add(dict);
        }

        return allClasses;
    }

    private static ArrayList<Class> getClasses(String url, String courseNum) {
        ArrayList<Class> classes = new ArrayList<>();

        try {
            // Connect to the subject's time information website
            Document doc = Jsoup.connect(url).get();
            System.out.println("Connected to: " + url);

            // Parse through the HTML of the website to get class info
            Elements courses = doc.select("table[style=border:solid 1px #999;margin-bottom:4px]"); // Selecting all the courses
            Element startCourse = courses.select("table:contains(" + courseNum + ")").first(); // Select the table before the classes of the focus course
            Element endCourse = selectNextCourse(courses, startCourse); // Select the table after the classes of the focus course

            if (startCourse != null) { // If the course has been found
                System.out.println("Selected: " + startCourse.text());

                 //All the classes for the course in the HTML File are tables and can be found in between the startCourse and endCourse tables
                 //The startCourse table it the table of the selected course, while the endCourse table it the table after
                parseClassesBetween(doc.select("table"), startCourse, endCourse, classes);

            } else {
                System.out.println("Unable to find course number: " + courseNum);
            }

        } catch (IOException e) {
            System.out.println("Whar??"); // Whar??
            System.out.println("Error: " + e);
        }

        return classes;
    }

    private static Element selectNextCourse (Elements courses, Element startCourse) {
        boolean foundStart = false;
        for (Element course : courses) {
            if (foundStart) {
                return course;
            }
            if (course.equals(startCourse)) {
                foundStart = true;
            }
        }

        return null;
    }

    private static void parseClassesBetween(Elements allTables, Element startCourse, Element endCourse, ArrayList<Class> classes) {
        boolean withinRange = false;

        for (Element table : allTables) {
            if (table == endCourse) {
                withinRange = false;
            }

            if (withinRange) {
                String[] info = table.text().split("\\s+"); // Split all the course information into an array
                Class c = parseClassInfo(info); // Parse through the array to grab important info
                classes.add(c);
            }

            if (table == startCourse) {
                withinRange = true;
            }
        }
    }

    private static Class parseClassInfo(String[] info) {
        // I hate this stupid ass fucking function, but it works
        // The course website is awful so parsing through it is a pain in the ass

        String sln = "";
        String section = "";
        String type = "Lecture";
        ArrayList<String> dayTime = new ArrayList<>();
        String status = "";
        int enrolled = 0;
        int capacity = 0;
        int fee = 0;
        StringBuilder additionalInfo = new StringBuilder();

        for (int i = 0; i < info.length; i++) {

            if (info[i].matches("\\d{5}") || info[i].matches(">\\d{5}")) { // If it's the sln code
                if (!info[i].contains(">")) {
                    sln = info[i];
                } else {
                    sln = info[i].substring(1);
                }
            } else if (info[i].equals("Open") || info[i].equals("Closed")) { // Status
                status = info[i];
            } else if (info[i].matches("\\d+/")) { // Enrollment info ex. (29/   30)
                enrolled = Integer.parseInt(info[i].substring(0, info[i].length() - 1));
                if (info[i + 1].contains("E")) {
                    capacity = Integer.parseInt(info[i + 1].substring(0,2)); // substring because some capacity as an "E" at the end which stands for estimation
                } else {
                    capacity = Integer.parseInt(info[i + 1]);
                }
            } else if (info[i].matches("\\d{3,4}-\\d{3,4}") || info[i].matches("\\d{3,4}-\\d{3,4}P") || info[i].contains("arranged")) { // Time and days
                dayTime.add(info[i - 1] + ":" + info[i]);
            } else if (info[i].contains("$")) {
                fee = Integer.parseInt(info[i].substring(1));
            }else if (0 < i && i < 4) {
                if (section.isEmpty() && info[i].matches("\\w{1,2}")) { // Section
                    section = info[i];
                } else if (info[i].contains("QZ")) {
                    type = "Quiz";
                }
            } else {
                additionalInfo.append(info[i]).append(" ");
            }
        }

        return new Class(sln, section, type, dayTime, status, enrolled, capacity, fee, additionalInfo.toString());
    }
}
