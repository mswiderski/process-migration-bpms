package org.jbpm.process.migration.test;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jbpm.process.migration.RemoteTestBase;
import org.junit.Test;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Task;

public class AddTaskAfterActiveTest extends RemoteTestBase {

    private static final String PROCESS_ID_V1 = "process-migration-testv1.AddTaskAfterActive";
    private static final String PROCESS_ID_V2 = "process-migration-testv2.AddTaskAfterActive";

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
        Assertions.assertThat(activeTask).isNotNull();
        Assertions.assertThat(activeTask.getName()).isEqualTo("Added Task");
        
        testv2.completeTask(activeTask);
        
        ProcessInstanceLog pil = testv2.getProcessInstanceLog(pid);
        Assertions.assertThat(pil).isNotNull();
        Assertions.assertThat(pil.getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        
    }
    
    @Test
    public void testMultiMigration() {
        List<Long> pids = new ArrayList<Long>();
        for (int i=0; i<10; ++i) {
            ProcessInstance piv1 = testv1.startProcess(PROCESS_ID_V1);
            long pid = piv1.getId();
            Assertions.assertThat(piv1.getProcessId()).isEqualTo(PROCESS_ID_V1);
            pids.add(pid);
        }
        
        migrateMultiProcessInstances(DEPID_TESTV1, DEPID_TESTV2, PROCESS_ID_V1, PROCESS_ID_V2);
        
        for (Long pid : pids) {
            ProcessInstance piv2 = testv2.getProcessInstance(pid);
            Assertions.assertThat(piv2).isNotNull();
            Assertions.assertThat(piv2.getProcessId()).isEqualTo(PROCESS_ID_V2);
            
            Task activeTask = testv2.getActiveTask(pid);
            Assertions.assertThat(activeTask).isNotNull();
            Assertions.assertThat(activeTask.getName()).isEqualTo("Active Task");
            
            testv2.completeTask(activeTask);
            
            activeTask = testv2.getActiveTask(pid);
            Assertions.assertThat(activeTask).isNotNull();
            Assertions.assertThat(activeTask.getName()).isEqualTo("Added Task");
            
            testv2.completeTask(activeTask);
            
            ProcessInstanceLog pil = testv2.getProcessInstanceLog(pid);
            Assertions.assertThat(pil).isNotNull();
            Assertions.assertThat(pil.getStatus()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        }
        
    }
    
}
