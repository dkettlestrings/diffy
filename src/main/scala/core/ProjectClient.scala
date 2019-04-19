package core

import scala.util.Try

/** This defines the interface for a Project.
  *
  * Whether a Project is local to your JVM or running on a remote server, the ProjectClient allows you to interact
  * with it in the same way.
  */
trait ProjectClient[A] {

  /** Returns all of the Branches in this Project.
    */
  def getBranches(): Set[Branch[A]]

  /** Checks whether there exists a Branch with the given name.
    *
    * Use the relative (not fully qualified) path.
    */
  def branchExists(name: String): Boolean

  /** Gets you the Branch of the given name.
    *
    * Use the relative (not fully qualified) path.  Also, as per test/examples/BasicUsage, this is the recommended
    * means of interacting with Branches.  That is, do not use long-lived object references to Branches; instead,
    * invoke this method whenever you need a Branch.  Concurrency concerns are the biggest reason for this.
    */
  def getBranch(branchName: String): Branch[A]

  /** Simply gives you the branch name with the project name prepended.
    */
  def getFullyQualifiedName(branch: Branch[A]): String

  /** Put a new Node on the head of the Branch.
    */
  def commit(diff: Seq[Diff[A]], branch: Branch[A]): Branch[A]

  /** Create a new Branch starting from the given Node and Branch.
    *
    * Use a relative (not fully qualified) path for the newBranchName.  The resulting branch will have name
    * oldBranchName/newBranchName
    */
  def branchFrom(startingNode: Node[A], newBranchName: String, oldBranchName: String): Branch[A]

  /** Merge the Nodes of "fromBranch" onto "ontoBranch" using the MergeStrategy.
    */
  def merge(fromBranch: Branch[A], ontoBranch: Branch[A], strategy: MergeStrategy[A]): Try[Branch[A]]

}
