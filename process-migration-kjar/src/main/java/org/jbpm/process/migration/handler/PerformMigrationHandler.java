/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.migration.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.migration.MigrationManager;
import org.jbpm.process.migration.NodeMapping;
import org.jbpm.process.migration.ProcessData;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformMigrationHandler implements WorkItemHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformMigrationHandler.class);

	@SuppressWarnings("unchecked")
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		ProcessData data = (ProcessData) workItem.getParameter("in_processdata");
		logger.debug("Migration::ProcessData given is {}", data);
        if (data == null) {
            throw new IllegalArgumentException("No process data data input given, quiting...");
        }
        
		List<NodeMapping> nodeMapping = new ArrayList<NodeMapping>();
		Map<String, String>[] inNodeMapping = (Map<String, String>[]) workItem.getParameter("in_nodemapping");
		logger.debug("Migration::Node mapping provided {}", (Object) inNodeMapping);
		if (inNodeMapping != null) {
			for (Map<String, String> inputMapping : inNodeMapping) {
				nodeMapping.add(new NodeMapping(inputMapping.get("nodemap_sourceNodeId"), inputMapping.get("nodemap_targetNodeId")));
			}
		}
		logger.debug("Migration::Performing migration for {} with node mapping {}", data, nodeMapping);
		String outcome = MigrationManager.migrate(data, nodeMapping);
		
		logger.debug("Migration::Migration finished for {} with outcome {}", data, outcome);
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("out_outcome", outcome);
		
		logger.debug("Migration::Migration task completed for {}", data);
		manager.completeWorkItem(workItem.getId(), results);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// no-op

	}

}
