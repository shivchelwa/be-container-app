package com.tibco.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tibco.cep.kernel.model.knowledgebase.DuplicateExtIdException;
import com.tibco.cep.runtime.model.element.Concept;
import com.tibco.cep.runtime.model.event.SimpleEvent;
import com.tibco.cep.runtime.service.tester.beunit.BETestEngine;
import com.tibco.cep.runtime.service.tester.beunit.ExpectationType;
import com.tibco.cep.runtime.service.tester.beunit.Expecter;
import com.tibco.cep.runtime.service.tester.beunit.TestDataHelper;

/**
 * @description 
 */
public class BEUnitTestSuite {
	private static BETestEngine engine;
	private static TestDataHelper helper;
	private static Expecter expecter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		engine = new BETestEngine("/Users/shivkumarchelwa/workspace/docker/CHC/be_container_app-1.0.0.ear", "/Users/shivkumarchelwa/Applications/BE5.5/be/5.5/bin/be-engine.tra",
				"Deployments/be_container_app.cdd", "default", "inference-class", true);

		// Start the test engine
		engine.start();
		
		// Create a helper to work with test data
		helper = new TestDataHelper(engine);
		
		// Create an Expecter object to test rule execution, modifications, assertions, etc.
		expecter = new Expecter(engine);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		try {
			engine.shutdown();
		} catch (Exception localException) {
		}
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	* Test whether a particular Concept was modified by the engine during rule execution
	*/
	@Test
	public void testEligibilityResponseModified() throws Exception {
		engine.resetSession(); // (optional) reset the rule session, which will clear working memory, restart timers, and clear the data from any previous tests

		assertTestData();
		
		engine.executeRules();

		expecter.expectModified("/Concepts/EligibilityResponse", "inforce");
	}
	
	/**
	* Test whether one rule fired after another rule during rule execution, in order 
	* (use expectUnordered to test whether both rules fired in any order) 
	*/
	@Test
	public void testRuleOrder() throws Exception {
		engine.resetSession(); // (optional) reset the rule session, which will clear working memory, restart timers, and clear the data from any previous tests

		assertTestData();
		
		engine.executeRules();
		List<String> rules = new ArrayList<String>();
		rules.add("/Rules/DetermineCoverage");
		rules.add("/Rules/ReplyEligibility");
		expecter.expectOrdered(rules, ExpectationType.RULE_EXECTION);
	}
	
	/**
	* Test whether DetermineCoverage Rule has fired
	*/
	@Test
	public void testDetermineCoverageRuleFired() throws Exception {
		System.out.println("##testDetermineCoverageRuleFired##");
		
		engine.resetSession(); // (optional) reset the rule session, which will clear working memory, restart timers, and clear the data from any previous tests

		assertTestData();
		
		engine.executeRules();

		expecter.expectRuleFired("/Rules/DetermineCoverage");
	}
	
	/**
	* Test whether ReplyEligibility Rule has fired
	*/
	@Test
	public void testReplyEligibilityFired() throws Exception {
		System.out.println("##testReplyEligibilityFired##");
		
		engine.resetSession(); // (optional) reset the rule session, which will clear working memory, restart timers, and clear the data from any previous tests

		assertTestData();
		
		engine.executeRules();

		expecter.expectRuleFired("/Rules/ReplyEligibility");
	}

	private void assertTestData() throws Exception, DuplicateExtIdException {
		String eligibilityReqConceptJson = "{	\"resourceType\": \"EligibilityRequest\",	\"ID\": \"23454\",	\"status\": \"active\",	\"patient\": {		\"reference\": \"Patient/G23445\"	},	\"created\": \"2019-01-01T07:15:30.000\",	\"organization\": {		\"reference\": \"Organization/ABC\"	},	\"insurer\": {		\"reference\": \"Organization/Acme\"	},	\"coverage\": {		\"reference\": \"C-000008\"	}, \"requestId\": \"JUNIT_TEST_1\"}";
		Concept eligibilityReqConcept = helper.createConceptFromJSON("/Concepts/EligibilityRequest", eligibilityReqConceptJson);

		String eligibilityResConceptJson = "{	\"resourceType\": \"EligibilityRequest\",	\"ID\": \"JUNIT_TEST_1\",	\"status\": \"active\",	\"patient\": {		\"reference\": \"Patient/G23445\"	},	\"created\": \"2019-01-01T07:15:30.000\",	\"organization\": {		\"reference\": \"Organization/ABC\"	},	\"insurer\": {		\"reference\": \"Organization/Acme\"	},	\"coverage\": {		\"reference\": \"C-000008\"	}}";
		Concept eligibilityResConcept = helper.createConceptFromJSON("/Concepts/EligibilityResponse", eligibilityResConceptJson);


		String coverageConceptJson = "{    \"CoverageId\": \"C-000008\",    \"EffectiveStartDate\": \"2019-01-01T00:00:00.000Z\",    \"EffectiveEndDate\": \"2019-12-31T23:23:59.999Z\",    \"Deductible\": 3000,    \"OutOfPocketMax\": 5500,    \"Copay\": \"30\",    \"Coinsurance\": \"30\",    \"Covered\": \"Individual+Family\"  }";
		Concept coverageConcept = helper.createConceptFromJSON("/Concepts/Coverage", coverageConceptJson);
		
		String findCoverageReqJson = "{\"FindCoverageRequest\":{\"requestId\": \"JUNIT_TEST_1\"}}";
		SimpleEvent promotionEvent = (SimpleEvent) helper.createEventFromJSON("/Events/FindCoverageRequest", findCoverageReqJson);
		engine.assertEvent(promotionEvent, true);
		
		List<Concept> conceptList = new ArrayList<Concept>();
		conceptList.add(eligibilityReqConcept);
		conceptList.add(eligibilityResConcept);
		conceptList.add(coverageConcept);
		
		engine.assertConcepts(conceptList, true);
	}
}