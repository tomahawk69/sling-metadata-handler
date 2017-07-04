REST entries:
- GET http://localhost:8080/metadata
- GET http://localhost:8080/metadata/<name of the type>
- POST http://localhost:8080/metadata 
  and put a JSON into the body
- POST http://localhost:8080/metadata?type=cnd 
  and put a CND into the body
- DELETE http://localhost:8080/metadata/<name of the type>

Example NodeType JSON:
{
    namespaces: {
        "ngnt": "http://www.ngoar.com/pmm/jahia/nt/1.0"
    }
    nodetypes: [	
        {
            "name": "ngnt:test2",
            "isMixin": false,
            "isAbstract": false,
            "declaredSupertypes": [
                "nt:base"
            ],
            "properties": [
                {
                    "name": "ngf:type",
                    "requiredType": "String",
                    "isMultiple": false,
                    "isMandatory": false,
                    "isAutoCreated": false,
                    "isProtected": false
                },
                {
                    "name": "ngfm:merge",
                    "requiredType": "Boolean",
                    "isMultiple": false,
                    "isMandatory": false,
                    "isAutoCreated": false,
                    "isProtected": false
                }
            ],
            "nodeDefinitions": [
                {
                    "name": "default",
                    "requiredTypes": [
                      "nt:base"
                    ],
                    "isMandatory": false,
                    "isAutoCreated": false,
                    "isProtected": false,
                    "allowsSameTypeSiblings": false
                },
                {
                    "name": "*",
                    "primaryType": "nt:base",
                    "requiredTypes": [
                      "nt:base"
                    ],
                    "isMandatory": false,
                    "isAutoCreated": false,
                    "isProtected": false,
                    "allowsSameTypeSiblings": false
                }
            ]
        }	
    ]
}

How to add required dependencies
- mvn sling:install-file -Dsling.file=gson-2.8.1.jar
  add a gson lib to Sling 
Added dependencies can be found at http://localhost:8080/system/console/bundles 

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
- ACL for the module
- rewrite MetadataProcessorImpl.getAdminSession 
- pathInfo as standalone class (or existed solution)
 