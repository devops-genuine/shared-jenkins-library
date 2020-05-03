import hudson.model.Result
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

/**
 * @param currentBuild a Jenkins RunWrapper object for the current build
 * @param repoName an docker repo name
 * @param projectName an application name
 * @param releaseVersion an release version's number
 */
def call(RunWrapper currentBuild, String repoName, String projectName, String releaseVersion) {

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
    	sh "docker image build -t ${repoName}/${projectName}:${releaseVersion}-${env.BUILD_NUMBER} ."
	    sh "docker image push ${repoName}/${projectName}:${releaseVersion}-${env.BUILD_NUMBER}"
    }
}