package com.sample.postgress.stepdefs;

import org.springframework.boot.test.web.server.LocalServerPort;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class TestStepDefs {

    @LocalServerPort
    private int port;

    @Given("the system is running")
    public void theSystemIsRunning() {
        System.out.println("System is running");
    }

    @Then("it should work")
    public void itShouldWork() {
        System.out.println("It works");
    }
}