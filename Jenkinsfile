node {
   // Mark the code checkout 'stage'....
   stage 'Checkout'

   // Get some code from a GitHub repository
   git branch: env.BRANCH_NAME, url: 'https://github.com/Nincodedo/NinsModLister.git'

   stage 'Build'
   // Run the gradle build
   bat 'gradlew.bat clean setupCIWorkspace build'
   
   stage 'Archive'
   archive includes: 'build/libs/*.jar'
}
