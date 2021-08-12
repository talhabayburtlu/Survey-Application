# Survey-Application
This project creates an area for a campany to manage their actions by looking survey results that are created by them. Customers are able to create memberships and fill surveys.
This project is created with Spring Boot & React JS.

## How does it work?
MySQL has been used for data storing purposes. A schema named 'survey' must be created and also an user must be created with both username and password 'survey'. 
Backend of the project uses 8080 port and fronend of the project uses 3000. Backend can be runned from SurveyApplication.java and frontend can be runned from js/src/index.js.

In order to run backend spring.mail.username and spring.mail.password must be changed to another mail with the password for changing/approving the password of the users 
functionality in application.properties file.

## Functionalities
•	**Unauthenticated User:** User can register with his/her personal information and a verification mail is sent to mentioned email. The token should be entered with mail 
information to verify endpoint. If a user hasn’t verified his/her account, all secured endpoints plus login endpoint is not available to use. Also, it is possible to use 
forget password endpoint to renew password by sending a token via email to user’s mail. The token and user’s mail should be sent to forget password endpoint in order to
change unauthenticated user’s password.

•	**Authenticated User:** Authentication is provided with login endpoint. The login endpoint gives a JWT for accessing secured endpoints. User is able to see survey results, 
available (not answered) topics for him/her, send answer for available topics and also request proposal topics and answers. Also authenticated user is able to change 
password without an email process.

•	**Admin:** The difference between admin and user is stored like a role inside MySQL database. Spring security dynamically creates JWT for roles that a user has. So, 
admin is also able to login with same endpoint that a user uses. Admin can create topics, see the results of the topics, approve or delete requested topics and update 
the content of the approved topics and add/delete new answers. Also, an admin is able to do things like a user does. For example, admin can submit an answer to a topic, 
change password etc.

## Screenshots from Admin View

### Creation of survey
![Creation of survey](https://i.imgur.com/sq6hXyw.png)

### Approving survey requests
![Approving survey requests](https://imgur.com/qlTzBwt.png)

## Screenshots from User View

### Filling Surveys
![Filling Surveys](https://i.imgur.com/wfTEfbL.png)

### Reviewing Survey Results
![Reviewing Survey Results](https://imgur.com/No8kVD3.png)
