REQUIREMENTS :
JENKINS RUNNING IN UBUNTU IN WSL2 AS SERVICE
SONARQUBE RUNNING IN UBUNTU IN WSL2 AS SERVICE
POSTGRESQL RUNNING IN UBUNTU IN WSL2 AS SERVICE
TRIVY IS INSTALLED IN UBUNTU (VULNERABILITY DB JAVA DB ARE REFRESHED AND COPIED TO SEPARATE FOLDER AT HOME )

Plugins & prerequisites — I list plugins you must have: Workspace Cleanup, HTML Publisher, SonarQube Scanner plugin, Git plugin, GitHub plugin/ssh-agent, Pipeline Utility Steps, JaCoCo / coverage plugin (if used), Trivy installed or use container, publishHTML plugin.

make sure gh is installed in jenkins running node i.e. ubuntu on wsl2
make sure "sudo snap install yq" is installed in jenkins running node i.e. ubuntu on wsl2

also configure github credentials and sonarqube credentials as token in jenkins
configure sonarqube quality gate too with ngrok jenkins link to sonarqube inside sonarqube.

VERIFY REQUIREMENTS:
```
# VULNERABILITY DB & JAVA DB ARE REFRESHED AND COPIED TO SEPARATE FOLDER
pratheush@Lord-Shiva:/mnt/c/Users/prath/trivy-jv-db$ ls
db  fanal  java-db

# verifying trivy installed in ubuntu in wsl2
pratheush@Lord-Shiva:/mnt/c/Users/prath/trivy-jv-db$ trivy --version
Version: 0.67.2
Vulnerability DB:
  Version: 2
  UpdatedAt: 2025-11-29 00:32:53.451729707 +0000 UTC
  NextUpdate: 2025-11-30 00:32:53.451729426 +0000 UTC
  DownloadedAt: 2025-11-29 02:02:34.289507375 +0000 UTC
Java DB:
  Version: 1
  UpdatedAt: 2025-11-29 00:56:54.466709448 +0000 UTC
  NextUpdate: 2025-12-02 00:56:54.466709208 +0000 UTC
  DownloadedAt: 2025-11-29 02:11:32.771302717 +0000 UTC

# verify jenkins inside ubuntu in wsl2 as service   
pratheush@Lord-Shiva:/mnt/c/Users/prath/trivy-jv-db$ jenkins --version
2.528.2

# login to jenkins to check connection from jenkins to github through ssh
pratheush@Lord-Shiva:/mnt/c/Users/prath/trivy-jv-db$ sudo su - jenkins
[sudo] password for pratheush:
Welcome to Ubuntu 22.04.5 LTS (GNU/Linux 6.6.87.2-microsoft-standard-WSL2 x86_64)

 * Documentation:  https://help.ubuntu.com
 * Management:     https://landscape.canonical.com
 * Support:        https://ubuntu.com/pro

 System information as of Sat Nov 29 11:10:45 IST 2025

  System load:  0.52                Processes:             96
  Usage of /:   1.1% of 1006.85GB   Users logged in:       2
  Memory usage: 91%                 IPv4 address for eth0: 172.26.238.72
  Swap usage:   100%

 * Strictly confined Kubernetes makes edge and IoT secure. Learn how MicroK8s
   just raised the bar for easy, resilient and secure K8s cluster deployment.

   https://ubuntu.com/engage/secure-kubernetes-at-the-edge

This message is shown once a day. To disable it please create the
/var/lib/jenkins/.hushlogin file.
jenkins@Lord-Shiva:~$

# checking ssh connection from jenkins to github via SSH
jenkins@Lord-Shiva:~$ ssh -T git@github.com
Hi Pratheush! You've successfully authenticated, but GitHub does not provide shell access.
jenkins@Lord-Shiva:~$



# CHECKING JENKINS SERVICE SONAR POSTGRESQL SERVICE IN UBUNTU IN WSL2
pratheush@Lord-Shiva:~$ service sonar status
● sonar.service - SonarQube service
     Loaded: loaded (/etc/systemd/system/sonar.service; enabled; vendor preset: enabled)
     Active: active (running) since Fri 2025-11-28 16:13:15 IST; 18h ago
   Main PID: 366 (java)
      Tasks: 174 (limit: 4689)
     Memory: 1.0G
        CPU: 10min 45.076s
     CGroup: /system.slice/sonar.service
             ├─ 366 java -Xms8m -Xmx32m --add-exports=java.base/jdk.internal.ref=ALL-UNNAMED --add-opens=java.base/java.la>
             ├─ 535 /usr/lib/jvm/java-21-openjdk-amd64/bin/java -Xms4m -Xmx64m -XX:+UseSerialGC -Dcli.name=server -Dcli.sc>
             ├─ 856 /usr/lib/jvm/java-21-openjdk-amd64/bin/java -Des.networkaddress.cache.ttl=60 -Des.networkaddress.cache>
             ├─1037 /usr/lib/jvm/java-21-openjdk-amd64/bin/java -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djava.io.t>
             └─1128 /usr/lib/jvm/java-21-openjdk-amd64/bin/java -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djava.io.t>

Nov 28 16:13:15 Lord-Shiva systemd[1]: Starting SonarQube service...
Nov 28 16:13:15 Lord-Shiva systemd[1]: Started SonarQube service.
pratheush@Lord-Shiva:~$ service postgresql status
● postgresql.service - PostgreSQL RDBMS
     Loaded: loaded (/lib/systemd/system/postgresql.service; enabled; vendor preset: enabled)
     Active: active (exited) since Fri 2025-11-28 16:13:19 IST; 18h ago
   Main PID: 528 (code=exited, status=0/SUCCESS)
        CPU: 1ms

Nov 28 16:13:19 Lord-Shiva systemd[1]: Starting PostgreSQL RDBMS...
Nov 28 16:13:19 Lord-Shiva systemd[1]: Finished PostgreSQL RDBMS.
pratheush@Lord-Shiva:~$ service jenkins status
● jenkins.service - Jenkins Continuous Integration Server
     Loaded: loaded (/lib/systemd/system/jenkins.service; enabled; vendor preset: enabled)
     Active: active (running) since Fri 2025-11-28 16:14:14 IST; 18h ago
   Main PID: 299 (java)
      Tasks: 47 (limit: 4689)
     Memory: 585.9M
        CPU: 3min 10.352s
     CGroup: /system.slice/jenkins.service
             └─299 /usr/bin/java -Djava.awt.headless=true -jar /usr/share/java/jenkins.war --webroot=/var/cache/jenkins/wa>

Nov 29 10:33:07 Lord-Shiva jenkins[299]:         at PluginClassLoader for kubernetes-credentials-provider//com.cloudbees.j>
Nov 29 10:33:07 Lord-Shiva jenkins[299]:         at hudson.triggers.SafeTimerTask.run(SafeTimerTask.java:92)
Nov 29 10:33:07 Lord-Shiva jenkins[299]:         at jenkins.security.ImpersonatingScheduledExecutorService$1.run(Impersona>
Nov 29 10:33:07 Lord-Shiva jenkins[299]:         at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executor>
Nov 29 10:33:07 Lord-Shiva jenkins[299]:         at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317)
Nov 29 10:33:07 Lord-Shiva jenkins[299]:         at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFu>
Nov 29 10:33:07 Lord-Shiva jenkins[299]:         at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPool>
Nov 29 10:33:07 Lord-Shiva jenkins[299]:         at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoo>
Nov 29 10:33:07 Lord-Shiva jenkins[299]:         at java.base/java.lang.Thread.run(Thread.java:1583)
Nov 29 10:33:07 Lord-Shiva jenkins[299]: 2025-11-29 05:03:07.050+0000 [id=42]        INFO        c.c.j.p.k.KubernetesCrede>



ACCESS JENKINS SONARQUBE ON WEB-BROWSER:
http://localhost:8080/
jenkins username : pratheush
jenkins password : pratheush123


http://localhost:9000/admin/permissions
sonarqube username : admin
sonarqube password : Pratheush@123


```


