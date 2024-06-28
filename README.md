# docker-training

This repo contains example Docker projects and files. Understanding and running the projects here will help you learn the basics of Docker. The following resources are highly recommended for those that are new to Docker:

- [Docker Basics](https://docs.docker.com/guides/docker-concepts/the-basics/what-is-a-container/)
- [Docker language-specific guides](https://docs.docker.com/language/)
- [Docker Compose](https://docs.docker.com/compose/)
- [ChatGPT](https://chatgpt.com/)

These projects will help you complete the following tasks:

- [ ] Start and stop containers
- [ ] Create and run an ephemeral container (running an individual from within the docker container, command finishes, docker container is destroyed).
- [ ] Create and run an interactive container (dropping to a bash shell)
- [ ] View container list, view images, view volumes, view networks. For example, create a volume and attach to a docker container, or connect via Dockerfile or Docker Compose.
- [ ] Download/pull Docker images
- [ ] Write a basic Docker manifest (Dockerfile)
- [ ] Build an initial Dockerfile using the Ubuntu base image
- [ ] Run a basic web server using Docker Compose as a background daemon. You will need to write a docker-compose.yml manifest.

After playing with this repo, you can run `docker container prune`, `docker image prune`, and `docker volume prune` to free up storage space.

### Prerequisites

Docker Desktop needs to be installed. One of the key benefits of Docker is that it automatically downloads the other dependencies that are needed to run the projects.

To run the chess client, you will also need VSCode so you can open the project in a dev container. VSCode is also recommended to examine the contents of the repo.

## chess

"chess" is a more advanced implementation of Docker. The project is a command line multiplayer implementation of the game of chess written in Java. The implementation uses Docker Compose for the server application, which also relies on a MySQL image running in a separate container.

To start the server, navigate to the [chess/server](/chess/server/) directory and run `docker compose up`.

To stop the server and remove its containers, hit CTRL+C in the terminal window where you started the server, or navigate to the same directory and run `docker compose down`.

To start the client (once the server is running), open the chess folder in VSCode, reopen the project in the devcontainer, and run [client/.../Main.java](./chess/client/src/main/java/Main.java).

### Principles

- [compose.yaml](./chess/server/compose.yaml) is a typical Compose configuration file that tells Docker Compose how to start the project.
    - Two services are defined, the server application and the MySQL database.
    - Port 8080 of the server application is mapped to port 8080 of the host machine, which allows the chess server to be accessed from the host. After starting the server, try visiting [localhost:8080/](http://localhost:8080/) in your browser to see the server in action.
    - The db (database) service uses a basic MySQL image and configures the database with environment variables.
    - The db service also runs a health check to check if the database is running and prepared for queries. If this were not implemented, the server service would attempt to start before the database was ready and would fail.
    - A volume, db-data, is defined, allowing the db service to store its data persistently between sessions. Without a volume, the database would be deleted each time the server was stopped.
- The [Dockerfile](./chess/server/Dockerfile) tells Docker how to create the server image.
    - It uses a base openjdk image.
    - It then installs maven, the Java packaging tool used by the chess project, and uses it to compile and package the source code.
    - Port 8080 is exposed as noted earlier.
    - The CMD instruction runs the server application as the container starts.

## ephemeral-container

Ephemeral containers are containers that start, execute some code, then stop and remove themselves. They are good for one-off actions. "ephemeral-container" is an extremely simple example of a container that starts, runs an example script, and stops.

Navigate to the ephemeral-container folder and build the image with `docker build -t ephemeral-container .`.

Run and then delete the image in a container with `docker run --rm ephemeral-container`.

### Principles

- The [Dockerfile](./ephemeral-container/Dockerfile) creates a lightweight image and copies in the example script, [run_script.sh](./ephemeral-container/run_script.sh).
    - The RUN instruction runs a command that makes the script executable within the container.
    - The CMD instruction tells the container to run the script as it starts.
- The `-t` option in the `build` command tags the image with a human-readable name to make it easier to reference in the next command.
- The `--rm` option in the `run` command tells Docker to delete the container after running it. This prevents your system from becoming cluttered with old containers. This does not, however, delete the image, so the container can quickly be started again in the future.

## interactive-container

Interactive containers allow you to interact with them via your terminal. "interactive-container" starts a simple container built on an Ubuntu image, installs basic utilities for you to use, and starts the terminal.

Navigate to the interactive-container folder and build the image with `docker build -t interactive-container .`. This is a larger image, so the build process will not be as fast.

Run the image interactively with `docker run -it --rm interactive-container`.

Notice that commands you enter in your terminal are now passed to your container. Try basic Linux bash commands and installed commands like `curl`, `vim`, `nano`, and `less`. Type `exit` to exit the shell and delete the container.

### Principles

- The [Dockerfile](./interactive-container/Dockerfile) uses the base Ubuntu image and installs a number of common commands with the RUN instruction.
- The `-it` option in the `run` command tells Docker to open the container interactively in the terminal window. It combines two options, `--interactive` and `--tty`.