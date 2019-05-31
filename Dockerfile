FROM airhacks/glassfish
COPY ./target/httpTutorial.war ${DEPLOYMENT_DIR}
