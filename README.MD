REST entries:
- GET http://localhost:8080/metadata
- GET http://localhost:8080/metadata/<name of the type>
- POST http://localhost:8080/metadata?type=cnd 
  and put a CND into the body
- DELETE http://localhost:8080/metadata/<name of the type>

How to whitelist the module in sling:
 - go to OSGI configuration (http://localhost:8080/system/console/configMgr)
 - found the Apache Sling Whitelist Fragment
 - press a + button ("create new factory configuration")
 - add the values:
  name = metadata-handler
  Whitelisted BSNs = org.apache.sling.metadata-handler
- press "Save" button  
  
How to deploy module into running Sling
- create a maven run configuration:
 clean package sling:install
- pom.xml contains a hardcoded information about Sling address:
 <obr>http://localhost:8080</obr>
- run configuration (Sling also should be started previously)

How to debug an application:
- run a Sling with parameters
 -Xmx384M -agentlib:jdwp=transport=dt_socket,address=<port number>,server=y,suspend=n
 the port number e.g. 30303 
- Create a remote Remote run configuration
- Replace the port number with one from the first step (e.g. 30303)
- run it; works like a charm  

TODO:
- DELETE multiple types
- POST multiple types as JSON
- ACL for the module
- rewrite MetadataProcessorImpl.getAdminSession 
- pathInfo as standalone class (or existed solution)
 