/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.softech.gw.pmodule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ssledz
 */
public class ProjectConfiguration {

    public static class TaskConfiguration {

        private String task;
        private String arg;

        public TaskConfiguration() {
        }

        public TaskConfiguration(String task, String arg) {
            this.task = task;
            this.arg = arg;
        }

        public String getTask() {
            return task;
        }

        public String getArg() {
            return arg;
        }
    }
    private String moduleName;
    private String svnPath;
    private String svnCheckoutPath;
    private String moduleDownloadUrl;
    private String buildXmlPath;
    private List<TaskConfiguration> tasks = new LinkedList<TaskConfiguration>();
    
    private ProjectConfiguration parent;

    public String getModuleName() {
        return moduleName;
    }

    public String getSvnPath() {
        return svnPath;
    }

    public String getSvnCheckoutPath() {
        return svnCheckoutPath;
    }

    public String getModuleDownloadUrl() {
        return moduleDownloadUrl;
    }

    public String getBuildXmlPath() {
        return buildXmlPath;
    }

    public List<TaskConfiguration> getTasks() {
        return tasks;
    }

    public ProjectConfiguration getParent() {
        return parent;
    }

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        ProjectConfiguration conf = new ProjectConfiguration();
        ProjectConfiguration root = conf;

        conf.moduleName = "DSPCommon";
        conf.svnCheckoutPath = "\\modules";
        conf.svnPath = "file:///C:/Users/ssledz/svn-repository/dspcommon/trunk/modules";
        conf.buildXmlPath = "\\modules\\ant\\build-common.xml";
        conf.tasks.add(new TaskConfiguration("CheckoutTask", null));
        conf.tasks.add(new TaskConfiguration("AntTask", "push-common-into-products"));
        
        conf.parent = new ProjectConfiguration();
        conf = conf.parent;
        conf.moduleName = "PolicyCenter";
        conf.svnCheckoutPath = "\\modules\\configuration";
        conf.moduleDownloadUrl = "http://localhost:8080/pc-repository/PolicyCenter7.0.6.zip";
        conf.svnPath = "file:///C:/Users/ssledz/svn-repository/pc/trunk/modules/configuration";
        conf.buildXmlPath = "\\modules\\ant\\build.xml";
        conf.tasks.add(new TaskConfiguration("DownloadTask", null));
        conf.tasks.add(new TaskConfiguration("UnzipTask", null));
        conf.tasks.add(new TaskConfiguration("CheckoutTask", null));

        conf.parent = new ProjectConfiguration();
        conf = conf.parent;
        conf.moduleName = "BillingCenter";
        conf.svnCheckoutPath = "\\modules\\configuration";
        conf.moduleDownloadUrl = "http://localhost:8080/pc-repository/BillingCenter7.0.2_patch_1_2.zip";
        conf.svnPath = "file:///C:/Users/ssledz/svn-repository/bc/trunk/modules/configuration";
        conf.buildXmlPath = "\\modules\\ant\\build.xml";
        conf.tasks.add(new TaskConfiguration("DownloadTask", null));
        conf.tasks.add(new TaskConfiguration("UnzipTask", null));
        conf.tasks.add(new TaskConfiguration("CheckoutTask", null));

        conf.parent = new ProjectConfiguration();
        conf = conf.parent;
        conf.moduleName = "ContactManager";
        conf.svnCheckoutPath = "\\modules\\configuration";
        conf.moduleDownloadUrl = "http://localhost:8080/pc-repository/ContactManager7.0.3.zip";
        conf.svnPath = "file:///C:/Users/ssledz/svn-repository/cm/trunk/modules/configuration";
        conf.buildXmlPath = "\\modules\\ant\\build.xml";
        conf.tasks.add(new TaskConfiguration("DownloadTask", null));
        conf.tasks.add(new TaskConfiguration("UnzipTask", null));
        conf.tasks.add(new TaskConfiguration("CheckoutTask", null));

        
        
        
        String json = gson.toJson(root);
        System.out.println(json);
    }
}