IN PRODUCTION READY JENKINSFILE PIPELINE ::
⚙️ Part 2: Is this Production Ready?
❌ GIT_COMMIT_SHORT SHOULD BE defined in STAGE LIKE CHECKOUT STAGE INSIDE SCRIPT AND SET env.GIT_COMMIT_SHORT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim().
❌ SonarQube Quality Gate misconfigured.
❌ Manifest update too aggressive (could break YAML).
❌ No retry/backoff for DockerHub push (network hiccups can fail builds).
❌ No resource limits or agent labels (production pipelines should run on controlled agents).
❌ No notifications (Slack/email) for failures.
❌ No parallelization (tests, scans could run in parallel to save time).



👍. Let’s walk through a production-ready Jenkins pipeline that builds your app, scans it, pushes the Docker image, updates the Kubernetes manifest in GitHub, and lets ArgoCD automatically deploy it.


🔄 Pipeline Flow Overview
Your stages are well-structured. The manifest update should happen after the Docker image is built and pushed to DockerHub, because only then do you know the new image tag to update in the Kubernetes manifest.

So the correct order is:
1. Checkout
2. Security scans (Trivy filesystem, SonarQube, Quality Gate)
3. Build & Test App
4. Build Docker Image
5. Trivy Scan Docker Image
6. Push Docker Image to DockerHub
7. Trigger ManifestUpdate (update YAML in GitHub repo) ← this is where ArgoCD picks it up


🏗️ Stage-by-Stage Explanation
1. Checkout
```
stage('Checkout') {
    steps {
        git branch: 'main', url: 'https://github.com/org/app-repo.git'
    }
}
```
Pulls source code from GitHub.
Includes both application code and Kubernetes manifests (often kept in a separate GitOps repo).

2. Trivy Scan - Filesystem - Maven Dependencies
```
stage('Trivy Scan - Filesystem - Maven Dependencies') {
    steps {
        sh 'trivy fs --exit-code 1 --severity HIGH,CRITICAL .'
    }
}
```
Scans your source code and dependencies for vulnerabilities.
Ensures no critical CVEs before build.

3. Build & Test App
```
stage('Build & Test App') {
    steps {
        sh 'mvn clean install'
    }
}
```
Compiles the app, runs unit tests.
Produces JAR/WAR or other build artifacts.

4. SonarQube Analysis
```
stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv('SonarQubeServer') {
            sh 'mvn sonar:sonar'
        }
    }
}
```
Static code analysis for quality and security.

5. Quality Gate
```
stage("Quality Gate") {
    steps {
        timeout(time: 5, unit: 'MINUTES') {
            waitForQualityGate abortPipeline: true
        }
    }
}
```
Ensures SonarQube quality gate passes before proceeding.

6. Build Docker Image
```
stage('Build Docker Image') {
    steps {
        script {
            def imageTag = "my-app:${env.BUILD_NUMBER}"
            sh "docker build -t my-dockerhub-user/my-app:${imageTag} ."
        }
    }
}
```
Builds Docker image with unique tag (e.g., build number or git commit SHA).

7. Trivy Scan - Docker Image
```
stage('Trivy Scan - Docker Image') {
    steps {
        sh "trivy image --exit-code 1 --severity HIGH,CRITICAL my-dockerhub-user/my-app:${env.BUILD_NUMBER}"
    }
}
```
Scans built Docker image for vulnerabilities.


8. Push Docker Image to DockerHub
```
stage('Push Docker Image to DockerHub') {
    steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
            sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
            sh "docker push my-dockerhub-user/my-app:${env.BUILD_NUMBER}"
        }
    }
}
```
Pushes image to DockerHub.
Now you have a new version available for deployment.


9. Trigger ManifestUpdate (GitOps)
This is the critical stage where Jenkins updates the Kubernetes manifest repo (the GitOps repo watched by ArgoCD).
```
stage('Trigger ManifestUpdate') {
    steps {
        script {
            // Clone GitOps repo containing manifests
            sh "git clone https://github.com/org/k8s-manifests.git"
            dir('k8s-manifests') {
                // Update Deployment manifest with new image tag
                sh """
                sed -i 's|image: my-dockerhub-user/my-app:.*|image: my-dockerhub-user/my-app:${env.BUILD_NUMBER}|g' deployment.yaml
                git config user.email 'jenkins@org.com'
                git config user.name 'Jenkins CI'
                git add deployment.yaml
                git commit -m 'Update image tag to ${env.BUILD_NUMBER}'
                git push https://github.com/org/k8s-manifests.git HEAD:main
                """
            }
        }
    }
}
```
What happens here:
1. Jenkins clones the GitOps repo (separate from app source repo).
2. Updates the Deployment manifest with the new Docker image tag.
3. Commits and pushes the change.

