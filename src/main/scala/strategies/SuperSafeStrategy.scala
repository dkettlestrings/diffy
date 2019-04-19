package strategies

import core.{Branch, MergeNode, MergeStrategy}

import scala.util.{Failure, Success, Try}

/** A simple MergeStrategy
  *
  * Note that this exists for demonstration purposes only.  It is assumed that any MergeStrategy you are using is more
  * sophisticated.  However, if you are tempted to use it, here is how it works (for succinctness, I will refer to
  * the "fromBranch" as FB and the "ontoBranch" as OB):
  *
  * First, it assumes that the first Node of FB is a Node in OB, i.e. that FB is a Branch of OB.  If that is not the
  * case, you get a Failure.  Next, it simply pulls out all of the diffs (operations) from the Nodes in FB.  It then
  * attempts to put them over the top of the head of OB.  If applying all of the diffs from FB on the head of OB
  * results in a valid object (valid in that we don't get an Exception calling resolve()), then we succeed.
  */
class SuperSafeStrategy[A] extends MergeStrategy[A] {

  override def merge(fromBranch: Branch[A], ontoBranch: Branch[A], idOfNewNode: String): Try[Branch[A]] = {

    /* This strategy requires a common ancestor shared by the two branches.  Note that the ".first" represents the
    first node in the "from" branch, but it is assumed that it branched from some node in the "onto" branch.  That's
    why even though we are getting the "first", it still has a "previous".
     */
    val commonAncestor = fromBranch.first.previous.get
    if(!ontoBranch.contains(commonAncestor)) {return Failure(new RuntimeException("NaiveMergeStrategy requires a common ancestor between branches"))}

    // Try just adding all commits from "fromBranch" onto the end of "ontoBranch"
    val diffFromBranch = fromBranch.nodes.map(_.operation.get)
    val mergeNode = new MergeNode[A](ontoBranch.latest, fromBranch, diffFromBranch, idOfNewNode)
    val testBranch = ontoBranch.commit(mergeNode)

    // Just check that we don't blow up.  If we don't, success!
    testBranch.latest.resolve() match {
      case e: Exception => Failure(e)
      case _ => Success(testBranch)
    }
  }

}

object SuperSafeStrategy {

  def of[A](): SuperSafeStrategy[A] = new SuperSafeStrategy[A]
}
