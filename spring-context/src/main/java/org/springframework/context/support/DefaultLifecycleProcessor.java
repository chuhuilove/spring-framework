/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.Lifecycle;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.Phased;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 *
 *
 *
 *F:\apache-tomcat-8.5.41\bin\catalina.bat run
 * [2019-12-30 04:27:21,657] Artifact mdb-web:war exploded: Waiting for server connection to start artifact deployment...
 * Console output is saving to: F:\chunyangzi-program\mdbLog\javaAgent.log
 * Using CATALINA_BASE:   "C:\Users\Administrator\.IntelliJIdea2019.1\system\tomcat\Unnamed_mdbweb_3"
 * Using CATALINA_HOME:   "F:\apache-tomcat-8.5.41"
 * Using CATALINA_TMPDIR: "F:\apache-tomcat-8.5.41\temp"
 * Using JRE_HOME:        "C:\Program Files\Java\jdk1.8.0_162"
 * Using CLASSPATH:       "F:\apache-tomcat-8.5.41\bin\bootstrap.jar;F:\apache-tomcat-8.5.41\bin\tomcat-juli.jar"
 * Connected to the target VM, address: '127.0.0.1:49887', transport: 'socket'
 * 30-Dec-2019 16:27:23.607 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Server version:        Apache Tomcat/8.5.41
 * 30-Dec-2019 16:27:23.616 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Server built:          May 4 2019 09:17:16 UTC
 * 30-Dec-2019 16:27:23.617 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Server number:         8.5.41.0
 * 30-Dec-2019 16:27:23.617 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log OS Name:               Windows 10
 * 30-Dec-2019 16:27:23.617 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log OS Version:            10.0
 * 30-Dec-2019 16:27:23.618 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Architecture:          amd64
 * 30-Dec-2019 16:27:23.618 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Java Home:             C:\Program Files\Java\jdk1.8.0_162\jre
 * 30-Dec-2019 16:27:23.618 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log JVM Version:           1.8.0_162-b12
 * 30-Dec-2019 16:27:23.619 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log JVM Vendor:            Oracle Corporation
 * 30-Dec-2019 16:27:23.619 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log CATALINA_BASE:         C:\Users\Administrator\.IntelliJIdea2019.1\system\tomcat\Unnamed_mdbweb_3
 * 30-Dec-2019 16:27:23.619 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log CATALINA_HOME:         F:\apache-tomcat-8.5.41
 * 30-Dec-2019 16:27:23.621 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Djava.util.logging.config.file=C:\Users\Administrator\.IntelliJIdea2019.1\system\tomcat\Unnamed_mdbweb_3\conf\logging.properties
 * 30-Dec-2019 16:27:23.622 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager
 * 30-Dec-2019 16:27:23.622 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:49887,suspend=y,server=n
 * 30-Dec-2019 16:27:23.623 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -javaagent:C:\Users\Administrator\.IntelliJIdea2019.1\system\captureAgent\debugger-agent.jar
 * 30-Dec-2019 16:27:23.623 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Dcom.sun.management.jmxremote=
 * 30-Dec-2019 16:27:23.623 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Dcom.sun.management.jmxremote.port=1099
 * 30-Dec-2019 16:27:23.623 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Dcom.sun.management.jmxremote.ssl=false
 * 30-Dec-2019 16:27:23.624 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Dcom.sun.management.jmxremote.password.file=C:\Users\Administrator\.IntelliJIdea2019.1\system\tomcat\Unnamed_mdbweb_3\jmxremote.password
 * 30-Dec-2019 16:27:23.624 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Dcom.sun.management.jmxremote.access.file=C:\Users\Administrator\.IntelliJIdea2019.1\system\tomcat\Unnamed_mdbweb_3\jmxremote.access
 * 30-Dec-2019 16:27:23.624 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Djava.rmi.server.hostname=127.0.0.1
 * 30-Dec-2019 16:27:23.625 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Djdk.tls.ephemeralDHKeySize=2048
 * 30-Dec-2019 16:27:23.625 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Djava.protocol.handler.pkgs=org.apache.catalina.webresources
 * 30-Dec-2019 16:27:23.625 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Dignore.endorsed.dirs=
 * 30-Dec-2019 16:27:23.625 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Dcatalina.base=C:\Users\Administrator\.IntelliJIdea2019.1\system\tomcat\Unnamed_mdbweb_3
 * 30-Dec-2019 16:27:23.691 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Dcatalina.home=F:\apache-tomcat-8.5.41
 * 30-Dec-2019 16:27:23.691 淇℃伅 [main] org.apache.catalina.startup.VersionLoggerListener.log Command line argument: -Djava.io.tmpdir=F:\apache-tomcat-8.5.41\temp
 * 30-Dec-2019 16:27:23.692 淇℃伅 [main] org.apache.catalina.core.AprLifecycleListener.lifecycleEvent The APR based Apache Tomcat Native library which allows optimal performance in production environments was not found on the java.library.path: [C:\Program Files\Java\jdk1.8.0_162\bin;C:\WINDOWS\Sun\Java\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\Program Files\TortoiseSVN\bin;C:\Program Files\Java\j2sdk-image\bin;C:\Program Files\Java\j2sdk-image\jre\bin;C:\apache-maven-3.6.1\bin;D:\instantclient_11_2;C:\WINDOWS\System32\OpenSSH\;D:\soft\Git\cmd;D:\soft\googleprotoc\bin;C:\Program Files\WinRAR;D:\software\nodejs\;D:\soft\apache-ant-1.10.5\bin;D:\soft\nc111nt;D:\soft\gradle-4.10.3\bin;C:\ProgramData\Oracle\Java\javapath;C:\Users\Administrator\AppData\Local\Microsoft\WindowsApps;C:\Users\Administrator\AppData\Roaming\npm;;.]
 * 30-Dec-2019 16:27:23.891 淇℃伅 [main] org.apache.coyote.AbstractProtocol.init Initializing ProtocolHandler ["http-nio-8080"]
 * 30-Dec-2019 16:27:23.937 淇℃伅 [main] org.apache.tomcat.util.net.NioSelectorPool.getSharedSelector Using a shared selector for servlet write/read
 * 30-Dec-2019 16:27:23.966 淇℃伅 [main] org.apache.coyote.AbstractProtocol.init Initializing ProtocolHandler ["ajp-nio-8009"]
 * 30-Dec-2019 16:27:23.983 淇℃伅 [main] org.apache.tomcat.util.net.NioSelectorPool.getSharedSelector Using a shared selector for servlet write/read
 * 30-Dec-2019 16:27:23.984 淇℃伅 [main] org.apache.catalina.startup.Catalina.load Initialization processed in 985 ms
 * 30-Dec-2019 16:27:24.031 淇℃伅 [main] org.apache.catalina.core.StandardService.startInternal Starting service [Catalina]
 * 30-Dec-2019 16:27:24.031 淇℃伅 [main] org.apache.catalina.core.StandardEngine.startInternal Starting Servlet Engine: Apache Tomcat/8.5.41
 * 30-Dec-2019 16:27:24.043 淇℃伅 [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["http-nio-8080"]
 * 30-Dec-2019 16:27:24.063 淇℃伅 [main] org.apache.coyote.AbstractProtocol.start Starting ProtocolHandler ["ajp-nio-8009"]
 * 30-Dec-2019 16:27:24.074 淇℃伅 [main] org.apache.catalina.startup.Catalina.start Server startup in 91 ms
 * Connected to server
 * [2019-12-30 04:27:24,327] Artifact mdb-web:war exploded: Artifact is being deployed, please wait...
 * 30-Dec-2019 16:27:34.047 淇℃伅 [localhost-startStop-1] org.apache.catalina.startup.HostConfig.deployDirectory Deploying web application directory [F:\apache-tomcat-8.5.41\webapps\manager]
 * 30-Dec-2019 16:27:34.239 淇℃伅 [localhost-startStop-1] org.apache.jasper.servlet.TldScanner.scanJars At least one JAR was scanned for TLDs yet contained no TLDs. Enable debug logging for this logger for a complete list of JARs that were scanned but no TLDs were found in them. Skipping unneeded JARs during scanning can improve startup time and JSP compilation time.
 * 30-Dec-2019 16:27:34.340 淇℃伅 [localhost-startStop-1] org.apache.catalina.startup.HostConfig.deployDirectory Deployment of web application directory [F:\apache-tomcat-8.5.41\webapps\manager] has finished in [293] ms
 * 30-Dec-2019 16:27:43.515 淇℃伅 [RMI TCP Connection(3)-127.0.0.1] org.apache.jasper.servlet.TldScanner.scanJars At least one JAR was scanned for TLDs yet contained no TLDs. Enable debug logging for this logger for a complete list of JARs that were scanned but no TLDs were found in them. Skipping unneeded JARs during scanning can improve startup time and JSP compilation time.
 * SLF4J: Class path contains multiple SLF4J bindings.
 * SLF4J: Found binding in [jar:file:/F:/cyzi/QMDB-DEV-NEW/java/MDBWeb/mdb-web/target/mdb-web/WEB-INF/lib/log4j-slf4j-impl-2.7.jar!/org/slf4j/impl/StaticLoggerBinder.class]
 * SLF4J: Found binding in [jar:file:/F:/cyzi/QMDB-DEV-NEW/java/MDBWeb/mdb-web/target/mdb-web/WEB-INF/lib/logback-classic-1.1.11.jar!/org/slf4j/impl/StaticLoggerBinder.class]
 * SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
 * SLF4J: Actual binding is of type [org.apache.logging.slf4j.Log4jLoggerFactory]
 * 16:27:46.650 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Root WebApplicationContext: initialization started MDC{}
 * 16:27:46.709 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Refreshing Root WebApplicationContext: startup date [Mon Dec 30 16:27:46 CST 2019]; root of context hierarchy MDC{}
 * 16:27:46.855 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Registering annotated classes: [class com.ztesoft.config.AppConfig] MDC{}
 * 16:27:46.922 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : No annotated classes found for specified class/package [<NONE>] MDC{}
 * 16:27:48.327 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Loading XML bean definitions from class path resource [spring/spring-config.xml] MDC{}
 * 16:27:49.732 [RMI TCP Connection(3)-127.0.0.1] [ERROR]  : file： nulltns/mdb_tnsnames.xml not exist! MDC{}
 * true
 * true
 * 16:27:50.846 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Root WebApplicationContext: initialization completed in 4188 ms MDC{}
 * 16:27:50.971 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : FrameworkServlet 'dispatcherServlet': initialization started MDC{}
 * 16:27:50.991 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Refreshing WebApplicationContext for namespace 'dispatcherServlet-servlet': startup date [Mon Dec 30 16:27:50 CST 2019]; parent: Root WebApplicationContext MDC{}
 * 16:27:50.997 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Registering annotated classes: [class com.ztesoft.config.DispatcherConfig,class com.ztesoft.config.WebSocketConfig] MDC{}
 * 16:27:52.868 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : read from file serviceCfg\tns/mdb_tnsnames.xml success! MDC{}
 * 16:27:56.065 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/getHostsByCluster],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.cluster.response.HostInfo> com.ztesoft.mdb.cluster.controller.ClusterController.getHostsByCluster(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.066 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/delAgentData],methods=[POST]}" onto public int com.ztesoft.mdb.cluster.controller.ClusterController.delAgentData(com.ztesoft.mdb.cluster.dto.AgentInfo) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.067 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/submitAgentData],methods=[POST]}" onto public int com.ztesoft.mdb.cluster.controller.ClusterController.submitAgentData(com.ztesoft.mdb.cluster.dto.AgentInfo) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.067 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/addCluster],methods=[POST]}" onto public int com.ztesoft.mdb.cluster.controller.ClusterController.addCluster(com.ztesoft.mdb.cluster.dto.ClusterInfoReq) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.067 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/getAllCluster],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.reqdto.DsnInfoReq> com.ztesoft.mdb.cluster.controller.ClusterController.getAllCluster() MDC{}
 * 16:27:56.067 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/updateBuildList],methods=[POST]}" onto public int com.ztesoft.mdb.cluster.controller.ClusterController.updateBuildList(com.ztesoft.mdb.cluster.dto.BuildInfo) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.067 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/getClusterState],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.cluster.dto.ClusterInfo> com.ztesoft.mdb.cluster.controller.ClusterController.getClusterState(java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException,com.jcraft.jsch.SftpException,com.ztesoft.exception.memdatabase.MemDataBaseException,com.ztesoft.exception.network.SocketReadOrWriteException,com.ztesoft.exception.network.ConnectSocketException MDC{}
 * 16:27:56.068 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/delClusterInfo],methods=[POST]}" onto public int com.ztesoft.mdb.cluster.controller.ClusterController.delClusterInfo(java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.069 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/getBuildList],methods=[POST]}" onto public java.util.Collection com.ztesoft.mdb.cluster.controller.ClusterController.getBuildList() MDC{}
 * 16:27:56.069 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/editCluster],methods=[POST]}" onto public int com.ztesoft.mdb.cluster.controller.ClusterController.editCluster(com.ztesoft.mdb.cluster.dto.ClusterInfoReq) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.069 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/testConn],methods=[POST]}" onto public java.lang.String com.ztesoft.mdb.cluster.controller.ClusterController.testConn(com.ztesoft.mdb.cluster.dto.ClusterInfoReq) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.069 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/getAgentList],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.cluster.dto.AgentInfo> com.ztesoft.mdb.cluster.controller.ClusterController.getAgentList() MDC{}
 * 16:27:56.070 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cluster/doDeployOpt],methods=[POST]}" onto public int com.ztesoft.mdb.cluster.controller.ClusterController.doDeployOpt(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.090 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/opServ/doRepairOne],methods=[POST]}" onto public java.lang.String com.ztesoft.mdb.cluster.controller.OpServController.doRepairOne(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.091 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/opServ/doSshQueryCmd],methods=[POST]}" onto public java.lang.String com.ztesoft.mdb.cluster.controller.OpServController.doSshQueryCmd(java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.092 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/opServ/qryClusterState],methods=[POST]}" onto public java.lang.String[] com.ztesoft.mdb.cluster.controller.OpServController.qryClusterState(java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.092 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/opServ/qryCreateState],methods=[POST]}" onto public java.lang.String com.ztesoft.mdb.cluster.controller.OpServController.qryCreateState(java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.093 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/opServ/doRepairLoad],methods=[POST]}" onto public java.lang.String com.ztesoft.mdb.cluster.controller.OpServController.doRepairLoad(java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException,java.lang.IllegalAccessException,com.ztesoft.exception.network.SocketReadOrWriteException,com.ztesoft.exception.memdatabase.MemDataBaseException,com.ztesoft.exception.network.ConnectSocketException,com.jcraft.jsch.SftpException,java.lang.NoSuchFieldException MDC{}
 * 16:27:56.096 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/opServ/doOpClusterServ],methods=[POST]}" onto public java.lang.String com.ztesoft.mdb.cluster.controller.OpServController.doOpClusterServ(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.099 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cache/getCacheDatas],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.reqdto.CacheInfoReq> com.ztesoft.mdb.configserv.controller.CacheController.getCacheDatas(java.lang.String,java.lang.String) MDC{}
 * 16:27:56.100 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/cache/getCacheTabDatas],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.reqdto.CacheTableInfoReq> com.ztesoft.mdb.configserv.controller.CacheController.getCacheTabDatas(java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.102 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/client/submitData],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClientController.submitData(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.104 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/client/getDatas],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.reqdto.ClientInfoReq> com.ztesoft.mdb.configserv.controller.ClientController.getDatas(java.lang.String,java.lang.String) MDC{}
 * 16:27:56.130 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/release],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.release(java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,java.io.IOException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.132 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/saveTabSpaceDatas],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.saveTabSpaceDatas(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.135 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/publishScreenDatas],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.publishScreenDatas(java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.136 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/getHostDatas],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.reqdto.HostInfoReq> com.ztesoft.mdb.configserv.controller.ClusCfgController.getHostDatas(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.137 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/saveUserDatas],methods=[POST]}" onto public void com.ztesoft.mdb.configserv.controller.ClusCfgController.saveUserDatas(java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.137 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/publishUserDatas],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.publishUserDatas(java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.138 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/getConfigValue/{clusterName}/{configName}],methods=[GET]}" onto public java.lang.String com.ztesoft.mdb.configserv.controller.ClusCfgController.getUserDatas(java.lang.String,java.lang.String) MDC{}
 * 16:27:56.138 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/getUserDatas],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.UserConfigInfo> com.ztesoft.mdb.configserv.controller.ClusCfgController.getUserDatas(java.lang.String) MDC{}
 * 16:27:56.138 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/getSliceDatas],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.reqdto.GrpSliceRangeReq> com.ztesoft.mdb.configserv.controller.ClusCfgController.getSliceDatas(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.139 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/saveSysDatas],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.saveSysDatas(java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.145 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/getScreenDatas],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.reqdto.ScreenHostInfoReq> com.ztesoft.mdb.configserv.controller.ClusCfgController.getScreenDatas(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.146 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/saveTableDatas],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.saveTableDatas(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.146 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/getPdbDatas],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.reqdto.PdbInfoReq> com.ztesoft.mdb.configserv.controller.ClusCfgController.getPdbDatas(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.148 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/getSysDatas],methods=[POST]}" onto public com.ztesoft.mdb.configserv.dto.reqdto.SysInfoReq com.ztesoft.mdb.configserv.controller.ClusCfgController.getSysDatas(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.148 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/getDataNodes],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.reqdto.DataNodeInfoReq> com.ztesoft.mdb.configserv.controller.ClusCfgController.getDataNodes(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.148 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/rollback],methods=[POST]}" onto public boolean com.ztesoft.mdb.configserv.controller.ClusCfgController.rollback(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.150 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/saveScreenDatas],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.saveScreenDatas(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.150 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/saveHostDatas],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.saveHostDatas(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.153 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/saveGroupDatas],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.saveGroupDatas(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.154 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/savePdbDatas],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.savePdbDatas(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.154 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/getGroupDatas],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.ClusterInfo> com.ztesoft.mdb.configserv.controller.ClusCfgController.getGroupDatas(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.155 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/publishSysDatas],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.publishSysDatas(java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.156 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/saveDataNodes],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.saveDataNodes(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.156 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/checkOffLineFile],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.checkOffLineFile(java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.156 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/getTableDatas],methods=[POST]}" onto public com.ztesoft.mdb.configserv.dto.reqdto.TabResp com.ztesoft.mdb.configserv.controller.ClusCfgController.getTableDatas(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.157 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/getTabSpaceDatas],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.reqdto.TableSpaceInfoReq> com.ztesoft.mdb.configserv.controller.ClusCfgController.getTabSpaceDatas(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.158 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/clusCfg/saveSliceDatas],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.ClusCfgController.saveSliceDatas(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.164 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/tns/submitAgentData],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.TnsController.submitAgentData(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.165 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/tns/submitTnsData],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.TnsController.submitTnsData(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.166 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/tns/rollbackTns],methods=[POST]}" onto public int com.ztesoft.mdb.configserv.controller.TnsController.rollbackTns(java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.168 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/tns/getTnsDatas],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.reqdto.DsnInfoReq> com.ztesoft.mdb.configserv.controller.TnsController.getTnsDatas(java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.169 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/tns/publishTns],methods=[POST]}" onto public java.lang.String com.ztesoft.mdb.configserv.controller.TnsController.publishTns(java.lang.String) MDC{}
 * 16:27:56.169 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/tns/getAgentDatas],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.configserv.dto.reqdto.ClusterAgentInfoReq> com.ztesoft.mdb.configserv.controller.TnsController.getAgentDatas(java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.173 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dataCheck/checkData],methods=[POST]}" onto public com.ztesoft.mdb.vo.ResultVO com.ztesoft.mdb.dataopt.controller.DataCheckController.checkData(com.ztesoft.mdb.dataopt.dto.CheckRequest) throws java.lang.Exception MDC{}
 * 16:27:56.174 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dataCheck/getCheckLog],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.dataopt.dto.CheckLogResp> com.ztesoft.mdb.dataopt.controller.DataCheckController.getCheckLog(java.lang.String,java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.174 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dataCheck/getAllTables],methods=[POST]}" onto public com.ztesoft.mdb.dataopt.dto.TabResp com.ztesoft.mdb.dataopt.controller.DataCheckController.getAllTables(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.174 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dataCheck/getAllTableInfo],methods=[POST]}" onto public java.util.List<java.util.Map<java.lang.String, java.lang.Object>> com.ztesoft.mdb.dataopt.controller.DataCheckController.getAllTableInfo(java.lang.String,java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.175 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dataCheck/getTableStruct],methods=[POST]}" onto public java.util.List<java.util.Map<java.lang.String, java.lang.Object>> com.ztesoft.mdb.dataopt.controller.DataCheckController.getTableStruct(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,java.sql.SQLException,com.ztesoft.exception.network.ConnectSocketException MDC{}
 * 16:27:56.176 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dataCheck/getCheckDetail],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.dataopt.dto.CheckResultResp> com.ztesoft.mdb.dataopt.controller.DataCheckController.getCheckDetail(java.lang.String,java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.181 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dataImpEx/getPath]}" onto public java.util.Map<java.lang.String, java.lang.Object> com.ztesoft.mdb.dataopt.controller.DataImpExController.getPath(java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.ztesoft.exception.memdatabase.MemDataBaseException,com.ztesoft.exception.network.SocketReadOrWriteException,com.ztesoft.exception.network.ConnectSocketException MDC{}
 * 16:27:56.182 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dataImpEx/dataMove],methods=[POST]}" onto public com.ztesoft.mdb.vo.ResultVO com.ztesoft.mdb.dataopt.controller.DataImpExController.dataMove(com.ztesoft.mdb.dataopt.dto.ImpExRequest) throws java.lang.Exception MDC{}
 * 16:27:56.183 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dataImpEx/impExOp],methods=[POST]}" onto public com.ztesoft.mdb.vo.ResultVO com.ztesoft.mdb.dataopt.controller.DataImpExController.impExOp(com.ztesoft.mdb.dataopt.dto.ImpExRequest) throws java.lang.Exception MDC{}
 * 16:27:56.185 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/sliceRuleChg/dataMove],methods=[POST]}" onto public com.ztesoft.mdb.vo.ResultVO com.ztesoft.mdb.dataopt.controller.SliceRuleChgCtl.dataMove(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.185 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/sliceRuleChg/getAdvTabDatas],methods=[POST]}" onto public com.ztesoft.mdb.configserv.dto.reqdto.TabResp com.ztesoft.mdb.dataopt.controller.SliceRuleChgCtl.getAdvTabDatas(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.186 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/sliceRuleChg/saveAdvTabData],methods=[POST]}" onto public int com.ztesoft.mdb.dataopt.controller.SliceRuleChgCtl.saveAdvTabData(java.lang.String,java.lang.String,java.lang.String) throws java.io.FileNotFoundException,com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,com.jcraft.jsch.SftpException MDC{}
 * 16:27:56.187 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/sliceRuleChg/getProgressBar],methods=[POST]}" onto public com.ztesoft.mdb.vo.ProgressVO com.ztesoft.mdb.dataopt.controller.SliceRuleChgCtl.getProgressBar(java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.ztesoft.exception.memdatabase.MemDataBaseException,com.ztesoft.exception.network.SocketReadOrWriteException,com.ztesoft.exception.network.ConnectSocketException,com.jcraft.jsch.SftpException,com.jcraft.jsch.JSchException MDC{}
 * 16:27:56.187 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/sliceRuleChg/getDataDisplay],methods=[POST]}" onto public java.util.List<java.util.Map<java.lang.String, java.lang.Object>> com.ztesoft.mdb.dataopt.controller.SliceRuleChgCtl.getDataDisplay(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.188 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/log/getLogs],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.utils.dto.OptLogDto> com.ztesoft.mdb.log.controller.LogController.getLogs(com.ztesoft.mdb.log.dto.LogReqDto) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.190 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/loginController/login],methods=[POST]}" onto public int com.ztesoft.mdb.login.controller.LoginController.login(com.ztesoft.mdb.login.request.LoginInfo) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.191 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/loginController/logout],methods=[POST]}" onto public int com.ztesoft.mdb.login.controller.LoginController.logout(java.lang.String,javax.servlet.http.HttpServletRequest) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.191 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/loginController/portalLogin],methods=[POST]}" onto public com.ztesoft.mdb.login.request.LoginInfo com.ztesoft.mdb.login.controller.LoginController.portalLogin(java.lang.String,java.lang.String,javax.servlet.http.HttpServletRequest) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.191 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/loginController/checkout]}" onto public int com.ztesoft.mdb.login.controller.LoginController.checkout(javax.servlet.http.HttpServletRequest) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.194 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/loginController/modifyPassWord]}" onto public int com.ztesoft.mdb.login.controller.LoginController.modifyPassWord(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.196 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/question/delData],methods=[POST]}" onto public int com.ztesoft.mdb.question.controller.QuestController.delData(com.ztesoft.mdb.question.dto.QuestionDto) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.197 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/question/getQuestions],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.question.dto.QuestionDto> com.ztesoft.mdb.question.controller.QuestController.getQuestions(com.ztesoft.mdb.question.dto.QuestionDto) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.197 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/question/modData],methods=[POST]}" onto public int com.ztesoft.mdb.question.controller.QuestController.modData(com.ztesoft.mdb.question.dto.QuestionDto) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.198 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/question/addData],methods=[POST]}" onto public int com.ztesoft.mdb.question.controller.QuestController.addData(com.ztesoft.mdb.question.dto.QuestionDto) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.208 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/sql/list/{type}/{tabId}/{wsId}],methods=[GET]}" onto public java.lang.String com.ztesoft.mdb.sql.controller.SQLEditorController.list(java.lang.String,java.lang.String,java.lang.String,javax.servlet.http.HttpServletRequest) throws com.ztesoft.framework.exception.BaseAppException,java.sql.SQLException MDC{}
 * 16:27:56.209 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/sql/list],methods=[POST]}" onto public java.util.List<java.util.Map<java.lang.String, java.lang.Object>> com.ztesoft.mdb.sql.controller.SQLEditorController.list(com.ztesoft.mdb.vo.R,javax.servlet.http.HttpServletRequest) throws java.sql.SQLException,com.ztesoft.framework.exception.BaseAppException,com.ztesoft.exception.memdatabase.MemDataBaseException,com.ztesoft.exception.network.SocketReadOrWriteException MDC{}
 * 16:27:56.210 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/sql/edit],methods=[POST]}" onto public java.lang.String com.ztesoft.mdb.sql.controller.SQLEditorController.edit(com.ztesoft.mdb.vo.R,javax.servlet.http.HttpServletRequest) throws com.ztesoft.framework.exception.BaseAppException,java.sql.SQLException MDC{}
 * 16:27:56.210 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/sql/exportFile],methods=[POST]}" onto public void com.ztesoft.mdb.sql.controller.SQLEditorController.exportFile(java.lang.String,javax.servlet.http.HttpServletResponse) MDC{}
 * 16:27:56.260 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/process/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.List<com.ztesoft.mdb.vo.ProcessVO> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.process(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.261 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/sequence/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.List<com.ztesoft.mdb.sqlMonitor.dto.sequenceInfo> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.sequence(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.263 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/mdbrole/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.List<com.ztesoft.mdb.sqlMonitor.dto.roleInfo> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.role(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.263 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/qryLockResource/{clusterName}/{hostType}/{wsId}],methods=[GET]}" onto public java.lang.Object[] com.ztesoft.mdb.sqlMonitor.DailyMonitorController.queryLockResource(java.lang.String,java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.264 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/clusterCfg/{clusterName}],methods=[GET]}" onto public java.util.List<java.util.Map<java.lang.String, java.lang.String>> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.loadClusterCfg(java.lang.String) throws com.ztesoft.framework.exception.BaseAppException MDC{}
 * 16:27:56.264 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/sqltop/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.List<com.ztesoft.mdb.sqlMonitor.dto.sqlperfInfo> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.sqltop(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.264 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/ddltop/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.List<com.ztesoft.mdb.sqlMonitor.dto.sqlperfInfo> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.ddltop(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.265 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/LogAudit/{clusterName}/{dayNumber}/{wsId}],methods=[GET]}" onto public java.util.List<com.ztesoft.mdbweb.interfaces.sqlmonitor.resp.LogAuditResp> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.queryAuditLog(java.lang.String,int,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.265 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/opServOne/{clusterName}/{nodeName}/{ip}/{ifCreate}/{ifStart}/{ifLoad}/{wsId}],methods=[GET]}" onto public com.ztesoft.mdb.vo.ProgressVO com.ztesoft.mdb.sqlMonitor.DailyMonitorController.opServOne(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.265 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/opCluster/{clusterName}/{opName}/{wsId}],methods=[GET]}" onto public java.lang.String com.ztesoft.mdb.sqlMonitor.DailyMonitorController.opCluster(java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.267 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/dataNodeList/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.List<java.lang.String> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.getDataNodeList(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.267 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/opNode/{clusterName}/{nodeName}/{ip}/{opName}/{wsId}],methods=[GET]}" onto public com.ztesoft.mdb.vo.ProgressVO com.ztesoft.mdb.sqlMonitor.DailyMonitorController.opNode(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String) throws com.ztesoft.framework.exception.BaseAppException,com.jcraft.jsch.JSchException,java.io.IOException MDC{}
 * 16:27:56.268 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/warningLog/{clusterName}/{dayNumber}/{wsId}],methods=[POST]}" onto public java.util.List<com.ztesoft.mdb.vo.LogVO> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.queryWarningLog(java.lang.String,int,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.268 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/sysState/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.List<com.ztesoft.mdb.sqlMonitor.dto.NodeInfoResp> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.sysState(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.268 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/agentpressure/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.List<com.ztesoft.mdb.sqlMonitor.dto.pressureInfo> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.agentpress(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.269 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/pressur3hour/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.Map<java.lang.String, java.util.List<com.ztesoft.mdb.vo.PressureVO>> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.pressure3hour(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.270 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/mdbuser/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.List<com.ztesoft.mdb.sqlMonitor.dto.mdbuserInfo> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.getuser(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.270 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/nodepressure/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.List<com.ztesoft.mdb.sqlMonitor.dto.pressureInfo> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.nodepress(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.270 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/pressure6hours/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.Map<java.lang.String, java.util.List<com.ztesoft.mdb.vo.PressureVO>> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.pressure6hours(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.270 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/mdbClusterShow/{wsId}],methods=[POST]}" onto public java.lang.String com.ztesoft.mdb.sqlMonitor.DailyMonitorController.mdbClusterShow(com.ztesoft.mdb.sqlMonitor.dto.MdbClusterShowDto,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.271 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/pressure12hours/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.Map<java.lang.String, java.util.List<com.ztesoft.mdb.vo.PressureVO>> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.pressure12hours(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.272 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/dailyMonitor/tableSpaceInfo/{clusterName}/{wsId}],methods=[GET]}" onto public java.util.List<com.ztesoft.mdbweb.interfaces.sqlmonitor.resp.TableSpaceInfoResp> com.ztesoft.mdb.sqlMonitor.DailyMonitorController.tableSpaceInfo(java.lang.String,java.lang.String) throws java.lang.Exception MDC{}
 * 16:27:56.332 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/mdbDataCheck/dataCheckCommand],methods=[POST]}" onto public java.lang.String com.ztesoft.mdb.sqlMonitor.MdbDataCheckController.dataCheckCommand(com.ztesoft.mdb.sqlMonitor.dto.MdbDataCheckDto) MDC{}
 * 16:27:56.334 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/sqlperf/group],methods=[POST]}" onto public java.util.Map<java.lang.String, java.lang.Object> com.ztesoft.mdb.sqlperf.SqlperfController.group(com.ztesoft.mdb.vo.SqlperfVO,javax.servlet.http.HttpServletRequest) throws com.ztesoft.framework.exception.BaseAppException,com.ztesoft.exception.memdatabase.MemDataBaseException,com.ztesoft.exception.network.SocketReadOrWriteException,java.sql.SQLException MDC{}
 * 16:27:56.335 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/sqlperf/list],methods=[POST]}" onto public java.util.Map<java.lang.String, java.lang.Object> com.ztesoft.mdb.sqlperf.SqlperfController.list(com.ztesoft.mdb.vo.SqlperfVO) throws com.ztesoft.framework.exception.BaseAppException,com.ztesoft.exception.memdatabase.MemDataBaseException,com.ztesoft.exception.network.SocketReadOrWriteException,java.sql.SQLException,com.ztesoft.exception.network.ConnectSocketException MDC{}
 * 16:27:56.337 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/pubCtl/encryptData],methods=[POST]}" onto public java.lang.String com.ztesoft.mdb.utils.PubController.encryptData(java.lang.String) MDC{}
 * 16:27:56.338 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped "{[/pubCtl/triggerDataJob],methods=[POST]}" onto public void com.ztesoft.mdb.utils.PubController.triggerDataJob(java.lang.String) MDC{}
 * 16:27:56.561 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped URL path [/**] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler] MDC{}
 * 16:27:56.927 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Looking for @ControllerAdvice: WebApplicationContext for namespace 'dispatcherServlet-servlet': startup date [Mon Dec 30 16:27:50 CST 2019]; parent: Root WebApplicationContext MDC{}
 * 16:27:57.242 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Initializing ExecutorService  'defaultSockJsTaskScheduler' MDC{}
 * 16:27:57.547 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped URL path [/systemWebSocket] onto handler of type [class org.springframework.web.socket.server.support.WebSocketHttpRequestHandler] MDC{}
 * 16:27:57.547 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped URL path [/sockjs/systemWebSocket/**] onto handler of type [class org.springframework.web.socket.sockjs.support.SockJsHttpRequestHandler] MDC{}
 * 16:27:57.548 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped URL path [/webConfigSocket/*] onto handler of type [class org.springframework.web.socket.server.support.WebSocketHttpRequestHandler] MDC{}
 * 16:27:57.563 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Mapped URL path [/webRefreshSocket/*] onto handler of type [class org.springframework.web.socket.server.support.WebSocketHttpRequestHandler] MDC{}
 * 16:27:57.688 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : Starting beans in phase 2147483647 MDC{}
 * 16:27:57.770 [RMI TCP Connection(3)-127.0.0.1] [INFO ]  : FrameworkServlet 'dispatcherServlet': initialization completed in 6798 ms MDC{}
 * [2019-12-30 04:27:57,804] Artifact mdb-web:war exploded: Artifact is deployed successfully
 * [2019-12-30 04:27:57,805] Artifact mdb-web:war exploded: Deploy took 33,478 milliseconds
 *
 *
 *
 *
 *
 *
 *
 * {@link LifecycleProcessor}策略的默认实现
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 3.0
 */
public class DefaultLifecycleProcessor implements LifecycleProcessor, BeanFactoryAware {

	private final Log logger = LogFactory.getLog(getClass());

	private volatile long timeoutPerShutdownPhase = 30000;

	private volatile boolean running;

	@Nullable
	private volatile ConfigurableListableBeanFactory beanFactory;


	/**
	 * Specify the maximum time allotted in milliseconds for the shutdown of
	 * any phase (group of SmartLifecycle beans with the same 'phase' value).
	 * <p>The default value is 30 seconds.
	 */
	public void setTimeoutPerShutdownPhase(long timeoutPerShutdownPhase) {
		this.timeoutPerShutdownPhase = timeoutPerShutdownPhase;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
			throw new IllegalArgumentException(
					"DefaultLifecycleProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
		}
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

	private ConfigurableListableBeanFactory getBeanFactory() {
		ConfigurableListableBeanFactory beanFactory = this.beanFactory;
		Assert.state(beanFactory != null, "No BeanFactory available");
		return beanFactory;
	}


	// Lifecycle implementation

	/**
	 * Start all registered beans that implement {@link Lifecycle} and are <i>not</i>
	 * already running. Any bean that implements {@link SmartLifecycle} will be
	 * started within its 'phase', and all phases will be ordered from lowest to
	 * highest value. All beans that do not implement {@link SmartLifecycle} will be
	 * started in the default phase 0. A bean declared as a dependency of another bean
	 * will be started before the dependent bean regardless of the declared phase.
	 */
	@Override
	public void start() {
		startBeans(false);
		this.running = true;
	}

	/**
	 * Stop all registered beans that implement {@link Lifecycle} and <i>are</i>
	 * currently running. Any bean that implements {@link SmartLifecycle} will be
	 * stopped within its 'phase', and all phases will be ordered from highest to
	 * lowest value. All beans that do not implement {@link SmartLifecycle} will be
	 * stopped in the default phase 0. A bean declared as dependent on another bean
	 * will be stopped before the dependency bean regardless of the declared phase.
	 */
	@Override
	public void stop() {
		stopBeans();
		this.running = false;
	}

	@Override
	public void onRefresh() {
		startBeans(true);
		this.running = true;
	}

	@Override
	public void onClose() {
		stopBeans();
		this.running = false;
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}


	// Internal helpers

	private void startBeans(boolean autoStartupOnly) {
		Map<String, Lifecycle> lifecycleBeans = getLifecycleBeans();
		Map<Integer, LifecycleGroup> phases = new HashMap<>();
		lifecycleBeans.forEach((beanName, bean) -> {
			if (!autoStartupOnly || (bean instanceof SmartLifecycle && ((SmartLifecycle) bean).isAutoStartup())) {
				int phase = getPhase(bean);
				LifecycleGroup group = phases.get(phase);
				if (group == null) {
					group = new LifecycleGroup(phase, this.timeoutPerShutdownPhase, lifecycleBeans, autoStartupOnly);
					phases.put(phase, group);
				}
				group.add(beanName, bean);
			}
		});
		if (!phases.isEmpty()) {
			List<Integer> keys = new ArrayList<>(phases.keySet());
			Collections.sort(keys);
			for (Integer key : keys) {
				phases.get(key).start();
			}
		}
	}

	/**
	 * Start the specified bean as part of the given set of Lifecycle beans,
	 * making sure that any beans that it depends on are started first.
	 * @param lifecycleBeans a Map with bean name as key and Lifecycle instance as value
	 * @param beanName the name of the bean to start
	 */
	private void doStart(Map<String, ? extends Lifecycle> lifecycleBeans, String beanName, boolean autoStartupOnly) {
		Lifecycle bean = lifecycleBeans.remove(beanName);
		if (bean != null && bean != this) {
			String[] dependenciesForBean = getBeanFactory().getDependenciesForBean(beanName);
			for (String dependency : dependenciesForBean) {
				doStart(lifecycleBeans, dependency, autoStartupOnly);
			}
			if (!bean.isRunning() &&
					(!autoStartupOnly || !(bean instanceof SmartLifecycle) || ((SmartLifecycle) bean).isAutoStartup())) {
				if (logger.isTraceEnabled()) {
					logger.trace("Starting bean '" + beanName + "' of type [" + bean.getClass().getName() + "]");
				}
				try {
					bean.start();
				}
				catch (Throwable ex) {
					throw new ApplicationContextException("Failed to start bean '" + beanName + "'", ex);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully started bean '" + beanName + "'");
				}
			}
		}
	}

	private void stopBeans() {
		Map<String, Lifecycle> lifecycleBeans = getLifecycleBeans();
		Map<Integer, LifecycleGroup> phases = new HashMap<>();
		lifecycleBeans.forEach((beanName, bean) -> {
			int shutdownPhase = getPhase(bean);
			LifecycleGroup group = phases.get(shutdownPhase);
			if (group == null) {
				group = new LifecycleGroup(shutdownPhase, this.timeoutPerShutdownPhase, lifecycleBeans, false);
				phases.put(shutdownPhase, group);
			}
			group.add(beanName, bean);
		});
		if (!phases.isEmpty()) {
			List<Integer> keys = new ArrayList<>(phases.keySet());
			keys.sort(Collections.reverseOrder());
			for (Integer key : keys) {
				phases.get(key).stop();
			}
		}
	}

	/**
	 * Stop the specified bean as part of the given set of Lifecycle beans,
	 * making sure that any beans that depends on it are stopped first.
	 * @param lifecycleBeans a Map with bean name as key and Lifecycle instance as value
	 * @param beanName the name of the bean to stop
	 */
	private void doStop(Map<String, ? extends Lifecycle> lifecycleBeans, final String beanName,
			final CountDownLatch latch, final Set<String> countDownBeanNames) {

		Lifecycle bean = lifecycleBeans.remove(beanName);
		if (bean != null) {
			String[] dependentBeans = getBeanFactory().getDependentBeans(beanName);
			for (String dependentBean : dependentBeans) {
				doStop(lifecycleBeans, dependentBean, latch, countDownBeanNames);
			}
			try {
				if (bean.isRunning()) {
					if (bean instanceof SmartLifecycle) {
						if (logger.isTraceEnabled()) {
							logger.trace("Asking bean '" + beanName + "' of type [" +
									bean.getClass().getName() + "] to stop");
						}
						countDownBeanNames.add(beanName);
						((SmartLifecycle) bean).stop(() -> {
							latch.countDown();
							countDownBeanNames.remove(beanName);
							if (logger.isDebugEnabled()) {
								logger.debug("Bean '" + beanName + "' completed its stop procedure");
							}
						});
					}
					else {
						if (logger.isTraceEnabled()) {
							logger.trace("Stopping bean '" + beanName + "' of type [" +
									bean.getClass().getName() + "]");
						}
						bean.stop();
						if (logger.isDebugEnabled()) {
							logger.debug("Successfully stopped bean '" + beanName + "'");
						}
					}
				}
				else if (bean instanceof SmartLifecycle) {
					// Don't wait for beans that aren't running...
					latch.countDown();
				}
			}
			catch (Throwable ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Failed to stop bean '" + beanName + "'", ex);
				}
			}
		}
	}


	// overridable hooks

	/**
	 * Retrieve all applicable Lifecycle beans: all singletons that have already been created,
	 * as well as all SmartLifecycle beans (even if they are marked as lazy-init).
	 * @return the Map of applicable beans, with bean names as keys and bean instances as values
	 */
	protected Map<String, Lifecycle> getLifecycleBeans() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		Map<String, Lifecycle> beans = new LinkedHashMap<>();
		String[] beanNames = beanFactory.getBeanNamesForType(Lifecycle.class, false, false);
		for (String beanName : beanNames) {
			String beanNameToRegister = BeanFactoryUtils.transformedBeanName(beanName);
			boolean isFactoryBean = beanFactory.isFactoryBean(beanNameToRegister);
			String beanNameToCheck = (isFactoryBean ? BeanFactory.FACTORY_BEAN_PREFIX + beanName : beanName);
			if ((beanFactory.containsSingleton(beanNameToRegister) &&
					(!isFactoryBean || matchesBeanType(Lifecycle.class, beanNameToCheck, beanFactory))) ||
					matchesBeanType(SmartLifecycle.class, beanNameToCheck, beanFactory)) {
				Object bean = beanFactory.getBean(beanNameToCheck);
				if (bean != this && bean instanceof Lifecycle) {
					beans.put(beanNameToRegister, (Lifecycle) bean);
				}
			}
		}
		return beans;
	}

	private boolean matchesBeanType(Class<?> targetType, String beanName, BeanFactory beanFactory) {
		Class<?> beanType = beanFactory.getType(beanName);
		return (beanType != null && targetType.isAssignableFrom(beanType));
	}

	/**
	 * Determine the lifecycle phase of the given bean.
	 * <p>The default implementation checks for the {@link Phased} interface, using
	 * a default of 0 otherwise. Can be overridden to apply other/further policies.
	 * @param bean the bean to introspect
	 * @return the phase (an integer value)
	 * @see Phased#getPhase()
	 * @see SmartLifecycle
	 */
	protected int getPhase(Lifecycle bean) {
		return (bean instanceof Phased ? ((Phased) bean).getPhase() : 0);
	}


	/**
	 * Helper class for maintaining a group of Lifecycle beans that should be started
	 * and stopped together based on their 'phase' value (or the default value of 0).
	 */
	private class LifecycleGroup {

		private final int phase;

		private final long timeout;

		private final Map<String, ? extends Lifecycle> lifecycleBeans;

		private final boolean autoStartupOnly;

		private final List<LifecycleGroupMember> members = new ArrayList<>();

		private int smartMemberCount;

		public LifecycleGroup(
				int phase, long timeout, Map<String, ? extends Lifecycle> lifecycleBeans, boolean autoStartupOnly) {

			this.phase = phase;
			this.timeout = timeout;
			this.lifecycleBeans = lifecycleBeans;
			this.autoStartupOnly = autoStartupOnly;
		}

		public void add(String name, Lifecycle bean) {
			this.members.add(new LifecycleGroupMember(name, bean));
			if (bean instanceof SmartLifecycle) {
				this.smartMemberCount++;
			}
		}

		public void start() {
			if (this.members.isEmpty()) {
				return;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Starting beans in phase " + this.phase);
			}
			Collections.sort(this.members);
			for (LifecycleGroupMember member : this.members) {
				doStart(this.lifecycleBeans, member.name, this.autoStartupOnly);
			}
		}

		public void stop() {
			if (this.members.isEmpty()) {
				return;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Stopping beans in phase " + this.phase);
			}
			this.members.sort(Collections.reverseOrder());
			CountDownLatch latch = new CountDownLatch(this.smartMemberCount);
			Set<String> countDownBeanNames = Collections.synchronizedSet(new LinkedHashSet<>());
			Set<String> lifecycleBeanNames = new HashSet<>(this.lifecycleBeans.keySet());
			for (LifecycleGroupMember member : this.members) {
				if (lifecycleBeanNames.contains(member.name)) {
					doStop(this.lifecycleBeans, member.name, latch, countDownBeanNames);
				}
				else if (member.bean instanceof SmartLifecycle) {
					// Already removed: must have been a dependent bean from another phase
					latch.countDown();
				}
			}
			try {
				latch.await(this.timeout, TimeUnit.MILLISECONDS);
				if (latch.getCount() > 0 && !countDownBeanNames.isEmpty() && logger.isInfoEnabled()) {
					logger.info("Failed to shut down " + countDownBeanNames.size() + " bean" +
							(countDownBeanNames.size() > 1 ? "s" : "") + " with phase value " +
							this.phase + " within timeout of " + this.timeout + ": " + countDownBeanNames);
				}
			}
			catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}


	/**
	 * Adapts the Comparable interface onto the lifecycle phase model.
	 */
	private class LifecycleGroupMember implements Comparable<LifecycleGroupMember> {

		private final String name;

		private final Lifecycle bean;

		LifecycleGroupMember(String name, Lifecycle bean) {
			this.name = name;
			this.bean = bean;
		}

		@Override
		public int compareTo(LifecycleGroupMember other) {
			int thisPhase = getPhase(this.bean);
			int otherPhase = getPhase(other.bean);
			return Integer.compare(thisPhase, otherPhase);
		}
	}

}
