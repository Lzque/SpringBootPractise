package com.xsc.utils;

import java.net.URL;

/**
 * 获取本项目盘符路径
 */
public class ProjectPath {
    private ProjectPath() {

    }

    /**
     * @return 项目模块路径
     */
    public static String projectPath() {
        // 输出 D:/javaDemo/SpringBoot/SpringPractice/target/classes/
        URL url = ProjectPath.class.getClassLoader().getResource("");
        assert url != null;
        String path = url.getFile();
        // 处理 /target/classes/
        for (int i = 0; i < 3; i++) {
            int index = path.lastIndexOf("/");
            path = path.substring(0, index);
        }
        return path;
    }
}
