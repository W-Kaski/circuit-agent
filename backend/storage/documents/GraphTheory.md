# Graph Theory Essentials

Graphs consist of vertices (nodes) and edges.

## Graph Representation
- **Adjacency Matrix**: A 2D array where `matrix[i][j]` represents an edge.
- **Adjacency List**: An array of lists; efficient for sparse graphs.

## Search Algorithms
### Breadth-First Search (BFS)
- Uses a **Queue**.
- Finds the shortest path in unweighted graphs.
- Complexity: $O(V + E)$.

### Depth-First Search (DFS)
- Uses a **Stack** (or recursion).
- Used for topological sorting and finding cycles.
- Complexity: $O(V + E)$.

## Shortest Path
- **Dijkstra's Algorithm**: Weighted graphs, no negative edges. $O(E \log V)$.
- **Bellman-Ford**: Handles negative weights. $O(VE)$.
