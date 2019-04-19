import core.{BasicIDGenerator, Project}
import org.scalatest.{Matchers, FlatSpec}

class TestProject extends FlatSpec with Matchers {

  "A Project" should "be constructed by an initial instance" in {
    val person = new Person("dave")
    val project = new Project[Person](person, "project_for_a_person", BasicIDGenerator)

    project.branchExists("master") should be (true)
    project.getBranches().size should be (1)
    project.getBranches().toList.head.name should be ("master")
    project.getFullyQualifiedName(project.getBranch("master")) should be ("project_for_a_person/master")
  }

  "A Project" should "support branching" in {
    val person = new Person("dave")
    val project = new Project[Person](person, "project_for_a_person", BasicIDGenerator)
    val originalBranches = project.getBranches()

    originalBranches.size should be (1)
    originalBranches.map(_.name).contains("master") should be (true)

    val masterBranch = originalBranches.head
    val newBranch = project.branchFrom(masterBranch.latest, "new_branch", "master")

    val newBranches = project.getBranches()
    newBranches.size should be (2)
    newBranches.map(_.name).contains("master") should be (true)
    newBranches.map(_.name).contains("master/new_branch") should be (true)
  }

  "A Project" should "blow up when you branch from a non-existent branch" in {
    val person = new Person("dave")
    val project = new Project[Person](person, "project_for_a_person", BasicIDGenerator)

    intercept[RuntimeException] {project.branchFrom(project.getBranch("master").latest, "new_branch", "noSuchBranch")}
  }

  "A Project" should "not allow you to commit if the new instance is unstable" in {

    val person = new Person("dave")
    val project = new Project[Person](person, "project_for_a_person", BasicIDGenerator)
    val masterBranch = project.getBranch("master")

    // You can't commit if the new instance blows up, preserving the branch
    intercept[RuntimeException] {project.commit(Seq(PersonOperations.flakyOperation()), masterBranch)}
    masterBranch.nodes.size should be (1)
  }

  "A Project" should "not allow you to merge if the strategy fails" in {

    val person = new Person("dave")
    val project = new Project[Person](person, "project_for_a_person", BasicIDGenerator)
    val masterBranch = project.getBranch("master")

    val newBranch_v1 = project.branchFrom(masterBranch.latest, "new_branch", "master")
    val newBranch_v2 = project.commit(Seq(PersonOperations.changeName("bob")), masterBranch)

    // The merge should fail, preserving the branches
    val newMaster = project.merge(newBranch_v2, masterBranch, new FlakyMergeStrategy[Person])
    newMaster.isFailure should be (true)
    masterBranch.nodes.size should be (1)
    newBranch_v2.nodes.size should be (2) // Remember, the creation of the branch adds a node

  }

}
