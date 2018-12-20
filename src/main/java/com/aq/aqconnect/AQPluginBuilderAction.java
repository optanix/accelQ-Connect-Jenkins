package com.aq.aqconnect;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import org.json.simple.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by Vinay on 7/31/2016.
 */
public class AQPluginBuilderAction extends Builder implements SimpleBuildStep {

    private String jobId;
    private String userName;
    private String secretKey;
    private String projectName;
    private String appURL;

    //run params
    private String runParamStr;

    @DataBoundConstructor
    public AQPluginBuilderAction(String jobId, String userName, String secretKey, String projectName, String appURL, String runParamStr) {
        this.jobId = jobId;
        this.userName = userName;
        this.secretKey = secretKey;
        this.projectName = projectName;
        this.appURL = appURL;
        this.runParamStr = runParamStr;
    }

    public String getUserName() { return userName; }
    public String getSecretKey() { return secretKey; }
    public String getJobId() { return jobId; }
    public String getProjectName() { return projectName; }
    public String getAppURL() { return appURL; }
    public String getRunParamStr() { return runParamStr; }

    private String getRunParamJsonPayload(String runParamStr) {
        if(runParamStr == null || runParamStr.trim().length() == 0)
            return null;
        JSONObject json = new JSONObject();
        String[] splitOnAmp = runParamStr.split("&");
        for(String split: splitOnAmp) {
            String[] splitOnEquals = split.split("=");
            if(splitOnEquals.length == 2) {
                String key = splitOnEquals[0].trim(), value = splitOnEquals[1].trim();
                if(!key.equals("") && !value.equals("")) {
                    json.put(key, value);
                }
            }
        }
        return json.toJSONString();
    }


//    @Override
//    public DescriptorImpl getDescriptor() {
//        return (DescriptorImpl)super.getDescriptor();
//    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        PrintStream out = listener.getLogger();
        //login via AQ REST client
        AQPluginRESTClient aqPluginRESTClient = AQPluginRESTClient.getInstance();
        aqPluginRESTClient.setUpBaseURL(this.appURL.trim());

        if(aqPluginRESTClient.doLogin(this.userName, this.secretKey, this.projectName)) {
            out.println(AQPluginConstants.LOG_DELIMITER + "Connection Successful");
            out.println();


            String parseArgs = run.getEnvironment(listener).expand(this.runParamStr);
            out.println("Parsed Args: " + parseArgs);

            String runParamJsonPayload = getRunParamJsonPayload(parseArgs);

            out.println("Json Payload: "+runParamJsonPayload);

            JSONObject realJobObj = aqPluginRESTClient.triggerJob(Integer.parseInt(jobId), runParamJsonPayload);

            if(realJobObj.get("cause") != null) {
                throw new AQPluginException((String) realJobObj.get("cause"));
            }

            long realJobPid = (long) realJobObj.get("pid");
            long passCount = 0, failCount = 0, runningCount = 0, totalCount = 0, notRunCount = 0;
            String jobPurpose = (String) realJobObj.get("purpose");
            String jobStatus = "";
            JSONObject summaryObj;
            int attempt = 0;
            out.println("Purpose: " + jobPurpose);
            out.println();

            do {
                summaryObj = aqPluginRESTClient.getJobSummary(realJobPid);
                if(summaryObj.get("cause") != null) {
                    throw new AQPluginException((String) summaryObj.get("cause"));
                }
                if(summaryObj.get("summary") != null) {
                    summaryObj = (JSONObject) summaryObj.get("summary");
                }
                passCount = (Long) summaryObj.get("pass");
                failCount = (Long) summaryObj.get("fail");
                notRunCount = (Long) summaryObj.get("notRun");
                out.println("Status: " + summaryObj.get("status"));
                out.println("Pass: " + passCount);
                out.println("Fail: " + failCount);
                //out.println("Running: " + runningCount);
                out.println("Not Run: " + notRunCount);
                out.println();
                jobStatus = ((String) summaryObj.get("status")).toUpperCase();
                if(jobStatus.equals(AQPluginConstants.TEST_JOB_STATUS.SCHEDULED.getStatus().toUpperCase()))
                    ++attempt;
                if(attempt == AQPluginConstants.JOB_PICKUP_RETRY_COUNT) {
                    throw new AQPluginException("No agent available to pickup the job");
                }
                Thread.sleep(AQPluginConstants.JOB_STATUS_POLL_TIME);
            } while(!jobStatus.equals(AQPluginConstants.TEST_JOB_STATUS.COMPLETED.getStatus().toUpperCase())
                    && !jobStatus.equals(AQPluginConstants.TEST_JOB_STATUS.ABORTED.getStatus().toUpperCase())
                    && !jobStatus.equals(AQPluginConstants.TEST_JOB_STATUS.FAILED.getStatus().toUpperCase()));
            String resultAccessURL = aqPluginRESTClient.getResultExternalAccessURL(Long.toString(realJobPid));
            out.print("Click on this ");
            listener.hyperlink(resultAccessURL, "link");
            out.println(" for more details");
            out.println();

            if(failCount > 0
                    || jobStatus.equals(AQPluginConstants.TEST_JOB_STATUS.ABORTED.getStatus().toUpperCase())
                    || jobStatus.equals(AQPluginConstants.TEST_JOB_STATUS.FAILED.getStatus().toUpperCase())) {
                throw new AQPluginException(AQPluginConstants.LOG_DELIMITER + "Run Failed");
            }

        } else {
            throw new AQPluginException(AQPluginConstants.LOG_DELIMITER + "Connection Failed");
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        /* public FormValidation doCheckJobId(@QueryParameter String jobId) {
            try{
                int jobPid = Integer.parseInt(jobId);
                if(jobPid <= 0)
                    return FormValidation.error("Job ID must be a positive number");
                return FormValidation.ok();
            } catch(Exception e) {
                return FormValidation.error("Job ID should be an Integer");
            }
        } */

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "accelQ Connect";
        }

    }

}
