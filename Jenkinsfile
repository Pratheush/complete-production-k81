pipeline {

  /************************************************************************
   * Parameters
   * - allow toggling cleanup after build (useful for debugging vs prod)
   ************************************************************************/  
  parameters {
  booleanParam(name: 'CLEANUP_ENABLED', defaultValue: true, description: 'Clean up Docker images after Pushed into Dockerhub Repository & Deploying into K8 cluster')
  }
  
  // Define the agent where the pipeline will run.
  // 'any' means Jenkins will pick any available agent.
  // If you have a specific label for your Windows 11 machine (e.g., 'windows-agent'),
  // you can use 'agent { label 'windows-agent' }' instead.
  // agent { label 'windows-docker' }
  // agent { label 'linux' }
  // agent { label 'linux-docker' }  Run only on agents with Docker installed
  // agent { label 'docker' }
  // Use a specific agent label for consistency and to ensure the right machine is used.
  agent any
  
  tools {
    // If you have a configured Maven tool in Jenkins, reference it by name.
    maven "maven_3_9_5"
    // make sure this name matches the configured JDK name inside Jenkins this way we can specify jdk specifically an another version for a pipeline
    // jdk "jdk21"
  }
  
  // If you don't want Jenkins to do that auto-checkout and only want to rely on your git step, you could set:  skipDefaultCheckout(true)
  // skipDefaultCheckout(false) means: Jenkins will perform the default checkout scm at the start of the pipeline.
  // Keep this to have full control over the checkout process.
  options {
    
    skipDefaultCheckout(true) // We control checkout manually
    
    // buildDiscarder(logRotator(numToKeepStr: '5'))

    buildDiscarder(logRotator(numToKeepStr: '10')) // Keep last 10 builds

    // global timeout for whole pipeline (fail-safe)
    timeout(time: 30, unit: 'MINUTES') // Fail if pipeline runs too long

    // Show timestamps in console output
    timestamps()
  }

  // Define environment variables.
  // IMPORTANT: Replace 'your-dockerhub-username' and 'your-repo-name'
  // The 'docker-hub-credentials' should be a 'Secret text' credential in Jenkins or UsernameAndPassword type
  // storing your Docker Hub password/token.
  environment {
    DOCKER_IMAGE_NAME = 'pratheush/spring-taskapp-jenkins-k8-cicd'
    // Stored DockerHub Credentials In Jenkins credentials ID
    DOCKER_CREDENTIALS_ID = 'dockerhub-uname-pwd-token'
    SONAR_TOKEN = credentials('jenkins-sonarqube-token')

    REGISTRY = "pratheush"
    IMAGE_NAME = "spring-taskapp-jenkins-k8-cicd"
    
    // Declarative pipelines don’t allow sh inside environment. This will fail. Fix: Move this into a script block in a Checkout stage and set it with env.GIT_COMMIT_SHORT = ....
    //GIT_COMMIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
    
    // MANIFEST_REPO must be SSH URL, NOT HTTPS : The sshagent does NOT authenticate HTTPS URLs.
    MANIFEST_REPO = "https://github.com/Pratheush/spring-taskapp-k8-manifests.git"
    MANIFEST_REPO = "git@github.com:Pratheush/spring-taskapp-k8-manifests.git"


    MANIFEST_REPO_DIR = "manifests"
    DEPLOYMENT_FILE = "taskdeployment.yaml"
  }

  // Define triggers for the pipeline.
  // 'githubPush()' configures the pipeline to be triggered by GitHub push events.
  triggers {
    // This allows GitHub to trigger builds on push (needs webhook configured)
    // // Ensure you have configured a GitHub webhook in your repository settings pointing to your Jenkins instance.
    githubPush()
  }

  // Define the stages of your CI/CD pipeline.
  stages {
      
    // Stage 1: Checkout Source Code 
    stage('Checkout') {
      steps {
        echo '📦 Checking out source code...'
        // Checkout the SCM (Source Code Management) configured for this job. This typically points to your GitHub repository.
        // The git step is correctly configured for a public repo. For a private one, use credentials.
        git branch: 'main', url: 'https://github.com/Pratheush/complete-production-k81.git'

        // Use two-step checkout so we can capture commit SHA reliably
        // If repository is private and needs credentials, add credentials: 'your-cred-id'
        // checkout([
        //   $class: 'GitSCM',
        //   branches: [[name: '*/main']],
        //   userRemoteConfigs: [[url: 'https://github.com/Pratheush/complete-production-k81.git']]
        // ])

        script {
          // Capture short commit SHA into env variable
          env.GIT_COMMIT_SHORT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
          echo "Detected short commit: ${env.GIT_COMMIT_SHORT}"
        }
      }
    }


    // HERE at this stage Trivy scan means Early detection of vulnerable dependencies before packaging or containerizing
    // Scan Spring Boot Dependencies with Trivy
    // Trivy supports scanning Java projects via:
    // pom.xml (Maven), .jar files(with limitations for SpringBoot fat JARs), local filesystem (trivy fs.)
    // This scans your project directory, including pom.xml, and flags vulnerable libraries
    // --vuln-type library FLAG Scans for vulnerabilities in application dependencies (e.g., Maven libraries)
    // . Target directory — the current working directory (your Spring Boot project root)
    // trivy fs  will Scans the local filesystem (your source code directory)
    // It scans Dependency versions and known CVEs and Vulnerabilities in third-party libraries (like Spring, Jackson, Hibernate, etc.) and scans pom.xml and target directory
    // WARN	'--vuln-type' is deprecated. Use '--pkg-types' instead.
    stage('Trivy Scan - Filesystem - Maven Dependencies') {
      steps {
        script {
          echo "🔍 Trivy Scan for Maven Dependencies (Filesystem)..."
          // sh 'trivy fs . --exit-code 1 --severity HIGH,CRITICAL --no-progress'
          // sh 'trivy fs --exit-code 1 --severity HIGH,CRITICAL --no-progress --vuln-type library .'

          // Download HTML template for Trivy report
          sh "curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/html.tpl > html.tpl"

          // Run Trivy scan and generate HTML report
          sh "trivy fs --exit-code 1 --severity HIGH,CRITICAL --no-progress --pkg-types library --format template --template \"@html.tpl\" -o report-trivy-fs.html ."

          // Publish the report to Jenkins UI
          // 📦 Scans your  and resolved dependencies for known CVEs.
          // 🧠 Helps catch issues before Docker packaging or deployment.
          // 📊 Makes results visible in Jenkins under HTML Reports, so you don’t need to dig through logs.
          publishHTML([
            reportDir: '.',
            reportFiles: 'report-trivy-fs.html',
            reportName: 'Trivy Filesystem Maven Dependency Scan Security-Report'
          ])

        }
      }
    }

    // Stage 2: Build Spring Boot Application
    // FOR ONE-LINER AND SIMPLICITY WE CAN USE DIRECTLY HERE IN BUILDING AND TESTING APP :: sh 'mvn clean verify sonar:sonar -Dsonar.login=$SONAR_TOKEN'
    // FOR PRODUCTION SEPARATE STAGES ARE CONFIGURED
    stage('Build & Test App') {
      steps {
        echo '🛠️ Building Spring Boot application with Maven...'
        // Use 'bat' for Windows
        // 'mvn clean package' cleans the target directory and packages the application into a JAR file (typically in the 'target' directory).
        // -DskipTests to skip tests during build, remove if you want tests to run
        // bat './mvnw clean package -DskipTests'
        // bat './mvnw clean verify'
        // sh 'mvn clean package'
        sh 'mvn clean verify'
      }
    }

    // Running Unit Tests on SPringBoot Application
    // Merge BUILD & TEST APP  and TEST stages: Since 'mvn clean verify' already runs tests, the separate 'mvn test' stage may be redundant unless you're isolating test types (e.g., unit vs integration).
    // stage('Test') {
    //   steps {
    //     echo 'Running Unit Tests SpringBoot Application'
    //     sh 'mvn test'
    //   }
    // }

    // Running Static Code Analysis Via SonarQube
    stage('SonarQube Analysis') {
      steps {
        echo 'Going to Run static code analysis SpringBoot Application'
        withSonarQubeEnv(installationName: 'sonarqube') {
          echo 'Starting Running STATIC CODE ANALYSIS on SPRINGBOOT APPLICATION....'  
          // Below here Default Sonar configured and specified in Jenkins would be used
          // sh 'mvn verify sonar:sonar -Dsonar.login=$SONAR_TOKEN'
          // Below here specifying which sonar maven version plugin should be used
          //sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.0.2155:sonar -Dsonar.login=$SONAR_TOKEN'

          // as -Dsonar.login has been deprecated we use -Dsonar.token instead
          // sh 'mvn verify sonar:sonar -Dsonar.token=$SONAR_TOKEN'

          // Tell SonarQube Where to Find the Report Update SonarQube analysis stage like this: 
          // sh 'mvn verify sonar:sonar -Dsonar.token=$SONAR_TOKEN -Dsonar.projectKey=mycicd2 -Dsonar.host.url=http://localhost:9000 -Dsonar.junit.reportPaths=target/surefire-reports,target/failsafe-reports -Dsonar.coverage.jacoco.xmlReportPaths=target/jacoco-report/jacoco.xml'
           sh 'mvn sonar:sonar -Dsonar.token=$SONAR_TOKEN -Dsonar.projectKey=spring-taskapp-jenkins-k8-cicd-projectKey -Dsonar.projectName=spring-taskapp-jenkins-k8-cicd-projectName -Dsonar.projectVersion=1.0 -Dsonar.host.url=http://localhost:9000 -Dsonar.junit.reportPaths=target/surefire-reports,target/failsafe-reports -Dsonar.coverage.jacoco.xmlReportPaths=target/jacoco-report/jacoco.xml'
        //   sh """
        //   mvn sonar:sonar \
        //     -Dsonar.token=$SONAR_TOKEN \
        //     -Dsonar.projectKey=spring-taskapp-jenkins-k8-cicd-projectKey \
        //     -Dsonar.projectName=spring-taskapp-jenkins-k8-cicd-projectName \ 
        //     -Dsonar.projectVersion=1.0 \ 
        //     -Dsonar.host.url=http://localhost:9000 \
        //     -Dsonar.junit.reportPaths=target/surefire-reports,target/failsafe-reports \
        //     -Dsonar.coverage.jacoco.xmlReportPaths=target/jacoco-report/jacoco.xml
        //   """

        }
      }
    }

    // SonarQube Quality Gate misconfigured. (RECOMMENDED FOR PRODUCTION ) waitForQualityGate doesn’t take credentialsId. It uses the SonarQube server configured in Jenkins.
    // Just use waitForQualityGate abortPipeline: true (RECOMMENDED FOR PRODUCTION )
    // Enable Quality Gates (Optional but Recommended)
    // we wanted to make sure a quality gate is passed 
    // In SonarQube, configure a Quality Gate , Add a webhook in SonarQube to notify Jenkins of pass/fail status
    // we are gonna get Three minutes timeout and we are gonna wait for quality-gate TO PASS/FAIL and BASED ON QUALITY-GATE if the quality-gates pass/fail . if it comes with failed quality-gate this pipeline is going to abort
    // or if quality-gate comes with pass then pipeline will continue further and succeed
    // after adding Quality-Gate add webhook in sonarqube adding ngrok jenkins into it
    stage("Quality Gate") {
      steps {
        echo 'Quality Gate PASS/FAIL CHECK'
        echo '⏳ Waiting for SonarQube Quality Gate (timeout 5m)'
        // Wait for quality gate result. This uses the SonarQube server configured above.
        // abortPipeline:true will fail the Jenkins build if quality gate is FAILED
        timeout(time: 5, unit: 'MINUTES') {
          // waitForQualityGate abortPipeline: true
          // No need for credentialsId here if already in withSonarQubeEnv : JUST USE waitForQualityGate abortPipeline: true
          waitForQualityGate abortPipeline: true, credentialsId: 'jenkins-sonarqube-token'
        }
      }
    }


    // Stage 3: Build Docker Image
    // docker build -t spring-taskapp-jenkins-k8-cicd:1.0 .
     stage('Build Docker Image') {
      steps {
        echo '🐳 Building Docker image...(tagged with commit sha and latest)'
        // Build the Docker image.
        // Assumes a Dockerfile exists in the root of your project.
        // .    the current directory where Dockerfile and project file lives
        // build   command passes to batch file which builds Docker Image
        // -t     tags the image with the name from the environment variable and gives it the latest tag
        // Uses multi-tag Docker image (short-commit and latest) — good practice.
        script {
          echo '🐳 Building Docker image INSIDE SCRIPT SECTION'

          // def imageTag = "my-app:${env.BUILD_NUMBER}"
          // sh "docker build -t my-dockerhub-user/my-app:${imageTag} ."

          // Build image with commit-sha tag and also tag as latest  
          sh """
            docker build -t ${env.REGISTRY}/${env.IMAGE_NAME}:${env.GIT_COMMIT_SHORT} .
            docker tag ${env.REGISTRY}/${env.IMAGE_NAME}:${env.GIT_COMMIT_SHORT} ${env.REGISTRY}/${env.IMAGE_NAME}:latest
            """
          env.IMAGE_FULL = "${env.REGISTRY}/${env.IMAGE_NAME}:${env.GIT_COMMIT_SHORT}"
          echo ' Docker image BUILT '    
          
          
          echo "✅ Built image: $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT} and tagged as latest"
          echo "✅ Built image: ${env.IMAGE_FULL} and tagged as latest"
        }
      }
    }

   /*  THIS WAY IS SCANNING OUR FINAL IMAGE FOR VULNERABILITIES
    - --exit-code 1 will fail the build if vulnerabilities of specified severity are found
    - You can add --format json or --template to generate reports. - For HTML reports, use Trivy’s template
    - Use double quotes around @html.tpl inside the shell string to avoid Groovy parsing issues.
    when we want to track severity only HIGH and CRITICAL we mention severity with --severity flag. flags like low/medium/high/critical etc so here we are ignoring all severity less than high.
    --no-progress flag will suppress the progress in the console-output log.
    --exit-code 1 means that if trivy finds anything in built docker image with any vulnerability i.e. in a case of High/Critical severity then return as failure.by default exit-code is always zero or 0
    so in case of failure i.e. vulnerability found with severity of HIGH/CRITICAL then return the exit-code 1 and thus the job will fail.
    --format template --template -o *.html flag is saving the result of Trivy scanning into the html file using format-template into the template of html.tpl. format Table is (default)
    --image flag tells to trivy-scan the docker-image
    --exit-code 1   # Exit with code 1 if vulnerabilities found
    --severity HIGH,CRITICAL   # Only scan/report high severity issues */
    stage('Trivy Scan - Docker Image') {
            steps {
              script {
                echo "🔍 Trivy Scan Docker Image Built For Vulnerabilities...with severity of HIGH/CRITICAL"

                // Option 2: Docker (uncomment if using containerized Trivy)
                // sh 'docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image --exit-code 1 --severity HIGH,CRITICAL $FULL_IMAGE'

                // Use Trivy container to scan image; it will fetch the image from local docker daemon
                // sh """
                // docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v \$(pwd):/work -w /work aquasec/trivy:latest image --exit-code 1 --severity HIGH,CRITICAL --no-progress --format template --template "@contrib/html.tpl" -o report-trivy-image.html ${env.IMAGE_FULL}
                // """

                sh "curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/html.tpl > html.tpl"
                //trivy image --format template --template "@html.tpl" -o report.html $IMAGE_NAME


                //sh "trivy image --exit-code 1 --severity HIGH,CRITICAL --no-progress ${env.IMAGE_TAG}"
                //sh "trivy image --exit-code 1 --severity HIGH,CRITICAL --no-progress ${env.LATEST_TAG}"

                // sh "trivy image --exit-code 1 --severity HIGH,CRITICAL --no-progress --format template --template "@html.tpl" -o report.html ${env.IMAGE_TAG}"
                // sh "trivy image --exit-code 1 --severity HIGH,CRITICAL --no-progress --format template --template "@html.tpl" -o report.html ${env.LATEST_TAG}"

                // sh "trivy image --exit-code 1 --severity HIGH,CRITICAL --no-progress --format template --template \"@html.tpl\" -o \"report-${env.IMAGE_TAG}.html\"  ${env.IMAGE_TAG}"
                // sh "trivy image --exit-code 1 --severity HIGH,CRITICAL --no-progress --format template --template \"@html.tpl\" -o report-trivy-docker-image-latest.html  $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT}"
                sh "trivy image --exit-code 1 --severity HIGH,CRITICAL --no-progress --format template --template \"@html.tpl\" -o report-trivy-docker-image-latest.html  ${env.IMAGE_FULL}"

                // Publish Trivy HTML report (for latest tag)
                publishHTML([
                  reportDir: '.',
                  reportFiles: 'report-trivy-docker-image-latest.html',
                  reportName: 'Trivy Final DockerImage Scan Security-Report'
                ])

                }
            }
    }
    
    // No retry/backoff for DockerHub push (network hiccups can fail builds).(RECOMMENDED FOR PRODUCTION)
    // This defines a named pipeline stage that focuses on pushing a built Docker image to DockerHub.
    // Inside the steps block, the script section allows you to run Groovy-based custom logic
    // DOCKER_CREDENTIALS_ID refers to the Jenkins credentials ID configured in Jenkins to store your DockerHub username/password or token. 
    // Push Docker Image to DockerHub stage using withDockerRegistry {} is more robust and secure for Windows environments:
    // - Cross-platform compatible: No piping issues like you’d face with --password-stdin.
    // Credentials handling — use withCredentials inside script blocks and reference credential IDs as strings or env values. Avoid printing secrets.
    stage('Push Docker Image to DockerHub') {
      steps {
        echo '🚀 Pushing Docker image... using withCredentials : usernamePassword Way'
        // inside withCredentials: withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID,........ use/try working credentialsId: env.DOCKER_CREDENTIALS_ID
        withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID,
                                          usernameVariable: 'DOCKER_USER',
                                          passwordVariable: 'DOCKER_PASS')]) {
            // Retry push a few times because network can be flaky
            retry(3) {                                            
            sh """
               echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
               docker push ${env.REGISTRY}/${env.IMAGE_NAME}:${env.GIT_COMMIT_SHORT}
               docker push ${env.REGISTRY}/${env.IMAGE_NAME}:latest
           """
           }
        }
        echo "🚀 Docker image $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT} pushed to DockerHub successfully!"
        echo "✅ Tagged as latest: $REGISTRY/$IMAGE_NAME:latest"
      }
    }
    
