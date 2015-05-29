node('jdk7') {

	stage 'build'

		checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/apemberton/todo-web.git']]])
		env.PATH="${tool 'mvn-3.2.2'}/bin:${env.PATH}"
		sh 'mvn clean package'
		archive 'target/*.war'

	stage 'integration-test' 

		sh 'mvn verify'
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

stage 'production'
	echo 'mvn cargo:deploy'
	// sh 'puppet apply manifest.pp'

