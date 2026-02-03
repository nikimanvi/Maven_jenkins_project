package com.example;

/**
 * Simple application for Maven Jenkins project
 */
public class App {
    
    public static void main(String[] args) {
        System.out.println("Hello from Maven Jenkins Project!");
        App app = new App();
        System.out.println(app.getMessage());
    }
    
    public String getMessage() {
        return "Build successful with Jenkins!";
    }
    
    public int add(int a, int b) {
        return a + b;
    }
    
    public int multiply(int a, int b) {
        return a * b;
    }
}
