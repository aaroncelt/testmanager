package testmanager.reporting.service.scenariogroup;

import testmanager.reporting.domain.reporting.Pair;

/**
 * The Interface ScenarioGroupGenerationStrategy.
 */
public interface ScenarioGroupGenerationStrategy {

	/**
	 * Generate scenario name and phase.
	 * 
	 * @param testname the testname
	 * @return the pair left is the scenario name right is the scenario phase
	 *         name
	 */
	Pair<String, String> generateScenarioNameAndPhase(String testname);

}
