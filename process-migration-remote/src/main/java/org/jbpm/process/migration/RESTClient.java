package org.jbpm.process.migration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.client.api.RemoteRestRuntimeEngineBuilder;
import org.kie.remote.client.api.RemoteTaskService;
import org.kie.services.client.api.RemoteRuntimeEngineFactory;
import org.kie.services.client.api.command.RemoteRuntimeEngine;

public class RESTClient {
    
    protected RuntimeEngine runtimeEngine;

    protected TaskService taskService;

    protected KieSession kieSession;
    
    protected AuditService auditService;

    protected RemoteTaskService remoteTaskService;
    
    private final String deploymentId;
    private final URL url;
    private Class<?>[] classes;

    private String username;
    private String password;
    
    public RESTClient(String deploymentId, String username, String password, Class<?>... classes) {
        this(deploymentId, "http://localhost:8080/business-central", username, password, classes);
    }

    public RESTClient(String deploymentId, String url, String username, String password, Class<?>... classes) {
        this.deploymentId = deploymentId;
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL.", e);
        }
        this.classes = classes;
        this.username = username;
        this.password = password;
        buildRuntimeEngine();
    }

    private void buildRuntimeEngine() {
        RemoteRestRuntimeEngineBuilder builder = RemoteRuntimeEngineFactory.newRestBuilder()
                .addDeploymentId(deploymentId)
                .addUrl(url)
                .addUserName(username)
                .addPassword(password)
                .addExtraJaxbClasses(classes);
        runtimeEngine = builder.build();
        kieSession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();
        auditService = runtimeEngine.getAuditService();
        remoteTaskService = ((RemoteRuntimeEngine) runtimeEngine).getRemoteTaskService();
    }
    
    public AuditService getAuditService() {
        return auditService;
    }
    
    public KieSession getKieSession() {
        return kieSession;
    }
    
    public RuntimeEngine getRuntimeEngine() {
        return runtimeEngine;
    }
    
    public TaskService getTaskService() {
        return taskService;
    }
    
    public RemoteTaskService getRemoteTaskService() {
        return remoteTaskService;
    }
    
    public ProcessInstance startProcess(String processId) {
        return kieSession.startProcess(processId);
    }
    
    public ProcessInstance startProcess(String processId, Map<String, Object> params) {
        return kieSession.startProcess(processId, params);
    }
    
    public ProcessInstance getProcessInstance(long processInstanceId) {
        return kieSession.getProcessInstance(processInstanceId);
    }
    
    public ProcessInstanceLog getProcessInstanceLog(long processInstanceId) {
        return auditService.findProcessInstance(processInstanceId);
    }
    
    public Task getActiveTask(long processInstanceId) {
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
        status.add(Status.Reserved);
        List<TaskSummary> taskIds = taskService.getTasksByStatusByProcessInstanceId(processInstanceId, status, null);
        if (taskIds.isEmpty()) {
            return null;
        }
        Long taskId = taskIds.get(0).getId();
        return taskService.getTaskById(taskId);
    }
    
    public void completeTask(Task task) {
        long taskId = task.getId();
        taskService.start(taskId, RemoteTestBase.USERNAME);
        taskService.complete(taskId, RemoteTestBase.USERNAME, null);
    }
    
    public void sendSignal(String signalName, long processInstanceId) {
        kieSession.signalEvent(signalName, null, processInstanceId);
    }

}
