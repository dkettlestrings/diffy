import core.Diff

/** A class used for tests. */
class Person(val name: String)

object PersonOperations {

  def changeName(newName: String): Diff[Person] = { p: Person => new Person(newName)}

  def flakyOperation(): Diff[Person] = {
    if(true){throw new RuntimeException}
    {a: Person => a} // Just to satisfy the compiler
  }

}
