Diffy: Version Control for Your JVM Objects
=============================================

[![Build Status](https://travis-ci.org/dkettlestrings/diffy.svg?branch=master)](https://travis-ci.org/dkettlestrings/diffy)

# What Is It?

Diffy is a version control system for JVM objects.  It let's you maintain a 
history of your object using standard version control operations such as 
commit, branch, and merge.

# How Do I Use It?

The interface is designed to be similar your standard version control systems's
interface.  For example, suppose you had an `Employee` class along with 
operations on instances:

```
#!scala
class Employee(val name: String, val email: String)

object EmployeeOperations {
  def changeName(newName: String): Diff[Employee] = {employee: Employee => new Employee(newName, employee.email)}

  def changeEmail(newEmail: String): Diff[Employee] = {employee: Employee => new Employee(employee.name, newEmail)}
}

```

Then you can create an object history (known as a `Project`) and commit to it 
like this:

```
#!scala
val originalEmployee = new Employee("David Kettlestrings", "david.kettlestrings@company.com")
val project = new Project[Employee](originalEmployee, "employee-history", BasicIDGenerator)
val addMiddleInitial = EmployeeOperations.changeName("David E. Kettlestrings")
project.commit(Seq(addMiddleInitial), project.getBranch("master"))

// Latest has changes
project.getBranch("master").latest.resolve().name should be ("David E. Kettlestrings")
project.getBranch("master").latest.resolve().email should be ("david.kettlestrings@company.com")

//The one before (the original) is unchanged
project.getBranch("master").beforeLatest(1).resolve().name should be ("David Kettlestrings")
project.getBranch("master").beforeLatest(1).resolve().email should be ("david.kettlestrings@company.com")

```

Instead of putting a full set of examples here, they are placed within the 
`examples` package inside of `test` so that they are ensured to be 
up-to-date.  Also, all these examples (including this test) have a bunch more
exposition explaining what's going on.  Think of them as tutorials.

# Concepts

## Definitions

* **Project** - This maps to the concept of a project in the version control 
repository sense.  It contains all of the history (branches, commits, etc.) 
for a *single object*.

* **Node** - A single node in the Project's history graph.  Nodes corresponds 
to individual commits, branch events, and merge events.

* **Branch** - An ordered, linear sequence of Nodes sharing a name (the 
branch name).

* **Diff** - A function `A => A` where A is the type of the object in the 
`Project`.  These are the diffs between successive states in the 
object's history.

## Design

The most fundamental aspect of the design of this project is that **diffs**, 
not state, are the fundamental mechanism in storing an object's history.  
That is, instead of storing a new state and having the version control system
"figure out" the diff between them, it is up to the client to specify the diff 
and the version control mechanism will "figure out" (resolve) the state.

# How Do I Build/Test It?

It's a standard SBT build.  Go to the root of the project and execute

`sbt clean build`

or you can execute everything that's part of the Travis CI build and run the
contents of `.travis.yml`.  As of now that runs the build, the tests, the test
coverage reports, and generates the Scaladocs.