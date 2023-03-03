# LET'S CODE SPRING BOOT 3

## Quirks related to Gradle and QueryDSL 
In IntelliJ IDEA:
* You may or may not have to go to `Settings` > `Annotation Processors` and then enable the use of annotation processors. We're not sure. It changes by the minute. 
* go to `Build, Execution, Deployment` > `Build Tools` > `Gradle` then select `Build and Run` using IntelliJ IDEA and `Run tests using` IntelliJ IDEA.
* If you have annotation processors disabled, and you have deleted `src/generated`, you should be able to run the `main(String[] args)` method in IntelliJ IDEA. But just _once_. You don't get a second chance unless you delete the `main(String [] args)` method. It is cursed.