ArgoCD is configured to watch this GitOps repo. Once the manifest changes, ArgoCD automatically syncs and deploys the new version into Kubernetes.



🔗 Production-Ready Flow
1. Developer commits code → Jenkins pipeline runs.
2. Jenkins builds, tests, scans, and pushes Docker image.
3. Jenkins updates GitOps repo with new image tag.
4. ArgoCD detects manifest change → deploys new version to cluster.

📊 Example Flow in Practice
1. App repo: github.com/org/app-repo
2. GitOps repo: github.com/org/k8s-manifests
3. DockerHub repo: dockerhub.com/my-dockerhub-user/my-app
4. ArgoCD watches org/k8s-manifests → auto-syncs to cluster.

✅ Answer to your key question: The manifest update should be done in the final stage (Trigger ManifestUpdate) after pushing the Docker image. Jenkins updates the GitOps repo, and ArgoCD handles deployment automatically.

======================================================================

🔑 Do You Need Two GitHub Repositories?
Yes, in most production setups you use two repositories:

1. Application Source Repo

    * Contains your app code (Java, Spring Boot, etc.).
    * Jenkins checks out this repo, builds, tests, scans, and produces a Docker image.
    * Example: github.com/org/app-repo.

2. GitOps / Manifests Repo
    * Contains only Kubernetes YAML manifests (Deployments, Services, Ingress, etc.).
    * Jenkins updates this repo with the new Docker image tag after pushing to DockerHub.
    * ArgoCD continuously watches this repo and syncs changes to the cluster.
    * Example: github.com/org/k8s-manifests.
    

    
🧩 Why Two Repos?
1. Separation of concerns:
    * Developers focus on application code.
    * Ops/ArgoCD focuses on manifests and cluster state.
2. Security: ArgoCD only needs read access to the manifests repo, not the app repo.
3. Auditability: Every deployment change is a Git commit in the manifests repo.
4. Rollback: Easy to revert to a previous manifest commit.


⚙️ Alternative (Single Repo)
1. You can keep both code and manifests in one repo.
2. Jenkins would update manifests in the same repo.
3. ArgoCD would watch a specific folder (e.g., /manifests).
4. Downside: Mixing app code and cluster config makes auditing and RBAC harder.
👉 In production-ready GitOps, teams almost always split into two repos.


🔗 Example Flow with Two Repos
1. Developer pushes code → app-repo.
2. Jenkins pipeline runs: build, test, scan, push Docker image.
3. Jenkins updates k8s-manifests repo with new image tag in Deployment YAML.
4. ArgoCD detects manifest change → deploys new version to Kubernetes.


✅ Answer: Yes — the recommended production-ready flow uses two GitHub repositories: one for application code and one for Kubernetes manifests. Jenkins bridges them by updating the manifests repo after pushing the Docker image, and ArgoCD handles deployment automatically.


***
***
***





✅ IMPORTANT CONCEPT FIRST — Where to update manifests?

You should update your Kubernetes manifest AFTER:
1. App is built
2. Security scans passed
3. Docker image is built
4. Docker image is pushed to DockerHub

👉 THEN update the manifest file with the new image tag.
👉 Commit & push to the GitOps manifest repo.
👉 ArgoCD sees the Git change → pulls → deploys.


⭐ Manifest update happens in → stage('Trigger ManifestUpdate') (towards the end)



✅ Production Jenkins Pipeline Flow (GitOps Ready)
Stage 1: Checkout
Pulls the application source code.

Stage 2: Trivy Scan - Filesystem / Maven Dependencies
Scans code and dependency vulnerabilities.

Stage 3: Build & Test App
Runs mvn clean package.

Stage 4: SonarQube Analysis
Sends code to SonarQube server.

Stage 5: Quality Gate
Fails the pipeline if quality gate fails.

Stage 6: Build Docker Image
Builds Docker image using app artifact.

Stage 7: Trivy Scan - Docker Image
Scans container image for vulnerabilities.

Stage 8: Push Docker Image to DockerHub
Pushes image with two tags:
    * short Git commit hash: myapp:abcd123
    * latest

Stage 9: Trigger ManifestUpdate (GitOps) — MOST IMPORTANT
This stage will:
✔ Clone manifest repo (not the application repo)
✔ Update deployment.yaml image tag
✔ Commit
✔ Push back to GitHub
✔ ArgoCD will automatically detect it



🚀 FULL PRODUCTION-READY JENKINSFILE (GitOps Version)

