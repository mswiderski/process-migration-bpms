# Process Migration Repository

Process migration repository is a Business Central repository containing 2 migration processes. The repository can be cloned directly into Business Central, built, and deployed according to the following steps.

### Prerequisite

* Red Hat JBoss BPM Suite 6.2.x
* Update versions in all pom.xml files (located in the root folders) which should correspond to the version of your BPM Suite instance (e.g. 6.3.0.Final-redhat-5)
* Set location of the product maven repository in the process-migration-kjar and process-migration-remote projects's pom.xml
* Only an application user having the role "kiemgmt" is able to start migration processes
* To execute the test processes, create the application user "ibek" having the roles "admin,kiemgmt,rest-all,rest-client"

### Installation - maven build & upload KJAR

1. In process-migration-kjar project execute "mvn clean package" to build the KJAR
2. Open Authoring -> Artifact Repository where you will upload our KJAR.
3. A)To deploy the KJAR, open Deploy -> Process Deployments and create new deployment unit
3. B)To deploy the KJAR, call REST request "curl -v -u ibek -X POST 'localhost:8080/business-central/rest/deployment/org.kie.bpms:process-migration-kjar:1.0.0-SNAPSHOT/deploy'"
4. Open Process Management -> Process Definition in order to start migration processes - single or multi

### Installation - clone repo & build

1. Clone the updated repository from your local filesystem to Business Central in the Administration screen "file:///home/ibek/Work/github/process-migration-bpms".
2. Open Project Authoring in Business Central, select process-migration-service project from process-migration-bpms repository, and build and deploy this project in the Project Editor.
3. Switch project to process-migration-kjar and build and deploy the project in the Project Editor.
4. Open Process Management -> Process Definition in order to start migration processes - single or multi

### Testing

1. Open Project Authoring in Business Central, select process-migration-testv1 project from process-migration-bpms repository, and build and deploy this project in the Project Editor.
2. Switch project to process-migration-testv2 and build and deploy the project in the Project Editor.
3. A) #MANUAL - In Process Management -> Process Definition you will find 1.0 and 2.0 processes defined in the test projects, start one of the 1.0 process, start the single migration process, map or not some nodes, and afterwards the process instance will migrate to 2.0 version.
3. B) #AUTOMATIC - In folder called "process-migration-remote" execute "mvn clean test" in order to start the REST tests automatically

Current deployment id = org.kie.bpms:process-migration-test:1.0.0-SNAPSHOT

Target deployment id = org.kie.bpms:process-migration-test:2.0.0-SNAPSHOT

Process V1 ID | Process V2 ID*
--- | ---
process-migration-testv1.AddErrorBoundaryEvent | process-migration-testv2.AddErrorBoundaryEvent
process-migration-testv1.AddTaskAfterActive | process-migration-testv2.AddTaskAfterActive
process-migration-testv1.AddTaskBeforeActive | process-migration-testv2.AddTaskBeforeActive
process-migration-testv1.RemoveActiveTask | process-migration-testv2.RemoveActiveTask
process-migration-testv1.RemoveNonActiveTask | process-migration-testv2.RemoveNonActiveTask
process-migration-testv1.ReplaceActiveTask | process-migration-testv2.ReplaceActiveTask
process-migration-testv1.SwithXORGatewayConditions | process-migration-testv2.SwithXORGatewayConditions

\* It's recommended to use unique process identifiers in every version.

### Limits and Unsupported Scenarios

* When a new or modified task requires inputs which are not available in the migrated v2 process instance.
* Modifying the tasks prior to the active task where the changes have an impact on the further processing.
* Removing a human task which is currently active (can only be replaced - requires to be mapped to another human task)
* Adding a new task parallel to the single active task (all branches in AND gateway are not activated - process will stuck)
* Changing or removing the active recurring timer events (won’t be changed in DB)
* Fixing or updating inputs and outputs in an active task (task data aren’t migrated)
* Node mapping updates only the task node name and description! (other task fields won’t be mapped including the TaskName variable)

### Additional Information

http://mswiderski.blogspot.cz/2014/11/process-instance-migration-made-easy.html

