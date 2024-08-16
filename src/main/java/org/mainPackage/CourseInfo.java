package org.mainPackage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class CourseInfo {
    public static ArrayList<Map<Class, ArrayList<Class>>> getUserCourses(Scanner sc) {
        System.out.print("Number of classes: ");
        int courseQuantity = Integer.parseInt(sc.nextLine());

        // When making a schedule, all courses are going to have the same year and quarter
        System.out.print("Year: ");
        String year = sc.nextLine();
        System.out.print("Quarter: ");
        String quarter = sc.nextLine().toUpperCase().substring(0, 3);

        String[] urls = new String[courseQuantity];
        String[] courseNumbers = new String[courseQuantity];
        for (int i = 0; i < courseQuantity; i++) {
            System.out.print("Course " + (i + 1) + " abbreviation (or as it shows in the url): ");
            String abbrev = sc.nextLine().toLowerCase();
            System.out.print("Course " + (i + 1) + " number: ");
            String course_num = sc.nextLine();

            // getting a list of all the urls
            urls[i] = String.format("https://www.washington.edu/students/timeschd/%s%s/%s.html", quarter, year, abbrev);
            courseNumbers[i] = course_num;
        }

        System.out.println("Gathering course information...");

        return gatherCourseInfo(urls, courseNumbers, courseQuantity);
    }

    private static ArrayList<Map<Class, ArrayList<Class>>> gatherCourseInfo(String[] urls, String[] courseNumbers, int courseQuantity) {

        // Get all the courses' classes
        ArrayList<ArrayList<Class>> courses = new ArrayList<>();
        for (int i = 0; i < courseQuantity; i++) {
            courses.add(CourseInfo.getClasses(urls[i], courseNumbers[i]));
        }

        // Organize the ArrayList<ArrayList<Class>> to a more organized dictionary
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
            Document doc = Jsoup.connect(url).get();
            System.out.println("Connected to: " + url);

            Elements courses = doc.select("table[style=border:solid 1px #999;margin-bottom:4px]");
            Element startCourse = courses.select("table:contains(" + courseNum + ")").first(); // Select the table before the classes of the focus course
            Element endCourse = selectNextCourse(courses, startCourse);

            if (startCourse != null) { // If the course has been found
                System.out.println("Selected: " + startCourse.text());
                parseClassesBetween(doc.select("table"), startCourse, endCourse, classes);
            } else {
                System.out.println("Unable to find course number: " + courseNum);
            }

        } catch (IOException e) {
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
                String[] info = table.text().split("\\s+");
                Class c = parseClassInfo(info);
                classes.add(c);
            }

            if (table == startCourse) {
                withinRange = true;
            }
        }
    }

    private static Class parseClassInfo(String[] info) {
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
