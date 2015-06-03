node('docker&&1234') {
	stage 'build'
	docker.withRegistry('https://registry.hub.docker.com'){
	def maven3 = docker.image('maven:3.3.3-jdk-8')
	maven3.pull()
	docker.withServer('tcp://127.0.0.1:1234') {
		maven3.inside() {
			checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/apemberton/todo-web.git']]])
			sh 'mvn clean package'
			archive 'target/*.war'

		stage 'integration-test' 
			sh 'mvn verify'
		}
	}
	}
}

stage 'quality-and-functional-test'

	parallel(qualityTest: {
    	node('jdk7') {
    		echo 'sonar scan'
        	// sh 'mvn sonar:sonar'
    	}
    }, functionalTest: {
    	echo 'selenium test'
        // build 'sauce-labs-test'
    })

    try {
        checkpoint('Testing Complete')
    } catch (NoSuchMethodError _) {
        echo 'Checkpoint feature available in Jenkins Enterprise by CloudBees.'
    }


stage 'approval'
	input 'Do you approve deployment to production?'

stage 'production'
	echo 'mvn cargo:deploy'
	// sh 'puppet apply manifest.pp'

