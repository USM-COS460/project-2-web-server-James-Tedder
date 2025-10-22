## COS 460/540 - Computer Networks
# Project 2: HTTP Server

# James Tedder

This project is written in Java on Windows.

## How to compile

First make sure you have a version of java installed on your machine. Download the java file. Run the javac command in the command line on the java file. This should compile the file.

## How to run

Once you've compiled run the java command on the java file with the port number as the first argument and the document root as the second argument.

## My experience with this project

I learned alot about web servers and how to accept and respond to requests. This project was fairly difficult for me because I was trying to use a buffered writer for my output stream. This made it so I couldn't send properly formatted jpegs to the browser. I fixed this by using a normal output stream. This has allowed my code to properly transmit the jpegs. I learned about HTTP formatting and codes. I learned how to use multiple threads to support multiple connections and I learned how to send multiple file types over a socket.
