# Traffic shifting in Lambda

This example assumes you know Lambda, API Gateway and SAM already.

To run this do: ``sh copy-and-deploy.sh``. This will deploy the first version.

To install a new version run the command again. Now go to CodeDeploy -> Deployments and observe a new deployment is in progress.

## Test the output
First install [Artillary](https://artillery.io/). Then do ``artillery run artillery.yaml``.
Observe that it logs the function version. If you do that while making a deploy of a new version (running the able sh script), you should obverse a lot of call with version $x and a few (approximately 10%) with version $x+1.


## Make a deployment fail and rollback
To make a deployment fail go to the run this command:


