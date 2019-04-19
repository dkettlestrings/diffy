package core

import scala.collection.mutable
import scala.util.{Success, Try, Failure}

/**  The main class for creating a history for an object.
  *
  * A Project allows you to do version control for an object.  For a complete tutorial, see test/examples/BasicUsage.
  */
class Project[A](seedInstance: A, val projectName: String, idGenerator: IDGenerator) extends ProjectClient[A] {

  val Seperator = "/"
  val MasterBranchName = "master"

  private var branches = mutable.Set(new Branch[A](Seq(new SeedNode[A](seedInstance, idGenerator.getID())), MasterBranchName))

  override def getBranches(): Set[Branch[A]] = branches.toSet

  override def branchExists(name: String): Boolean = getBranches().map(_.name).contains(name)

  override def getBranch(branchName: String): Branch[A] = {
    getBranches().find(_.name == branchName).get
  }

  override def getFullyQualifiedName(branch: Branch[A]): String = projectName + Seperator + branch.name

  private def addBranch(branch: Branch[A]): Unit = {
    assert(!branchExists(branch.name))
    branches += branch
    assert(branchExists(branch.name))
  }

  private def deleteBranch(branchName: String): Unit = {
    assert(branchExists(branchName))
    branches -= getBranch(branchName)
    assert(!branchExists(branchName))
  }

  private def updateBranch(updated: Branch[A]): Unit = {
    assert(branchExists(updated.name))
    deleteBranch(updated.name)
    addBranch(updated)
    assert(branchExists(updated.name))
  }

  private def commit(node: Node[A], branch: Branch[A]): Branch[A] = {
    val updatedBranch = branch.commit(node)
    updateBranch(updatedBranch)
    getBranch(updatedBranch.name)
  }

  override def commit(diff: Seq[Diff[A]], branch: Branch[A]): Branch[A] = {
    commit(new ChildNode[A](branch.latest, diff, idGenerator.getID()), branch)
  }

  override def branchFrom(startingNode: Node[A], newBranchName: String, oldBranchName: String): Branch[A] = {

    if(!branchExists(oldBranchName)) {throw new RuntimeException("Unrecognized branch: " + oldBranchName)}

    val newBranch = new Branch[A](Seq(new BranchNode[A](startingNode, idGenerator.getID())), oldBranchName + Seperator + newBranchName)
    addBranch(newBranch)
    getBranch(newBranch.name)
  }

  override def merge(fromBranch: Branch[A], ontoBranch: Branch[A], strategy: MergeStrategy[A]): Try[Branch[A]] = {
    val possibleBranch = strategy.merge(fromBranch, ontoBranch, idGenerator.getID())
    possibleBranch match {
      case Success(newBranch) => {
        updateBranch(newBranch)
        Success(newBranch)
      }
      case Failure(e) => Failure(e)
    }

  }

}
