# Process Migration Repository

Process migration repository is a Business Central repository containing 2 migration processes. The repository can be cloned directly into Business Central, built, and deployed according to the following steps.

### Prerequisite

* Red Hat JBoss BPM Suite 6.x
* Only an application user having the role "kiemgmt" is able to start migration processes.
* To execute the test processes, create the application user "ibek" having the roles "admin,kiemgmt,rest-all,rest-client".

### Installation

1. Update versions in all pom.xml files (located in the root folders) which should correspond to the version of your BPM Suite instance (e.g. 6.4.0.Final-redhat-1).
2. Clone the updated repository from your local filesystem to Business Central in the Administration screen "file:///home/ibek/Work/github/process-migration-bpms".
3. Open Project Authoring in Business Central, select process-migration-service project from process-migration-bpms repository, and build and deploy this project in the Project Editor.
4. Switch project to process-migration-kjar and build and deploy the project in the Project Editor.
5. Open Process Management -> Process Definition in order to start migration processes - single or multi

### Testing

1. Open Project Authoring in Business Central, select process-migration-testv1 project from process-migration-bpms repository, and build and deploy this project in the Project Editor.
2. Switch project to process-migration-testv2 and build and deploy the project in the Project Editor.
3. In Process Management -> Process Definition you will find 1.0 and 2.0 processes defined in the test projects, start one of the 1.0 process, start the single migration process, map or not some nodes, and afterwards the process instance will migrate to 2.0 version.

### Limits of this Solution

TODO

### Additional Information

http://mswiderski.blogspot.cz/2014/11/process-instance-migration-made-easy.html

