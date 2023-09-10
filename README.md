## Test it out

- Clone this repository and `cd` into it: `git clone <url> && cd <projectName>`
- Install to your local Maven repo: `./mvnw clean install` (compiles and installs to your ~/.m2 directory so that you can depend on them)
- In your project, add a dependency on the project:

  ```xml
  <dependency>
    <groupId>uk.co.jonathonhenderson</groupId>
    <artifactId>breport</artifactId>
    <version>1.0-SNAPSHOT</version>
  </dependency>
  ```
- Run your tests to see the output (currently uses `System.out.println` but can be changed to write to a file)
- Try adding some of the annotations to some tests, e.g.

  ```java
  @Test
  @Given("some setup happens")
  @When("something occurs")
  @Then("I expect something to happen")
  void someTest() {
    // ...
  }
  
  // alternatively...
  
  @Test
  @BReport(
      given = "some setup happens",
      when = "something occurs",
      then = "I expect something to happen")
  void anotherTest() {
    // ...
  }
  ```
- Run your tests again to see the output (again, currently uses `System.out.println` but can be changed to write to a file)