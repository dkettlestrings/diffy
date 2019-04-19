import core.{Branch, MergeStrategy}

import scala.util.{Failure, Try}


class FlakyMergeStrategy[A] extends MergeStrategy[A] {

  override def merge(fromBranch: Branch[A], ontoBranch: Branch[A], id: String): Try[Branch[A]] = {
    Failure(new RuntimeException("kaboom!"))
  }

}
