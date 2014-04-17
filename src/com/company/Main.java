package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Eduard Tarassov
 *         Version: 17/04/2014
 *         This program loads data from a URL connection and extracts specific data from there. Furthermore, it allows to use this
 *         content afterwards.
 */

public class Main {

    private static List<String> urlList = new ArrayList<String>(); // This list contains all URLs provided by user in the command prompt.
    private static List<String> wordList = new ArrayList<String>(); // This list contains all words for searching on pages provided by user in command prompt.
    private static List<String> optionList = new ArrayList<String>(); // This list contains all options provided by user in command prompt.
    private static long startTime; // This value will immediately initialised after program launches and will contain start time of the program.
    private static boolean[] optionOccur = new boolean[4]; // Element 0 is for -v, 1 is for e, 2 is for w, 3 is for c.
    // private static BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US); // We are going to use US locale as it most widely occurs in websites content.
    private static List<String> textResult = new ArrayList<String>(); //This List will contain line by line text from the page. So this allows us to keep all sentences separately.
    private static List<Integer> overallWordOccur = new ArrayList<Integer>(); //This list contains amount of searched word occurs on all pages. Size of this list depends exactly on wordList size.
    private static int charsPerPage; // Very straightforward. Contains number of chars on current page.

    /*
    * Main method, start point of program execution.
     */
    public static void main(String[] args) {
        // Initially setting all the options to false. Using those semaphores to decide afterwards which options user has chosen.
        for (int i = 0; i < optionOccur.length; i++)
            optionOccur[i] = false;

        // Taking all the arguments provided by user and sending them to userinputsort method to sort them between lists.
        for (String argument : args)
            userInputSort(argument);


        // Retrieving all the options one by one and processing them.
        for (int i = 0; i < optionList.size(); i++)
            optionProcessing(i);

        // responsible for -v option moves.
        startOptionV();

        // Taking string from the urlList and passing it to dataRetrieving() method.
        for (int i = 0; i < urlList.size(); i++) {
            dataRetrievingFromUrl(i);
            // Making a big space in console (between each page).
            textSpaces(10);
        }
        // Finishes operations responsible for -w option.
        finishOptionW();
        // Finishes operations responsible for -v option.
        finishOptionV();
    }

    private static void userInputSort(String value) {
        if (value.startsWith("http://www.")) // If value is url.
            urlList.add(value); // Add to list of urls.
        else if ((value.startsWith("-") || (value.length() == 2))) // Else if value is an option.
            optionList.add(value); // Add to list of options.
        else { // Else (value is applied word).
            wordList = Arrays.asList(value.split(",")); // Separating all the words between commas and putting everything into list of words.
            for (int i = 0; i < wordList.size(); i++)
                overallWordOccur.add(0); // Initialising initial set of values for amount of words found on pages.
        }
    }

     /*
     * This method is responsible for setting up the connection and read data.
     * Also it calls other methods of program to make different worth operations.
      */
    private static void dataRetrievingFromUrl(int i) {
        // Declaring necessary objects. Very straightforward.
        URL url;
        InputStream is = null;
        BufferedReader br = null;

        String s; // Value for every line of text we import.


        try {
            // Creating an URL connection.
            url = new URL(urlList.get(i));
            // Opening an input stream from the URL connection.
            is = url.openStream();
            // First of all we open our URL with InputStreamReader. Next we apply it to BufferedReader.
            // !!! Using BufferedReader to buffer the stream allows us to use readLine method. Thus implementation becomes easier and faster.
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            System.out.println("Start processing website: " + urlList.get(i));
            textSpaces(1); // Making a small space in the console.
            charsPerPage = 0;
            while ((s = br.readLine()) != null) {  // For every not empty line...
                cleanHtmlTags(s);  // This method removes all the html tags and other trash from the data. (not sure am I supposed to make this)
            }
            System.out.println("There were found " + charsPerPage + " chars on this web page.");
            textSpaces(1); // Making a small space in the console.

            startOptionE(); // Starting operation on -e option.


        } catch (MalformedURLException mue) {  // Catching exception of URL connection.

            System.err.println(mue);
            mue.printStackTrace();
            System.exit(1);

        } catch (IOException ioe) { // Input Stream exception.

            System.err.println(ioe);
            ioe.printStackTrace();
            System.exit(1);

        } finally {

            try {
                is.close(); // Closing the inputStream to prevent memory leak.
                br.close(); // Closing the BufferedReader to prevent memory leak. Now InputStreamReader closes automatically.
            } catch (IOException ioe) {  // Once more Input Stream exception.
                System.err.println(ioe);
            }

        }
    }

