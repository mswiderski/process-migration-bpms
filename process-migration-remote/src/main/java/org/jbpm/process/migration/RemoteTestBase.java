package org.jbpm.process.migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Task;
import org.kie.remote.jaxb.gen.JaxbStringObjectPairArray;
import org.kie.remote.jaxb.gen.JaxbWrapperType;
import org.kie.remote.jaxb.gen.util.JaxbStringObjectPair;
import org.kie.remote.jaxb.gen.util.JaxbUnknownAdapter;
import org.kie.services.client.serialization.jaxb.impl.JaxbPaginatedList;

import static org.kie.remote.client.jaxb.ConversionUtil.convertMapToJaxbStringObjectPairArray;

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
        Map<String, String>[] a = new Map[1];
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
        
        /**List<Object> serializedList = new ArrayList<Object>(1);
        serializedList.add(convertMapToJaxbStringObjectPairArray(srcTarget));
        
        // convert to JaxbListWrapper
        org.kie.remote.jaxb.gen.List listWrapper = new org.kie.remote.jaxb.gen.List();
        listWrapper.getElements().addAll(serializedList);
        listWrapper.setType(JaxbWrapperType.ARRAY);
        listWrapper.setComponentType(JaxbStringObjectPairArray.class.getCanonicalName());
        
        mapping.put("out_mapping", listWrapper);*/
        
        JaxbUnknownAdapter adapter = new JaxbUnknownAdapter();
        
        Map<String, Object>[] marray = new Map[1];
        marray[0] = srcTarget;
        org.kie.remote.jaxb.gen.List marshalledArray = null;
        try {
            marshalledArray = (org.kie.remote.jaxb.gen.List) adapter.marshal(marray);
            mapping.put("out_mapping", marshalledArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return mapping;
    }
    
}
