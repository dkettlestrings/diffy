package object core {

  /**
    * Shorthand for Function1[A,A].  It represents diffs (operations).
    */
  type Diff[A] = A => A

  /**
    * A [[Diff]] representing no change between two nodes.
    * It is the identity function.
    * @param a An arbitrary value.
    * @tparam A An arbitrary type.
    * @return What you put in.
    */
  def NoOp[A](a: A): Diff[A] = a => a

}
