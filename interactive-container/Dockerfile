# Use a base image
FROM ubuntu:latest

# Update the package list and install common utilities
RUN apt-get update && apt-get install -y \
    bash \
    curl \
    vim \
    nano \
    less \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /workspace

# Set the entrypoint to bash so that the container starts with a terminal
ENTRYPOINT ["/bin/bash"]