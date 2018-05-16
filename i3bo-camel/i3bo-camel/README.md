# Configure ‘Wildfly with Camel’ to use SSL and AUTH

## Setup & Configuration

The following software needs to be setup to get this sample going. Please follow the links and setup the software

1. Windfly - 10.1.0.FINAL : http://wildfly.org/downloads/
2. Red Hat JBoss Developer studio (JBDS) 11.3.0.GA, more info at http://www.jboss.com/products/devstudio

For some reason the documentation around this is a bit patchy and hence would like you to rely on the raw docs at github found at https://github.com/wildfly/quickstart/tree/10.x/guide

Specifically refer to https://github.com/wildfly/quickstart/blob/10.x/guide/GettingStarted.asciidoc for setting up the softwares. When creating the servers ensure that you select Wildfly server 10.x. 

Additionally we need Wildfly-Camel patch for Camel integration into Wildfly.

- Wildly-Camel patch 4.9.0 : https://github.com/wildfly-extras/wildfly-camel/releases

To setup and configure the Wildfly-camel patch please follow the documentation at https://wildfly-extras.github.io/wildfly-camel/, specifically the section ‘Standalone Server’ as we will be using the same. Note: when you unpack the archive, ensure that you unpack the archive into the root folder of Wildfly Home directory and ensure that you merge the files and not replace the existing folders with the ones from the archive.

Once the Wildly-Camel patch is started in standalone mode as in documentation, it is time to setup another server with the Wildly-Camel configuration within JBDS. Follow the same process to create a server. But select the “Wildfly with Camel” configuration in the respective window in the process wizard flow, see screenshot below.
￼
This will create a Wildfly server with Camel integrated. We will be deploying i3bo-camel web app project to this server.
