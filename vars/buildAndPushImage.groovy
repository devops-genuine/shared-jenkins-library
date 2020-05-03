import hudson.model.Result
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

/**
 * @param currentBuild a Jenkins RunWrapper object for the current build
 * @param repoName an docker repo name
 * @param projectName an application name
 */
def call(RunWrapper currentBuild, Srting repoName, String projectName) {

    def currentResult = currentBuild.currentResult
    def previousResult = currentBuild.getPreviousBuild()?.getResult()

    def buildIsFixed =
        currentResult == Result.SUCCESS.toString() &&
        currentResult != previousResult &&
        previousResult != null

    def badResult =
        currentResult in [Result.UNSTABLE.toString(), Result.FAILURE.toString()]
    if (buildIsFixed || badResult) {
    	sh "/usr/local/bin/aws ecr create-repository --repository-name ${projectName} --image-tag-mutability IMMUTABLE || true"
    	sh "/usr/local/bin/aws ecr get-login-password --region ap-southeast-1 | docker login --username AWS --password-stdin ${repoName}/${projectName}"
    	sh "docker image build -t ${repoName}/${projectName}:${env.BRANCH_NAME}-${env.BUILD_NUMBER} ."
	    //sh "docker login -u '$USER' -p '$PASS'"
	    //sh "docker image push ${hubUser}/${project}:beta-${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
    }
}