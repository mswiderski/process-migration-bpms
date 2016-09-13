package org.jbpm.process.migration.test;

import org.assertj.core.api.Assertions;
import org.jbpm.process.migration.RemoteTestBase;
import org.junit.Test;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Task;

public class RemoveNonActiveTaskTest extends RemoteTestBase {

    private static final String PROCESS_ID_V1 = "process-migration-testv1.RemoveNonActiveTask";
    private static final String PROCESS_ID_V2 = "process-migration-testv2.RemoveNonActiveTask";

    private static final String PROCESS2_ID_V1 = "process-migration-testv1.RemoveNonActiveBeforeTask";
    private static final String PROCESS2_ID_V2 = "process-migration-testv2.RemoveNonActiveBeforeTask";

    @Test
    public void testSingleMigration() {
        ProcessInstance piv1 = testv1.startProcess(PROCESS_ID_V1);
        long pid = piv1.getId();
        Assertions.assertThat(piv1.getProcessId()).isEqualTo(PROCESS_ID_V1);
        
        Task activeTask = testv1.getActiveTask(pid);
        Assertions.assertThat(activeTask).isNotNull();
        Assertions.assertThat(activeTask.getName()).isEqualTo("Active Task");
        
        migrateSingleProcessInstance(DEPID_TESTV1, DEPID_TESTV2, pid, PROCESS_ID_V2);
        
        ProcessInstance piv2 = testv2.getProcessInstance(pid);
        Assertions.assertThat(piv2).isNotNull();
        Assertions.assertThat(piv2.getProcessId()).isEqualTo(PROCESS_ID_V2);
        
        activeTask = testv2.getActiveTask(pid);
        Assertions.assertThat(activeTask).isNotNull();
        Assertions.assertThat(activeTask.getName()).isEqualTo("Active Task");
        
        testv2.completeTask(activeTask);
        
        activeTask = testv2.getActiveTask(pid);
        Assertions.assertThat(activeTask).isNull();
        
        ProcessInstanceLog pil = testv2.getProcessInstanceLog(pid);
        Assertions.assertThat(pil).isNotNull();
        Assertions.assertThat(pil.getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        
    }
    
    @Test
    public void testRemoveNonActiveBeforeActiveTask() {
        
        ProcessInstance piv1 = testv1.startProcess(PROCESS2_ID_V1);
        long pid = piv1.getId();
        Assertions.assertThat(piv1.getProcessId()).isEqualTo(PROCESS2_ID_V1);
        Assertions.assertThat(piv1.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        assertTaskAndComplete(testv1, PROCESS2_ID_V1, piv1.getId(), "Active Task");
        
        migrateSingleProcessInstance(DEPID_TESTV1, DEPID_TESTV2, pid, PROCESS2_ID_V2);
        
        assertProcessInstance(testv2, PROCESS2_ID_V2, piv1.getId(), ProcessInstance.STATE_ACTIVE);    

        assertTaskAndComplete(testv2, PROCESS2_ID_V2, piv1.getId(), "Non-active Task");                
        assertProcessInstance(testv2, PROCESS2_ID_V2, piv1.getId(), ProcessInstance.STATE_COMPLETED);
    }
    
}
