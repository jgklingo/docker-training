# Use a lightweight base image
FROM alpine:latest

# Copy a script into the container
COPY run_script.sh /run_script.sh

# Make the script executable
RUN chmod +x /run_script.sh

# Set the command to run your desired command or script
CMD ["/run_script.sh"]