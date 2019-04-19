package strategies

import core.{BasicIDGenerator, Project}
import examples.{Employee, EmployeeOperations}
import org.scalatest.{FlatSpec, Matchers}

class TestSuperSafeStrategy extends FlatSpec with Matchers {

  "The SuperSafeStrategy" should "place the fromBranch commits after the ontoBranch" in {

    val originalEmployee = new Employee("David Kettlestrings", "david.kettlestrings@company.com")
    val project = new Project[Employee](originalEmployee, "employee-history", BasicIDGenerator)

    project.branchFrom(project.getBranch("master").latest, "newBranch", "master")

    val addMiddleInitial = EmployeeOperations.changeName("David E. Kettlestrings")
    project.commit(Seq(addMiddleInitial), project.getBranch("master"))

    val changeEmailDomain = EmployeeOperations.changeEmail("david.kettlestrings@business.com")
    project.commit(Seq(changeEmailDomain), project.getBranch("master/newBranch"))

    // master and master/newBranch are out of sync (both have advanced one
    // commit since the newBranch spun off

    // Merge newBranch onto master by putting commit in newBranch after last
    // commit in master
    project.merge(project.getBranch("master/newBranch"), project.getBranch("master"), SuperSafeStrategy.of[Employee]())
    project.getBranch("master").nodes.length should be (3)

    val first = project.getBranch("master").beforeLatest(2).resolve()
    val second = project.getBranch("master").beforeLatest(1).resolve()
    val third = project.getBranch("master").latest.resolve()

    first.name should be ("David Kettlestrings")
    first.email should be ("david.kettlestrings@company.com")

    second.name should be ("David E. Kettlestrings")
    second.email should be ("david.kettlestrings@company.com")

    third.name should be ("David E. Kettlestrings")
    third.email should be ("david.kettlestrings@business.com")
  }

  "The SuperSafeStrategy" should "reverse the order of commits if you reverse arguments of merge" in {

    val originalEmployee = new Employee("David Kettlestrings", "david.kettlestrings@company.com")
    val project = new Project[Employee](originalEmployee, "employee-history", BasicIDGenerator)

    project.branchFrom(project.getBranch("master").latest, "newBranch", "master")

    val addMiddleInitial = EmployeeOperations.changeName("David E. Kettlestrings")
    project.commit(Seq(addMiddleInitial), project.getBranch("master"))

    val changeEmailDomain = EmployeeOperations.changeEmail("david.kettlestrings@business.com")
    project.commit(Seq(changeEmailDomain), project.getBranch("master/newBranch"))

    // master and master/newBranch are out of sync (both have advanced one
    // commit since the newBranch spun off
  }

}
