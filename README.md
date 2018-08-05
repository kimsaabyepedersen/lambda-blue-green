# Traffic shifting in Lambda

This example assumes you know Lambda, API Gateway and SAM already.

To run this do: ``sh copy-and-deploy.sh``. This will deploy the first version.

To install a new version run the command again. Now go to CodeDeploy -> Deployments and observe a new deployment is in progress. It uses the method `Canary10Percent10Minutes` which means it lets 10% of the traffic go to the new version of the Lambda function for 10 minutes and then shifts all traffic over - unless there is an error and the new deployment is rolled back.

## Sunshine scenario
* First install [Artillary](https://artillery.io/). 
* Then go to CloudFormation and view the Output of the first deployment of the stack (this can also be viewed in the API Gateway). It will contain a the URL to the API. Note down the part after https:// and before .execute-api.\<your-region-of-choice\>.amazonaws.com. 
* Now open the file `artillery-tmp.yaml` and adjust the `target` line. Save it as artillery.yaml

Now that it is set up, run: ``artillery run artillery.yaml``.

Observe that it logs the function version. If you do that while making a deploy of a new version (running the above sh script), you should obverse a lot of call with version $x and a few (approximately 10%) with version $x+1.


## Make a deployment fail and rollback
Follow the steps from the sunshine scenario but also do this as a last step:
``artillery quick --count 10 -n 10 https://<INSERT VALUE FROM API GATEWAY HERE>.execute-api.<YOUR REGION>.amazonaws.com/Prod/showMyIp?failversion=<THE NEW VERSION>` 

This fire 100 requests to the API that should trigger an error (see the Java code).
Now you should observe the the first artillery command should only display the version of the current function and if you go to CodeDeploy you will see that the deployment of the new version has been rolled back.

