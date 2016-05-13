package org.jbpm.process.migration.test;

import org.assertj.core.api.Assertions;
import org.jbpm.process.migration.RemoteTestBase;
import org.junit.Test;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Task;

public class AddTaskBeforeActiveTest extends RemoteTestBase {

    private static final String PROCESS_ID_V1 = "process-migration-testv1.AddTaskBeforeActive";
    private static final String PROCESS_ID_V2 = "process-migration-testv2.AddTaskBeforeActive";

    private static final String ACTIVE_NODE_ID = "_18771A1A-9DB9-4CA1-8C2E-19DEE24A1776";
    private static final String ADDED_NODE_ID = "_94643E69-BD97-4E4A-8B4A-364FEB95CA3C";

    @Test
    public void testSingleMigration() throws Exception {
        ProcessInstance piv1 = testv1.startProcess(PROCESS_ID_V1);
        long pid = piv1.getId();
        Assertions.assertThat(piv1.getProcessId()).isEqualTo(PROCESS_ID_V1);
        
        Task activeTask = testv1.getActiveTask(pid);
        Assertions.assertThat(activeTask).isNotNull();
        Assertions.assertThat(activeTask.getName()).isEqualTo("Active Task");
        
        try {
            migrateSingleProcessInstanceWithMapping(DEPID_TESTV1, DEPID_TESTV2, pid, PROCESS_ID_V2, ACTIVE_NODE_ID, ADDED_NODE_ID);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        
        ProcessInstance piv2 = testv2.getProcessInstance(pid);
        Assertions.assertThat(piv2).isNotNull();
        Assertions.assertThat(piv2.getProcessId()).isEqualTo(PROCESS_ID_V2);
        
        activeTask = testv2.getActiveTask(pid);
        Assertions.assertThat(activeTask).isNotNull();
        Assertions.assertThat(activeTask.getName()).isEqualTo("Added Task");
        
        testv2.completeTask(activeTask);
        
        activeTask = testv2.getActiveTask(pid);
        Assertions.assertThat(activeTask).isNotNull();
        Assertions.assertThat(activeTask.getName()).isEqualTo("Active Task");
        
        testv2.completeTask(activeTask);
        
        ProcessInstanceLog pil = testv2.getProcessInstanceLog(pid);
        Assertions.assertThat(pil).isNotNull();
        Assertions.assertThat(pil.getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        
    }
    
}
