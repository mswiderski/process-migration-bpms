package org.jbpm.process.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.jaxb.gen.util.JaxbUnknownAdapter;

public class RemoteTestBase {

    protected RESTClient migration;
    protected RESTClient testv1;
    protected RESTClient testv2;

    public static final String DEPID_MIGRATION = "org.kie.bpms:process-migration-kjar:1.0.0-SNAPSHOT";
    public static final String DEPID_TESTV1 = "org.kie.bpms:process-migration-test:1.0.0-SNAPSHOT";
    public static final String DEPID_TESTV2 = "org.kie.bpms:process-migration-test:2.0.0-SNAPSHOT";

    public static final String SINGLE_MIGRATION_PROCESS_ID = "single-instance-migration";
    public static final String MULTI_MIGRATION_PROCESS_ID = "multi-instance-migration";

    public static final String USERNAME = "ibek";
    public static final String PASSWORD = "ibek1234-";
    
    public RemoteTestBase() {
        migration = new RESTClient(DEPID_MIGRATION, USERNAME, PASSWORD);
        testv1 = new RESTClient(DEPID_TESTV1, USERNAME, PASSWORD);
        testv2 = new RESTClient(DEPID_TESTV2, USERNAME, PASSWORD);
    }
    
    public void migrateSingleProcessInstance(String currentDeploymentUnit, String targetDeploymentUnit, long processInstanceId, String targetProcessId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processData", new ProcessData(currentDeploymentUnit, targetDeploymentUnit, processInstanceId, targetProcessId));
        params.put("mapNodes", false);
        ProcessInstance migrationProcess = migration.getKieSession().startProcess(SINGLE_MIGRATION_PROCESS_ID, params);
        Assertions.assertThat(migrationProcess).isNotNull();
        Assertions.assertThat(migrationProcess.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
    
    public void migrateSingleProcessInstanceWithMapping(String currentDeploymentUnit, String targetDeploymentUnit, long processInstanceId, String targetProcessId, String sourceNode, String targetNode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processData", new ProcessData(currentDeploymentUnit, targetDeploymentUnit, processInstanceId, targetProcessId));
        params.put("mapNodes", true);
        ProcessInstance migrationProcess = migration.getKieSession().startProcess(SINGLE_MIGRATION_PROCESS_ID, params);
        Task mapTask = migration.getActiveTask(migrationProcess.getId());
        long taskId = mapTask.getId();
        migration.getTaskService().start(taskId, USERNAME);
        
        migration.getTaskService().complete(taskId, USERNAME, serializeMappingParameters(sourceNode, targetNode));
    }
    
    public void migrateMultiProcessInstances(String currentDeploymentUnit, String targetDeploymentUnit, String sourceProcessId, String targetProcessId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processData", new ProcessData(currentDeploymentUnit, targetDeploymentUnit, sourceProcessId, targetProcessId));
        params.put("mapNodes", false);
        ProcessInstance migrationProcess = migration.getKieSession().startProcess(MULTI_MIGRATION_PROCESS_ID, params);
        long pid = migrationProcess.getId();
        for (int i=0; i<5 && migrationProcess != null && migrationProcess.getState() == ProcessInstance.STATE_ACTIVE; ++i) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            migrationProcess = migration.getProcessInstance(pid);
        }
        Assertions.assertThat(migrationProcess).isNull();
    }
    
    public Map<String, Object> serializeMappingParameters(String sourceNodeId, String targetNodeId) {
        Map<String, Object> mapping = new HashMap<String, Object>();
        Map<String, Object> srcTarget = new HashMap<String, Object>();
        srcTarget.put("nodemap_sourceNodeId", sourceNodeId);
        srcTarget.put("nodemap_targetNodeId", targetNodeId);
        
        JaxbUnknownAdapter adapter = new JaxbUnknownAdapter();
        
        List<Map<String, Object>> marray = new ArrayList<Map<String,Object>>();
        marray.add(srcTarget);
        try {
            mapping.put("out_mapping", adapter.marshal(marray));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return mapping;
    }
    
    protected void assertTaskAndComplete(RESTClient client, String processId, Long processInstanceId, String taskName) {
        List<TaskSummary> tasks = client.getTaskService().getTasksByStatusByProcessInstanceId(processInstanceId, Arrays.asList(Status.Reserved), "en-UK");
        Assertions.assertThat(tasks).isNotNull();
        Assertions.assertThat(tasks.size()).isEqualTo(1);
        
        TaskSummary task = tasks.get(0);
        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getProcessId()).isEqualTo(processId);
        Assertions.assertThat(task.getDeploymentId()).isEqualTo(client.getDeploymentId());
        Assertions.assertThat(task.getName()).isEqualTo(taskName);
        
        client.getTaskService().start(task.getId(), "john");
        client.getTaskService().complete(task.getId(), "john", null);
    }

    protected void assertProcessInstance(RESTClient client, String processId, long processInstanceId, int status) {
        ProcessInstanceLog instance = client.getAuditService().findProcessInstance(processInstanceId);
        Assertions.assertThat(instance).isNotNull();
        Assertions.assertThat(instance.getProcessId()).isEqualTo(processId);
        Assertions.assertThat(instance.getExternalId()).isEqualTo(client.getDeploymentId());
        Assertions.assertThat(instance.getStatus().intValue()).isEqualTo(status);
    }
    
}