//     stage('Push Docker Image to DockerHub') {
//       steps {
//         echo '🔐 Logging in to DockerHub using withDockerRegistry...'
//         withDockerRegistry([credentialsId: DOCKER_CREDENTIALS_ID, url: '']) {
//         //sh "docker push ${env.IMAGE_TAG}"
//         sh "docker push ${env.LATEST_TAG}"
//       }
//       echo "🚀 Docker image ${env.IMAGE_TAG} pushed to DockerHub successfully!"
//       echo "✅ Tagged as latest: ${env.LATEST_TAG}"
//     }
//    }



//    stage('Trigger ManifestUpdate') {
//        steps {
//            script {
//                // Clone GitOps repo containing manifests
//                sh "git clone https://github.com/Pratheush/spring-taskapp-k8-manifests.git"
//                dir('manifests') {
//                    // Update Deployment manifest with new image tag
//                    sh """
//                    sed -i 's|image: my-dockerhub-user/my-app:.*|image: my-dockerhub-user/my-app:${env.BUILD_NUMBER}|g' deployment.yaml
//                    git config user.email 'jenkins@org.com'
//                    git config user.name 'Jenkins CI'
//                    git add deployment.yaml
//                    git commit -m 'Update image tag to ${env.BUILD_NUMBER}'
//                    git push https://github.com/org/k8s-manifests.git HEAD:main
//                    """
//                }
//            }
//        }
//    }



   stage('Trigger ManifestUpdate (GitOps)') {
    steps {
        script {
            echo "===== Starting Manifest Update Stage ====="
            echo '🔁 Updating GitOps manifests (create branch + PR)'
            try {
                // sshagent([...]): Temporarily loads SSH credentials (private key) so git/gh can authenticate to GitHub over SSH.
                // Use sshagent with registered SSH key that has push rights to the manifest repo
                // Ensure credential id 'github-ssh-key' exists and corresponds to the private SSH key
                sshagent(['github-ssh-key']) {

                    // Detect target branch dynamically (main or master)
                    // trim(): Removes trailing newline. Result is the base branch to target for the PR(pull-request)
                    def TARGET_BRANCH = sh(
                        // Lists remote branches for the manifest repo.If “main” exists, output “main”; otherwise output “master”.
                        script: "git ls-remote --heads ${MANIFEST_REPO} | grep main >/dev/null && echo main || echo master",
                        returnStdout: true
                    ).trim()

                    echo "Detected Kubernetes manifest branch: ${TARGET_BRANCH}"

                    // Clean clone of manifest repo
                    // Ensures any previous clone is removed, making a clean workspace
                    // git clone -b TARGET_BRANCH ... manifests: Clones the manifest repo and checks out the base branch into the “manifests” directory.
                    sh """
                        rm -rf ${MANIFEST_REPO_DIR}
                        git clone -b ${TARGET_BRANCH} ${MANIFEST_REPO} ${MANIFEST_REPO_DIR}
                    """

                    // dir(): Changes the working directory to the cloned repo so subsequent git/shell commands operate inside it.
                    dir(MANIFEST_REPO_DIR) {

                        // Update all deployment YAMLs with new image tag
                        // for file in $(grep -rl 'image:' ./):
                        // grep -r: Recursively searches for lines containing “image:” starting at current dir.
                        // -l: Prints only filenames that match. Loop: Iterates over each file found.
                        // sed -i 's|image: .|image: <new>|g' $file:* : sed -i: In-place edit each file.
                        // sed -i 's|image: .*|image: ${DOCKER_IMAGE_NAME}:${GIT_COMMIT_SHORT}|g' \$file  : Replaces any line starting with “image: ” followed by anything with the exact new image reference, including the commit tag.
                        // Result: Every “image:” line becomes, for example, “image: pratheush/spring-taskapp-jenkins-k8-cicd:a1b2c3d”.
                        // USE sed... OR yq e -i '.spec.template.spec.containers[0].image = "${DOCKER_IMAGE_NAME}:${GIT_COMMIT_SHORT}"' taskdeployment.yaml
                        sh """
                            echo "Updating ALL deployment YAMLs..."
                            for file in \$(grep -rl 'image:' ./); do
                                echo "Updating image in: \$file"
                                sed -i 's|image: .*|image: ${DOCKER_IMAGE_NAME}:${GIT_COMMIT_SHORT}|g' \$file
                            done
                        """

                        // Detect changes
                        // git status --porcelain: Outputs a machine-friendly list of changed files. Empty means no changes.
                        // if (!CHANGES): If nothing changed (e.g., files already had the same image tag), log and exit early to avoid creating a no-op PR.
                        // sh(script: ..., returnStdout: true) runs the shell command inside Jenkins and returns the text output..trim() removes extra spaces or newlines
                        // SO EXAMPLE : IF WE CHANGED A FILE : manifest.yaml then : If there are changes → CHANGES will contain something like "M manifest.yaml".
                        // If no changes → CHANGES will be an empty string "".
                        // if (!CHANGES) means: if there are no changes. In Groovy, an empty string is considered falsey.
                        // If CHANGES is empty → it prints "No manifest changes detected. Skipping commit/push." and stops (return).
                        // If CHANGES has content → the pipeline continues (commit/push happens later).
                        def CHANGES = sh(script: "git status --porcelain", returnStdout: true).trim()
                        if (!CHANGES) {
                            echo "No manifest changes detected. Skipping commit/push."
                            return
                        }

                        // Git identity
                        // git config: Sets committer identity so the commit is properly attributed.
                        sh """
                            git config user.email "jenkins@company.com"
                            git config user.name "Jenkins CI Pratheush"
                        """

                        // Create PR branch
                        // PR_BRANCH: Names a feature branch uniquely by commit SHA (e.g., update-image-a1b2c3d).
                        // git checkout -b: Creates and switches to that new branch.
                        def PR_BRANCH = "update-image-${GIT_COMMIT_SHORT}"
                        sh "git checkout -b ${PR_BRANCH}"

                        // Commit changes
                        // git add .: Stages all modified files (the YAMLs).
                        // git commit -m: Creates a commit with a clear message including the new image tag.
                        sh """
                            git add .
                            git commit -m "Update image tag to ${DOCKER_IMAGE_NAME}:${GIT_COMMIT_SHORT} (Automated by Jenkins)"
                        """

                        // Push branch
                        // git push origin PR_BRANCH: Sends the new branch to GitHub using the SSH credentials from sshagent.
                        sh "git push origin ${PR_BRANCH}"
                        echo "Branch ${PR_BRANCH} pushed successfully."

  
                        // Create Pull Request via GitHub CLI

                        // This PR is pushing changes from Jenkins’ local repo copy to the remote GitHub repo. The head branch is the new feature branch 
                        // you pushed to the remote, and the base branch is the existing branch on the remote repo that you want to merge into.
                        // You create a Pull Request (PR): The GitHub CLI (gh pr create) tells GitHub: “Take the branch I just pushed (--head)”: “Compare it against the main/master branch (--base)”: “Open a PR so humans can review and merge.”
                        // The pull request is always between two remote branches (your new head branch vs the base branch).
                        // every pipeline run creates a new feature branch (named with the commit SHA).It starts locally in Jenkins → then gets pushed to GitHub remote.
                        // The pull request is opened between: Head branch (new branch you just pushed to remote) 
                        // Base branch (main/master on remote)

                        // git checkout -b update-image-<commitSHA> : At this point, it(new-feature-branch) only exists inside Jenkins’ local clone.
                        // git push origin update-image-<commitSHA> : That command uploads the branch to the remote GitHub repository. Now GitHub knows about this branch.
                        // gh pr create --head update-image-<commitSHA> --base main : GitHub interprets: 1. Head branch: the new branch you just pushed to the remote repo (your feature branch). 2. Base branch: the existing branch on the remote repo you want to merge into (usually main or master).

                        // The new feature branch that Jenkins creates locally becomes the head branch at the remote repo once it’s pushed. The pull request is always between two remote branches:
                        // Head = your new feature branch (remote)
                        // Base = the target branch (remote)

                        // gh pr create: Uses GitHub CLI to open a PR.
                        // MANIFEST_REPO = "https://github.com/Pratheush/spring-taskapp-k8-manifests.git"
                        // MANIFEST_REPO_SSH = "git@github.com:Pratheush/spring-taskapp-k8-manifests.git"
                        // --repo: Converts the full URL to “owner/repo” format (e.g., Pratheush/spring-taskapp-k8-manifests).
                        // --head: Sets the source branch (your new feature branch).
                        // --base: Sets the target branch (main/master detected earlier).                     
                        // --title/--body: Provides a clear PR title and description for reviewers.
                        // FOR HTTPS URL EXTRACTION IS DIFFERENT SO REPLACE CODE DIFFER LITTLE BIT
                        // sh """
                        //     gh pr create \
                        //         --repo ${MANIFEST_REPO.replace('https://github.com/', '').replace('.git','')} \
                        //         --head ${PR_BRANCH} \
                        //         --base ${TARGET_BRANCH} \
                        //         --title "Update image to ${DOCKER_IMAGE_NAME}:${GIT_COMMIT_SHORT}" \
                        //         --body "Automated PR created by Jenkins pipeline to update Kubernetes manifests."
                        // """
                        // FOR SSH URL EXTRACTION IS DIFFERENT SO REPLACE CODE DIFFER LITTLE BIT
                        sh """
                            gh pr create \
                                --repo ${MANIFEST_REPO.replace('git@github.com:', '').replace('.git','')} \
                                --head ${PR_BRANCH} \
                                --base ${TARGET_BRANCH} \
                                --title "Update image to ${DOCKER_IMAGE_NAME}:${GIT_COMMIT_SHORT}" \
                                --body "Automated PR created by Jenkins pipeline to update Kubernetes manifests."
                        """

                        echo "Pull Request created for branch: ${PR_BRANCH}"
                    }
                }
            } 
            // ❗6. Error handling: you set UNSTABLE then throw e → Jenkins marks FAILURE
            //     UNSTABLE is overridden by failure.
            //     If you want “unstable but continue”, do NOT throw.
            //     Fix:
            //     catch (Exception e) {
            //      echo "❌ Manifest update failed: ${e.message}"
            //     currentBuild.result = 'UNSTABLE'
            //     return
            //     }
            catch (Exception e) {
                // catch (Exception e): Catches any failure during the stage, marks the build as UNSTABLE, logs the error message, and rethrows to surface the failure.
                
                echo "❌ Manifest update failed: ${e.message}"
                currentBuild.result = 'UNSTABLE'
                throw e
            }

            echo "===== Manifest Update Completed Successfully ====="
        }
    }
}

   
   stage('Clean Docker Images (optional)') {
      when {
        expression { params.CLEANUP_ENABLED }
      }
      steps {
        echo "🧹 Cleaning up local Docker images as cleanup is enabled..."
        script {
          def status1 = sh(script: "docker rmi $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT}", returnStatus: true)
          def status2 = sh(script: "docker rmi $REGISTRY/$IMAGE_NAME:latest", returnStatus: true)
          echo "🧹 Cleanup status: IMAGE_TAG=${status1}, LATEST_TAG=${status2}"

        // FOR PRODUCTION RECOMMENDED docker rmi may fail if containers are running.
        // Use || true to never fail pipeline on rmi errors (e.g., image in use)
          sh "docker rmi ${env.REGISTRY}/${env.IMAGE_NAME}:${env.GIT_COMMIT_SHORT} || true"
          sh "docker rmi ${env.REGISTRY}/${env.IMAGE_NAME}:latest || true"
        }
      }
   }

    
  }

  // Uses cleanWs() in post block — clean workspace ensures clean builds
  // cleanWs() is a predefined method provided by the Workspace Cleanup Plugin in Jenkins. It’s specifically designed for use in Declarative Pipelines, and you’re using it perfectly in the post section to clean up the workspace after every build.
  // Define post-build actions, e.g., send notifications. 
  // Plugin Name: Pipeline: Basic Steps
  // Functionality Provided: archiveArtifacts, deleteDir, stash, unstash, and other essential pipeline steps
  post {
    always {
      echo '🧾 Archiving reports and test results'
      // Archive JUnit test reports  
      // Publish JUnit results (if any). This will not fail if no files found.
      // junit '**/surefire-reports/*.xml, **/failsafe-reports/*.xml'
      junit allowEmptyResults: true, testResults: '**/surefire-reports/*.xml, **/failsafe-reports/*.xml'
      
      // recordCoverage requires the Coverage plugin. Ensure it’s installed.
      // Record Jacoco coverage reports
      // jacoco() is not available method since jacoco plugin is not installed in jenkins as jacoco plugin is depracted
      // recordCoverage tools: [jacoco()]
      // publishCoverage adapters: [jacocoAdapter('target/jacoco-report/jacoco.xml')], sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
      recordCoverage(tools: [[parser: 'JACOCO', pattern: 'target/jacoco-report/jacoco.xml']], sourceDirectories: [[path: 'src/main/java']])

      // Optional: record coverage if plugin exists and report exists
      // recordCoverage(tool: 'JaCoCo', pattern: 'target/jacoco-report/jacoco.xml')  // enable if you have coverage plugin

      // Archive all Trivy HTML reports 
      // report-${env.IMAGE_TAG}.html,  report-${env.LATEST_TAG}.html
      // archiveArtifacts artifacts: '**/trivy-report*.json', onlyIfSuccessful: true
      // archiveArtifacts artifacts: '**/report-${env.IMAGE_TAG}.html', onlyIfSuccessful: true
      // archiveArtifacts artifacts: '**/report-${env.LATEST_TAG}.html', onlyIfSuccessful: true
      // archiveArtifacts artifacts: 'report-*.html', onlyIfSuccessful: false
      archiveArtifacts artifacts: 'report-*.html', allowEmptyArchive: true


      echo "🏁 Cleaning Workspace."
      cleanWs()
      echo "🏁 Pipeline execution finished."
      // You can add more post-build actions here, e.g., email notifications, Slack messages.
      // For example:
      // mail to: 'your-email@example.com',
      //      subject: "Jenkins Build ${currentBuild.displayName}: ${currentBuild.currentResult}",
      //      body: "Build ${currentBuild.displayName} (${env.BUILD_URL}) finished with status ${currentBuild.currentResult}"
    }
    success {
      echo '✅ Pipeline executed successfully.'
      echo '🚀 Docker image successfully built and pushed!'
    }
    unstable {
      echo "⚠️ Build is unstable - check warnings/quality gates"
    }
    failure {
      // ❌ No notifications (Slack/email) for failures.(RECOMMENDED FOR PRODUCTION)  
      echo "❌ CI/CD Pipeline failed! See console log for details."
    }
  }
}
