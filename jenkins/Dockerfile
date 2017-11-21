FROM jenkins/jenkins:lts
RUN /usr/local/bin/install-plugins.sh antisamy-markup-formatter timestamper ws-cleanup workflow-aggregator pipeline-github-lib pipeline-maven

ENV JENKINS_USER admin
ENV JENKINS_PASS admin

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

COPY executors.groovy /usr/share/jenkins/ref/init.groovy.d/
COPY default-user.groovy /usr/share/jenkins/ref/init.groovy.d/