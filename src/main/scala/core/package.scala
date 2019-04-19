/** Shared type definitions
  */
package object core {

  /** Shorthand for Function1[A,A].  It represents diffs (operations).
    */
  type Diff[A] = A => A

  /** A [[Diff]] representing no change between this node and its parent
    */
  def NoOp[A](a: A) = a

}
