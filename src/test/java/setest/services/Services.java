package setest.services;

import setest.utils.ScriptPackageManager;
import org.springframework.stereotype.Service;
import setest.utils.testCase.TestCaseExecutor;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author asahi
 * @version 1.0
 * @project Software-Testing-Homework
 * @description
 * @date 2024/6/4 09:21:01
 */
@Service
public class Services {
    private String className;

    private TestCaseExecutor executor;

    public boolean chooseClass(String name) {
        if (!TestCaseExecutor.classIsValid(name))
            return false;
        className = name;
        return true;
    }

    public void uploadCase(InputStream file) throws Exception {
        executor = new TestCaseExecutor(file, className);
    }

    public List<String> getMethods(String className) throws ClassNotFoundException {
        return TestCaseExecutor.getMethods(className);
    }

    public List<String> getClasses() {
        return TestCaseExecutor.getClasses();
    }

    public List<Boolean> executeCase(String methodName) {
        return executor.execute(methodName);
    }

    public InputStream uploadScript(MultipartFile file) throws Exception {
        return ScriptPackageManager.packageCheck(file,
                TestCaseExecutor.clazzRootPath.substring(0, TestCaseExecutor.clazzRootPath.lastIndexOf('.')));
    }
}
