# Kevin-Bacon
This program tackles the important social network problem of finding an actor's "Bacon number". Starting with an actor, see if they have been in a movie with someone who has been in a movie with someone who has been in a movie... who has been in a movie with Kevin Bacon. They're usually at most 6 steps away. 

In the Kevin Bacon game, the vertices are actors and the edge relationship is "appeared together in a movie". The goal is to find the shortest path between two actors. Traditionally the goal is to find the shortest path to Kevin Bacon, but we'll allow anybody to be the center of the acting universe. We'll also do some analyses to help find better Bacons. Since "degree" means the number of edges to/from a vertex, I'll refer to the number of steps away as the "separation" rather than the common "degrees of separation".

The easiest way to play the Kevin Bacon game is to do a breadth-first search (BFS). This builds a tree of shortest paths from every actor who can reach Kevin Bacon back to Kevin Bacon. More generally, given a root, BFS builds a shortest-path tree from every vertex that can reach the root back to the root. It is a tree where every vertex points to its parent, and the parent is the next vertex in a shortest path to the root. For the purposes of this project, we will store the tree as a directed graph. Once the tree is constructed, we can find the vertex for an actor of interest, and follow edges back to the root, tracking movies (edge labels) and actors (vertices) along the way.

For this project, we use a large set of actor-movie data (thanks to Brad Miller at Luther College). The three main files, actors.txt, movies.txt, and movie-actors.txt are large — 9,235 actors, 7,067 movies, and 21,370 movie-actor pairs, resulting in 32,337 edges.

The Kevin-Bacon game has the following functionalities: 
- change the center of the acting universe to a valid actor
- find the shortest path to an actor from the current center of the universe
- find the number of actors who have a path (connected by some number of steps) to the current center
- find the average path length over all actors who are connected by some path to the current center

In addition, the program will help you find other possible Bacons, according to two different criteria:
- degree (number of costars).
- average separation (path length) when serving as center of the universe. This functionality takes time for a large data set

The game is implemented in such a way that it handles numerous edge cases. 

This project was done as an assignment for Dartmouth’s Computer Science course. If you are a professor teaching this course and would like me to make the repository private, please reach out to me at [here](mailto:aimenaabdulaziz@gmail.com). Thanks!

Enjoy the game :)





