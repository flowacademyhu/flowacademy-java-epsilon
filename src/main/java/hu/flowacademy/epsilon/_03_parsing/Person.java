package hu.flowacademy.epsilon._03_parsing;

// Little data class we use in both DOM and SAX parser examples
class Person {
    final String firstName;
    final String lastName;
    final int age;

    Person(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
}
