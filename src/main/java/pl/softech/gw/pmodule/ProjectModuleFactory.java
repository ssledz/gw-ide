/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.pmodule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import pl.softech.gw.App;
import pl.softech.gw.Utils;
import pl.softech.gw.ant.AntTaskExecutorFactory;
import pl.softech.gw.download.ResourceDownloader;
import pl.softech.gw.svn.SvnTool;
import pl.softech.gw.zip.Unzip;

/**
 *
 * @author ssledz
 */
public class ProjectModuleFactory {

    private final SvnTool svnTool;
    private final ResourceDownloader downloadTool;
    private final Unzip unzipTool;
    private final AntTaskExecutorFactory antTool;

    public ProjectModuleFactory(SvnTool svnTool, ResourceDownloader downloadTool, Unzip unzipTool, AntTaskExecutorFactory antTool) {
        this.svnTool = svnTool;
        this.downloadTool = downloadTool;
        this.unzipTool = unzipTool;
        this.antTool = antTool;
    }

    private void createTask(ProjectModuleBuilder builder, ProjectConfiguration.TaskConfiguration tc) {

        Class<?>[] params = tc.getArg() != null ? new Class<?>[]{String.class} : new Class<?>[0];
        Method m = null;
        try {
            m = ProjectModuleBuilder.class.getMethod(String.format("create%s", tc.getTask()),
                    params);
            if (tc.getArg() != null) {
                m.invoke(builder, tc.getArg());
            } else {
                m.invoke(builder);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public ProjectModule create(File projectDir, ProjectConfiguration configuration) {

        ProjectModuleBuilder builder = new ProjectModuleBuilder();
        builder.setDownloader(downloadTool);
        builder.setSvnTool(svnTool);
        builder.setUnzipTool(unzipTool);
        builder.setAntTaskExecutorFactory(antTool);
        builder.setProjectDir(projectDir);


        for (ProjectConfiguration it = configuration; it != null; it = it.getParent()) {

            builder.setModuleName(it.getModuleName());
            builder.setSvnCheckoutPath(it.getSvnCheckoutPath());
            builder.setSvnPath(it.getSvnPath());
            builder.setBuildXmlPath(it.getBuildXmlPath());
            builder.setModuleDownloadUrl(it.getModuleDownloadUrl());

            for (ProjectConfiguration.TaskConfiguration tc : it.getTasks()) {
                createTask(builder, tc);
            }

            if (it.getParent() != null) {
                builder = builder.createParent();
            }
        }


        return builder.build();
    }

    public ProjectModule create(File projectDir, String fileName) {
        try {
            InputStream in = App.class.getClassLoader().getResourceAsStream(fileName);
            String conf = Utils.readInputStreamAsString(in, "UTF-8");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            ProjectConfiguration pc = gson.fromJson(conf, ProjectConfiguration.class);
            return create(projectDir, pc);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
