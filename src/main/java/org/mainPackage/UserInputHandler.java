package org.mainPackage;

import java.util.Scanner;

public class UserInputHandler {
    private final Scanner sc;

    public UserInputHandler(Scanner sc) {
        this.sc = sc;
    }

    public String getYear() {
        System.out.print("Year: ");
        return sc.nextLine();
    }

    public String getQuarter() {
        System.out.print("Quarter: ");
        return sc.nextLine().toUpperCase().substring(0, 3); // Only return the first 3 letters of the quarter upper case
    }

    public int getCourseQuantity() {
        System.out.print("Number of classes: ");
        return Integer.parseInt(sc.nextLine());
    }

    public void gatherCourseDetails(int courseQuantity, String year, String quarter, String[] urls, String[] courseNumbers) {
        for (int i = 0; i < courseQuantity; i++) {
            System.out.print("Course " + (i + 1) + " abbreviation (or as it shows in the url): ");
            String abbrev = sc.nextLine().toLowerCase();
            System.out.print("Course " + (i + 1) + " number: ");
            String course_num = sc.nextLine();

            // getting a list of all the urls
            urls[i] = String.format("https://www.washington.edu/students/timeschd/%s%s/%s.html", quarter, year, abbrev);
            courseNumbers[i] = course_num;
        }
    }

    public boolean getExcludeFull(){
        System.out.print("Exclude full classes (Y/N): ");
        return sc.nextLine().equalsIgnoreCase("Y");
    }

    public boolean getExcludeClosed(){
        System.out.print("Exclude Closed classes (Y/N): ");
        return sc.nextLine().equalsIgnoreCase("Y");
    }

    public int getStartTime(){
        System.out.print("Start time (without colon ex. \"830\"): ");
        return Integer.parseInt(sc.nextLine());
    }

    public int getBlockOutCount(){
        System.out.print("Number of Block-out times: ");
        return Integer.parseInt(sc.nextLine());
    }

    public String[] getBlockOutTimes(int numBOT){
        String[] blockOutTimes = new String[numBOT];

        for (int i = 0; i < numBOT; i++) {
            System.out.print("Enter block-out time #" + (i + 1) + ": ");
            blockOutTimes[i] = sc.nextLine();
        }

        return blockOutTimes;
    }
}