Everything here is correct for real-world CI/CD + GitOps workflows.
```
pipeline {
    agent any

    environment {
        REGISTRY = "yourdockerhubusername"
        IMAGE_NAME = "my-springboot-app"
        GIT_COMMIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        MANIFEST_REPO = "git@github.com:your-org/k8s-manifests.git"
        MANIFEST_REPO_DIR = "manifests"
        DEPLOYMENT_FILE = "deployment.yaml"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Trivy Scan - Filesystem - Maven Dependencies') {
            steps {
                sh """
                trivy fs --exit-code 0 .
                trivy fs --exit-code 1 --severity HIGH,CRITICAL .
                """
            }
        }

        stage('Build & Test App') {
            steps {
                sh "mvn clean package -DskipTests=false"
            }
        }

        stage('SonarQube Analysis') {
            environment {
                SONAR_SCANNER_OPTS = "-Xmx1024m"
            }
            steps {
                withSonarQubeEnv("SonarQubeServer") {
                    sh """
                    mvn sonar:sonar \
                       -Dsonar.projectKey=myapp \
                       -Dsonar.host.url=\$SONAR_HOST_URL \
                       -Dsonar.login=\$SONAR_AUTH_TOKEN
                    """
                }
            }
        }

        stage("Quality Gate") {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                docker build -t $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT} .
                docker tag $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT} $REGISTRY/$IMAGE_NAME:latest
                """
            }
        }

        stage('Trivy Scan - Docker Image') {
            steps {
                sh """
                trivy image --exit-code 1 --severity HIGH,CRITICAL $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT}
                """
            }
        }

        stage('Push Docker Image to DockerHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    sh """
                    docker login -u $USER -p $PASS
                    docker push $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT}
                    docker push $REGISTRY/$IMAGE_NAME:latest
                    """
                }
            }
        }

        stage('Trigger ManifestUpdate') {
            steps {
                sshagent(['github-ssh-key']) {
                    sh """
                    rm -rf $MANIFEST_REPO_DIR
                    git clone $MANIFEST_REPO $MANIFEST_REPO_DIR

                    cd $MANIFEST_REPO_DIR

                    # Update image tag in Kubernetes Deployment manifest
                    sed -i 's|image: .*|image: $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT}|g' $DEPLOYMENT_FILE

                    git add $DEPLOYMENT_FILE
                    git commit -m "Update image to $IMAGE_NAME:${GIT_COMMIT_SHORT}"
                    git push origin main
                    """
                }
            }
        }
    }
}

```



📦 Example deployment.yaml before update
```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  replicas: 2
  template:
    spec:
      containers:
        - name: myapp
          image: yourdockerhubusername/my-springboot-app:latest
```


After Jenkins updates deployment.yaml
```
image: yourdockerhubusername/my-springboot-app:ab12cd
```


🎯 What Happens Next (ArgoCD)
ArgoCD (configured already) watches your manifest repo:
```
Repo: https://github.com/your-org/k8s-manifests
Path: /myapp
Sync Policy: Automated (optional)
```
As soon as Jenkins pushes commit → ArgoCD detects → syncs → Kubernetes deployment updated → rollout begins.


🧠 Summary (Easy To Remember)
CI (Jenkins)
Build → Scan → Build Image → Push Image → Update Manifests → Push manifest commit

CD (ArgoCD)
ArgoCD detects manifest change → deploys automatically.





--------------------------------------------------------------
--------------------------------------------------------------


🟩 Do You Need Two Jenkinsfiles?
1. Option A (Most common in production) → One Jenkinsfile in the application repo only.
    * This Jenkinsfile handles the full CI/CD pipeline: build, test, scan, push Docker image, and finally update the manifests repo.
    * The update step is just a git clone of the manifests repo, sed/yq to patch the image tag, commit, and push.
    * No Jenkinsfile is needed inside the manifests repo because Jenkins is not building anything there — it only commits changes.

2. Option B (Less common, but possible) → Two Jenkinsfiles:
    * App repo Jenkinsfile → Build/test/scan/push Docker image.
    * Manifests repo Jenkinsfile → Triggered by Jenkins (or webhook) to update manifests.
    * This adds complexity because you now orchestrate two pipelines.
    * Usually avoided unless you want strict separation of CI (app) and CD (manifests).

👉 In most production GitOps setups, teams stick to one Jenkinsfile in the app repo and let Jenkins update the manifests repo directly.


Two Github Repository : 
1. Application repo for code 
2. Gitops repo for Kubernetes manifests
Jenkins updates the GitOps repo.
ArgoCD sees the commit → syncs → deploys new version.



✅ Answer to your question:
* You need two GitHub repositories (app + manifests).
* You usually need only one Jenkinsfile (in the app repo).
* That Jenkinsfile updates the manifests repo(GitOps repo) in the final stage.
* ArgoCD then handles deployment automatically.


+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


🔶 STAGE 1 (BEST)
SSH-agent + secure SSH cloning + clean scripting
🟦 Version 1 – SSH Agent with GitHub SSH Key
```
stage('Trigger ManifestUpdate') {
    steps {
        sshagent(['github-ssh-key']) {
            sh """
            rm -rf $MANIFEST_REPO_DIR
            git clone $MANIFEST_REPO $MANIFEST_REPO_DIR

            cd $MANIFEST_REPO_DIR

            sed -i 's|image: .*|image: $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT}|g' $DEPLOYMENT_FILE

            git add $DEPLOYMENT_FILE
            git commit -m "Update image to $IMAGE_NAME:${GIT_COMMIT_SHORT}"
            git push origin main
            """
        }
    }
}
```
🔍 Steps Explained
* sshagent(['github-ssh-key']) → Uses Jenkins‑stored SSH key to authenticate with GitHub.
    * Jenkins loads your GitHub SSH private key safely
    * No password in logs
    * Most secure corporate method
* rm -rf $MANIFEST_REPO_DIR → Cleans any old repo clone.
    * Ensures fresh manifests repo every time
    * No leftover files risk
* git clone $MANIFEST_REPO $MANIFEST_REPO_DIR → Clones the manifests repo.
* sed -i 's|image: .*|image: repo/app:tag|g' deployment.yaml → Updates the image tag in the Deployment YAML using commit SHA (${GIT_COMMIT_SHORT}).
    * Replaces the image tag in manifest
    * Clean & predictable pattern
* git add, git commit, git push origin main → Commits and pushes the change via SSH.
    * Secure push with SSH
    * No credentials exposed
    * Works reliably with GitHub/Bitbucket/GitLab

✅ Pros
* Secure (SSH keys are safer than embedding passwords).
* Clean repo handling (removes old clone).
* Uses commit SHA → traceable to exact code version.
✔ Most secure (SSH keys)
✔ Cleanest code (simple & readable)
✔ Most commonly used in production banking, enterprise, regulated industries
✔ NO credentials exposed in logs
✔ Exactly matches GitOps best practices

❌ Cons
* Slightly verbose.
* Requires SSH key management in Jenkins.



---

