import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Program that utilizes the methods in Graph library to add functionality to the Kevin Bacon game
 * @author Aimen Abdulaziz
 * @author Angelic McPherson
 */

public class BaconGame {
    public static String currentCenter = "Kevin Bacon"; // center of universe
    public static Map<String, String> actorsMap = new HashMap<String, String>(); // actor id --> actor name
    public static Map<String, String> moviesMap = new HashMap<String, String>(); // movie id --> movie name
    public static Map<String, Set<String>> movieActors = new HashMap<String, Set<String>>(); // movie name --> set of actors
    public static Graph<String, Set<String>> baconGraph = new AdjacencyMapGraph<>(); // all actors with set of movies as edges
    public static BufferedReader input;
    public static Graph<String, Set<String>> shortestPathData; // graph from bfs

    public BaconGame(){

    }

    /**
     * Reads all the files
     * @param actorPath location of the actors file
     * @param moviePath location of the movies file
     * @param actorsMoviePath location of the actor and movie file
     * @throws IOException
     */
    public static void fileReader(String actorPath, String moviePath, String actorsMoviePath) throws IOException {
        // Open the file, if possible
        // read actors file
        try {
            input = new BufferedReader(new FileReader(actorPath));
        }
        catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
        }
        try {
            String line;
            while ((line = input.readLine()) != null) { // file not empty
                String[] actors = line.split("\\|");
                String actorId = actors[0];
                String actorName = actors[1];
                actorsMap.put(actorId, actorName);
            }
        }
        catch (IOException e) {
            System.out.println("I/O Error\n" + e.getMessage());
        }
        finally {
            // Close the file, if possible
            try {
                input.close();
            }
            catch (IOException e) {
                System.err.println("Cannot close file.\n" + e.getMessage());
            }
        }
        // Open the file, if possible
        // read movies file
        try {
            input = new BufferedReader(new FileReader(moviePath));
        }
        catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
        }
        try {
            String line;
            while ((line = input.readLine()) != null) { // file not empty
                String[] movies = line.split("\\|");
                String movieId = movies[0];
                String movieName = movies[1];
                moviesMap.put(movieId, movieName);
            }
        }
        catch (IOException e) {
            System.out.println("I/O Error\n" + e.getMessage());
        } finally {
            // Close the file, if possible
            try {
                input.close();
            }
            catch (IOException e) {
                System.err.println("Cannot close file.\n" + e.getMessage());
            }
        }

        // Open the file, if possible
        // read movie-actors file
        try {
            input = new BufferedReader(new FileReader(actorsMoviePath));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open file.\n" + e.getMessage());
        }
        try {
            String line;
            while ((line = input.readLine()) != null) { // file not empty
                String[] movieActorsList = line.split("\\|");
                String movieId = movieActorsList[0];
                String actorId = movieActorsList[1];
                if (movieActors.containsKey(moviesMap.get(movieId))) {
                    movieActors.get(moviesMap.get(movieId)).add(actorsMap.get(actorId)); // add actors to the set
                } else {
                    movieActors.put(moviesMap.get(movieId), new HashSet<>()); // movie name --> set of an actor
                    movieActors.get(moviesMap.get(movieId)).add(actorsMap.get(actorId));
                }
            }
        } catch (IOException e) {
            System.out.println("I/O Error\n" + e.getMessage());
        } finally {
            // Close the file, if possible
            try {
                input.close();
            } catch (IOException e) {
                System.err.println("Cannot close file.\n" + e.getMessage());
            }
        }
    }

    /**
     * Builds the graph from the file read
     * @return the graph with all the actors
     */
    public static Graph<String, Set<String>> buildGraph() {
        // create vertex for all actors
        for (String actorID : actorsMap.keySet()) {
            baconGraph.insertVertex(actorsMap.get(actorID));
        }

        // create the edges
        for (Map.Entry<String, Set<String>> entry : movieActors.entrySet()) { // movie name --> set of actors
            for (String actor : entry.getValue()){ // loop over all the actors of the movie
                for (String coStar : entry.getValue()){ // loop over all the actors of the movie
                    if (!actor.equals(coStar)){ // make sure the actors aren't the same
                        if (baconGraph.hasEdge(actor, coStar)) {
                            baconGraph.getLabel(actor, coStar).add(entry.getKey()); // add movie to the set of movies the actors co-starred
                        } else {
                            baconGraph.insertDirected(actor, coStar, new HashSet<>()); // create empty set
                            baconGraph.getLabel(actor, coStar).add(entry.getKey()); // add first movie
                        }
                    }
                }
            }
        }
        return baconGraph;
    }

    /**
     * Method to change the center of the universe
     * @param actor the actor's name
     * @param bfsGraph the created bfs Graph
     * @return A string showing the center, connected actors, and avg separation
     */
    public static String changeCenter(String actor, Graph<String, Set<String>> bfsGraph) {
        currentCenter = actor;
        return currentCenter +" is now the center of the acting universe, connected to " + connectedActors(bfsGraph) + "/9235 actors with average separation of " + averageSep(currentCenter, false);
    }

    /**
     * Shortest path from the center of the universe
     * @param s actor's name
     */
    public static void findShortestPath(String s) {
        List<String> path = GraphLibrary.getPath(shortestPathData, s);
        System.out.println(s + " number is " + (path.size()-1));
        for (int i = 0; i < path.size()-1; i++){
            System.out.println(path.get(i) + " appeared in " + baconGraph.getLabel(path.get(i), path.get(i+1)) + " with " + path.get(i+1));
        }
    }

    /**
     * Method to sort center of universes by their average separation
     * This method sorts the actors in increasing or decreasing order of average separation according to the user input
     * Best center of universe has low average separation
     * Worst center of universe has high average separation
     * @param num num of actors
     * @return the top or bottom num of actors
     */

    public static List<String> sortedByUniversalCenter(int num) {
        int finalNum = num;
        class SeparationComparator implements Comparator<String> {
            @Override
            public int compare(String s1, String s2) {
                if (finalNum > 0) {
                    // top (positive value) --> low average separation
                    return Double.compare(BaconGame.averageSep(s1, true), BaconGame.averageSep(s2, true));
                } else {
                    return Double.compare(BaconGame.averageSep(s2, true), BaconGame.averageSep(s1, true));

                }
            }
        }
        // add all actors by comparing their average separation to the pq
        PriorityQueue<String> pq = new PriorityQueue<String>(new SeparationComparator());
        for (String actor : baconGraph.vertices()) {
            pq.add(actor);
        }

        ArrayList<String> sortedList = new ArrayList<String>();
        // if number is negative, get its absolute value
        if (num < 0) {
            num = Math.abs(num);
        }
        while (!pq.isEmpty() && num > 0) {
            sortedList.add(pq.remove());
            num--;
        }
        return sortedList;
    }

    /**
     * infinite separation equals not connected/missing vertices
     * @return set of vertices
     */
    public static Set<String> infiniteSeparation() {
        return GraphLibrary.missingVertices(baconGraph, shortestPathData);
    }

    /**
     * Method to list actors sorted by non-infinite separation distance from the current center
     * @param lowNum lower boundary
     * @param highNum upper boundary
     * @return a list of the actors with avg separation between the bounds
     */
    public static List<String> sortedBySeparation(int lowNum, int highNum) {
        // store the distance from the root in a map to access the value at constant time later on
        // map is efficient because getDegree will only be executed once
        Map<String, Integer> separationMap = new HashMap<String, Integer>(); // actor --> separation
        for (String actor: baconGraph.vertices()) {
            if (shortestPathData.hasVertex(actor)) {
                separationMap.put(actor, BaconGame.getDistance(actor));
            }
        }
        class DistanceComparator implements Comparator<String> {
            @Override
            public int compare(String s1, String s2) {
                // top (positive value) means low average separation
                return separationMap.get(s1)-separationMap.get(s2);
            }
        }
        // add all actors by comparing their average separation to the pq
        PriorityQueue<String> pq = new PriorityQueue<String>(new DistanceComparator());
        for (String actor: baconGraph.vertices()) {
            if (shortestPathData.hasVertex(actor)){
                if (lowNum <= separationMap.get(actor) && separationMap.get(actor) <= highNum )
                    pq.add(actor);
            }
        }
        ArrayList<String> sortedList = new ArrayList<String>();

        while (!pq.isEmpty()){
            sortedList.add(pq.remove());
        }
        return sortedList;
    }

    /**
     * Return the distance of an actor from the root
     * @param actor actor's name
     * @return the number of people between the center and the actor
     */
    public static int getDistance(String actor){
        List<String> path = GraphLibrary.getPath(shortestPathData, actor); // get the shortest path from the center to the actor
        return (path.size()-1);
    }

    /**
     * Method to return a list of actors by degree
     * @param lowNum lower boundary
     * @param highNum upper boundary
     * @return a list of actors sorted by degree within a range
     */
    public static List<String> sortedByDegree(int lowNum, int highNum){
        // store the degree in a map to access the degree at constant time
        // map is efficient because getDegree will only be executed once
        Map<String, Integer> degreeMap = new HashMap<String, Integer>(); // actor --> degree
        for (String actor: baconGraph.vertices()) {
            degreeMap.put(actor, baconGraph.outDegree(actor));
        }
        class DegreeComparator implements Comparator<String> {
            @Override
            public int compare(String s1, String s2) {
                // top (positive value) means low average separation
                return degreeMap.get(s1)-degreeMap.get(s2);
            }
        }
        // add all actors by comparing their average separation to the pq
        PriorityQueue<String> pq = new PriorityQueue<String>(new DegreeComparator());
        for (String actor: baconGraph.vertices()) {
            if (lowNum <= degreeMap.get(actor) && degreeMap.get(actor) <= highNum )
                pq.add(actor);
        }
        ArrayList<String> sortedList = new ArrayList<String>();

        while (!pq.isEmpty()){
            sortedList.add(pq.remove());
        }
        return sortedList;
    }

    /**
     * Method to return the number of connected actors
     * @param bfsGraph the bfs Graph
     * @return num of edges in the graph
     */
    public static int connectedActors(Graph<String, Set<String>> bfsGraph){
        return bfsGraph.numEdges();
    }

    /**
     * Method to compute the average separation for each center of universe
     * @param actor actor's name
     * @param comparator a comparator
     * @return calls averageSeparation on actor and a given BFS Graph
     */
    public static double averageSep(String actor, boolean comparator){
        if (comparator) {
            Graph<String, Set<String>> tempGraph = GraphLibrary.BFS(baconGraph, actor);
            return GraphLibrary.averageSeparation(tempGraph, actor);
        }
        else{
            return GraphLibrary.averageSeparation(shortestPathData, actor);
        }
    }

    public static void main(String[] args) throws IOException {
        // test with the graph formed by the data read from movieTest.txt, actorTest.txt, and movie-actorTest.txt files
        BaconGame.fileReader("inputs/actors.txt", "inputs/movies.txt", "inputs/movie-actors.txt");
        // graph built from the files we just read
        buildGraph();
        shortestPathData = GraphLibrary.BFS(baconGraph, currentCenter);
        System.out.println("Commands:\n" +
                "c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation \n" +
                "d <low>,<high>: list actors sorted by degree, with degree between low and high\n" +
                "i: list actors with infinite separation from the current center \n" +
                "p <name>: find path from <name> to current center of the universe\n" +
                "s <low>,<high>: list actors sorted by non-infinite separation distance from the current center, with separation between low and high\n" +
                "u <name>: make <name> the center of the universe\n" +
                "q: quit game");
        Scanner input = new Scanner(System.in);
        System.out.println("\n"+ currentCenter +" is now the center of the acting universe, connected to " + connectedActors(shortestPathData) + "/9235 actors with average separation of " + averageSep(currentCenter, false));
        boolean gameMode = true;
        while (gameMode) {
            System.out.println("\n" + currentCenter + " game >");
            String a = input.nextLine();
            if (a.charAt(0) == 'u') {
                try {
                    // edge case: the provided actor is not in the graph
                    if (!baconGraph.hasVertex(a.substring(2))) {
                        System.out.println(a.substring(2) + " is not in the graph");
                    } else {
                        // edge case: provided actor is the same as the current center
                        if (Objects.equals(currentCenter, a.substring(2))) {
                            System.err.println(currentCenter + " is already the center of the universe");
                        } else {
                            currentCenter = a.substring(2); // change the current center to the new actor
                            shortestPathData = GraphLibrary.BFS(baconGraph, currentCenter);
                            System.out.println(changeCenter(currentCenter, shortestPathData));
                        }
                    }
                }
                catch (Exception e){
                    System.err.println("Invalid input. Please read the instruction for all valid keys");
                }
            }
            else if (a.charAt(0) == 'i') {
                System.out.println(infiniteSeparation());
            }

            else if (a.charAt(0) == 'c') { // create a method for each letter to be called in the main method
                try{// edge case: num is 0
                    String s = a.substring(2);  // start from character at i=2 and continues until the end
                    System.out.println(sortedByUniversalCenter((Integer.parseInt(s))));
                }
                catch (Exception e){
                    System.err.println("Invalid input. Please read the instruction for all valid keys");
                }
            }

            else if (a.charAt(0) == 'p') {
                try{
                    String s = a.substring(2);
                    findShortestPath(s);
                }
                catch (Exception e){
                    System.err.println("Invalid input. Please read the instruction for all valid keys");
                }
            }

            else if (a.charAt(0) == 's') {
                try {
                    String firstNum = a.substring(2, a.indexOf(','));
                    String secondNum = a.substring(a.indexOf(',') + 1);
                    //edge case: low is greater than high
                    if (Integer.parseInt(firstNum) > Integer.parseInt(secondNum)) {
                        System.err.println("The first number is greater than the second");
                        System.err.println("Please reenter the numbers");
                    }
                    //edge case: low and high are equal
                    else if (Integer.parseInt(firstNum) == Integer.parseInt(secondNum)) {
                        System.err.println("The first number is equal to the second");
                        System.err.println("Please reenter the numbers");
                    } else {
                        System.out.println(sortedBySeparation((Integer.parseInt(firstNum)), (Integer.parseInt(secondNum))));
                    }
                }
                catch (Exception e){
                    System.err.println("Invalid input. Please read the instruction for all valid keys");
                }
            }

            else if (a.charAt(0) == 'd') {
                try {
                    String firstNum = a.substring(2, a.indexOf(','));
                    String secondNum = a.substring(a.indexOf(',') + 1);
                    //edge case: low is greater than high
                    if (Integer.parseInt(firstNum) > Integer.parseInt(secondNum)) {
                        System.err.println("The first number is greater than the second");
                        System.err.println("Please reenter the numbers");
                    }
                    //edge case: low and high are equal
                    else if (Integer.parseInt(firstNum) == Integer.parseInt(secondNum)) {
                        System.err.println("The first number is equal to the second");
                        System.err.println("Please reenter the numbers");
                    } else {
                        System.out.println(sortedByDegree((Integer.parseInt(firstNum)), (Integer.parseInt(secondNum))));
                    }
                }
                catch (Exception e){
                    System.out.println("Invalid input. Please read the instruction for all valid keys");
                }
            }
            else if (a.charAt(0) == 'q'){
                System.out.println("The End!");
                gameMode = false;
            }
            else{
                System.err.println("Invalid input. Please read the instruction for all valid keys");
            }
        }
    }
}