    /*
    * Responsible for setting the start time of the program. (option -v)
    */
    private static void startOptionV() {
        if (optionOccur[0]) {
            startTime = System.currentTimeMillis();
        }
    }

    /*
    * Finally counts the difference between finish time of program and start time.
    * Also converts result into seconds.
    */
    private static void finishOptionV() {
        if (optionOccur[0]) {
            long diff = System.currentTimeMillis() - startTime;

            System.out.println("Data retrieving and processing has taken: " + diff / 1000 + " seconds and " + diff % 1000 + " milliseconds.");
        }
    }

    /*
    * Basically, responsible not only for option -e, but for option with number of words found as well.
     */
    private static void startOptionE() {
        if (optionOccur[1]) {
            for (int i = 0; i < wordList.size(); i++) { // Checking every word provided by user.
                System.out.println("There were found matches of word < " + wordList.get(i) + " > in the next sentences: ");
                int wordOccur = 0;
                for (int j = 0; j < textResult.size(); j++) { // For every sentence we store in list...

                    if (textResult.get(j).contains(wordList.get(i))) {
                        System.out.println(textResult.get(j));
                        wordOccur++; // We increase number of words found if current sentence has it.
                    }

                }
                System.out.println("The word " + wordList.get(i) + " was found " + wordOccur + " times on this website.");
                overallWordOccur.set(i, overallWordOccur.get(i) + wordOccur); // Increasing the overall number of words from every page.
                textSpaces(1); // Small console space.

            }
            textResult.clear(); // Clearing all contents of the textResult. In other words we are setting it for the next page.
        }
    }

    /*
    * Simply printing the overall amount of  every provided by user words occur on all pages.
     */
    private static void finishOptionW() {
        for (int i = 0; i < overallWordOccur.size(); i++)
            System.out.println("Overall number of searched word < " + wordList.get(i) + " > found on different pages: " + overallWordOccur.get(i));
    }

    /*
    * The most complex method. Cleans all the html tags from the data retrieved from web.
     */
    private static void cleanHtmlTags(String line) {
        // Really awful idea, but probably do not have any better idea how to clean up everything from html tags without using Jsoup library.
        final String[] valueExceptions = {"{", "}", "#", "http", "[", "]", "_", ".view-", "--", "border-color", "background-color", "|", "/", "if (", "()", "=", ";"};
        // Returning a new BreakIterator instance for line breaks for the given locale.
        String current = line.replaceAll("\\<[^>]*>", "");

        boolean notHTML = true;
        // Taking out desired sentence.
        for (int i = 0; i < valueExceptions.length; i++)
            if (current.contains(valueExceptions[i]))
                notHTML = false; // if this sentence contains any html tags, then we won't save it.

        if ((notHTML)/* && (current.length() > 3) && (current.charAt(0) != ' ')*/) {
            textResult.add(current);   // Finally we add the absolutely clean sentence into our list.
            charsPerPage += current.length(); // At the same time there is no better place in the project to count amount of chars.
        }
    }

    /*
    * This method is straightforward. Simply checking if certain option occurs.
     */
    private static void optionProcessing(int i) {
        char option = optionList.get(i).charAt(1);

        switch (option) {
            case 'v':
                optionOccur[0] = true;
                break;
            case 'e':
                optionOccur[1] = true;
                break;
            case 'w':
                optionOccur[2] = true;
                break;
            case 'c':
                optionOccur[3] = true;
                break;
            default:
                // User input mistake
                textSpaces(1); // Small console space.
                System.out.println("Cannot process option < " + optionList.get(i) + " >.");
                textSpaces(1);  // Small console space.
                break;
        }
    }

    /*
    * Current method makes user interface more convenient to read. Simply adds spaces and separates text blocks.
    * Freelines parameter shows how huge will be our space.
    */
    private static void textSpaces(int freelines) {
        System.out.println();
        for (int j = 0; j < 8; j++)
            System.out.print("_________");

        while (freelines != 0) { // Until we will process all lines.
            System.out.println("");
            freelines--; // Decrementing freelines.
        }

        for (int j = 0; j < 8; j++)
            System.out.print("_________");
        System.out.println("\n");
    }

}

