# tables

**Owner(s):** Carlos Devoto

A lightweight library containing to handle random generation of table results

## Usage

To use this library, you should add the following snippets of code to your ``build.gradle`` file:

```
repositories {
  maven {
    url 'https://raw.githubusercontent.com/cpdevoto/maven-repository/master'
  }
}
dependencies {
  implementation 'com.resolute:tables:3.24.3'
}
```

For an example, see https://raw.githubusercontent.com/cpdevoto/design-patterns/master/name-generator/build.gradle

## Building

To build a new version of this library and publish the resulting artifacts to GitHub:

  1. Edit the ``gradle.properties`` file and bump up the ``releaseNum`` by one.
  2. Run the following command: ``./gradlew clean build uploadArchives``
  3. ``cd`` to ``~/GitHub/maven-repository``.
  4. Run the following commands to commit your artifacts to GitHub
  
```
git add A- .
git commit -m "Released new version of tables"
git push
```  
