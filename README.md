# Build
mvn clean package && docker build -t com.http.tutorial/httpTutorial .

# RUN

docker rm -f httpTutorial || true && docker run -d -p 8080:8080 -p 4848:4848 --name httpTutorial com.http.tutorial/httpTutorial 