package org.jbpm.process.migration.test;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.drools.core.xml.jaxb.util.JaxbListWrapper;
import org.jbpm.process.migration.RemoteTestBase;
import org.junit.Test;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Task;
import org.kie.remote.jaxb.gen.util.JaxbUnknownAdapter;

public class MyTest extends RemoteTestBase {

    private static final String PROCESS_ID_V1 = "process-migration-testv1.AddTaskBeforeActive";
    private static final String PROCESS_ID_V2 = "process-migration-testv2.AddTaskBeforeActive";

    private static final String ACTIVE_NODE_ID = "_18771A1A-9DB9-4CA1-8C2E-19DEE24A1776";
    private static final String ADDED_NODE_ID = "_94643E69-BD97-4E4A-8B4A-364FEB95CA3C";

    @Test
    public void testSingleMigration() throws Exception {
        
        JaxbUnknownAdapter adapter = new JaxbUnknownAdapter();
        
        Map<String, Object> srcTarget = new HashMap<String, Object>();
        srcTarget.put("nodemap_sourceNodeId", "src");
        srcTarget.put("nodemap_targetNodeId", "target");
        
        Map<String, Object>[] marray = new Map[1];
        marray[0] = srcTarget;
        org.kie.remote.jaxb.gen.List marshalledArray = null;
        try {
            marshalledArray = (org.kie.remote.jaxb.gen.List) adapter.marshal(marray);
            
            Object [] objArr = marray;
            int length = objArr.length;
            String componentTypeName = marshalledArray.getComponentType();
            Class realArrComponentType = null;
            realArrComponentType = Map.class;

            // create and fill array
            Object realArr = Array.newInstance(realArrComponentType, objArr.length);
            System.out.println(realArr);
            for( int i = 0; i < length; ++i ) {
                Array.set(realArr, i, objArr[i]);
            }
            System.out.println(realArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
