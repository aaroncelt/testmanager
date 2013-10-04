package testmanager.reporting.service.scenariogroup;

import testmanager.reporting.domain.reporting.Pair;

/**
 * The Class Default implementation of ScenarioGroupGeneratorStrategy.
 */
public class DefaultScenarioGroupGeneratorStrategy implements ScenarioGroupGenerationStrategy {

	@Override
	public Pair<String, String> generateScenarioNameAndPhase(String testname) {
		return new Pair<String, String>(testname, "");
	}

}
