package com.chj.gr;


import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.junit.platform.engine.Constants;

/**
 * mvn test -Dtest=CucumberJUnit5Test2
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/test.feature")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.chj.gr.stepdefs")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports.html")
public class CucumberJUnit5Test2 {
}