🔶 STAGE 2 (OK but not ideal)
HTTPS clone + username/password embedded in push command
🟩 Version 2 – HTTPS Push with Jenkins Credentials
```
stage('Trigger ManifestUpdate') {
    steps {
        script {
            sh "git clone https://github.com/org/k8s-manifests.git"
            dir('k8s-manifests') {
                sh """
                sed -i 's|image: my-dockerhub-user/my-app:.*|image: my-dockerhub-user/my-app:${env.BUILD_NUMBER}|g' deployment.yaml
                git config user.email 'jenkins@org.com'
                git config user.name 'Jenkins CI'
                git add deployment.yaml
                git commit -m 'Update image tag to ${env.BUILD_NUMBER}'
                git push https://github.com/org/k8s-manifests.git HEAD:main
                """
            }
        }
    }
}
```
🔍 Steps Explained
* git clone https://github.com/org/k8s-manifests.git → Clones manifests repo via HTTPS. Works but requires proper authentication later.
* sed -i ... → Updates image tag with Jenkins build number Update the manifest using sed. Good, same logic as Stage 1.
* git config user.email/name → Sets commit identity. Manually configure Git username/email. Required because HTTPS clone doesn’t inherit SSH identity.
* git add, git commit, git push → Pushes changes via HTTPS. Requires credentials configured in pipeline environment. Credentials may be leaked if logging not suppressed

✅ Pros
* Simpler, easy to read.
* Uses Jenkins build number → easy to correlate with CI pipeline.
* Works with HTTPS credentials (common in enterprise setups).
✔ Works without SSH keys
✔ Good for small teams or simple setups
✔ Safe if GitHub token is used (not password)

❌ Cons
* Less secure if credentials are embedded in pipeline (must use Jenkins credentials store).
* Build number is less traceable than commit SHA (though still usable).
✗ Requires storing PAT/token
✗ Token may be printed accidentally
✗ Not as secure as SSH
✗ Slightly more complicated scripting



---
🔶 STAGE 3 (NOT RECOMMENDED – BAD PRACTICE)
Credentials embedded directly into the Git push URL
🟥 Version 3 – Username/Password Authentication
```
node {
    stage('Clone repository') {
        checkout scm
    }

    stage('Update GIT') {
        script {
            catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                withCredentials([usernamePassword(credentialsId: 'github', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                    sh "git config user.email raj@cloudwithraj.com"
                    sh "git config user.name RajSaha"
                    sh "cat deployment.yaml"
                    sh "sed -i 's+raj80dockerid/test.*+raj80dockerid/test:${DOCKERTAG}+g' deployment.yaml"
                    sh "git add ."
                    sh "git commit -m 'Done by Jenkins Job changemanifest: ${env.BUILD_NUMBER}'"
                    sh "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/${GIT_USERNAME}/kubernetesmanifest.git HEAD:main"
                }
            }
        }
    }
}
```
🔍 Steps Explained
* checkout scm → Clones repo based on Jenkins job SCM config.
* withCredentials([usernamePassword(...)]) → Injects GitHub username/password.
* sed -i ... → Updates image tag with ${DOCKERTAG}.
* git add, git commit, git push → Pushes changes using HTTPS with embedded credentials. Credentials completely exposed. Shows up in logs, debugging tools, audit trails

✅ Pros
* Explicit credential injection.
* Error handling with catchError.
* Shows file before/after change (cat deployment.yaml).

❌ Cons
* Uses username/password → less secure than SSH or token.
* Credentials appear in command line → risk of exposure in logs.
* More verbose, harder to maintain.
❌ Password/token visible in logs
❌ Very insecure
❌ Auditors or security teams will reject this
❌ Bad practice in enterprise
❌ Cannot be used in SOC2, ISO27001, or PCI environments



📊 Summary Table
Version	Auth Method	Image Tag	Security	Maintainability	Best Use Case
1	SSH key	Commit SHA	✅ Strong	✅ Clean	Production GitOps
2	HTTPS token	Build #	⚠️ Medium	✅ Simple	Small teams, easy setup
3	Username/Password	Custom tag	❌ Weak	⚠️ Verbose	Legacy setups


🏆 Best Choice: Version 1 (SSH Agent) Why?
1. Security: SSH keys are the most secure and widely accepted method for GitHub automation.
    * SSH agent uses a secure private key stored in Jenkins credentials.
    * No username/password.
    * No secrets printed in logs.
    * Fully supported by GitHub/Bitbucket.
2. Traceability: Uses commit SHA for image tag → directly links deployment to source code commit.
3. Clean workflow: Explicit repo cleanup and clone ensures no stale files.
4. Production‑ready: Matches GitOps best practices (ArgoCD teams often prefer SSH over HTTPS with passwords).ArgoCD recommends SSH access for secure repository polling.
5. Perfect for regulated industries. Banks, fintech, healthcare, enterprise all use SSH for GitOps pipelines.

## 📊 Summary Table

| Version | Auth Method       | Image Tag  | Security  | Maintainability | Best Use Case           |
| ------- | ----------------- | ---------- | --------- | --------------- | ----------------------- |
| **1**   | SSH key           | Commit SHA | ✅ Strong  | ✅ Clean         | Production GitOps       |
| **2**   | HTTPS token       | Build #    | ⚠️ Medium | ✅ Simple        | Small teams, easy setup |
| **3**   | Username/Password | Custom tag | ❌ Weak    | ⚠️ Verbose      | Legacy setups           |




***
---
===

✅ PRODUCTION VERSION of ManifestUpdate Stage
Includes:

✔ SSH
✔ Error handling
✔ Branch detection (main / master / feature branch)
✔ Auto PR creation
✔ Multi-manifest + multi-image update
✔ Commit only when changes exist
✔ Safe rollback
✔ Clean logs

