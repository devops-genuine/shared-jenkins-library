import hudson.model.Result
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

/**
 * @param currentBuild a Jenkins RunWrapper object for the current build
 * @param repoName an docker repo name
 * @param projectName an application name
 * @param releaseVersion an release version's number
 * @param targetNamespace a terget kubernetes namespace
 */
def call(RunWrapper currentBuild, String repoName, String projectName, String releaseVersion, String targetNamespace) {

    def currentResult = currentBuild.currentResult
    def previousResult = currentBuild.getPreviousBuild()?.getResult()

    def buildIsFixed =
        currentResult == Result.SUCCESS.toString() &&
        currentResult != previousResult &&
        previousResult != null

    def badResult =
        currentResult in [Result.UNSTABLE.toString(), Result.FAILURE.toString()]
    if (buildIsFixed || badResult) {
    	sh "helm upgrade --install ${projectName} ./${projectName} --set image.repository=${repoName}/${projectName} --set image.tag=${releaseVersion}-${env.BUILD_NUMBER} -n ${targetNamespace}"
    }
}