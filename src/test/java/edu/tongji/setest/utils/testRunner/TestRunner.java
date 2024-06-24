package edu.tongji.setest.utils.testRunner;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRunner {

    public static String runAllTests(String packagePath) throws JsonProcessingException {
        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        // 创建LauncherDiscoveryRequest来发现测试
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage(packagePath))
                .build();

        // 创建Launcher实例
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        // 获取测试执行摘要
        TestExecutionSummary summary = listener.getSummary();

        List<String> passed = new ArrayList<>();
        List<String> test_cases = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        List<String> test_functions = new ArrayList<>();

        summary.getTestsSucceededCount();
        for (int i = 0; i < summary.getTestsFoundCount(); i++) {
            passed.add("true");
        }

        // 记录失败的测试结果
        summary.getFailures().forEach(failure -> {
            test_cases.add(failure.getTestIdentifier().getDisplayName());
            passed.add("false");
            errorMessages.add(failure.getException().getMessage());
            test_functions.add(failure.getTestIdentifier().getUniqueId());
        });

        Map<String, List<String>> results = new HashMap<>();
        results.put("test_cases", test_cases);
        results.put("passed", passed);
        results.put("errorMessages", errorMessages);
        results.put("test_functions", test_functions);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(results);

        System.out.println("Total test cases: " + summary.getTestsFoundCount());
        System.out.println("Successful test cases: " + summary.getTestsSucceededCount());
        System.out.println("Failed test cases: " + summary.getFailures().size());

        return jsonString;
    }

    private static DiscoverySelector selectPackage(String packageName) {
        return DiscoverySelectors.selectPackage(packageName);
    }

    private static String[] extractClassAndMethod(String displayName) {
        int lastDotIndex = displayName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return new String[] {"UnknownClass", displayName};
        }
        String className = displayName.substring(0, lastDotIndex);
        String methodName = displayName.substring(lastDotIndex + 1);
        return new String[] {className, methodName};
    }

//    public static void main(String[] args) {
//        String packagePath = "cn.tju.sse.spring_backend"; // 替换为你的测试包名
//        String results = runAllTests(packagePath);
//
//    }
}
