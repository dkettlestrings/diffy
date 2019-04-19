package core

/** An node in an object's history graph.
  *
  * @tparam A The type of objects you are storing in the Node.
  */
sealed trait Node[A] {

  /** Returns the instance associated with this point in history.
    *
    * @return An instance of A representing the object at the point of time represented by Node.
    */
  def resolve(): A

  /** The operation (a.k.a. diff) between this Node and it's parent.
    *
    * In other words, if Node M is a parent of node N (i.e. M == N.previous) then
    * N.operation(M.resolve()) == N.resolve()
    *
    * @return An Endomorphism[A] such that the statement above holds.
    */
  def operation: Option[Diff[A]]

  /** The parent of this Node.
    *
    * @return An Option containing either the parent Node or None (if the Node has no parent).
    */
  def previous: Option[Node[A]]

  /** An ID for the node (think changeset ID).
    *
    * This ID must be unique within the Project (or at least the Branch) or there is a possibility for weirdness.  See
    * BasicIdGenerator.
    *
    * @return The unique ID for the Node.
    */
  def id: String

  /** A utility function to apply the diffs in order from left to right.
    *
    * @param diffs A Sequence of Endomorphisms
    * @return An Endomorphism that is the composition of diffs applied left to right
    */
  def composeDiffs(diffs: Seq[Diff[A]]): Diff[A] = {Function.chain(diffs)}
}

/** The basic, most common Node implementation.
  *
  * A ChildNode is your most common type of Node.  It has a parent and has no knowledge of any funky branching or
  * merging.
  *
  * @param diff All of the diffs you want to apply.  They are applied in order from left to right.
  */
final case class ChildNode[A](previousNode: Node[A], diff: Seq[Diff[A]], uuid: String) extends Node[A] {
  override def resolve(): A = composeDiffs(diff)(previousNode.resolve())

  override def operation = Some(composeDiffs(diff))

  override def previous = Some(previousNode)

  override def id = uuid
}

/** A Node used for creating the initial commit to a Project.
  *
  * The distinguishing characteristic of this Node implementation is that it does not have a parent.
  */
final case class SeedNode[A](instance: A, uuid: String) extends Node[A] {
  override def resolve(): A = instance

  override def operation = None

  override def previous = None

  override def id = uuid
}

/** A Node implementation used for creating new Branches.
  *
  * The distinguishing characteristic of this Node implementation is that the operation (diff) between itself and its
  * parent is a NoOp (no-operation).
  */
final case class BranchNode[A](previousNode: Node[A], uuid: String) extends Node[A] {
  override def resolve(): A = previousNode.resolve()

  override def operation = Some(NoOp[A])

  override def previous = Some(previousNode)

  override def id = uuid
}

/** A Node implementation used for merging.
  *
  * The distinguishing characteristics of this Node implementation are that
  *
  * 1. It has knowledge of which branch it was merged from
  * 2. It takes a Sequence of operations and composes them to get the final operation.
  *
  * @param diff All of the diffs you want to apply.  They are applied in order from left to right.
  */
final case class MergeNode[A](previousNode: Node[A], val mergedFrom: Branch[A], diff: Seq[Diff[A]], uuid: String) extends Node[A] {
  override def resolve(): A = composeDiffs(diff)(previousNode.resolve())

  override def operation = Some(composeDiffs(diff))

  override def previous = Some(previousNode)

  override def id = uuid
}