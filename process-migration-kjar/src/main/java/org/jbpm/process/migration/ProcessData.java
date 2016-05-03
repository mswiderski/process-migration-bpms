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

package org.jbpm.process.migration;


public class ProcessData implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    private String deploymentId;
    private Long processInstanceId;
    private String toProcessId;
    private String toDeploymentId;
    private String fromProcessId;

    public ProcessData() {
    }

    public ProcessData(String deploymentId, String toDeploymentId, Long processInstanceId, String toProcessId) {
        this.deploymentId = deploymentId;
        this.processInstanceId = processInstanceId;
        this.toProcessId = toProcessId;
        this.toDeploymentId = toDeploymentId;
    }

    public ProcessData(String deploymentId, String toDeploymentId, String fromProcessId, String toProcessId) {
        this.deploymentId = deploymentId;
        this.toDeploymentId = toDeploymentId;
        this.toProcessId = toProcessId;
        this.fromProcessId = fromProcessId;
    }

    public String getDeploymentId() {
        return this.deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public Long getProcessInstanceId() {
        return this.processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getToProcessId() {
        return this.toProcessId;
    }

    public void setToProcessId(String toProcessId) {
        this.toProcessId = toProcessId;
    }

    public String getToDeploymentId() {
        return this.toDeploymentId;
    }

    public void setToDeploymentId(String toDeploymentId) {
        this.toDeploymentId = toDeploymentId;
    }

    public String getFromProcessId() {
        return this.fromProcessId;
    }

    public void setFromProcessId(String fromProcessId) {
        this.fromProcessId = fromProcessId;
    }

    @Override
    public String toString() {
        return "ProcessData [deploymentId=" + deploymentId + ", processInstanceId=" + processInstanceId + ", toProcessId=" + toProcessId + ", toDeploymentId=" + toDeploymentId + "]";
    }



}