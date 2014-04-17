package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author Eduard Tarassov This program loads data from a URL connection and
 *         extracts specific data from there. Furthermore, it allows to use this
 *         content afterwards.
 */

public class Main {

    private static List<String> urlList = new ArrayList<String>();
    private static List<String> wordList = new ArrayList<String>();
    private static List<String> optionList = new ArrayList<String>();
    private static long startTime;
    private static boolean[] optionOccur = new boolean[4]; // Element 0 is for -v, 1 is for e, 2 is for w, 3 is for c.

    public static void main(String[] args) {
        for (int i = 0; i < optionOccur.length; i++)
            optionOccur[i] = false;

        for (String argument : args) {

            userInputSort(argument);
        }

        for (int i = 0; i < optionList.size(); i++)
            optionProcessing(i);

        startOptionV();

        // Taking string from the urlList and passing it to dataRetrieving() method.
        for (int i = 0; i < urlList.size(); i++) {
            dataRetrievingFromUrl(i);

            // Retrieving all the options one by one and processing them.
            for (int j = 0; j < optionList.size(); j++)
                optionProcessing(j);

            textSpaces(7);
        }

        finishOptionV();
    }

    private static void userInputSort(String value) {
        if (value.startsWith("http://www.")) // If value is url.
            urlList.add(value);
        else if ((value.startsWith("-") || (value.length() == 2))) // Else if value is option.
            optionList.add(value);
        else // Else (value is applied word)
            wordList = Arrays.asList(value.split(","));
    }


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

            while ((s = br.readLine()) != null)
                startOptionE(s);
            //System.out.println(s);
        } catch (MalformedURLException mue) {

            System.err.println(mue);
            mue.printStackTrace();
            System.exit(1);

        } catch (IOException ioe) {

            System.err.println(ioe);
            ioe.printStackTrace();
            System.exit(1);

        } finally {

            try {
                is.close(); // Closing the inputStream to prevent memory leak.
                br.close(); // Closing the BufferedReader to prevent memory leak. Now InputStreamReader closes automatically.
            } catch (IOException ioe) {
                System.err.println(ioe);
            }

        }
    }

    private static void startOptionV() {
        if (optionOccur[0]) {
            startTime = System.currentTimeMillis();
        }
    }

    private static void finishOptionV() {
        if (optionOccur[0]) {
            long diff = System.currentTimeMillis() - startTime;

            System.out.println("Data retrieving and processing has token: " + diff / 1000 + " seconds and " + diff % 1000 + " milliseconds.");
        }

    }

    private static void startOptionE(String line) {
        final String[] valueExceptions = {"<", ">", "{", "}", "#", "http", "[", "]", "_", ".view-", "--", "border-color", "background-color", "|", "/", "if (", "()", "=", ";", " ?", " :"};
        // Returning a new BreakIterator instance for line breaks for the given locale.
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US); // We are going to use US locale as it most widely occurs in websites content.

       /* // Extracting sentences with matched word.
        for (int j = 0; j < wordList.size(); j++) {
            textSpaces(1);
            System.out.println("Below are listed all the sentences with < " + wordList.get(j) + " > matches.");
            textSpaces(1);   */

        // Putting our line of text into BreakIterator.
        iterator.setText(line);
        // Integer start is responsible for position of start of sentence.
        int start = iterator.first();
        // Loop to go through sentences.
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String current = line.substring(start, end);
            current = current.replaceAll("\\<[^>]*>","");

            boolean notHTML = true;
            // Taking out desired sentence.
            for (int i = 0; i < valueExceptions.length; i++)
            if (current.contains(valueExceptions[i]))
                notHTML = false;

            if (notHTML)
                System.out.println(current);
        }

    }


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
                // user input mistake
                textSpaces(1);
                System.out.println("Cannot process option < " + optionList.get(i) + " >.");
                textSpaces(1);
                break;
        }
    }

    private static void textSpaces(int freelines) {
        System.out.println();
        for (int j = 0; j < 8; j++)
            System.out.print("_________");

        while (freelines != 0) {
            System.out.println("");
            freelines--;
        }

        for (int j = 0; j < 8; j++)
            System.out.print("_________");
        System.out.println("\n");
    }

}

