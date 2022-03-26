import java.io.IOException;
import java.util.*;

/**
 * Graph library with generic type vertex and edge
 * @author Aimen Abdulaziz
 * @author Angelic McPherson
 */
public class GraphLibrary<V,E> {
    static double totalSeparation; // total separation

    public GraphLibrary() {
    }

    /**
     * BFS to find shortest path tree for a current center of the universe.
     * @param G the given graph
     * @param start start vertex
     * @param <V> generic type vertex
     * @param <E> generic type edge
     * @return graph with specific center
     */
    public static <V,E> Graph<V,E> BFS(Graph<V, E> G, V start) {
        // edge case when the vertex is not in the graph
        if (!G.hasVertex(start)){
            System.out.println("No path to " + start);
            System.out.println("Please make sure " + start + " is spelt correctly");
            return new AdjacencyMapGraph<V, E>();
        }

        Graph<V,E> backTrack = new AdjacencyMapGraph<V,E>(); //initialize backTrack
        backTrack.insertVertex(start); //load start vertex with null parent
        Queue<V> queue = new LinkedList<V>(); //queue to implement BFS

        queue.add(start); //enqueue start vertex
        while (!queue.isEmpty()) { //loop until no more vertices
            V u = queue.remove(); //dequeue
            for (V v : G.outNeighbors(u)) { //loop over out neighbors
                if (!backTrack.hasVertex(v)) { //if neighbor not visited, then neighbor is discovered from this vertex
                    queue.add(v); //enqueue neighbor
                    backTrack.insertVertex(v); // add costar
                    backTrack.insertDirected(v, u, G.getLabel(u,v)); // insert directed edge from costar to the parent
                }
            }
        }
        return backTrack;
    }

    /**
     * Given a shortest path tree and a vertex, construct a path from the vertex back to the center of the universe.
     * @param tree the given graph
     * @param v any given vertex
     * @param <V> generic type vertex
     * @param <E> generic type edge
     * @return a list with the shortest path
     */
    public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {
        // boundary case when the tree is empty
        if (tree.numVertices() == 0){
            System.out.println("Empty tree");
            return new ArrayList<V>();
        }

        // boundary case when the provided vertex doesn't exist in the graph
        if (!tree.hasVertex(v)){
            System.out.println("No path to " + v);
            System.out.println("Please make sure " + v + " is spelt correctly");
            return new ArrayList<V>();
        }

        List<V> path = new ArrayList<V>();
        V current = v; //start at end vertex
        path.add(current);
        //loop from end vertex back to start vertex
        while (tree.outDegree(current) > 0){ //stop when root is reached
            current = tree.outNeighbors(current).iterator().next();
            path.add(current); //add this vertex to end of arraylist path
        }
        return path;
    }

    /**
     * Determine which vertices are in the graph but not the subgraph
     * @param graph the main graph
     * @param subgraph the smaller graph
     * @param <V> generic type vertex
     * @param <E> generic type edge
     * @return a set of vertices not in the subgraph
     */
    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph){
        Set<V> missingVertices = new HashSet<V>();
        for (V vertex: graph.vertices()) {
            if (!subgraph.hasVertex(vertex)){
                missingVertices.add(vertex);
            }
        }
        return missingVertices;
    }

    /**
     * Find the average distance-from-root in a shortest path tree.
     * @param tree the given graph
     * @param root the root of the graph
     * @param <V> generic type vertex
     * @param <E> generic type edge
     * @return a double that describes the avg separation
     */
    public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {
        if (tree.numVertices() == 0){
            return -1;
        }
        // boundary case when the root vertex is not in the shortest path tree
        if (!tree.hasVertex(root)){
            System.out.println(root + " is not in the shortest path tree");
            return -1;
        }
        totalSeparation = 0; // initialize total separation
        double edge = tree.numVertices();
        averageSeparationHelper(tree, root, 0);
        return totalSeparation / edge;
    }

    /**
     * Helper method for average separation
     * @param tree the graph
     * @param root the root of the graph
     * @param sum this counts the sum of the edges
     * @param <V> generic type vertex
     * @param <E> generic type edge
     */
    public static <V,E> void averageSeparationHelper(Graph<V,E> tree, V root, double sum){
        totalSeparation += sum;
        if (tree.inDegree(root) == 0) {
            return;
        }

        for (V child : tree.inNeighbors(root)) {
            averageSeparationHelper(tree, child, sum+1); //increment the sum; recurse with child
        }
    }

    /**
     * Hand-coded vertices and edges to test the methods in this file
     */
    public static void main(String[] args) throws IOException {
        // create graph
        Graph<String, String> relationships = new AdjacencyMapGraph<String, String>();

        // create vertices
        relationships.insertVertex("Kevin Bacon");
        relationships.insertVertex("Alice");
        relationships.insertVertex("Bob");
        relationships.insertVertex("Charlie");
        relationships.insertVertex("Dartmouth (Earl thereof)");
        relationships.insertVertex("Nobody");
        relationships.insertVertex("Nobody's Friend");

        // insert directed edges
        relationships.insertDirected("Kevin Bacon", "Alice", "{A Movie, E Movie}");
        relationships.insertDirected("Kevin Bacon","Bob","{A Movie}");
        relationships.insertDirected("Alice", "Bob","{A Movie}");
        relationships.insertDirected("Bob","Charlie","{C Movie}");
        relationships.insertDirected("Alice","Charlie", "{D Movie}");
        relationships.insertDirected("Charlie","Dartmouth (Earl thereof)","{B Movie}");
        relationships.insertDirected("Nobody","Nobody's Friend","{F Movie}");

        // test methods
        Graph<String, String> shortestPath = BFS(relationships, "Kevin Bacon");
        System.out.println(shortestPath);
        System.out.println(GraphLibrary.getPath(shortestPath, "Dartmouth (Earl thereof)"));
        System.out.println(GraphLibrary.missingVertices(relationships, shortestPath));
        System.out.println(GraphLibrary.averageSeparation(shortestPath, "Kevin Bacon"));
    }
}
