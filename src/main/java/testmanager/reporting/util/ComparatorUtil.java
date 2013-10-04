package testmanager.reporting.util;

import java.util.Comparator;

import testmanager.reporting.domain.reporting.ResultState;
import testmanager.reporting.domain.reporting.TestRunData;
import testmanager.reporting.service.reporting.SetRunManager;

public final class ComparatorUtil {
	private ComparatorUtil() {
	}

	public static final Comparator<SetRunManager> SET_RUN_TIME_ORDER_ASC = new Comparator<SetRunManager>() {
		@Override
		public int compare(SetRunManager o1, SetRunManager o2) {
			return o1.getStartDate().compareTo(o2.getStartDate());
		};
	};
	public static final Comparator<SetRunManager> SET_RUN_TIME_ORDER_DESC = new Comparator<SetRunManager>() {
		@Override
		public int compare(SetRunManager o1, SetRunManager o2) {
			return o2.getStartDate().compareTo(o1.getStartDate());
		}
	};

	public static final Comparator<TestRunData> TEST_RUN_ERROR_ORDER_ASC = new Comparator<TestRunData>() {
		@Override
		public int compare(TestRunData o1, TestRunData o2) {
			int result;
			if (ResultState.FAILED.equals(o1.getState()) || ResultState.FAILED.equals(o2.getState())) {
				result = compareByMessage(o1, o2);
			} else if (ResultState.NOT_AVAILABLE.equals(o1.getState()) || ResultState.NOT_AVAILABLE.equals(o2.getState())) {
				result = compareByMessage(o1, o2);
			} else if (!ResultState.PASSED.equals(o1.getState())) {
				result = -1;
			} else if (!ResultState.PASSED.equals(o2.getState())) {
				result = 1;
			} else {
				result = 0;
			}
			return result;
		}

		private int compareByMessage(TestRunData o1, TestRunData o2) {
			int result;
			if (o1.getErrorMessage() == null && o2.getErrorMessage() == null) {
				result = TEST_RUN_NAME_ORDER_ASC.compare(o1, o2);
			} else if (o1.getErrorMessage() != null && o2.getErrorMessage() == null) {
				result = -1;
			} else if (o1.getErrorMessage() == null && o2.getErrorMessage() != null) {
				result = 1;
			} else {
				result = o1.getErrorMessage().compareTo(o2.getErrorMessage());
			}
			return result;
		}
	};

	public static final Comparator<TestRunData> TEST_RUN_NAME_ORDER_ASC = new Comparator<TestRunData>() {
		@Override
		public int compare(TestRunData o1, TestRunData o2) {
			String s1 = o1.getTestName() + o1.getParamName();
			String s2 = o2.getTestName() + o2.getParamName();
			return s1.compareTo(s2);
		}
	};
}