🚀 FINAL PRODUCTION-READY MANIFEST UPDATE STAGE
```
stage('Trigger ManifestUpdate') {
    steps {
        script {

            echo "===== Starting Manifest Update Stage ====="

            // Wrap entire logic in try/catch for robust error handling
            try {

                sshagent(['github-ssh-key']) {

                    // Detect target branch dynamically
                    def TARGET_BRANCH = sh(
                        script: "git ls-remote --heads $MANIFEST_REPO | grep main >/dev/null && echo main || echo master",
                        returnStdout: true
                    ).trim()

                    echo "Detected Kubernetes manifest branch: ${TARGET_BRANCH}"

                    // Clone repo cleanly
                    sh """
                        rm -rf $MANIFEST_REPO_DIR
                        git clone -b ${TARGET_BRANCH} $MANIFEST_REPO $MANIFEST_REPO_DIR
                    """

                    dir(MANIFEST_REPO_DIR) {

                        // Multi-file & multi-image update in all Deployments
                        sh """
                            echo "Updating ALL deployment YAMLs..."

                            for file in \$(grep -rl 'image:' ./); do
                                echo "Updating image in: \$file"
                                sed -i 's|image: .*|image: $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT}|g' \$file
                            done
                        """

                        // Check if changes exist
                        def CHANGES = sh(script: "git status --porcelain", returnStdout: true).trim()
                        if (!CHANGES) {
                            echo "No manifest changes detected. Skipping commit/push."
                            return
                        }

                        // Configure git identity
                        sh """
                            git config user.email "jenkins@company.com"
                            git config user.name "Jenkins CI"
                        """

                        // Create feature branch for PR
                        def PR_BRANCH = "update-image-${GIT_COMMIT_SHORT}"
                        sh "git checkout -b ${PR_BRANCH}"

                        // Commit changes
                        sh """
                            git add .
                            git commit -m "Update image tag to $IMAGE_NAME:${GIT_COMMIT_SHORT} (Automated by Jenkins)"
                        """

                        // Push new branch
                        sh """
                            git push origin ${PR_BRANCH}
                        """

                        echo "Branch ${PR_BRANCH} pushed successfully."

                        // Create Pull Request via GitHub CLI (gh)
                        sh """
                            gh pr create \
                                --repo ${MANIFEST_REPO.replace('git@github.com:', '').replace('.git','')} \
                                --head ${PR_BRANCH} \
                                --base ${TARGET_BRANCH} \
                                --title "Update image to ${IMAGE_NAME}:${GIT_COMMIT_SHORT}" \
                                --body "Automated PR created by Jenkins pipeline to update Kubernetes manifests."
                        """

                        echo "Pull Request created for branch: ${PR_BRANCH}"
                    }
                }
            } catch (Exception e) {
                echo "❌ Manifest update failed: ${e.message}"
                currentBuild.result = 'UNSTABLE'
                throw e
            }

            echo "===== Manifest Update Completed Successfully ====="
        }
    }
}
```
🧠 EXPLANATION — WHY THIS IS PRODUCTION GRADE

✔ 1. SSH Authentication
`sshagent(['github-ssh-key'])`
Highly secure & industry standard.

✔ 2. Error Handling
The whole stage is wrapped inside:
`try {  ... } catch (Exception e) { ... }`
If anything breaks:
* pipeline doesn't crash silently
* Jenkins marks the build UNSTABLE
* detailed message logged
* exception re-thrown for visibility

✔ 3. Branch Detection
Your manifest repo may use main or master. Automatically detects:
```
def TARGET_BRANCH = sh(
    script: "git ls-remote --heads $MANIFEST_REPO | grep main >/dev/null && echo main || echo master",
    returnStdout: true
).trim()
```
* git ls-remote --heads $MANIFEST_REPO : Lists all branches (--heads) in the remote Git repository defined by $MANIFEST_REPO.
* grep main >/dev/null, Searches the branch list for the word main. >/dev/null discards the actual output, we only care if the match exists.
* && echo main || echo master : This is a conditional shell expression: If grep main succeeds (branch main exists), it echoes main.
Otherwise, it echoes master.
* returnStdout: true :Captures the output of the shell command into Jenkins/Groovy instead of just printing it.
* .trim() : Removes any leading/trailing whitespace or newline characters from the result.
* def TARGET_BRANCH = ...  : Stores the final result (main or master) into the Groovy variable TARGET_BRANCH.


✔ 4. Auto PR Creation
This is true GitOps practice:
✔ Do NOT push directly to main
✔ Create a separate branch
✔ Commit changes
✔ Push
✔ Create pull request
✔ ArgoCD syncs only after approved+merged
```
// Create Pull Request via GitHub CLI (gh)
sh """
    gh pr create \
        --repo ${MANIFEST_REPO.replace('git@github.com:', '').replace('.git','')} \
        --head ${PR_BRANCH} \
        --base ${TARGET_BRANCH} \
        --title "Update image to ${IMAGE_NAME}:${GIT_COMMIT_SHORT}" \
        --body "Automated PR created by Jenkins pipeline to update Kubernetes manifests."
"""
```

CHECK IF gh is installed and jenkins has access or not:
```
sudo apt install gh -y
sudo su - jenkins
jenkins@Lord-Shiva:~$ gh --version
gh version 2.4.0+dfsg1 (2022-03-23 Ubuntu 2.4.0+dfsg1-2)
https://github.com/cli/cli/releases/latest
```
🧩 Step‑by‑Step Explanation
gh pr create :
* This uses the GitHub CLI (gh) to create a new Pull Request (PR) on GitHub. Instead of manually opening GitHub and clicking “New PR,” Jenkins automates it.
* --repo ${MANIFEST_REPO.replace('git@github.com:', '').replace('.git','')} :
Specifies which repository the PR should be created in. 
  Example: If MANIFEST_REPO = git@github.com:org/k8s-manifests.git, after replacements it becomes org/k8s-manifests.
This is the GitOps repo containing Kubernetes manifests.
* --head ${PR_BRANCH}:
The branch containing your changes (e.g., updated Deployment YAML with new image tag). Jenkins created this branch earlier in the pipeline.
* --base ${TARGET_BRANCH} :
The branch you want to merge into (usually main or master). Earlier logic (git ls-remote) determined whether the repo uses main or master.
* --title "Update image to ${IMAGE_NAME}:${GIT_COMMIT_SHORT}" : The PR title shown in GitHub.
  Example: Update image to my-app:abc123
* --body "Automated PR created by Jenkins pipeline to update Kubernetes manifests." :  The PR description. Explains that Jenkins created this PR automatically.

WHAT DOES THIS COMMAND DO ? :
* This code automatically creates a Pull Request in the Kubernetes manifests GitHub repo using the GitHub CLI.
* The PR contains changes made by Jenkins (e.g., updated Docker image tag in Deployment YAML).
* It targets the correct base branch (main or master).
* It includes a descriptive title and body.



