## About ecims

ECIMS is the web application that provides electronic services to a parastatal in Nigeria managing the imports of certain category. Applicants register on the portal then apply to 
get a certificate to import items into the country. This application for a certificate goes through a workflow process where staff of the parastatal approve or disapprove the issuance of 
the certificate. The primary officer finally in approving makes use of an RSA SecureID hardware token or an SMS soft token to approve the application.
##


## Technical Details

The ecims mobile application is developed using Java, database is Microsoft SQL

## Install the Java
Before proceeding, make sure your computer has Java installed. Minimum version is Java 8. See Oracle website for documentation on Java installation

## Install the Microsoft SQL
Before proceeding, make sure your computer has Microsoft SQL installed. See guidance online for installtion of Microsoft SQL and its tools.

## Dependency
Generate WAR file using your favorite IDE such as Eclipse or your command prompt/bash. <br><br>

Using Eclipse:<br>
Right Click on Project and click on "Export"<br>
Proceed with the steps to generate the war file.<br>
Go to your project Directory and inside Dist Folder you will get war file that you copy on your tomcat webApp Folder.<br>
Start the tomcat.<br>
It automatically extracts the folder from the war file.
