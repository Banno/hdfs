version = "0.0.2-banno1"

desc "Build the hdfs image"
task :build do
  sh "docker build -t registry.banno-internal.com/hdfs:#{version} ."
end

desc "Push the hdfs image to the registry"
task :push do
  sh "docker push registry.banno-internal.com/hdfs:#{version}"
end

desc "Deploy to staging"
task :deploy do
  sh 'curl -X POST -H "Content-Type: application/json" http://dev.banno.com:8080/v2/apps -d@staging.json'
end