✔ 5. Multi-Image + Multi-File Update
This handles all YAML files:
```
// Multi-file & multi-image update in all Deployments
sh """
    echo "Updating ALL deployment YAMLs..."

    for file in \$(grep -rl 'image:' ./); do
        echo "Updating image in: \$file"
        sed -i 's|image: .*|image: $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT}|g' \$file
    done
"""                    
```
🧩 Step‑by‑Step Explanation
* grep -rl 'image:' ./ : Searches recursively (-r) through the current directory (./) for files containing the string image:.
-l means only list the filenames, not the matching lines.
So this finds all Kubernetes YAML files that declare a container image.
* for file in $( ... ); do ... done : Loops through each file found by grep. Each file path is stored in the variable file.
* echo "Updating image in: $file" : Prints which file is being updated (for logging/debugging).
* sed -i 's|image: .*|image: $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT}|g' $file : 
  Uses sed (stream editor) to replace the line starting with image: in each file. "
  .* matches whatever image value was there before.
  Replaces it with a new image string: `image: $REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT}`
  Example result: image: dockerhub.com/my-app:abc123
* -i edits the file in place.

This code automatically updates the container image reference in all Kubernetes Deployment YAML files in the repo.
* It finds every manifest that has an image: line.
* It replaces the old image with the new one built in Jenkins ($REGISTRY/$IMAGE_NAME:${GIT_COMMIT_SHORT}).
* It ensures all deployments use the latest image version.

So if you have:
deployment.yaml
cronjob.yaml
job.yaml
kustomization.yaml
All get updated.

In short: This snippet loops through all Kubernetes YAML files containing image: and replaces the image tag with the newly built Docker image, ensuring every deployment manifest is updated consistently.


✔ 6. Smart Change Detection
Does NOT commit when there is no change:
```
// Check if changes exist
def CHANGES = sh(script: "git status --porcelain", returnStdout: true).trim()
if (!CHANGES) {
    echo "No manifest changes detected. Skipping commit/push."
    return
}
```
🧩 Step‑by‑Step Explanation
* git status --porcelain : Runs git status in a special porcelain mode (machine‑friendly output).
Instead of verbose text, it outputs a short list of changed files.
```
git status --porcelain
 M part11.txt
 M part12.txt
```
If there are no changes, the output is empty.

* returnStdout: true  Captures the command’s output into the Groovy variable instead of printing it.
* .trim() : Removes any whitespace or newline characters from the captured output.
* def CHANGES = ...   Stores the result (list of changed files, or empty string if none) in the variable CHANGES.
* if (!CHANGES) : Checks if CHANGES is empty (meaning no modified files). If empty → no changes detected.
* echo "No manifest changes detected. Skipping commit/push."  : Prints a message to the Jenkins console log.
* return : Exits the current stage early, skipping the commit/push logic.

This code checks whether any files in the Git repository have been modified (for example, updated Kubernetes manifests).
If no changes exist, Jenkins logs a message and skips the commit/push step.If changes exist, the pipeline continues and commits/pushes them to the GitOps repo.


🛠️ Why It’s Useful in Production
* Prevents empty commits when nothing has changed.
* Keeps the GitOps repo clean and avoids unnecessary ArgoCD syncs.
* Saves time and avoids confusion in audit logs.


✔ 7. PR Branch Naming Convention
```
// Configure git identity
sh """
    git config user.email "jenkins@company.com"
    git config user.name "Jenkins CI"
"""

// Create feature branch for PR
def PR_BRANCH = "update-image-${GIT_COMMIT_SHORT}"
sh "git checkout -b ${PR_BRANCH}"

// Commit changes
sh """
    git add .
    git commit -m "Update image tag to $IMAGE_NAME:${GIT_COMMIT_SHORT} (Automated by Jenkins)"
"""

// Push new branch
sh """
    git push origin ${PR_BRANCH}
"""

```
🧩 Step‑by‑Step Explanation
Configure Git identity
```
git config user.email "jenkins@company.com"
git config user.name "Jenkins CI"
```
* Sets the commit author identity to Jenkins.
* Ensures commits are traceable to the CI/CD system, not a developer’s local identity.
* Important for auditability in GitOps.

Create feature branch for PR
```
def PR_BRANCH = "update-image-${GIT_COMMIT_SHORT}"
sh "git checkout -b ${PR_BRANCH}"
```
* Defines a new branch name like update-image-abc123 (using the short commit SHA of the app repo).
* Creates and switches to this new branch.
* This branch will hold the manifest changes (e.g., updated image tag).

Commit changes
```
git add .
git commit -m "Update image tag to $IMAGE_NAME:${GIT_COMMIT_SHORT} (Automated by Jenkins)"
```
* Stages all modified files (git add .).
* Commits them with a descriptive message showing the new image tag.
* Example commit message:  ` Update image tag to my-app:abc123 (Automated by Jenkins)`

Push new branch
`git push origin ${PR_BRANCH}`
* Pushes the new branch to the remote GitHub repository.
* This branch can then be used to open a Pull Request (PR) for review and merge into main/master.

This code snippet makes Jenkins create a new Git branch with updated Kubernetes manifests, commits the changes, and pushes the branch to GitHub.
* The branch name includes the app commit SHA → traceable to the exact build.
* The commit message documents the automated update.
* Once pushed, a Pull Request can be created (manually or via automation) to merge the changes into the main branch.
* ArgoCD will then detect the merged manifest changes and deploy the new image.

***
---
===


