# EmailService
 
Crumbs Email Service

We are using Amazon SES (Simple Email Service) to send e-mails to our customers.

Each e-mail we send is created from pre-made templates saved in SES.

Procedure to create a template:

- connect to your Amazon CLI
- create a <template_name>.json file, similar to the EmailConfirmationTemplate.json file.
- run command: aws ses create-template --cli-input-json file://<template-name>.json
- template is created

To delete a template run command:

- aws ses delete-template --template-name 

Endpoints:

1)
URL structure: /email/token/{token}

Description:
This endpoint takes in a token and tries to confirm the associated token.

Method: GET

Parameters:

Path Variable:
- token : String. Not null. Token you wish to confirm.

Returns:
String: Token confirmation response. 

Errors:

- NoSuchElementException: No token with that ID was found. I.e invalid token.

2)
URL structure: /email/{email}/name/{name}/token/{token}

Description:
This endpoint takes in three Strings: an email, a first name and a token. It sends a confirmation email to the associated email with template variables: name and link.

Method: GET

Path Variables:
- email: Email you wish to send confirmation email to.
- name: First name of recipient
- token: token used in the confirmation link.

Errors:

- AmazonSES errors