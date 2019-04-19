package core

/** A branch (ordered sequence of Nodes) in a Project.
  */
class Branch[A](val nodes: Seq[Node[A]], val name: String) {

  /** Adds a Node to the head of the Branch.
    *
    * Note that the Node you are adding must have its previous Node the same Node that is currently at the head of
    * the Branch.
    *
    * @param node The Node to add to the head of the Branch.
    * @return A new Branch instance with the new Node at the head.
    */
  def commit(node: Node[A]): Branch[A] = {
    if(node.previous.get != nodes.last) {throw new RuntimeException("The node you are trying to commit has " +
      "as its ancestor " + node.previous.get.id + ", but the last element of this branch is" + nodes.last.id)}

    new Branch[A](nodes :+ node, name)
  }

  /** The latest (or head) Node of the Branch.
    *
    * @return The latest Node.
    */
  def latest: Node[A] = nodes.last

  /** Gives you a Node before latest.
    *
    * Example: branch.beforeLatest(1) gives you the Node just before latest
    *
    * @param numberOfNodesToGoBack Number of Nodes to go back from latest.
    * @return The Node.
    */
  def beforeLatest(numberOfNodesToGoBack: Int): Node[A] = {nodes(nodes.length - (1 + numberOfNodesToGoBack))}

  /** The first node in the Branch.
    *
    * @return The first Node.
    */
  def first: Node[A] = nodes.head

  /** Checks for containment.
    *
    * @param node The Node to look for.
    * @return A Boolean representing whether the Node is in the Branch or not.
    */
  def contains(node: Node[A]): Boolean = nodes.contains(node)

}