SSH-SETUP IN JENKINS ::
```
pratheush@Lord-Shiva:/mnt/c/Users/prath$ sudo su - jenkins
Welcome to Ubuntu 22.04.5 LTS (GNU/Linux 6.6.87.2-microsoft-standard-WSL2 x86_64)

 * Documentation:  https://help.ubuntu.com
 * Management:     https://landscape.canonical.com
 * Support:        https://ubuntu.com/pro

 System information as of Fri Nov 28 16:32:50 IST 2025

  System load:  0.6                 Processes:             86
  Usage of /:   1.1% of 1006.85GB   Users logged in:       2
  Memory usage: 83%                 IPv4 address for eth0: 172.26.238.72
  Swap usage:   100%


This message is shown once a day. To disable it please create the
/var/lib/jenkins/.hushlogin file.
jenkins@Lord-Shiva:~$ ssh -T git@github.com
The authenticity of host 'github.com (20.207.73.82)' can't be established.
ED25519 key fingerprint is SHA256:+DiY3wvvV6TuJJhbpZisF0oiu7/zLDA0zPMSvHdkrujh4UvCOqU.
This key is not known by any other names
Are you sure you want to continue connecting (yes/no/[fingerprint])? yes
Warning: Permanently added 'github.com' (ED25519) to the list of known hosts.
git@github.com: Permission denied (publickey).
```



🗝 2. Generate SSH Keys for Jenkins
Run these commands inside Ubuntu (WSL):
# Switch to Jenkins user if Jenkins runs as a service
sudo su - jenkins

# Generate a new SSH key pair
ssh-keygen -t ed25519 -C "jenkins@yourcompany.com"
Press Enter for default path (/var/lib/jenkins/.ssh/id_ed25519).
Do not set a passphrase (Jenkins can’t handle interactive prompts).
This creates:
Private key → /var/lib/jenkins/.ssh/id_ed25519
Public key → /var/lib/jenkins/.ssh/id_ed25519.pub

ssh-keygen -t ed25519 -C "jenkins@yourcompany.com"
ssh-keygen : The program that creates SSH keypairs (private + public). It's built into OpenSSH on Linux/macOS and available on modern Windows.
-t ed25519 : chooses the key type/algorithm. ed25519 is a modern elliptic-curve algorithm:
-C "jenkins@yourcompany.com" : -C adds a comment to the public key file (the part after the key text).
Private key: ~/.ssh/id_ed25519 — keep secret. chmod should be 600 (read/write owner only).
Public key: ~/.ssh/id_ed25519.pub — safe to share (you copy this to servers, GitHub, etc.).

```
pratheush@Lord-Shiva:~$ sudo su - jenkins
[sudo] password for pratheush:
jenkins@Lord-Shiva:~$ ssh-keygen -t ed25519 -C "jenkins@rajhomelrnk8.com"
Generating public/private ed25519 key pair.
Enter file in which to save the key (/var/lib/jenkins/.ssh/id_ed25519):
Enter passphrase (empty for no passphrase):
Enter same passphrase again:
Your identification has been saved in /var/lib/jenkins/.ssh/id_ed25519
Your public key has been saved in /var/lib/jenkins/.ssh/id_ed25519.pub
The key fingerprint is:
SHA256:m7yQFFONWtxCZaTWgffqdpz6C9YupUijiuhk+RVeXfpwFSt7yNs jenkins@rajhomelrnk8.com
The key's randomart image is:
+--[ED25519 256]--+
|       oo*+    .+|
|       .=+o    .=|
|      ooo..  .+.+|
|      .+ . ...+* |
|      . S + .++..|
|     . o + o =+  |
|      o =   *. E |
|       o .o+ +   |
|       .+o..o.o  |
+----[SHA256]-----+

```


🌐 3. Add Public Key to GitHub
Go to GitHub → Settings → SSH and GPG keys → New SSH key.
Paste the contents of id_ed25519.pub.
Give it a name like “Jenkins WSL Key”.
Now GitHub trusts Jenkins.


🔒 4. Store Private Key in Jenkins Credentials
In Jenkins UI → Manage Jenkins → Credentials → Global credentials.
Add a new credential:
Kind: SSH Username with private key
ID: github-ssh-key (this matches your pipeline sshagent(['github-ssh-key']))
Username: git
Private Key: Paste contents of /var/lib/jenkins/.ssh/id_ed25519

🔗 5. Test SSH Connection
From Jenkins server (Ubuntu WSL):
ssh -T git@github.com
EXAMPLE OUTPUT::
jenkins@Lord-Shiva:~/.ssh$ ssh -T git@github.com
Hi Pratheush! You've successfully authenticated, but GitHub does not provide shell access.
jenkins@Lord-Shiva:~/.ssh$

⚙️ 6. Use in Jenkinsfile
Your pipeline already has:
sshagent(['github-ssh-key']) {
    sh """
        git clone git@github.com:Pratheush/spring-taskapp-k8-manifests.git
    """
}
The sshagent block loads the private key from Jenkins credentials.
Jenkins can now clone, commit, and push to GitHub securely.


🔑 7. About Tokens vs SSH
SSH keys → Used for Git operations (clone, push, PR creation).
GitHub Personal Access Tokens (PATs) → Used for API calls (e.g., GitHub CLI gh pr create).
You generate PAT in GitHub → Settings → Developer settings → Tokens.
Store it in Jenkins credentials (jenkins-github-token) and reference it in your pipeline if needed.

***
---
+++
===


Yes — ngrok free tier allows you to expose one local service at a time to the internet.
For CI/CD:
Expose Jenkins if you want GitHub webhooks.
SonarQube exposure is not required — Jenkins handles communication.

TRIVY SCANNING ::
trivy image --cache-dir /mnt/c/Users/prath/trivy-jv-db eclipse-temurin:21-jre-alpine


***
---
+++
===













































use k6 to see horrizontal autoscaler works or not




































































































































======================================================================

Triggered ManifestUpdate Stage :
stage('Trigger ManifestUpdate') {
       steps {
           script {
               // Clone GitOps repo containing manifests
               sh "git clone https://github.com/Pratheush/spring-taskapp-k8-manifests.git"
               dir('manifests') {
                   // Update Deployment manifest with new image tag
                   sh """
                   sed -i 's|image: my-dockerhub-user/my-app:.*|image: my-dockerhub-user/my-app:${env.BUILD_NUMBER}|g' deployment.yaml
                   git config user.email 'jenkins@org.com'
                   git config user.name 'Jenkins CI'
                   git add deployment.yaml
                   git commit -m 'Update image tag to ${env.BUILD_NUMBER}'
                   git push https://github.com/org/k8s-manifests.git HEAD:main
                   """
               }
           }
       }
   }











