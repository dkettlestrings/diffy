package core

import scala.util.Try

/** A MergeStrategy represents a strategy for how to resolve potential conflicts in a merge.
  *
  * The MergeStrategy represents the most complicated aspect of this project.  In general it must take a "fromBranch",
  * an "ontoBranch", and try to determine how to apply the diffs in the "fromBranch" onto the "ontoBranch".
  */
trait MergeStrategy[A] {

  /** This method attempts to merge two Branches.
    *
    * This method should do one of two things
    *
    * 1. Merge the branches by doing a newBranch = ontoBranch.commit(diffs) and returning Success(newBranch) or
    * 2. Failure(something)
    *
    * @return A Try since merging is an inherently dangerous operation.
    */
  def merge(fromBranch: Branch[A], ontoBranch: Branch[A], idOfNewNode: String): Try[Branch[A]]
}
