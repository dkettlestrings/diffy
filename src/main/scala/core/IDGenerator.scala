package core

/** A very basic trait for a service that generates unique IDs.
  *
  * Within the scope of this project, an IDGenerator is required to give Nodes a unique identifier.
  */
trait IDGenerator {

  def getID(): String
}

/** An IDGenerator backed by java.util.UUID.randomUUID()
  */
object BasicIDGenerator extends IDGenerator {

  override def getID(): String = {java.util.UUID.randomUUID().toString}
}
