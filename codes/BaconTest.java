import java.io.IOException;
import java.util.Set;

/**
 * Read the test file and confirm all the methods in Graph Library works as intended
 * @author Aimen Abdulaziz
 * @author Angelic McPherson
 */
public class BaconTest {
    public static void main(String[] args) throws IOException {
        // test with the graph formed by the data read from movieTest.txt, actorTest.txt, and movie-actorTest.txt files
        BaconGame.fileReader("inputs/actorsTest.txt", "inputs/moviesTest.txt", "inputs/movie-actorsTest.txt");
        // graph built from the files we just read
        Graph<String, Set<String>> buildGraphData = BaconGame.buildGraph();
        Graph<String, Set<String>> shortestPathData = GraphLibrary.BFS(buildGraphData, "Kevin Bacon");
        System.out.println(shortestPathData);
        System.out.println(GraphLibrary.getPath(shortestPathData, "Dartmouth (Earl thereof)"));
        System.out.println(GraphLibrary.missingVertices(buildGraphData, shortestPathData));
        System.out.println(GraphLibrary.averageSeparation(shortestPathData, "Kevin Bacon"));
    }
}

